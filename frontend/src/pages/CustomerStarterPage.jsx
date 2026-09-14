import { useState } from "react";
import { createTicket } from "../api/ticketApi.js";
import { useNavigate } from "react-router-dom";
import useCustomerStore from "../stores/customerStore.js";
import { connectStomp } from "../ws/stompClient.js";

function CustomerStartPage() {
    const [message, setMessage] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const navigate = useNavigate();

    const setTicketSession = useCustomerStore((state) => {
        return state.setTicketSession
    })

    async function handleSubmit(event) {
        event.preventDefault();

        if (!message.trim()) {
            setError("Please enter a message.");
            return;
        }

        setError("");
        setLoading(true);

        try {
            const ticket = await createTicket(message.trim());

            setTicketSession(ticket);
            
            connectStomp({
                sessionToken: ticket.sessionToken,
            
                onConnect: (client) => {
                    console.log("Customer STOMP connection established");
                
                    const subscription = client.subscribe(
                        `/topic/chat/${ticket.ticketId}`,
                        (message) => {
                            const chatMessage = JSON.parse(message.body);
                
                            console.log("Chat message received:", chatMessage);
                        }
                    );
                
                    console.log("Chat subscription created:", subscription.id);
                },
            
                onError: (error) => {
                    console.error("Customer STOMP connection failed:", error);
                }
            });

            navigate(`/ticket/${ticket.ticketId}`);
        } catch (error) {
            console.error("Failed to create ticket:", error);
            setError(error.message || error.error || "Failed to create ticket.");
        } finally {
            setLoading(false);
        }
    }

    return (
        <main>
            <h1>LiveDesk</h1>

            <p>How can we help you?</p>

            <form onSubmit={handleSubmit}>
                <textarea
                    value={message}
                    onChange={(event) => setMessage(event.target.value)}
                    placeholder="Describe your problem..."
                    maxLength={2000}
                    rows={6}
                />

                <button type="submit" disabled={loading}>
                    {loading ? "Starting..." : "Start Chat"}
                </button>
            </form>

            {error && <p>{error}</p>}
        </main>
    );
}

export default CustomerStartPage;