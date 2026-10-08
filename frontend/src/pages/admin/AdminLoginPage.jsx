import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { adminLogin } from '../../api/authApi.js';
import useAuth from '../../hooks/useAuth.js';

export default function AdminLoginPage(){
 const nav=useNavigate(); const {signIn}=useAuth(); const [email,setEmail]=useState('admin@tidemart.com'); const [password,setPassword]=useState('Admin@123'); const [err,setErr]=useState('');
 const submit=async()=>{try{const d=await adminLogin({email,password}); if(!(d.roles||[]).includes('ADMIN') && d.role!=='ADMIN') throw new Error('This account is not an admin account'); signIn(d); nav('/admin');}catch(e){setErr(e.response?.data?.message||e.message||'Admin login failed')}};
 return <div className="min-h-screen flex items-center justify-center bg-slate-50 p-4"><div className="bg-white rounded-2xl shadow p-6 w-full max-w-sm"><img src="/tidemart-logo.svg" alt="TideMart" className="w-40 mx-auto mb-4"/><h1 className="text-2xl font-extrabold text-center">Admin Portal</h1><p className="text-sm text-slate-500 text-center mb-5">For authorized TideMart administrators</p><input className="w-full border rounded-lg px-3 py-2 mb-3" placeholder="Admin email" value={email} onChange={e=>setEmail(e.target.value)}/><input className="w-full border rounded-lg px-3 py-2 mb-3" type="password" placeholder="Password" value={password} onChange={e=>setPassword(e.target.value)}/><button className="w-full bg-brand-accent text-white py-2 rounded-lg font-semibold" onClick={submit}>Admin Login</button>{err&&<p className="text-red-600 text-sm mt-3">{err}</p>}<button className="w-full mt-3 text-sm text-brand" onClick={()=>nav('/login')}>Back to customer login</button></div></div>
}
