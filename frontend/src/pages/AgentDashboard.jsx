import { useEffect, useState } from "react";
import useAuthStore from "../stores/authStore";

import { connectStomp, disconnectStomp } from "../ws/stompClient";
import TicketList from "../features/agent/ticketList";
import { getAssignedTickets, getResolvedTickets } from "../api/ticketApi";
import DashBoardHeader from "../components/dashboardHeader";
import Chat from "../features/chat/chat";

function AgentDashboardPage() {
  const token = useAuthStore((state) => state.token);
  const [selectedTicket, setSelectedTicket] = useState(null);
  const [assignedTickets, setAssignedTickets] = useState([]);
  const [resolvedTickets, setResolvedTickets] = useState([]);
  
  // Controls mobile toggle between list and active chat
  const [mobileView, setMobileView] = useState("list"); // 'list' | 'chat'

  useEffect(() => {
    if (!token) return;

    connectStomp({
      token,
      onConnect: (client) => {
        console.log("Agent WebSocket Connected");

        client.subscribe("/user/queue/notifications", (message) => {
          const notification = JSON.parse(message.body);
          console.log("Agent notification:", notification);
        });
      },
      onError: (err) => {
        console.error("Agent WebSocket Error:", err);
      },
    });

    return () => {
      disconnectStomp();
    };
  }, [token]);

  useEffect(() => {
    if (!token) return;

    getAssignedTickets(token)
      .then((tickets) => setAssignedTickets(tickets))
      .catch((error) => console.error("Failed to fetch assigned tickets:", error));

    getResolvedTickets(token)
      .then((tickets) => setResolvedTickets(tickets))
      .catch((error) => console.error("Failed to fetch resolved tickets:", error));
  }, [token]);

  const handleSelectTicket = (ticket) => {
    setSelectedTicket(ticket);
    setMobileView("chat"); // Auto-switch to chat panel on mobile
  };

  return (
    <div className="flex flex-col h-screen w-screen bg-slate-50/60 font-sans text-slate-800 overflow-hidden">
      {/* Header Bar */}
      <DashBoardHeader />

      {/* Main Workspace Grid / Flex Container */}
      <div className="flex-1 flex flex-row h-[calc(100vh-60px)] w-full overflow-hidden relative">
        {/* Left Sidebar: Ticket List */}
        <div
          className={`w-full md:w-80 lg:w-96 shrink-0 h-full border-r border-slate-200/80 bg-white transition-all duration-300 ${
            mobileView === "chat" ? "hidden md:block" : "block"
          }`}
        >
          <TicketList
            assignedTickets={assignedTickets}
            resolvedTickets={resolvedTickets}
            selectedTicket={selectedTicket}
            onSelectTicket={handleSelectTicket}
            authToken={token}
          />
        </div>

        {/* Right Panel: Chat Workspace */}
        <div
          className={`flex-1 h-full min-w-0 flex flex-col bg-slate-50/30 transition-all duration-300 ${
            mobileView === "list" ? "hidden md:flex" : "flex"
          }`}
        >
          {selectedTicket?.id ? (
            <div className="flex-1 flex flex-col h-full overflow-hidden">
              {/* Mobile Chat Top Bar with Back Button */}
              <div className="md:hidden px-4 py-2 bg-white border-b border-slate-100 flex items-center justify-between">
                <button
                  type="button"
                  onClick={() => setMobileView("list")}
                  className="flex items-center gap-1.5 text-xs font-semibold text-violet-600 hover:text-violet-700 cursor-pointer"
                >
                  <svg
                    className="w-4 h-4"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2.5"
                  >
                    <path d="M15 18l-6-6 6-6" />
                  </svg>
                  Back to Tickets
                </button>
                <span className="text-[11px] font-mono font-bold text-slate-400">
                  #{selectedTicket.id}
                </span>
              </div>

              {/* Live Chat View */}
              <div className="flex-1 h-full overflow-hidden">
                <Chat
                  ticketId={selectedTicket.id}
                  token={token}
                  currentUserSender={"AGENT"}
                  status={selectedTicket.status}
                />
              </div>
            </div>
          ) : (
            /* Empty Workspace Placeholder */
            <div className="flex-1 flex flex-col items-center justify-center p-6 text-center">
              <div className="w-16 h-16 rounded-3xl bg-violet-100/80 text-violet-600 flex items-center justify-center mb-4 shadow-sm">
                <svg
                  className="w-8 h-8"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="1.75"
                >
                  <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
                </svg>
              </div>
              <h3 className="text-base font-extrabold text-slate-900 mb-1">
                No Ticket Selected
              </h3>
              <p className="text-xs text-slate-400 max-w-xs leading-relaxed">
                Choose an assigned ticket from the sidebar on the left to start communicating with the customer.
              </p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default AgentDashboardPage;