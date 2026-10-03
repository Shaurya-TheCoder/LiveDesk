import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import useAuthStore from '../stores/authStore';

export default function Navbar() {
  const navigate = useNavigate();
  const location = useLocation();
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  const clearAuth = useAuthStore((state) => state.logout);

  const isLoginPage = location.pathname === "/agent/login";

  const handleAuthAction = () => {
    if (isAuthenticated) {
      clearAuth();
      navigate("/");
    } else {
      navigate("/agent/login");
    }
  };

  return (
    <nav className="w-full h-16 bg-white/80 backdrop-blur-md border-b border-slate-200/70 sticky top-0 z-50 transition-colors">
      <div className="w-full h-full px-6 md:px-8 flex items-center justify-between">
        {/* Left Side: Brand Logo */}
        <Link 
          to="/" 
          className="flex items-center gap-2.5 group focus:outline-none focus-visible:ring-2 focus-visible:ring-violet-500 rounded-lg"
        >
          <div className="p-2 rounded-xl bg-violet-600 text-white shadow-md shadow-violet-200 group-hover:bg-violet-700 group-hover:scale-105 transition-all duration-200">
            <svg 
              className="w-4 h-4 stroke-[2.5]" 
              viewBox="0 0 24 24" 
              fill="none" 
              stroke="currentColor" 
              strokeLinecap="round" 
              strokeLinejoin="round"
            >
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
            </svg>
          </div>
          <span className="text-lg md:text-xl font-black tracking-tight text-slate-900 select-none">
            Live<span className="text-violet-600">Desk</span>
          </span>
        </Link>

        {/* Right Side: Navigation & Auth Controls */}
        <div className="flex items-center gap-4 md:gap-6">
          <Link 
            to="/" 
            className="text-xs md:text-sm font-semibold text-slate-600 hover:text-violet-600 transition-colors py-1 px-2 rounded-md hover:bg-slate-50"
          >
            Home
          </Link>
          
          <button
            type="button"
            onClick={handleAuthAction}
            disabled={!isAuthenticated && isLoginPage}
            className={`px-4 py-2 rounded-xl text-xs md:text-sm font-semibold transition-all duration-200 flex items-center gap-2 shadow-xs ${
              !isAuthenticated && isLoginPage
                ? "bg-slate-100 text-slate-400 border border-slate-200 cursor-not-allowed shadow-none"
                : isAuthenticated
                ? "bg-slate-100 text-slate-700 hover:bg-slate-200/80 hover:text-slate-900 active:scale-[0.98] cursor-pointer"
                : "bg-violet-600 text-white hover:bg-violet-700 shadow-violet-200/60 active:scale-[0.98] cursor-pointer"
            }`}
          >
            {isAuthenticated && (
              <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
            )}
            {isAuthenticated ? "Logout" : "Agent Portal"}
          </button>
        </div>
      </div>
    </nav>
  );
}