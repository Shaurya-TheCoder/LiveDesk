import React, { useState, useEffect } from "react";
import AdminDashboardHeader from "../components/AdminDashboardHeader";
import AgentSidebar from "../components/AdminSidebar";
import OverallStatsGrid from "../components/OverallStatsGrid";
import AgentDetailPanel from "../components/AgentDetailPanel";
import TicketNotificationPopup from "../components/TicketNotificationPopup";
import { getAdminSystemStats, getAllAgents, getAgentDetails } from "../api/adminApi";
import useAuthStore from "../stores/authStore";
import { connectStomp } from "../ws/stompClient";

function AdminDashboardPage() {
  const token = useAuthStore((state) => state.token);

  const [agents, setAgents] = useState([]);
  const [selectedAgent, setSelectedAgent] = useState(null);
  const [agentDetails, setAgentDetails] = useState(null);
  const [agentLoading, setAgentLoading] = useState(false);
  
  const [systemStats, setSystemStats] = useState({
    totalCreated: 0,
    queued: 0,
    escalated: 0,
    closed: 0,
  });
  const [loading, setLoading] = useState(true);

  // STOMP Notification State
  const [activeNotification, setActiveNotification] = useState(null);

  // 1. Fetch system-wide stats & agent list on initial mount
  useEffect(() => {
    Promise.all([getAdminSystemStats(token), getAllAgents(token)])
      .then(([statsRes, agentsRes]) => {
        setSystemStats(statsRes);
        setAgents(agentsRes);
        if (agentsRes?.length > 0) {
          setSelectedAgent(agentsRes[0]); // Default select first agent
        }
      })
      .catch((err) => console.error("Failed to load admin data:", err))
      .finally(() => setLoading(false));
  }, [token]);

  // 2. Fetch detailed agent information whenever `selectedAgent` changes
  useEffect(() => {
    if (!selectedAgent?.id) return;

    setAgentLoading(true);
    getAgentDetails(selectedAgent.id, token)
      .then((detailsRes) => {
        setAgentDetails(detailsRes);
      })
      .catch((err) => console.error("Failed to load agent details:", err))
      .finally(() => setAgentLoading(false));
  }, [selectedAgent, token]);

  // 3. Connect via shared STOMP utility and listen for admin notifications
  useEffect(() => {
    if (!token) return;

    const client = connectStomp({
      token,
      onConnect: (stompClient) => {
        console.log("Admin Dashboard connected to STOMP broker");

        stompClient.subscribe("/topic/admin/notifications", (message) => {
          try {
            const notificationData = JSON.parse(message.body);
            // Expected payload: { type, ticketId, message }
            setActiveNotification(notificationData);
          } catch (e) {
            console.error("Failed to parse incoming notification payload:", e);
          }
        });
      },
      onError: (error) => {
        console.error("Admin STOMP connection error:", error);
      },
    });

    return () => {
      // Optional cleanup if unmounting or letting the singleton manage lifecycle
    };
  }, [token]);

  // Auto-dismiss notification after 6 seconds (if not hovered)
  useEffect(() => {
    if (!activeNotification) return;

    const timer = setTimeout(() => {
      setActiveNotification(null);
    }, 6000); // 6 seconds

    return () => clearTimeout(timer);
  }, [activeNotification]);

  return (
    <div className="flex flex-col h-screen w-full max-w-full bg-slate-50/60 font-sans text-slate-800 overflow-hidden box-border">
      {/* Top Header */}
      <AdminDashboardHeader />

      {/* Main Container */}
      <div className="flex-1 flex flex-col md:flex-row h-[calc(100vh-64px)] w-full overflow-hidden">
        {/* Left Side Panel: Agent List */}
        <div className="w-full md:w-80 lg:w-96 shrink-0 h-full border-r border-slate-200/80 bg-white overflow-y-auto">
          <AgentSidebar
            agents={agents}
            selectedAgent={selectedAgent}
            onSelectAgent={setSelectedAgent}
            loading={loading}
            token={token}
          />
        </div>

        {/* Right Main Content Panel */}
        <div className="flex-1 h-full overflow-y-auto p-4 sm:p-6 lg:p-8 space-y-6 bg-slate-50/30 min-w-0">
          {/* Always Visible System Overview Stats */}
          <OverallStatsGrid stats={systemStats} token={token} />

          {/* Selected Agent Performance & Ticket Metrics */}
          <AgentDetailPanel 
            agent={agentDetails} 
            loading={agentLoading} 
            token={token}
          />
        </div>
      </div>

      {/* Real-time STOMP Notification Popup */}
      {activeNotification && (
        <TicketNotificationPopup
          type={activeNotification.type}
          ticketId={activeNotification.ticketId}
          message={activeNotification.message}
          onClose={() => setActiveNotification(null)}
          onView={() => {
            console.log("Viewing ticket:", activeNotification.ticketId);
            setActiveNotification(null);
          }}
        />
      )}
    </div>
  );
}

export default AdminDashboardPage;