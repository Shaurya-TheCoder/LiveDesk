import React, { useEffect, useRef } from "react";

function MessageList({ messages = [], currentUserSender }) {
  const bottomRef = useRef(null);
  const prevMessagesLengthRef = useRef(messages.length);

  // Auto-scroll to bottom ONLY when new messages arrive at the bottom,
  // not when older messages are prepended to the top during pagination.
  useEffect(() => {
    const prevLength = prevMessagesLengthRef.current;
    const currentLength = messages.length;

    // If initial load or only 1 new message added (live chat update)
    if (prevLength === 0 || currentLength === prevLength + 1) {
      bottomRef.current?.scrollIntoView({ behavior: "smooth" });
    }

    prevMessagesLengthRef.current = currentLength;
  }, [messages]);

  // Helper to format timestamps (e.g., "08:20 PM")
  const formatTime = (timeString) => {
    if (!timeString) return "";
    try {
      const date = new Date(timeString);
      return date.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
    } catch {
      return timeString;
    }
  };

  // Helper to format date headers (e.g., "Today", "Yesterday", "Sep 22, 2026")
  const formatDateHeader = (timeString) => {
    if (!timeString) return "";
    try {
      const messageDate = new Date(timeString);
      const today = new Date();
      const yesterday = new Date();
      yesterday.setDate(today.getDate() - 1);

      if (messageDate.toDateString() === today.toDateString()) {
        return "Today";
      }
      if (messageDate.toDateString() === yesterday.toDateString()) {
        return "Yesterday";
      }
      return messageDate.toLocaleDateString([], {
        month: "short",
        day: "numeric",
        year: "numeric",
      });
    } catch {
      return timeString;
    }
  };

  return (
    /* Removed overflow-y-auto and h-full to prevent nested scrollbar conflicts */
    <div className="w-full space-y-4 font-sans">
      {messages.length === 0 ? (
        <div className="py-12 flex flex-col items-center justify-center text-center text-slate-400">
          <p className="text-xs font-medium">No messages in this ticket yet.</p>
        </div>
      ) : (
        messages.map((message, index) => {
          const isMe = message.sender === currentUserSender;
          const isCustomer = message.sender === "CUSTOMER";

          // Date header logic
          const currentTimestamp = message.timestamp || message.createdAt;
          const prevTimestamp =
            index > 0 ? messages[index - 1].timestamp || messages[index - 1].createdAt : null;

          const currentDateStr = currentTimestamp
            ? new Date(currentTimestamp).toDateString()
            : null;
          const prevDateStr = prevTimestamp
            ? new Date(prevTimestamp).toDateString()
            : null;

          const showDateHeader = currentDateStr && currentDateStr !== prevDateStr;

          return (
            <React.Fragment key={message.id || message.createdAt || index}>
              {/* Centered Date Separator */}
              {showDateHeader && (
                <div className="flex items-center justify-center my-4">
                  <div className="bg-slate-200/60 text-slate-500 text-[11px] font-semibold px-3 py-0.5 rounded-full border border-slate-200 shadow-2xs">
                    {formatDateHeader(currentTimestamp)}
                  </div>
                </div>
              )}

              {/* Message Row */}
              <div
                className={`flex items-end gap-2.5 ${
                  isMe ? "justify-end" : "justify-start"
                }`}
              >
                {/* Avatar for Incoming Messages */}
                {!isMe && (
                  <div
                    className={`w-7 h-7 rounded-full flex items-center justify-center font-bold text-[10px] shrink-0 border shadow-2xs ${
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
                    className={`py-2 px-3.5 text-xs sm:text-sm leading-relaxed rounded-2xl shadow-2xs transition-all ${
                      isMe
                        ? "bg-violet-600 text-white rounded-br-xs shadow-violet-200/50"
                        : "bg-white text-slate-800 border border-slate-200/80 rounded-bl-xs"
                    }`}
                  >
                    {message.content}
                  </div>

                  {/* Timestamp */}
                  {currentTimestamp && (
                    <span className="text-[10px] text-slate-400 mt-1 px-1 font-medium">
                      {formatTime(currentTimestamp)}
                    </span>
                  )}
                </div>

                {/* Avatar for Active User Messages */}
                {isMe && (
                  <div
                    className={`w-7 h-7 rounded-full flex items-center justify-center font-bold text-[10px] shrink-0 border shadow-2xs ${
                      isCustomer
                        ? "bg-slate-100 text-slate-700 border-slate-200"
                        : "bg-violet-100 text-violet-700 border-violet-200/60"
                    }`}
                  >
                    {isCustomer ? "CU" : "AG"}
                  </div>
                )}
              </div>
            </React.Fragment>
          );
        })
      )}

      {/* Invisible anchor target for initial/live scrolling */}
      <div ref={bottomRef} />
    </div>
  );
}

export default MessageList;