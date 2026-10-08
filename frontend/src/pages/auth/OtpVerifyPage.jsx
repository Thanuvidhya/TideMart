import { useState } from 'react';
import { Navigate, useLocation, useNavigate } from 'react-router-dom';
import { verifyOtp } from '../../api/authApi.js';
import useAuth from '../../hooks/useAuth.js';

export default function OtpVerifyPage() {
  const { state } = useLocation();
  const nav = useNavigate();
  const { signIn } = useAuth();
  const [code, setCode] = useState('');
  const [err, setErr] = useState('');
  if (!state?.phone) return <Navigate to="/login" replace />;
  const go = async () => {
    try { signIn(await verifyOtp(state.phone, code)); nav('/'); }
    catch (e) { setErr(e.response?.data?.message || 'Wrong OTP'); }
  };
  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-50 p-4">
      <div className="bg-white rounded-2xl shadow p-6 w-full max-w-sm">
        <h1 className="text-xl font-bold mb-1">Enter OTP</h1>
        <p className="text-sm text-slate-500 mb-3">Sent to {state.phone}</p>
        {state.demoOtp && <p className="mb-3 rounded-lg bg-orange-50 text-brand-accent px-3 py-2 text-sm">Demo OTP: <b>{state.demoOtp}</b></p>}
        <input className="w-full border rounded-lg px-3 py-2 mb-3 tracking-widest" maxLength={6} value={code} onChange={(e) => setCode(e.target.value)} />
        <button className="w-full bg-brand-accent text-white py-2 rounded-lg font-semibold" onClick={go}>Verify</button>
        {err && <p className="text-red-600 mt-3 text-sm">{err}</p>}
      </div>
    </div>
  );
}
