import { useState } from "react";

function MessageInput({onSend, onTyping}){
    const [message, setMessage] = useState("");

    const typingTimeoutRef = useRef(null);
    const isTypingRef = useRef(false);

    const handleSubmit = (event) => {
        event.preventDefault();

        const trimmedMessage = message.trim();

        if(!trimmedMessage)
            return;

        onSend(trimmedMessage);
        setMessage("");

        onTyping(false);
    }

    const handleTyping = (value) => {
    if (value.length > 0) {
        if (!isTypingRef.current) {
            onTyping(true);
            isTypingRef.current = true;
        }

        clearTimeout(typingTimeoutRef.current);

        typingTimeoutRef.current = setTimeout(() => {
            onTyping(false);
            isTypingRef.current = false;
        }, 500);
    } else {
        clearTimeout(typingTimeoutRef.current);

        if (isTypingRef.current) {
            onTyping(false);
            isTypingRef.current = false;
        }
    }

    setMessage(value);
    };

    useEffect(() => {
        return () => {
            clearTimeout(typingTimeoutRef.current);
        };
    }, []);

    return(
        <form className="chat-input" onSubmit={handleSubmit}> 
            <input type="text" 
            value={message} 
            onChange={(event) => handleTyping(event.target.value)} 
            placeholder="Type a message..." autoComplete="off" />
            <button type="submit"> Send </button> 
        </form>
    );
}  

export default MessageInput;