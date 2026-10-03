import React from "react";
import { Link } from "react-router-dom";

function AdminHeader() {
  return (
    <header className="w-full px-6 py-6 lg:px-8 bg-transparent flex flex-col sm:flex-row sm:items-center justify-between gap-4 shrink-0">
      {/* Title & Subtitle Section */}
      <div className="flex items-center gap-4">
        <div className="w-10 h-10 rounded-2xl bg-violet-100 text-violet-700 flex items-center justify-center font-black text-sm border border-violet-200/60 shadow-xs shrink-0">
          AD
        </div>
        <div>
          <h1 className="text-xl sm:text-2xl font-extrabold text-slate-900 tracking-tight leading-none">
            Admin Control Center
          </h1>
          <p className="text-xs font-medium text-slate-500 mt-1.5">
            Manage agents and monitor system-wide ticket metrics
          </p>
        </div>
      </div>

      {/* Action Button Section */}
      <div className="flex items-center shrink-0">
        <Link
          to="/admin/register-agent"
          className="flex items-center gap-2 px-4 py-2.5 text-xs font-bold text-white bg-violet-600 hover:bg-violet-700 rounded-xl transition-all shadow-xs shadow-violet-200 cursor-pointer"
        >
          <svg
            className="w-4 h-4"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2.5"
          >
            <line x1="12" y1="5" x2="12" y2="19" />
            <line x1="5" y1="12" x2="19" y2="12" />
          </svg>
          Register New Agent
        </Link>
      </div>
    </header>
  );
}

export default AdminHeader;