import React, { useState, useEffect } from "react";
import { closeTicket } from "../api/adminApi";

function AgentTicketsList({ tickets = [], agentName, loading, onTicketClosed, token }) {
  const [localTickets, setLocalTickets] = useState(tickets);
  const [ticketToClose, setTicketToClose] = useState(null);
  const [isClosing, setIsClosing] = useState(false);

  // Keep local tickets in sync if the parent passes new ticket data
  useEffect(() => {
    setLocalTickets(tickets);
  }, [tickets]);

  const handleConfirmClose = async () => {
    if (!ticketToClose) return;

    setIsClosing(true);
    try {
      await closeTicket(ticketToClose.id, token);
      
      // 1. Instantly remove from local UI state for immediate feedback
      setLocalTickets((prev) => prev.filter((t) => t.id !== ticketToClose.id));
      
      // 2. Notify parent component to sync its own state
      if (onTicketClosed) {
        onTicketClosed(ticketToClose.id);
      }
      
      setTicketToClose(null); // Close the modal
    } catch (err) {
      console.error("Failed to close ticket:", err);
    } finally {
      setIsClosing(false);
    }
  };

  // Helper to format Java LocalDateTime arrays or strings cleanly
  const formatDate = (dateInput) => {
    if (!dateInput) return "";
    const date = new Date(dateInput);
    if (isNaN(date.getTime())) return "";
    return date.toLocaleDateString("en-US", {
      month: "short",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  // Helper for dynamic priority badge styling
  const getPriorityBadgeClass = (priority) => {
    switch (priority?.toUpperCase()) {
      case "URGENT":
      case "HIGH":
        return "text-rose-700 bg-rose-50 border-rose-100";
      case "MEDIUM":
        return "text-amber-700 bg-amber-50 border-amber-100";
      default:
        return "text-slate-600 bg-slate-100 border-slate-200";
    }
  };

  // Helper for dynamic status badge styling
  const getStatusBadgeClass = (status) => {
    switch (status?.toUpperCase()) {
      case "RESOLVED":
        return "text-emerald-700 bg-emerald-50 border-emerald-100";
      case "CLOSED":
        return "text-slate-600 bg-slate-100 border-slate-200";
      default:
        return "text-blue-700 bg-blue-50 border-blue-100";
    }
  };

  if (loading) {
    return <div className="p-4 text-center text-xs text-slate-400">Loading resolved tickets...</div>;
  }

  if (localTickets.length === 0) {
    return (
      <div className="p-6 text-center text-xs text-slate-400">
        No resolved tickets found for <span className="font-semibold">{agentName}</span>.
      </div>
    );
  }

  return (
    <div className="space-y-2 relative">
      <p className="text-[11px] font-bold text-slate-400 uppercase tracking-wider px-1">
        Resolved by {agentName} ({localTickets.length})
      </p>

      {localTickets.map((ticket) => (
        <div 
          key={ticket.id} 
          className="p-3.5 bg-white border border-slate-100 rounded-2xl shadow-xs flex items-center justify-between gap-3"
        >
          <div className="space-y-1.5 flex-1 min-w-0">
            {/* Top row: ID, Status, Priority */}
            <div className="flex items-center flex-wrap gap-2">
              <span className="text-[10px] font-mono font-semibold text-slate-400">#{ticket.id}</span>
              
              {ticket.status && (
                <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full border ${getStatusBadgeClass(ticket.status)}`}>
                  {ticket.status}
                </span>
              )}

              {ticket.priority && (
                <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full border ${getPriorityBadgeClass(ticket.priority)}`}>
                  {ticket.priority}
                </span>
              )}
            </div>

            {/* Subject */}
            <h5 className="text-xs font-semibold text-slate-800 truncate">{ticket.subject}</h5>

            {/* Bottom metadata row: Dates */}
            <div className="flex items-center gap-3 text-[10px] text-slate-400">
              {ticket.createdAt && (
                <span>Created: <strong className="text-slate-600">{formatDate(ticket.createdAt)}</strong></span>
              )}
              {ticket.lastActivityAt && (
                <span>Last Activity: <strong className="text-slate-600">{formatDate(ticket.lastActivityAt)}</strong></span>
              )}
            </div>
          </div>

          <button
            onClick={() => setTicketToClose(ticket)}
            className="px-3 py-1.5 text-xs font-semibold text-rose-600 bg-rose-50 hover:bg-rose-100 border border-rose-100 rounded-xl transition-colors shrink-0 cursor-pointer"
          >
            Close
          </button>
        </div>
      ))}

      {/* Confirmation Modal Popup */}
      {ticketToClose && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-xs p-4">
          <div className="bg-white rounded-3xl p-6 max-w-sm w-full shadow-xl border border-slate-100 space-y-4 animate-in fade-in zoom-in-95 duration-150">
            <div className="space-y-1.5">
              <h3 className="text-sm font-extrabold text-slate-900">
                Close Ticket #{ticketToClose.id}?
              </h3>
              <p className="text-xs text-slate-500 leading-relaxed">
                Are you sure you want to proceed with the final closure of request <span className="font-semibold text-slate-700">"{ticketToClose.subject}"</span>?
              </p>
            </div>

            <div className="flex items-center justify-end gap-2 pt-2">
              <button
                onClick={() => setTicketToClose(null)}
                disabled={isClosing}
                className="px-4 py-2 text-xs font-semibold text-slate-600 bg-slate-100 hover:bg-slate-200 rounded-xl transition-colors cursor-pointer disabled:opacity-50"
              >
                Cancel
              </button>
              <button
                onClick={handleConfirmClose}
                disabled={isClosing}
                className="px-4 py-2 text-xs font-semibold text-white bg-rose-600 hover:bg-rose-700 rounded-xl transition-colors flex items-center gap-2 cursor-pointer disabled:opacity-50"
              >
                {isClosing ? "Closing..." : "Proceed & Close"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default AgentTicketsList;