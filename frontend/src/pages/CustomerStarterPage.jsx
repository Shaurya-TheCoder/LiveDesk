import React, { useState } from "react";
import { createTicket, recoverTicket } from "../api/ticketApi.js";
import { useNavigate } from "react-router-dom";
import useCustomerStore from "../stores/customerStore.js";
import { connectStomp } from "../ws/stompClient.js";
import RecoveryCardPopup from "../components/RecoveryCardPopup.jsx";

function CustomerStartPage() {
  const [subject, setSubject] = useState("");
  const [message, setMessage] = useState("");
  const [recoveryCode, setRecoveryCode] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [recoveryError, setRecoveryError] = useState("");
  const [popupData, setPopupData] = useState(null);

  const navigate = useNavigate();
  const setTicketSession = useCustomerStore((state) => state.setTicketSession);

  const topics = ["Account access", "Billing question", "Technical issue"];

  const quickArticles = [
    "Resetting your account password",
    "Managing subscription & billing",
    "API integration & webhooks guide",
  ];

  const handleRecoveryCodeChange = (e) => {
    const rawValue = e.target.value.replace(/[^a-zA-Z0-9]/g, "").toUpperCase();
    const formatted = rawValue.match(/.{1,4}/g)?.join("-").slice(0, 19) || rawValue;
    setRecoveryCode(formatted);
  };

  const showCustomPopup = (code) => {
    return new Promise((resolve) => {
      setPopupData({
        code,
        onClose: () => {
          setPopupData(null);
          resolve();
        },
      });
    });
  };

  async function handleSubmit(event) {
    event.preventDefault();

    if (!subject.trim()) {
      setError("Please enter a subject.");
      return;
    }

    if (!message.trim()) {
      setError("Please enter a message.");
      return;
    }

    setError("");
    setLoading(true);

    try {
      const ticket = await createTicket(subject.trim(), message.trim());

      if(ticket.status === 429){
        navigate('/too-many-requests', { 
          state: { fromRateLimitTrigger: true } 
        });
        return;
      }
        const { ticketId, sessionToken } = ticket;
        setTicketSession({ ticketId, sessionToken });

        // Show recovery popup and wait until user checks the box & clicks "Okay"
        if (ticket.recoveryCode) {
          await showCustomPopup(ticket.recoveryCode);
        }

        navigate(`/ticket/${ticketId}`);
    } catch (err) {
      setError(err.message || err.error || "Failed to create ticket.");
    } finally {
      setLoading(false);
    }
  }

  const handleRecoverTicket = async (e) => {
    e.preventDefault();
    if (recoveryCode.length < 19) {
      setRecoveryError("Please enter a valid 16-character code (XXXX-XXXX-XXXX-XXXX).");
      return;
    }
    setRecoveryError("");

    try {
      const chatSession = await recoverTicket(recoveryCode);
      const { ticketId, sessionToken } = chatSession;

      setTicketSession({ ticketId, sessionToken });

      navigate(`/ticket/${ticketId}`);
    } catch (error) {
      const errorMessage =
        error.response?.data?.message || "Something went wrong. Please try again later.";
      setRecoveryError(errorMessage);
    }
  };

  return (
    <div className="relative w-full min-h-[calc(100vh-80px)] py-2 md:py-4 px-4 sm:px-1 md:px-2 lg:px-3 bg-slate-50/60 font-sans text-slate-800 overflow-hidden flex items-center">
      {/* Enhanced Background Ambient Glows & Grid Pattern */}
      <div className="absolute top-0 left-0 w-125 h-125 rounded-full bg-linear-to-br from-violet-200/50 via-purple-100/30 to-transparent blur-[120px] pointer-events-none -z-10" />
      <div className="absolute bottom-0 right-0 w-125 h-125 rounded-full bg-linear-to-tl from-indigo-200/40 via-violet-100/30 to-transparent blur-[120px] pointer-events-none -z-10" />

      {/* Subtle Dot Grid Mask */}
      <div
        className="absolute inset-0 opacity-[0.25] pointer-events-none -z-10"
        style={{
          backgroundImage: `radial-gradient(#8b5cf6 0.75px, transparent 0.75px)`,
          backgroundSize: "24px 24px",
        }}
      />

      {/* Main Container Wrapper */}
      <div className="max-w-6xl mx-auto w-full grid grid-cols-1 lg:grid-cols-12 gap-10 lg:gap-16 items-start pt-2">
        {/* Left Column: Hero Text & Visual Enhancements */}
        <div className="lg:col-span-6 space-y-6 pt-2">
          {/* Top Pill + Live Response Time Badge */}
          <div className="flex items-center gap-3 flex-wrap">
            <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-violet-100/80 border border-violet-200/60 text-violet-700 text-xs font-semibold tracking-wide shadow-xs">
              <svg className="w-3.5 h-3.5" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 2L14.5 9.5L22 12L14.5 14.5L12 22L9.5 14.5L2 12L9.5 9.5L12 2Z" />
              </svg>
              WE'RE HERE TO HELP
            </div>

            <div className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-white/80 border border-slate-200/80 text-slate-600 text-xs font-medium backdrop-blur-sm shadow-xs">
              <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
              Avg response ~2 mins
            </div>
          </div>

          {/* Headline */}
          <h1 className="text-4xl sm:text-5xl lg:text-6xl font-extrabold tracking-tight text-slate-900 leading-[1.12]">
            How can we make <br />
            <span className="italic font-serif font-normal text-transparent bg-clip-text bg-linear-to-r from-violet-600 via-purple-600 to-indigo-600">
              things easier?
            </span>
          </h1>

          <p className="text-base text-slate-600 font-normal leading-relaxed max-w-xl">
            Share what's on your mind and a member of our support team will reach out to you shortly. You can also pick a common topic below.
          </p>

          {/* Topic Suggestion Chips */}
          <div className="pt-2 space-y-3">
            <span className="block text-xs font-bold text-slate-400 uppercase tracking-wider">
              Common Support Topics
            </span>
            <div className="flex flex-wrap gap-2">
              {topics.map((topic) => (
                <button
                  key={topic}
                  type="button"
                  onClick={() => setSubject(topic)}
                  className={`px-4 py-2.5 text-xs font-semibold border rounded-xl transition-all shadow-2xs cursor-pointer ${
                    subject === topic
                      ? "bg-violet-600 text-white border-violet-600 shadow-violet-200"
                      : "bg-white text-slate-700 border-slate-200/80 hover:border-violet-300 hover:bg-violet-50/50 hover:text-violet-700"
                  }`}
                >
                  {topic}
                </button>
              ))}
            </div>
          </div>

          {/* Knowledge Base Articles Quick Links */}
          <div className="pt-6 border-t border-slate-200/60 space-y-3">
            <span className="block text-xs font-bold text-slate-400 uppercase tracking-wider">
              Frequently Read Articles
            </span>
            <ul className="space-y-2.5">
              {quickArticles.map((article, idx) => (
                <li key={idx}>
                  <a
                    href="#kb"
                    className="group flex items-center gap-2.5 text-sm text-slate-600 hover:text-violet-600 transition-colors"
                  >
                    <div className="p-1 rounded-md bg-slate-100 group-hover:bg-violet-100 group-hover:text-violet-600 text-slate-400 transition-colors">
                      <svg className="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
                        <polyline points="14 2 14 8 20 8" />
                      </svg>
                    </div>
                    <span>{article}</span>
                  </a>
                </li>
              ))}
            </ul>
          </div>
        </div>

        {/* Right Column: Interactive Support Form Card */}
        <div className="lg:col-span-6">
          <div className="w-full p-6 sm:p-8 space-y-6 bg-white/90 backdrop-blur-md border border-slate-100 rounded-3xl shadow-xl shadow-slate-200/50 relative">
            {/* Header Bar */}
            <div className="flex items-center justify-between pb-5 border-b border-slate-100">
              <div className="flex items-center gap-3">
                <div className="p-2.5 rounded-2xl bg-violet-100/80 text-violet-600">
                  <svg className="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
                  </svg>
                </div>
                <div>
                  <h2 className="text-sm font-bold text-slate-800">Support Center</h2>
                  <p className="text-[11px] text-slate-400">Direct Message</p>
                </div>
              </div>
              <div className="flex items-center gap-1.5 px-3 py-1 text-xs font-semibold text-emerald-700 bg-emerald-50 rounded-full border border-emerald-100">
                <span className="w-1.5 h-1.5 bg-emerald-500 rounded-full animate-pulse"></span>
                Online now
              </div>
            </div>

            {/* Form Container */}
            <form onSubmit={handleSubmit} className="space-y-4">
              {/* Subject Field */}
              <div className="space-y-1.5">
                <label className="block text-xs font-bold text-slate-800">Subject</label>
                <input
                  type="text"
                  value={subject}
                  onChange={(e) => setSubject(e.target.value)}
                  placeholder="e.g. Need help with account login"
                  className="w-full px-4 py-3 text-sm text-slate-700 placeholder:text-slate-400 bg-white border border-slate-200 rounded-2xl focus:border-violet-500 focus:ring-4 focus:ring-violet-500/10 transition-all outline-none"
                />
              </div>

              {/* Message Field */}
              <div className="relative p-4 rounded-2xl border border-slate-200 focus-within:border-violet-500 focus-within:ring-4 focus-within:ring-violet-500/10 transition-all bg-white">
                <label className="block text-xs font-bold text-slate-800 mb-2">
                  Message Details
                </label>
                <textarea
                  value={message}
                  onChange={(e) => setMessage(e.target.value)}
                  placeholder="Describe your question or problem in detail..."
                  maxLength={2000}
                  rows={4}
                  className="w-full text-sm text-slate-700 placeholder:text-slate-400 bg-transparent resize-none outline-none border-none p-0 focus:ring-0"
                />

                <div className="flex items-center justify-between pt-2 border-t border-slate-100 text-[11px] text-slate-400">
                  <span>Usually replies in under 5 minutes</span>
                  <span className="font-medium text-slate-500">{message?.length || 0}/2000</span>
                </div>
              </div>

              {error && (
                <div className="p-3 text-xs font-medium text-red-600 bg-red-50 border border-red-100 rounded-xl">
                  {error}
                </div>
              )}

              <button
                type="submit"
                disabled={loading}
                className="w-full flex items-center justify-between px-6 py-4 text-sm font-semibold text-white bg-violet-600 rounded-2xl hover:bg-violet-700 disabled:opacity-70 transition-all shadow-lg shadow-violet-200 active:scale-[0.99] cursor-pointer"
              >
                <span>{loading ? "Sending ticket..." : "Start a conversation"}</span>
                <svg className="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                  <path d="M7 17L17 7M17 7H7M17 7V17" />
                </svg>
              </button>
            </form>

            {/* Existing Customer Recovery Section */}
            <div className="pt-5 border-t border-slate-100 space-y-3">
              <div className="flex items-center justify-between">
                <span className="text-xs font-bold text-slate-800">Already have a ticket?</span>
                <span className="text-[11px] text-slate-400">Enter recovery code</span>
              </div>

              <form onSubmit={handleRecoverTicket} className="flex gap-2">
                <input
                  type="text"
                  value={recoveryCode}
                  onChange={handleRecoveryCodeChange}
                  placeholder="XXXX-XXXX-XXXX-XXXX"
                  maxLength={19}
                  className="flex-1 px-3.5 py-2.5 text-xs tracking-widest font-mono font-medium text-slate-800 bg-slate-50 border border-slate-200 rounded-xl focus:border-violet-500 focus:bg-white focus:ring-4 focus:ring-violet-500/10 transition-all outline-none uppercase"
                />
                <button
                  type="submit"
                  className="px-4 py-2.5 text-xs font-semibold text-violet-700 bg-violet-100/70 rounded-xl hover:bg-violet-200/80 transition-colors cursor-pointer"
                >
                  Resume
                </button>
              </form>

              {recoveryError && (
                <p className="text-[11px] font-medium text-red-500">{recoveryError}</p>
              )}
            </div>

            <div className="flex items-center justify-center gap-1.5 text-xs text-slate-400 pt-1">
              <svg className="w-3.5 h-3.5 text-slate-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                <path d="M7 11V7a5 5 0 0 1 10 0v4" />
              </svg>
              <span>Your conversation is private and end-to-end encrypted</span>
            </div>
          </div>
        </div>
      </div>

      {/* Recovery Code Modal Popup */}
      {popupData && (
        <RecoveryCardPopup recoveryCode={popupData.code} onClose={popupData.onClose} />
      )}
    </div>
  );
}

export default CustomerStartPage;