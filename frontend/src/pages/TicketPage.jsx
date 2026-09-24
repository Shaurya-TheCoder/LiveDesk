import React from "react";
import { useParams, Link } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";

import { getTicket } from "../api/ticketApi.js";
import useCustomerStore from "../stores/customerStore.js";
import Chat from "../features/chat/chat.jsx";

function TicketPage() {
  const { ticketId } = useParams();

  const sessionToken = useCustomerStore((state) => state.sessionToken);

  const {
    data: ticket,
    isLoading,
    isError,
    error,
  } = useQuery({
    queryKey: ["ticket", ticketId],
    queryFn: () => getTicket(ticketId, sessionToken),
    enabled: Boolean(ticketId && sessionToken),
  });

  // Dynamic Status Badge Colors
  const getStatusBadge = (status) => {
    switch (status?.toUpperCase()) {
      case "RESOLVED":
      case "CLOSED":
        return "bg-emerald-50 text-emerald-700 border-emerald-200/80";
      case "IN_PROGRESS":
      case "OPEN":
        return "bg-violet-50 text-violet-700 border-violet-200/80";
      case "PENDING":
        return "bg-amber-50 text-amber-700 border-amber-200/80";
      default:
        return "bg-slate-100 text-slate-600 border-slate-200";
    }
  };

  // Helper to format LocalDateTime or date strings
  const formatDate = (dateString) => {
    if (!dateString) return "Recently";
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString("en-US", {
        month: "short",
        day: "numeric",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
      });
    } catch {
      return String(dateString);
    }
  };

  // Loading State
  if (isLoading) {
    return (
      <div className="relative w-full min-h-[calc(100vh-80px)] bg-slate-50/60 flex items-center justify-center p-4">
        <div className="flex flex-col items-center gap-3 p-6 bg-white/90 backdrop-blur-md rounded-2xl border border-slate-100 shadow-xl">
          <div className="w-8 h-8 border-3 border-violet-600 border-t-transparent rounded-full animate-spin"></div>
          <p className="text-xs font-semibold text-slate-600">Loading conversation details...</p>
        </div>
      </div>
    );
  }

  // Error State
  if (isError) {
    return (
      <div className="relative w-full min-h-[calc(100vh-80px)] bg-slate-50/60 flex items-center justify-center p-4 font-sans">
        <div className="w-full max-w-md p-6 bg-white/90 backdrop-blur-md border border-slate-100 rounded-3xl shadow-xl space-y-4 text-center">
          <div className="w-12 h-12 mx-auto rounded-2xl bg-rose-100 text-rose-600 flex items-center justify-center">
            <svg className="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <circle cx="12" cy="12" r="10" />
              <line x1="12" y1="8" x2="12" y2="12" />
              <line x1="12" y1="16" x2="12.01" y2="16" />
            </svg>
          </div>
          <h1 className="text-lg font-extrabold text-slate-900">Unable to load ticket</h1>
          <p className="text-xs text-slate-500 leading-relaxed">
            {error?.message || error?.error || "We couldn't retrieve this ticket session."}
          </p>
          <Link
            to="/"
            className="inline-block px-5 py-2.5 text-xs font-semibold text-violet-700 bg-violet-100/70 hover:bg-violet-200/80 rounded-xl transition-colors cursor-pointer"
          >
            Return to Support Center
          </Link>
        </div>
      </div>
    );
  }

  // Not Found State
  if (!ticket) {
    return (
      <div className="relative w-full min-h-[calc(100vh-80px)] bg-slate-50/60 flex items-center justify-center p-4 font-sans">
        <div className="w-full max-w-md p-6 bg-white/90 backdrop-blur-md border border-slate-100 rounded-3xl shadow-xl text-center space-y-3">
          <p className="text-sm font-bold text-slate-700">Ticket not found.</p>
          <Link
            to="/"
            className="inline-block px-4 py-2 text-xs font-semibold text-violet-600 hover:underline"
          >
            Go Back
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="relative w-full min-h-[calc(100vh-80px)] py-4 sm:py-6 px-4 md:px-8 bg-slate-50/60 font-sans text-slate-800 overflow-hidden flex flex-col items-center">
      {/* Background Ambient Glows & Grid Pattern */}
      <div className="absolute top-0 left-0 w-125 h-125 rounded-full bg-linear-to-br from-violet-200/50 via-purple-100/30 to-transparent blur-[120px] pointer-events-none -z-10" />
      <div className="absolute bottom-0 right-0 w-125 h-125 rounded-full bg-linear-to-tl from-indigo-200/40 via-violet-100/30 to-transparent blur-[120px] pointer-events-none -z-10" />

      {/* Subtle Dot Grid Mask */}
      <div
        className="absolute inset-0 opacity-[0.25] pointer-events-none -z-10"
        style={{
          backgroundImage: `radial-gradient(#8b5cf6 0.75px, transparent 0.75px)`,
          backgroundSize: "24px 24px",
        }}
      />

      {/* Main Content Card Container */}
      <main className="max-w-5xl w-full flex flex-col h-[calc(100vh-120px)] min-h-150 bg-white/90 backdrop-blur-md border border-slate-100 rounded-3xl shadow-xl shadow-slate-200/50 overflow-hidden">
        {/* Ticket Header Bar */}
        <header className="px-6 py-4 border-b border-slate-100 bg-white/60 flex flex-wrap items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="p-2.5 rounded-2xl bg-violet-100/80 text-violet-600">
              <svg className="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
              </svg>
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h1 className="text-base font-extrabold text-slate-900 tracking-tight">
                  Support Ticket
                </h1>
                <span className="text-xs font-mono font-semibold text-slate-400">
                  #{ticket.ticketId || ticketId}
                </span>
              </div>
              <p className="text-[11px] text-slate-400">
                Created on {formatDate(ticket.createdAt)}
              </p>
            </div>
          </div>

          {/* Ticket Metadata & Badges */}
          <div className="flex items-center gap-3">
            {ticket.status && (
              <span
                className={`text-xs font-bold px-3 py-1 rounded-full border capitalize ${getStatusBadge(
                  ticket.status
                )}`}
              >
                {ticket.status.toLowerCase().replace("_", " ")}
              </span>
            )}

            <div className="flex items-center gap-1.5 px-3 py-1 text-xs font-semibold text-emerald-700 bg-emerald-50 rounded-full border border-emerald-100">
              <span className="w-1.5 h-1.5 bg-emerald-500 rounded-full animate-pulse"></span>
              Live Session
            </div>
          </div>
        </header>

        {/* Chat Component Container */}
        <div className="flex-1 overflow-hidden relative flex flex-col bg-slate-50/30">
          <Chat
            ticketId={ticketId}
            sessionToken={sessionToken}
            currentUserSender={"CUSTOMER"}
            status={ticket.status}
          />
        </div>
      </main>
    </div>
  );
}

export default TicketPage;