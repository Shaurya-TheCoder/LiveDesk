import React from "react";

function MessageList({ messages = [], currentUserSender }) {
  // Helper to format timestamps gracefully
  const formatTime = (timeString) => {
    if (!timeString) return "";
    try {
      const date = new Date(timeString);
      return date.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
    } catch {
      return timeString;
    }
  };

  return (
    <div className="space-y-4 py-2 font-sans">
      {messages.map((message) => {
        // Dynamic Point-of-View check
        const isMe = message.sender === currentUserSender;
        const isCustomer = message.sender === "CUSTOMER";

        return (
          <div
            key={message.id || message.createdAt || Math.random()}
            className={`flex items-end gap-2.5 ${
              isMe ? "justify-end" : "justify-start"
            }`}
          >
            {/* Avatar for Incoming Messages (Left Side) */}
            {!isMe && (
              <div
                className={`w-7 h-7 rounded-full flex items-center justify-center font-bold text-[10px] shrink-0 border shadow-xs ${
                  isCustomer
                    ? "bg-slate-100 text-slate-700 border-slate-200"
                    : "bg-violet-100 text-violet-700 border-violet-200/60"
                }`}
              >
                {isCustomer ? "CU" : "AG"}
              </div>
            )}

            {/* Message Bubble Container */}
            <div
              className={`flex flex-col max-w-[80%] sm:max-w-[70%] ${
                isMe ? "items-end" : "items-start"
              }`}
            >
              {/* Sender Name Label */}
              <span className="text-[10px] font-semibold text-slate-400 mb-1 px-1">
                {isMe ? "You" : isCustomer ? "Customer" : "Support Agent"}
              </span>

              {/* Chat Bubble */}
              <div
                className={`py-1.5 px-3.5 text-xs sm:text-sm leading-relaxed rounded-2xl shadow-xs transition-all ${
                  isMe
                    ? "bg-violet-600 text-white rounded-br-xs shadow-violet-200"
                    : "bg-white text-slate-800 border border-slate-100 rounded-bl-xs"
                }`}
              >
                {message.content}
              </div>

              {/* Timestamp */}
              {(message.timestamp || message.createdAt) && (
                <span className="text-[10px] text-slate-400 mt-1 px-1 font-medium">
                  {formatTime(message.timestamp || message.createdAt)}
                </span>
              )}
            </div>

            {/* Avatar for Active User Messages (Right Side) */}
            {isMe && (
              <div
                className={`w-7 h-7 rounded-full flex items-center justify-center font-bold text-[10px] shrink-0 border shadow-xs ${
                  isCustomer
                    ? "bg-slate-100 text-slate-700 border-slate-200"
                    : "bg-violet-100 text-violet-700 border-violet-200/60"
                }`}
              >
                {isCustomer ? "CU" : "AG"}
              </div>
            )}
          </div>
        );
      })}
    </div>
  );
}

export default MessageList;
