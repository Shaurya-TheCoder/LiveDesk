import { useEffect, useState, useRef } from "react"; // Added useRef here
import useAuthStore from "../stores/authStore";

import { connectStomp, disconnectStomp, startAgentHeartbeat, stopAgentHeartbeat } from "../ws/stompClient";
import TicketList from "../features/agent/ticketList";
import { getAssignedTickets, getResolvedTickets } from "../api/ticketApi";
import DashBoardHeader from "../components/dashboardHeader";
import Chat from "../features/chat/chat";
import TicketNotificationPopup from "../components/TicketNotificationPopup"; // Make sure to import your popup component

function AgentDashboardPage() {
  const token = useAuthStore((state) => state.token);
  const [selectedTicket, setSelectedTicket] = useState(null);
  const [assignedTickets, setAssignedTickets] = useState([]);
  const [resolvedTickets, setResolvedTickets] = useState([]);
  const [notification, setNotification] = useState(null);
  
  const notificationSubscriptionRef = useRef(null);
  
  // Controls mobile toggle between list and active chat
  const [mobileView, setMobileView] = useState("list"); // 'list' | 'chat'

  const handleSelectTicket = (ticket) => {
    setSelectedTicket(ticket);
    setMobileView("chat"); // Auto-switch to chat panel on mobile
  };

  // Manage WebSocket connection and Agent Heartbeat lifecycle
  useEffect(() => {
    if (!token) return;

    connectStomp({
      token,
      onConnect: (client) => {
        console.log("Agent WebSocket Connected");
        startAgentHeartbeat();

        notificationSubscriptionRef.current?.unsubscribe(); 

        // FIX: Subscribe to the agent's general notification queue or topic 
        // using the 'client' argument passed by connectStomp
        notificationSubscriptionRef.current = client.subscribe(
          `/user/queue/notifications`, 
          (message) => {
            const receivedNotification = JSON.parse(message.body);
    
            console.log("Agent notification:", receivedNotification);
    
            setNotification(receivedNotification);
          }
        );
      },
      onError: (err) => {
        console.error("Agent WebSocket Error:", err);
      },
    });

    return () => {
      notificationSubscriptionRef.current?.unsubscribe();
      notificationSubscriptionRef.current = null;

      stopAgentHeartbeat();
      disconnectStomp();
    };
  }, [token]);

  // Fetch initial ticket data
  useEffect(() => {
    if (!token) return;

    getAssignedTickets(token)
      .then((tickets) => setAssignedTickets(tickets))
      .catch((error) => console.error("Failed to fetch assigned tickets:", error));

    getResolvedTickets(token)
      .then((tickets) => setResolvedTickets(tickets))
      .catch((error) => console.error("Failed to fetch resolved tickets:", error));
  }, [token]);

  const ticketId = selectedTicket?.id || selectedTicket?._id;

  return (
    <div className="flex flex-col h-screen max-h-screen w-full bg-slate-100/50 font-sans text-slate-800 overflow-hidden antialiased">
      {/* 2. Main Workspace Split View */}
      <div className="flex-1 flex flex-row min-h-0 w-full overflow-hidden relative">
        
        {/* Left Sidebar: Ticket List */}
        <aside
          className={`w-full md:w-80 lg:w-96 shrink-0 h-full border-r border-slate-200/80 bg-white transition-all duration-300 ease-in-out z-10 ${
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
        </aside>

        {/* Right Panel: Active Chat Workspace */}
        <main
          className={`flex-1 h-full min-w-0 flex flex-col bg-slate-50 transition-all duration-300 ease-in-out ${
            mobileView === "list" ? "hidden md:flex" : "flex"
          }`}
        >
          {ticketId ? (
            <div className="flex-1 flex flex-col h-full overflow-hidden">
              
              {/* Customer Strip Header Above Chat */}
              <div className="shrink-0 px-5 py-3 bg-white border-b border-slate-200/80 flex items-center justify-between shadow-2xs">
                
                {/* Customer Profile Details */}
                <div className="flex items-center gap-3">
                  {/* Mobile Back Button */}
                  <button
                    type="button"
                    onClick={() => setMobileView("list")}
                    className="md:hidden flex items-center justify-center p-1.5 rounded-lg bg-slate-100 text-slate-600 hover:bg-slate-200 transition-colors mr-1"
                  >
                    <svg className="w-4 h-4 stroke-[2.5]" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" d="M15 19l-7-7 7-7" />
                    </svg>
                  </button>

                  <div className="w-9 h-9 rounded-full bg-violet-100 text-violet-700 font-bold text-xs flex items-center justify-center ring-2 ring-violet-50">
                    {selectedTicket?.customerName ? selectedTicket.customerName.slice(0, 2).toUpperCase() : "AC"}
                  </div>

                  <div className="flex flex-col">
                    <div className="flex items-center gap-2">
                      <h4 className="text-sm font-bold text-slate-900 leading-none">
                        {selectedTicket?.customerName || "Anonymous Customer"}
                      </h4>
                      <span className="text-[11px] font-mono font-medium text-slate-400 bg-slate-100 px-1.5 py-0.5 rounded border border-slate-200/60">
                        #{ticketId.slice(-6)}
                      </span>
                    </div>
                    <p className="text-[11px] font-medium text-slate-400 mt-0.5">
                      {selectedTicket?.customerEmail || "No email provided"}
                    </p>
                  </div>
                </div>

                {/* Status Indicator */}
                <div className="flex items-center gap-2">
                  <span
                    className={`text-[11px] font-bold px-2.5 py-1 rounded-full border ${
                      selectedTicket?.status === "RESOLVED"
                        ? "bg-amber-50 text-amber-700 border-amber-200"
                        : "bg-emerald-50 text-emerald-700 border-emerald-200"
                    }`}
                  >
                    {selectedTicket?.status || "Active"}
                  </span>
                </div>
              </div>

              {/* Chat View */}
              <div className="flex-1 h-full overflow-hidden bg-slate-50/50">
                <Chat
                  ticketId={ticketId}
                  token={token}
                  currentUserSender="AGENT"
                  status={selectedTicket?.status}
                />
              </div>
            </div>
          ) : (
            /* Empty State Placeholder */
            <div className="flex-1 flex flex-col items-center justify-center p-8 text-center relative overflow-hidden bg-slate-50">
              <div className="w-16 h-16 rounded-3xl bg-violet-100/70 text-violet-600 flex items-center justify-center mb-4 shadow-2xs border border-violet-200/50">
                <svg
                  className="w-8 h-8"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="1.75"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                >
                  <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
                </svg>
              </div>

              <h3 className="text-base font-extrabold text-slate-900 mb-1">
                No Ticket Selected
              </h3>
              <p className="text-xs text-slate-500 max-w-xs leading-relaxed">
                Choose an assigned ticket from the sidebar on the left to start communicating with the customer.
              </p>
            </div>
          )}
        </main>

        {/* Floating Notification Pop-up */}
        {notification && (
          <TicketNotificationPopup
            type={notification.type}
            ticketId={notification.ticketId}
            message={notification.message}
            onClose={() => setNotification(null)}
            onView={() => {
              // Optional: Handle clicking "View Ticket" inside notification popup
              setNotification(null);
            }}
          />
        )}
      </div>
    </div>
  );
}

export default AgentDashboardPage;