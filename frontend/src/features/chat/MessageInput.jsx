import { useState, useEffect, useRef } from "react";

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

    return (
        <form
          onSubmit={handleSubmit}
          className="relative flex items-center gap-2 p-1.5 bg-white border border-slate-200 rounded-2xl focus-within:border-violet-500 focus-within:ring-4 focus-within:ring-violet-500/10 transition-all shadow-sm"
        >
          {/* Input Field */}
          <input
            type="text"
            value={message}
            onChange={(event) => handleTyping(event.target.value)}
            placeholder="Type a message..."
            autoComplete="off"
            className="flex-1 px-3 py-2 text-sm text-slate-700 placeholder:text-slate-400 bg-transparent outline-none border-none focus:ring-0"
          />
    
          {/* Send Button */}
          <button
            type="submit"
            disabled={!message?.trim()}
            className="flex items-center gap-1.5 px-4 py-2 text-xs font-semibold text-white bg-violet-600 rounded-xl hover:bg-violet-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all shadow-md shadow-violet-200 active:scale-[0.98] cursor-pointer shrink-0"
          >
            <span>Send</span>
            <svg
              className="w-3.5 h-3.5"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2.5"
            >
              <path d="M22 2L11 13M22 2l-7 20-4-9-9-4 20-7z" />
            </svg>
          </button>
        </form>
      );
}  

export default MessageInput;