import { useState } from "react";

function MessageInput({onSend, onTyping}){
    const [message, setMessage] = useState("");

    const handleSubmit = (event) => {
        event.preventDefault();

        const trimmedMessage = message.trim();

        if(!trimmedMessage)
            return;

        onSend(trimmedMessage);
        setMessage("");
    }

    return(
        <form className="chat-input" onSubmit={handleSubmit}> 
            <input type="text" 
            value={message} 
            onChange={(event) => {
                const value = event.target.value;
                setMessage(value);
                onTyping(message.length > 0);
            }} 
            placeholder="Type a message..." autoComplete="off" />
            <button type="submit"> Send </button> 
        </form>
    );
}  

export default MessageInput;