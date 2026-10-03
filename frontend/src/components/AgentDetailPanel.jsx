import React from "react";

function AgentDetailPanel({ agent, loading, token }) {
  // If no agent is selected at all
  if (!agent && !loading) {
    return (
      <div className="p-8 bg-white border border-slate-100 rounded-3xl text-center text-slate-400 text-xs">
        Select an agent from the left sidebar to view active metrics.
      </div>
    );
  }

  // Loading state placeholder
  if (loading) {
    return (
      <div className="p-6 bg-white border border-slate-100 rounded-3xl shadow-sm space-y-6 font-sans animate-pulse">
        {/* Header Skeleton */}
        <div className="flex items-center justify-between border-b border-slate-100 pb-4">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 rounded-2xl bg-slate-200" />
            <div className="space-y-2">
              <div className="w-32 h-4 bg-slate-200 rounded" />
              <div className="w-48 h-3 bg-slate-200 rounded" />
            </div>
          </div>
          <div className="flex items-center gap-3">
            <div className="w-20 h-6 bg-slate-200 rounded-full" />
            <div className="w-24 h-6 bg-slate-200 rounded-full" />
          </div>
        </div>

        {/* Metrics Grid Skeleton */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div className="p-4 rounded-2xl bg-slate-50 border border-slate-100 h-24" />
          <div className="p-4 rounded-2xl bg-slate-50 border border-slate-100 h-24" />
        </div>
      </div>
    );
  }

  const isOnline = agent?.isOnline ?? agent?.online ?? false;

  return (
    <div className="p-6 bg-white border border-slate-100 rounded-3xl shadow-sm space-y-6 font-sans">
      {/* Agent Info Header */}
      <div className="flex items-center justify-between border-b border-slate-100 pb-4">
        <div className="flex items-center gap-3">
          <div className="relative">
            <div className="w-12 h-12 rounded-2xl bg-violet-100 text-violet-700 font-extrabold text-base flex items-center justify-center">
              {agent?.name?.substring(0, 2).toUpperCase()}
            </div>
            {/* Status Indicator Dot on Avatar */}
            <span
              className={`absolute bottom-0 right-0 w-3 h-3 rounded-full border-2 border-white ${
                isOnline ? "bg-emerald-500" : "bg-slate-300"
              }`}
            />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h2 className="text-base font-extrabold text-slate-900">{agent?.name}</h2>
            </div>
            <p className="text-xs text-slate-400">{agent?.email}</p>
          </div>
        </div>

        <div className="flex items-center gap-3">
          {/* Online/Offline Status Badge */}
          <div
            className={`flex items-center gap-1.5 px-2.5 py-1 text-xs font-semibold rounded-full border ${
              isOnline
                ? "text-emerald-700 bg-emerald-50 border-emerald-100"
                : "text-slate-500 bg-slate-50 border-slate-200"
            }`}
          >
            <span
              className={`w-1.5 h-1.5 rounded-full ${
                isOnline ? "bg-emerald-500 animate-pulse" : "bg-slate-400"
              }`}
            />
            {isOnline ? "Online" : "Offline"}
          </div>

          <span className="text-xs font-bold px-3 py-1 rounded-full bg-slate-100 text-slate-600 border border-slate-200 capitalize">
            {agent?.role || "Support Agent"}
          </span>
        </div>
      </div>

      {/* Selected Agent Metrics */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <div className="p-4 rounded-2xl bg-violet-50/50 border border-violet-100">
          <p className="text-xs font-semibold text-violet-600 mb-1">Currently Assigned</p>
          <p className="text-3xl font-black text-violet-900">{agent?.assignedTicketCount || 0}</p>
        </div>
        <div className="p-4 rounded-2xl bg-emerald-50/50 border border-emerald-100">
          <p className="text-xs font-semibold text-emerald-600 mb-1">Total Resolved</p>
          <p className="text-3xl font-black text-emerald-900">{agent?.resolvedTicketCount || 0}</p>
        </div>
      </div>
    </div>
  );
}

export default AgentDetailPanel;