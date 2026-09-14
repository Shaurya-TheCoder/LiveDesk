function MessageList({ messages }) {
    return (
        <div className="chat-message-list">
            {messages.map((message) => (
                <div
                    key={message.id}
                    className={
                        message.sender === "CUSTOMER"
                            ? "chat-message chat-message--customer"
                            : "chat-message chat-message--agent"
                    }
                >
                    {message.content}
                </div>
            ))}
        </div>
    );
}

export default MessageList;

