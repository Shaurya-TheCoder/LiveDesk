import React from 'react';

export default function DashBoardHeader() {
  return (
    <div className="w-full bg-slate-100/90 border-b border-slate-200/80 px-6 py-3 flex items-center justify-between transition-colors">
      {/* Title & Icon Section */}
      <div className="flex items-center gap-3">
        <div className="p-2 rounded-xl bg-violet-600/10 text-violet-600 border border-violet-200/50 shrink-0">
          <svg 
            className="w-4 h-4 md:w-5 md:h-5 stroke-[2.2]" 
            viewBox="0 0 24 24" 
            fill="none" 
            stroke="currentColor" 
            strokeLinecap="round" 
            strokeLinejoin="round"
          >
            <rect x="3" y="3" width="7" height="7" rx="1.5" />
            <rect x="14" y="3" width="7" height="7" rx="1.5" />
            <rect x="14" y="14" width="7" height="7" rx="1.5" />
            <rect x="3" y="14" width="7" height="7" rx="1.5" />
          </svg>
        </div>
        <div className="flex flex-col">
          <h1 className="text-base md:text-lg font-extrabold tracking-tight text-slate-900 leading-tight">
            Agent Dashboard
          </h1>
          <p className="text-[11px] md:text-xs font-medium text-slate-500">
            Manage active support tickets and customer conversations
          </p>
        </div>
      </div>

      {/* Active Status Badge */}
      <div className="flex items-center gap-2 px-3 py-1 text-xs font-bold text-emerald-700 bg-emerald-50 rounded-full border border-emerald-200/70 shadow-2xs shrink-0">
        <span className="w-2 h-2 bg-emerald-500 rounded-full animate-pulse" />
        Live Overview
      </div>
    </div>
  );
}