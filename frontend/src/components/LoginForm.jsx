import React, { useState } from 'react';
import { login } from '../api/authApi';
import { useNavigate } from 'react-router-dom';
import useAuthStore from '../stores/authStore';

export default function LoginForm() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const setAuth = useAuthStore((state) => state.setAuth)
  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
        const response = await login(email, password);

        const { id, email: agentEmail, token } = response;

        if (!id || !agentEmail || !token) {
            throw new Error("Invalid login response");
        }

        console.log("Agent ID:", id);
        console.log("Agent Email:", agentEmail);
        console.log("JWT:", token);

        setAuth({
            id,
            email: agentEmail,
            token
        });

        navigate("/agent/dashboard");

    } catch (error) {
        console.error("Login failed:", error);
    }
  };

  return (
    <div className="relative w-full min-h-[calc(100vh-80px)] py-12 md:py-20 px-4 sm:px-6 lg:px-8 bg-slate-50/50 font-sans text-slate-800 flex items-center justify-center overflow-hidden"
    style={
      {backgroundImage:'url("/img/background.jpg")'}
    }>
      {/* Decorative Background Glows */}
      <div className="absolute top-1/4 left-1/3 w-96 h-96 rounded-full bg-violet-200/40 blur-[130px] pointer-events-none -z-10" />
      <div className="absolute bottom-1/4 right-1/3 w-96 h-96 rounded-full bg-indigo-200/40 blur-[130px] pointer-events-none -z-10" />

      {/* Main Login Card */}
      <div className="w-full max-w-md p-6 sm:p-8 space-y-6 bg-white border border-slate-100 rounded-3xl shadow-xl shadow-slate-200/50 relative z-10">
        
        {/* Card Header */}
        <div className="text-center space-y-2">
          <div className="inline-flex items-center justify-center p-3 rounded-2xl bg-violet-100/80 text-violet-600 mb-1">
            <svg className="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
              <circle cx="12" cy="7" r="4" />
            </svg>
          </div>
          <h2 className="text-2xl font-extrabold tracking-tight text-slate-900">
            Agent Portal Login
          </h2>
          <p className="text-xs text-slate-500 font-normal">
            Enter your credentials to access the support dashboard
          </p>
        </div>

        {/* Form Container */}
        <form onSubmit={handleSubmit} className="space-y-4">
          {/* Email Field */}
          <div className="space-y-1.5">
            <label className="block text-xs font-bold text-slate-800">
              Email Address
            </label>
            <div className="relative flex items-center">
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="agent@company.com"
                required
                className="w-full px-4 py-3 text-sm text-slate-700 placeholder:text-slate-400 bg-white border border-slate-200 rounded-2xl focus:border-violet-500 focus:ring-4 focus:ring-violet-500/10 transition-all outline-none"
              />
            </div>
          </div>

          {/* Password Field */}
          <div className="space-y-1.5">
            <label className="block text-xs font-bold text-slate-800">
              Password
            </label>
            <div className="relative flex items-center">
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                required
                className="w-full px-4 py-3 text-sm text-slate-700 placeholder:text-slate-400 bg-white border border-slate-200 rounded-2xl focus:border-violet-500 focus:ring-4 focus:ring-violet-500/10 transition-all outline-none"
              />
            </div>
          </div>

          {/* Submit Button */}
          <button
            type="submit"
            className="w-full flex items-center justify-center gap-2 px-6 py-3.5 mt-2 text-sm font-semibold text-white bg-violet-600 rounded-2xl hover:bg-violet-700 transition-all shadow-lg shadow-violet-200 active:scale-[0.99] cursor-pointer"
          >
            <span>Login</span>
            <svg className="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
              <path d="M5 12h14M12 5l7 7-7 7" />
            </svg>
          </button>
        </form>

        {/* Security Footer */}
        <div className="flex items-center justify-center gap-1.5 text-xs text-slate-400 pt-2 border-t border-slate-100">
          <svg className="w-3.5 h-3.5 text-slate-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
            <path d="M7 11V7a5 5 0 0 1 10 0v4" />
          </svg>
          <span>Authorized personnel only</span>
        </div>

      </div>
    </div>
  );
}