import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import useAuthStore from '../stores/authStore';

export default function Navbar() {
  const navigate = useNavigate();
  const location = useLocation();
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  const clearAuth = useAuthStore((state) => state.logout);

  const isLoginPage = location.pathname === "/agent/login";

  return (
    <nav className="w-full bg-white/80 backdrop-blur-md border-b border-slate-100 px-6 py-2.5 sticky top-0 z-50 transition-all">
      <div className="max-w-6xl mx-auto flex items-center justify-between">
        {/* Left side: Brand Logo */}
        <a 
          href="/" 
          className="flex items-center gap-2.5 group"
        >
          <div className="p-1.5 rounded-xl bg-violet-600 text-white shadow-md shadow-violet-200 group-hover:scale-105 transition-transform">
            <svg className="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
            </svg>
          </div>
          <span className="text-xl font-extrabold tracking-tight text-slate-900">
            Live<span className="text-violet-600">Desk</span>
          </span>
        </a>

        {/* Right side: Home Link and Login Button */}
        <div className="flex items-center space-x-6">
          <a 
            href="/" 
            className="text-sm font-semibold text-slate-600 hover:text-violet-600 transition-colors"
          >
            Home
          </a>
          
          <button
            onClick={() => {
              if (isAuthenticated) {
                clearAuth();
                navigate("/");
              } else {
                navigate("/agent/login");
              }
            }}
            disabled={!isAuthenticated && isLoginPage}
            className={`px-4 py-1.5 rounded-xl text-sm font-semibold transition-all shadow-sm ${
              !isAuthenticated && isLoginPage
                ? "bg-slate-100 text-slate-400 border border-slate-200 cursor-not-allowed shadow-none"
                : isAuthenticated
                ? "bg-slate-100 text-slate-700 hover:bg-slate-200/80 active:scale-[0.98] cursor-pointer"
                : "bg-violet-600 text-white hover:bg-violet-700 shadow-violet-200 active:scale-[0.98] cursor-pointer"
            }`}
          >
            {!isAuthenticated ? "Agent Portal" : "Logout"}
          </button>
        </div>
      </div>
    </nav>
  );
}