import React, { useEffect } from "react";

function TicketNotificationPopup({ type, ticketId, message, onClose, onView }) {
  // Automatically dismiss after 5 seconds if desired
  useEffect(() => {
    const timer = setTimeout(() => {
      onClose?.();
    }, 5000);
    return () => clearTimeout(timer);
  }, [onClose]);

  const isEscalated = type === "TICKET_ESCALATED";

  return (
    <div className="fixed top-5 right-5 z-50 max-w-sm w-full animate-slide-in">
      <div className="bg-white/95 border border-slate-100 rounded-3xl shadow-2xl p-4 flex items-start gap-3.5 backdrop-blur-md">
        
        {/* Icon based on notification type */}
        <div
          className={`w-10 h-10 rounded-2xl flex items-center justify-center shrink-0 ${
            isEscalated
              ? "bg-rose-100 text-rose-600"
              : "bg-violet-100 text-violet-600"
          }`}
        >
          {isEscalated ? (
            // Flame / Warning Icon for Escalated
            <svg className="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
              <path d="M12 9v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          ) : (
            // User Check / Assignment Icon for Assigned
            <svg className="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
              <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
              <circle cx="8.5" cy="7" r="4" />
              <polyline points="17 11 19 13 23 9" />
            </svg>
          )}
        </div>

        {/* Content */}
        <div className="flex-1 space-y-1">
          <div className="flex items-center justify-between">
            <h4 className="text-xs font-extrabold text-slate-900 tracking-wide uppercase">
              {isEscalated ? "Ticket Escalated" : "Ticket Assigned"}
            </h4>
            <span className="text-[10px] font-bold text-slate-400">
              #{ticketId?.slice(-6) || "---"}
            </span>
          </div>
          <p className="text-xs text-slate-500 leading-relaxed line-clamp-2">
            {message || (isEscalated 
              ? "This ticket has been escalated for priority review." 
              : "You have been assigned to this ticket.")}
          </p>

          {/* Optional Action Button */}
          {onView && (
            <button
              type="button"
              onClick={onView}
              className="mt-2 text-[11px] font-bold text-violet-600 hover:text-violet-700 transition-colors cursor-pointer"
            >
              View Ticket &rarr;
            </button>
          )}
        </div>

        {/* Close Button */}
        <button
          type="button"
          onClick={onClose}
          className="text-slate-400 hover:text-slate-600 transition-colors p-1 cursor-pointer"
        >
          <svg className="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
            <line x1="18" y1="6" x2="6" y2="18" />
            <line x1="6" y1="6" x2="18" y2="18" />
          </svg>
        </button>
      </div>
    </div>
  );
}

export default TicketNotificationPopup;