import React, { useState } from "react";
import { resolveTicket } from "../../api/ticketApi";

function TicketList({
  assignedTickets = [],
  resolvedTickets = [],
  selectedTicket,
  onSelectTicket,
  onResolveTicket, // Callback when resolve is confirmed
  authToken 
}) {
  const [activeTab, setActiveTab] = useState("assigned");
  const [ticketToResolve, setTicketToResolve] = useState(null);

  const displayTickets = activeTab === "assigned" ? assignedTickets : resolvedTickets;

  // Priority Badge Color Selector
  const getPriorityBadge = (priority) => {
    switch (priority?.toUpperCase()) {
      case "HIGH":
      case "URGENT":
        return "bg-rose-50 text-rose-700 border-rose-100";
      case "MEDIUM":
        return "bg-amber-50 text-amber-700 border-amber-100";
      case "LOW":
        return "bg-slate-100 text-slate-600 border-slate-200";
      default:
        return "bg-slate-100 text-slate-600 border-slate-200";
    }
  };

  // Helper to format LocalDateTime or date strings gracefully
  const formatDate = (dateString) => {
    if (!dateString) return "Just now";
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString("en-US", {
        month: "short",
        day: "numeric",
        hour: "2-digit",
        minute: "2-digit",
      });
    } catch {
      return String(dateString);
    }
  };

  // Truncate UUID for cleaner card display while keeping full ID in tooltip/key
  const formatUuid = (uuid) => {
    if (!uuid) return "#----";
    return `#${String(uuid).substring(0, 8)}...`;
  };

  // Handle Resolve Modal Confirmation
  const handleConfirmResolve = async () => {
    if (!ticketToResolve) return;

    try {
      const response  = await resolveTicket(ticketToResolve.id, authToken);
      console.log(response);

      if (onResolveTicket) {
        onResolveTicket(ticketToResolve.id);
      }
    } catch (error) {
      console.error("Failed to resolve ticket:", error);
    } finally {
      setTicketToResolve(null);
    }
  };

  return (
    <>
      <aside className="w-full sm:w-80 md:w-96 h-full flex flex-col bg-white border-r border-slate-100 font-sans text-slate-800">
        
        {/* Header & Tabs */}
        <div className="p-4 sm:p-5 border-b border-slate-100 space-y-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <div className="p-2 rounded-xl bg-violet-100/80 text-violet-600">
                <svg className="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                  <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
                </svg>
              </div>
              <h2 className="text-lg font-extrabold tracking-tight text-slate-900">
                Conversations
              </h2>
            </div>
            <span className="text-xs font-semibold px-2.5 py-1 rounded-full bg-slate-100 text-slate-600">
              {assignedTickets.length + resolvedTickets.length} total
            </span>
          </div>

          {/* Tab Switcher */}
          <div className="flex p-1 bg-slate-100/80 rounded-xl text-xs font-semibold text-slate-600">
            <button
              type="button"
              onClick={() => setActiveTab("assigned")}
              className={`flex-1 py-2 rounded-lg transition-all flex items-center justify-center gap-1.5 cursor-pointer ${
                activeTab === "assigned"
                  ? "bg-white text-violet-700 shadow-xs font-bold"
                  : "hover:text-slate-900"
              }`}
            >
              <span>Assigned</span>
              <span
                className={`px-1.5 py-0.5 rounded-full text-[10px] ${
                  activeTab === "assigned"
                    ? "bg-violet-100 text-violet-700"
                    : "bg-slate-200 text-slate-600"
                }`}
              >
                {assignedTickets.length}
              </span>
            </button>

            <button
              type="button"
              onClick={() => setActiveTab("resolved")}
              className={`flex-1 py-2 rounded-lg transition-all flex items-center justify-center gap-1.5 cursor-pointer ${
                activeTab === "resolved"
                  ? "bg-white text-violet-700 shadow-xs font-bold"
                  : "hover:text-slate-900"
              }`}
            >
              <span>Resolved</span>
              <span
                className={`px-1.5 py-0.5 rounded-full text-[10px] ${
                  activeTab === "resolved"
                    ? "bg-violet-100 text-violet-700"
                    : "bg-slate-200 text-slate-600"
                }`}
              >
                {resolvedTickets.length}
              </span>
            </button>
          </div>
        </div>

        {/* Ticket List Area */}
        <div className="flex-1 overflow-y-auto p-3 space-y-2">
          {displayTickets.length === 0 ? (
            <div className="text-center py-12 px-4 space-y-2">
              <div className="w-10 h-10 mx-auto rounded-2xl bg-slate-100 flex items-center justify-center text-slate-400">
                <svg className="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M22 12h-6l-2 3h-4l-2-3H2" />
                  <path d="M5.45 5.11L2 12v6a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-6l-3.45-6.89A2 2 0 0 0 16.76 4H7.24a2 2 0 0 0-1.79 1.11z" />
                </svg>
              </div>
              <p className="text-xs font-semibold text-slate-500">No {activeTab} tickets</p>
            </div>
          ) : (
            displayTickets.map((ticket) => {
              const isSelected = selectedTicket?.id === ticket.id;

              return (
                <div
                  key={ticket.id}
                  onClick={() => onSelectTicket(ticket)}
                  className={`p-3.5 rounded-2xl transition-all border cursor-pointer group ${
                    isSelected
                      ? "bg-violet-50/60 border-violet-200 shadow-xs"
                      : "bg-white border-transparent hover:bg-slate-50 hover:border-slate-100"
                  }`}
                >
                  <div className="flex items-center justify-between mb-1.5">
                    <span
                      title={ticket.id}
                      className={`text-xs font-mono font-bold ${
                        isSelected ? "text-violet-700" : "text-slate-800"
                      }`}
                    >
                      {formatUuid(ticket.id)}
                    </span>

                    {ticket.priority && (
                      <span
                        className={`text-[10px] font-bold px-2 py-0.5 rounded-full border capitalize ${getPriorityBadge(
                          ticket.priority
                        )}`}
                      >
                        {ticket.priority.toLowerCase()}
                      </span>
                    )}
                  </div>

                  {/* Ticket Subject */}
                  <p className="text-xs text-slate-700 line-clamp-1 font-semibold mb-2">
                    {ticket.subject || "No Subject Provided"}
                  </p>

                  <div className="flex items-center justify-between pt-2 border-t border-slate-100/80 text-[11px] text-slate-400">
                    <span>{formatDate(ticket.createdAt)}</span>

                    {/* Show Resolve Button only for Assigned / Open Tickets */}
                    {activeTab === "assigned" && (
                      <button
                        type="button"
                        onClick={(e) => {
                          e.stopPropagation(); // Prevent selecting ticket on button click
                          setTicketToResolve(ticket);
                        }}
                        className="px-2.5 py-1 text-[10px] font-bold text-emerald-700 bg-emerald-50 hover:bg-emerald-100 border border-emerald-200/60 rounded-lg transition-colors cursor-pointer"
                      >
                        Mark Resolved
                      </button>
                    )}
                  </div>
                </div>
              );
            })
          )}
        </div>
      </aside>

      {/* RESOLVE TICKET CONFIRMATION POPUP */}
      {ticketToResolve && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-xs animate-in fade-in duration-200">
          <div className="w-full max-w-sm p-6 bg-white border border-slate-100 rounded-3xl shadow-2xl space-y-5">
            <div className="text-center space-y-2">
              <div className="w-12 h-12 mx-auto rounded-2xl bg-emerald-100 text-emerald-600 flex items-center justify-center">
                <svg className="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                  <polyline points="20 6 9 17 4 12" />
                </svg>
              </div>

              <h3 className="text-lg font-extrabold text-slate-900">
                Resolve Conversation?
              </h3>

              <p className="text-xs text-slate-500 leading-relaxed">
                Are you sure you want to mark ticket{" "}
                <span className="font-mono font-bold text-slate-700">
                  {formatUuid(ticketToResolve.id)}
                </span>{" "}
                as resolved?
              </p>
            </div>

            {/* Actions */}
            <div className="flex gap-2.5 pt-1">
              <button
                type="button"
                onClick={() => setTicketToResolve(null)}
                className="flex-1 py-2.5 px-4 text-xs font-semibold text-slate-600 bg-slate-100 hover:bg-slate-200 rounded-xl transition-colors cursor-pointer"
              >
                Cancel
              </button>

              <button
                type="button"
                onClick={handleConfirmResolve}
                className="flex-1 py-2.5 px-4 text-xs font-semibold text-white bg-emerald-600 hover:bg-emerald-700 rounded-xl transition-colors shadow-xs shadow-emerald-200 cursor-pointer"
              >
                Yes, Resolve
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}

export default TicketList;