import { useParams } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";

import { getTicket } from "../api/ticketApi.js";
import useCustomerStore from "../stores/customerStore.js";

import Chat from "../features/chat/chat.jsx";

function TicketPage() {
    const { ticketId } = useParams();

    const sessionToken = useCustomerStore(
        (state) => state.sessionToken
    );

    const {
        data: ticket,
        isLoading,
        isError,
        error
    } = useQuery({
        queryKey: ["ticket", ticketId],
        queryFn: () => getTicket(ticketId, sessionToken),
        enabled: Boolean(ticketId && sessionToken)
    });

    if (isLoading) {
        return <p>Loading ticket...</p>;
    }

    if (isError) {
        return (
            <div>
                <h1>Unable to load ticket</h1>
                <p>{error.message || error.error}</p>
            </div>
        );
    }

    if (!ticket) {
        return <p>Ticket not found.</p>;
    }

    return (
        <main>
            <h1>Your Support Ticket</h1>

            <p>
                Ticket ID: {ticket.ticketId}
            </p>

            <p>
                Status: {ticket.status}
            </p>

            <p>
                Created: {ticket.createdAt}
            </p>

            <Chat ticketId={ticketId} sessionToken={sessionToken}/>
        </main>
    );
}

export default TicketPage;