import React, { useState, useEffect } from "react";
import { getAgentResolvedTickets } from "../api/adminApi";
import AgentTicketsList from "./AgentTicketsList";

function AgentSidebar({ agents, selectedAgent, onSelectAgent, loading, token }) {
  const [viewMode, setViewMode] = useState("agents"); // 'agents' | 'resolved'
  const [resolvedTickets, setResolvedTickets] = useState([]);
  const [ticketsLoading, setTicketsLoading] = useState(false);

  useEffect(() => {
    if (selectedAgent?.id && viewMode === "resolved") {
      setTicketsLoading(true);
      getAgentResolvedTickets(selectedAgent.id, token)
        .then((data) => setResolvedTickets(data))
        .catch((err) => console.error("Error fetching agent resolved tickets:", err))
        .finally(() => setTicketsLoading(false));
    }
  }, [selectedAgent, viewMode]);

  return (
    <div className="flex flex-col h-full bg-white">
      {/* Sidebar Mode Tabs */}
      <div className="p-3 border-b border-slate-100 bg-slate-50/50 flex gap-1">
        <button
          type="button"
          onClick={() => setViewMode("agents")}
          className={`flex-1 py-1.5 text-xs font-bold rounded-lg transition-all ${
            viewMode === "agents"
              ? "bg-white text-violet-700 shadow-xs"
              : "text-slate-500 hover:text-slate-800"
          }`}
        >
          Agents List
        </button>
        <button
          type="button"
          disabled={!selectedAgent}
          onClick={() => setViewMode("resolved")}
          className={`flex-1 py-1.5 text-xs font-bold rounded-lg transition-all ${
            viewMode === "resolved"
              ? "bg-white text-violet-700 shadow-xs"
              : "text-slate-500 hover:text-slate-800 disabled:opacity-40"
          }`}
        >
          Resolved Tickets
        </button>
      </div>

      {/* Main List Container */}
      <div className="flex-1 overflow-y-auto p-3 space-y-2">
        {viewMode === "agents" ? (
          loading ? (
            <div className="p-4 text-center text-xs text-slate-400">Loading agents...</div>
          ) : (
            agents.map((agent) => {
              const isSelected = selectedAgent?.id === agent.id;
              return (
                <div
                  key={agent.id}
                  onClick={() => onSelectAgent(agent)}
                  className={`p-3 rounded-2xl border transition-all cursor-pointer flex items-center justify-between ${
                    isSelected
                      ? "bg-violet-50/60 border-violet-200/80 shadow-xs"
                      : "bg-white border-slate-100 hover:border-slate-200"
                  }`}
                >
                  <div className="flex items-center gap-3">
                    <div className="w-9 h-9 rounded-full bg-violet-100 text-violet-700 font-bold text-xs flex items-center justify-center">
                      {agent.name?.substring(0, 2).toUpperCase() || "AG"}
                    </div>
                    <div>
                      <h4 className="text-xs font-bold text-slate-900">{agent.name}</h4>
                      <p className="text-[11px] text-slate-400">{agent.email}</p>
                    </div>
                  </div>
                  <span
                    className={`w-2 h-2 rounded-full ${
                      agent.status === "ACTIVE" ? "bg-emerald-500" : "bg-slate-300"
                    }`}
                  />
                </div>
              );
            })
          )
        ) : (
          <AgentTicketsList
            tickets={resolvedTickets}
            agentName={selectedAgent?.name}
            loading={ticketsLoading}
            token={token}
          />
        )}
      </div>
    </div>
  );
}

export default AgentSidebar;