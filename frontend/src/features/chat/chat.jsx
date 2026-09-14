import { useEffect, useState, useRef } from "react";

import MessageList from "./MessageList";
import MessageInput from "./MessageInput";
import { getTicketMessage } from "../../api/ticketApi";
import { connectStomp } from "../../ws/stompClient";

function Chat({ ticketId, sessionToken}) {
    const [messages, setMessages] = useState([]);
    const [isOtherUserTyping, setIsOtherUserTyping] = useState(false);

    const subscriptionRef = useRef(null);
    const typingSubscriptionRef = useRef(null);
    const stompClientRef = useRef(null);

    useEffect(() => {
        if(!ticketId || !sessionToken){
            return;
        }

        const client = connectStomp({
            sessionToken,

            onConnect: (stompClient) => {
                stompClientRef.current = stompClient;
                console.log("Subscribing to Chat:", ticketId);

                subscriptionRef.current?.unsubscribe();

                subscriptionRef.current = stompClient.subscribe(
                    `/topic/chat/${ticketId}`,
                    (message) => {
                        const receivedMessage = JSON.parse(message.body);

                        console.log("Received chat message:", receivedMessage);

                        setMessages((currentMessages) => [
                            ...currentMessages,
                            receivedMessage
                        ]);
                    }
                )

                typingSubscriptionRef.current?.unsubscribe();

                typingSubscriptionRef.current = stompClient.subscribe(
                    `/topic/chat/${ticketId}/typing`,
                    (message) => {
                        const typingResponse = JSON.parse(message.body);

                        console.log("Received typing indication:", typingResponse);

                        setIsOtherUserTyping(receivedTyping.typing);
                    }
                )
            },
            onError: (error) => { 
                console.error("Chat STOMP error:", error); 
            } 
        }); 
        return () => { 
            subscriptionRef.current?.unsubscribe();
            subscriptionRef.current = null;

            typingSubscriptionRef.current?.unsubscribe();
            typingSubscriptionRef.current = null;

            console.log("Chat subscription unsubscribed"); 
        }; 
    }, [ticketId, sessionToken]);

    useEffect(() => {
        if(!ticketId || !sessionToken)
            return;
        
        getTicketMessage(ticketId, sessionToken)
        .then((response) => {
            setMessages(response.content);
        })
        .catch((error) => {
            console.error("failed To fetch chat history: ", error);
        });

    }, [ticketId, sessionToken]);

    const handleSendMessage = (content) => {
        console.log("Message to send:", content);
        if(!stompClientRef.current?.connected){
            console.error("Stomp Client Is Not Connected!");
            return;
        }

        stompClientRef.current.publish({
            destination: `/app/chat/${ticketId}`,
            body: JSON.stringify({
                content
            })
        });
    };

    const handleTyping = (typing) => {
        if(!stompClientRef.current?.connected){
            return;
        }

        stompClientRef.current.publish({
            destination: `/app/chat/${ticketId}/typing`,

            body: JSON.stringify(typing)
        });
    }

    return (
        <div className="chat">
            <MessageList messages={messages} />

            <MessageInput onSend={handleSendMessage} onTyping={handleTyping}/>
        </div>
    );
}

export default Chat;
