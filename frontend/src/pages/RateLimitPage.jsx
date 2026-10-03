import React, { useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

export default function RateLimitPage() {
  const location = useLocation();
  const navigate = useNavigate();

  // Extract our hidden flag from navigation history state
  const isLegitimateRedirect = location.state?.fromRateLimitTrigger;

  useEffect(() => {
    // If someone manually types /too-many-requests, kick them back to safety
    if (!isLegitimateRedirect) {
      navigate('/', { replace: true }); 
    }
  }, [isLegitimateRedirect, navigate]);

  // If they didn't come from a 429 error, render nothing while the useEffect redirects
  if (!isLegitimateRedirect) {
    return null; 
  }

  return (
    <div 
      className="relative w-full min-h-[calc(100vh-80px)] py-12 md:py-20 px-4 sm:px-6 lg:px-8 bg-slate-50/50 font-sans text-slate-800 flex items-center justify-center overflow-hidden"
      style={{ backgroundImage: 'url("/img/background.jpg")' }}
    >
      {/* Decorative Background Glows */}
      <div className="absolute top-1/4 left-1/3 w-96 h-96 rounded-full bg-rose-200/30 blur-[130px] pointer-events-none -z-10" />
      <div className="absolute bottom-1/4 right-1/3 w-96 h-96 rounded-full bg-amber-200/30 blur-[130px] pointer-events-none -z-10" />

      {/* Main Rate Limit Card */}
      <div className="w-full max-w-md p-6 sm:p-8 space-y-6 bg-white border border-slate-100 rounded-3xl shadow-xl shadow-slate-200/50 relative z-10 text-center">
        
        {/* Icon Header */}
        <div className="inline-flex items-center justify-center w-14 h-14 rounded-2xl bg-rose-100 text-rose-600 mb-1 shadow-inner">
          <svg className="w-7 h-7" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
            <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
        </div>

        {/* Title & Description */}
        <div className="space-y-2">
          <h2 className="text-2xl font-extrabold tracking-tight text-slate-900">
            Access Temporarily Blocked
          </h2>
          <p className="text-xs text-slate-500 leading-relaxed max-w-xs mx-auto">
            Too many requests have been detected from your network. Please slow down and try again shortly.
          </p>
        </div>

        {/* Action Button */}
        <button
          type="button"
          onClick={() => navigate('/', { replace: true })}
          className="w-full flex items-center justify-center gap-2 px-6 py-3.5 text-sm font-semibold text-white bg-violet-600 rounded-2xl hover:bg-violet-700 transition-all shadow-lg shadow-violet-200 active:scale-[0.99] cursor-pointer"
        >
          <span>Return to Home</span>
          <svg className="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
            <path d="M5 12h14M12 5l7 7-7 7" />
          </svg>
        </button>

        {/* Security / System Footer */}
        <div className="flex items-center justify-center gap-1.5 text-xs text-slate-400 pt-2 border-t border-slate-100">
          <svg className="w-3.5 h-3.5 text-slate-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
            <path d="M7 11V7a5 5 0 0 1 10 0v4" />
          </svg>
          <span>Automated Security Protection</span>
        </div>

      </div>
    </div>
  );
}