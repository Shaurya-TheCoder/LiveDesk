import React, { useState } from "react";

export default function RecoveryCardPopup({ recoveryCode, onClose }) {
  const [hasNotedCode, setHasNotedCode] = useState(false);
  const [copied, setCopied] = useState(false);

  const handleCopy = () => {
    if (recoveryCode) {
      navigator.clipboard.writeText(recoveryCode);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    }
  };

  const handleConfirm = () => {
    if (hasNotedCode) {
      onClose();
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-sm animate-in fade-in duration-200">
      
      {/* Modal Card */}
      <div className="w-full max-w-md p-6 sm:p-8 bg-white border border-slate-100 rounded-3xl shadow-2xl shadow-slate-900/20 relative space-y-6">
        
        {/* Header Icon & Title */}
        <div className="text-center space-y-2">
          <div className="w-12 h-12 mx-auto rounded-2xl bg-violet-100/80 text-violet-600 flex items-center justify-center shadow-xs">
            <svg className="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
              <path d="M7 11V7a5 5 0 0 1 10 0v4" />
            </svg>
          </div>

          <h3 className="text-xl font-extrabold tracking-tight text-slate-900 pt-1">
            Save Your Recovery Code
          </h3>

          <p className="text-xs text-slate-500 leading-relaxed max-w-xs mx-auto">
            Use this unique code to access or resume your support ticket from any device.
          </p>
        </div>

        {/* Recovery Code Display Box */}
        <div className="p-4 bg-slate-50 border border-slate-200/80 rounded-2xl space-y-2">
          <span className="block text-[10px] font-bold text-slate-400 uppercase tracking-wider text-center">
            Your Ticket Recovery Key
          </span>

          <div className="flex items-center justify-between gap-2 px-3 py-2 bg-white border border-slate-200 rounded-xl">
            <span className="font-mono text-base font-extrabold tracking-wider text-violet-700 select-all">
              {recoveryCode || "XXXX-XXXX-XXXX-XXXX"}
            </span>

            <button
              type="button"
              onClick={handleCopy}
              className="px-2.5 py-1 text-xs font-semibold text-slate-600 hover:text-violet-600 hover:bg-violet-50 rounded-lg transition-colors cursor-pointer flex items-center gap-1"
            >
              {copied ? (
                <>
                  <svg className="w-3.5 h-3.5 text-emerald-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                    <polyline points="20 6 9 17 4 12" />
                  </svg>
                  <span className="text-emerald-600">Copied</span>
                </>
              ) : (
                <>
                  <svg className="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <rect x="9" y="9" width="13" height="13" rx="2" ry="2" />
                    <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1" />
                  </svg>
                  <span>Copy</span>
                </>
              )}
            </button>
          </div>
        </div>

        {/* Warning Banner */}
        <div className="p-3 text-xs text-amber-800 bg-amber-50/80 border border-amber-200/60 rounded-xl flex items-start gap-2.5">
          <svg className="w-4 h-4 text-amber-600 shrink-0 mt-0.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z" />
            <line x1="12" y1="9" x2="12" y2="13" />
            <line x1="12" y1="17" x2="12.01" y2="17" />
          </svg>
          <span className="leading-tight">
            Please keep this code safe. You won't be able to view it again after closing this window.
          </span>
        </div>

        {/* Checkbox Confirmation Requirement */}
        <label className="flex items-start gap-3 cursor-pointer group">
          <input
            type="checkbox"
            checked={hasNotedCode}
            onChange={(e) => setHasNotedCode(e.target.checked)}
            className="mt-0.5 w-4 h-4 text-violet-600 border-slate-300 rounded-md focus:ring-violet-500 focus:ring-offset-0 cursor-pointer"
          />
          <span className="text-xs text-slate-600 group-hover:text-slate-800 transition-colors select-none font-medium">
            I have saved or noted down my recovery code in a safe place.
          </span>
        </label>

        {/* Confirmation Button */}
        <button
          type="button"
          onClick={handleConfirm}
          disabled={!hasNotedCode}
          className="w-full py-3.5 px-6 text-sm font-semibold text-white bg-violet-600 rounded-2xl hover:bg-violet-700 disabled:bg-slate-200 disabled:text-slate-400 disabled:cursor-not-allowed transition-all shadow-lg shadow-violet-200 disabled:shadow-none cursor-pointer"
        >
          Okay, I'm ready
        </button>

      </div>
    </div>
  );
}