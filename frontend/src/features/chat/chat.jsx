import { useEffect, useState, useRef } from "react";

import MessageList from "./MessageList";
import MessageInput from "./MessageInput";
import { getTicketMessage, getAgentPresence } from "../../api/ticketApi";
import { getStompClient } from "../../ws/stompClient";

function Chat({ ticketId, token, sessionToken, currentUserSender, status }) {
  const [messages, setMessages] = useState([]);
  const [isOtherUserTyping, setIsOtherUserTyping] = useState(false);
  const [isAgentOnline, setIsAgentOnline] = useState(false);
  const [showClosedModal, setShowClosedModal] = useState(false);

  const subscriptionRef = useRef(null);
  const typingSubscriptionRef = useRef(null);
  const stompClientRef = useRef(null);

  const isTicketClosed =
    status?.toUpperCase() === "RESOLVED" || status?.toUpperCase() === "CLOSED";

  useEffect(() => {
    if (!ticketId) return;

    const stompClient = getStompClient();

    if (!stompClient?.connected) {
      console.error("STOMP client is not connected!");
      return;
    }

    stompClientRef.current = stompClient;

    subscriptionRef.current?.unsubscribe();
    subscriptionRef.current = stompClient.subscribe(
      `/topic/chat/${ticketId}`,
      (message) => {
        const receivedMessage = JSON.parse(message.body);
        setMessages((currentMessages) => [...currentMessages, receivedMessage]);
      }
    );

    typingSubscriptionRef.current?.unsubscribe();
    typingSubscriptionRef.current = stompClient.subscribe(
      `/topic/chat/${ticketId}/typing`,
      (message) => {
        const typingResponse = JSON.parse(message.body);
        if (typingResponse.sender === currentUserSender) return;
        setIsOtherUserTyping(typingResponse.typing);
      }
    );

    return () => {
      subscriptionRef.current?.unsubscribe();
      subscriptionRef.current = null;
      typingSubscriptionRef.current?.unsubscribe();
      typingSubscriptionRef.current = null;
      stompClientRef.current = null;
    };
  }, [ticketId, currentUserSender]);

  useEffect(() => {
    if (!ticketId || (!sessionToken && !token)) return;

    getTicketMessage(ticketId, { sessionToken, token })
      .then((response) => {
        setMessages(response.content);
      })
      .catch((error) => {
        console.error("Failed to fetch chat history: ", error);
      });
  }, [ticketId, sessionToken, token, currentUserSender]);

  useEffect(() => {
    if (currentUserSender !== "CUSTOMER" || !ticketId || !sessionToken) return;

    getAgentPresence(ticketId, sessionToken)
      .then((response) => {
        setIsAgentOnline(response.online);
      })
      .catch((error) => {
        console.error("Failed to fetch agent presence:", error);
      });
  }, [ticketId, sessionToken, currentUserSender]);

  const handleSendMessage = (content) => {
    if (isTicketClosed) {
      setShowClosedModal(true);
      return;
    }

    if (!stompClientRef.current?.connected) {
      console.error("Stomp Client Is Not Connected!");
      return;
    }

    stompClientRef.current.publish({
      destination: `/app/chat/${ticketId}`,
      body: JSON.stringify({ content }),
    });
  };

  const handleTyping = (typing) => {
    if (isTicketClosed || !stompClientRef.current?.connected) return;

    stompClientRef.current.publish({
      destination: `/app/chat/${ticketId}/typing`,
      body: JSON.stringify(typing),
    });
  };

  return (
    <div className="flex flex-col h-full w-full bg-white font-sans text-slate-800 relative">
      {/* Header Bar / Agent Status (Visible for Customer) */}
      {currentUserSender === "CUSTOMER" && (
        <div className="flex items-center justify-between px-6 py-3.5 border-b border-slate-100 bg-white/80 backdrop-blur-md">
          <div className="flex items-center gap-2.5">
            <div className="relative">
              <div className="w-8 h-8 rounded-full bg-violet-100 text-violet-600 flex items-center justify-center font-bold text-xs">
                AG
              </div>
              <span
                className={`absolute bottom-0 right-0 w-2.5 h-2.5 rounded-full border-2 border-white ${
                  isAgentOnline ? "bg-emerald-500" : "bg-slate-300"
                }`}
              />
            </div>
            <div>
              <h3 className="text-xs font-bold text-slate-900">Support Agent</h3>
              <p className="text-[11px] font-medium text-slate-400">
                {isAgentOnline ? "Active & online" : "Offline"}
              </p>
            </div>
          </div>

          <div
            className={`flex items-center gap-1.5 px-2.5 py-1 text-xs font-semibold rounded-full border ${
              isAgentOnline
                ? "text-emerald-700 bg-emerald-50 border-emerald-100"
                : "text-slate-500 bg-slate-50 border-slate-200"
            }`}
          >
            <span
              className={`w-1.5 h-1.5 rounded-full ${
                isAgentOnline ? "bg-emerald-500 animate-pulse" : "bg-slate-400"
              }`}
            />
            {isAgentOnline ? "Agent online" : "Agent offline"}
          </div>
        </div>
      )}

      {/* Ticket Resolved/Closed Banner */}
      {isTicketClosed && (
        <div className="px-4 py-2.5 bg-amber-50/90 border-b border-amber-200/60 text-amber-800 text-xs font-semibold flex items-center justify-center gap-2">
          <svg className="w-4 h-4 text-amber-600 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <circle cx="12" cy="12" r="10" />
            <line x1="12" y1="8" x2="12" y2="12" />
            <line x1="12" y1="16" x2="12.01" y2="16" />
          </svg>
          <span>This ticket is {status.toLowerCase()}. New messages cannot be sent.</span>
        </div>
      )}

      {/* Messages Scroll Area */}
      <div className="flex-1 overflow-y-auto p-4 sm:p-6 bg-slate-50/50">
        <MessageList messages={messages} currentUserSender={currentUserSender} />

        {/* Animated Typing Indicator */}
        {isOtherUserTyping && !isTicketClosed && (
          <div className="flex items-center gap-2 mt-3 px-3 py-2 w-fit rounded-2xl bg-white border border-slate-100 shadow-xs text-xs text-slate-500">
            <div className="flex items-center gap-1">
              <span className="w-1.5 h-1.5 bg-violet-500 rounded-full animate-bounce [animation-delay:-0.3s]" />
              <span className="w-1.5 h-1.5 bg-violet-500 rounded-full animate-bounce [animation-delay:-0.15s]" />
              <span className="w-1.5 h-1.5 bg-violet-500 rounded-full animate-bounce" />
            </div>
            <span className="font-medium">
              {currentUserSender === "AGENT" ? "Customer is typing..." : "Agent is typing..."}
            </span>
          </div>
        )}
      </div>

      {/* Footer Message Input Section */}
      <div className="p-4 border-t border-slate-100 bg-white">
        <MessageInput
          onSend={handleSendMessage}
          onTyping={handleTyping}
          disabled={isTicketClosed}
        />
      </div>

      {/* CLOSED TICKET WARNING MODAL */}
      {showClosedModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-xs animate-in fade-in duration-200">
          <div className="w-full max-w-sm p-6 bg-white border border-slate-100 rounded-3xl shadow-2xl space-y-4 text-center">
            <div className="w-12 h-12 mx-auto rounded-2xl bg-amber-100 text-amber-600 flex items-center justify-center">
              <svg className="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                <path d="M7 11V7a5 5 0 0 1 10 0v4" />
              </svg>
            </div>

            <div className="space-y-1.5">
              <h3 className="text-base font-extrabold text-slate-900">
                Conversation Closed
              </h3>
              <p className="text-xs text-slate-500 leading-relaxed">
                This ticket has been marked as{" "}
                <span className="font-bold text-slate-700 capitalize">
                  {status?.toLowerCase()}
                </span>
                . Messaging is disabled for this conversation.
              </p>
            </div>

            <button
              type="button"
              onClick={() => setShowClosedModal(false)}
              className="w-full py-2.5 text-xs font-semibold text-white bg-violet-600 hover:bg-violet-700 rounded-xl transition-colors shadow-xs shadow-violet-200 cursor-pointer"
            >
              Understand & Close
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

export default Chat;
