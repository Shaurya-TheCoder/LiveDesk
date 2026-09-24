export default function DashBoardHeader(){
    return(
        <div className="flex items-center justify-between pb-4 border-b border-slate-100 mb-6">
        <div className="flex items-center gap-3">
            <div className="p-2.5 rounded-2xl bg-violet-100/80 text-violet-600">
            <svg className="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                <rect x="3" y="3" width="7" height="7" rx="1.5" />
                <rect x="14" y="3" width="7" height="7" rx="1.5" />
                <rect x="14" y="14" width="7" height="7" rx="1.5" />
                <rect x="3" y="14" width="7" height="7" rx="1.5" />
            </svg>
            </div>
            <div>
            <h1 className="text-2xl font-extrabold tracking-tight text-slate-900">
                Agent Dashboard
            </h1>
            <p className="text-xs font-medium text-slate-400">
                Manage active support tickets and customer conversations
            </p>
            </div>
        </div>

        {/* Active Status Badge */}
        <div className="flex items-center gap-1.5 px-3 py-1 text-xs font-semibold text-emerald-700 bg-emerald-50 rounded-full border border-emerald-100">
            <span className="w-1.5 h-1.5 bg-emerald-500 rounded-full animate-pulse"></span>
            Live Overview
        </div>
        </div>
    );
}