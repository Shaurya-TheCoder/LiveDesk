import React from "react";

function OverallStatsGrid({ stats }) {
  const cards = [
    { label: "Total Created", value: stats.totalCreated, color: "text-slate-900", bg: "bg-white" },
    { label: "Queued", value: stats.queued, color: "text-amber-600", bg: "bg-amber-50/50" },
    { label: "Escalated", value: stats.escalated, color: "text-rose-600", bg: "bg-rose-50/50" },
    { label: "Closed", value: stats.closed, color: "text-emerald-600", bg: "bg-emerald-50/50" },
  ];

  return (
    <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
      {cards.map((card, i) => (
        <div
          key={i}
          className={`p-4 rounded-3xl border border-slate-100 shadow-xs backdrop-blur-md ${card.bg}`}
        >
          <p className="text-[11px] font-bold text-slate-400 uppercase tracking-wider mb-1">
            {card.label}
          </p>
          <p className={`text-2xl font-black ${card.color}`}>{card.value || 0}</p>
        </div>
      ))}
    </div>
  );
}

export default OverallStatsGrid;