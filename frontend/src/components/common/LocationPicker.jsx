import { useEffect, useRef, useState } from 'react';
import { checkPincode } from '../../api/orderApi.js';
import { listAddresses, saveAddress } from '../../api/userApi.js';
import useAuth from '../../hooks/useAuth.js';

const blank = { name: '', phone: '', line1: '', city: '', state: '', pincode: '', isDefault: false };

export default function LocationPicker() {
  const { auth } = useAuth();
  const box = useRef(null);
  const [open, setOpen] = useState(false);
  const [list, setList] = useState([]);
  const [selId, setSelId] = useState(localStorage.getItem('tidemart_addr'));
  const [pin, setPin] = useState(localStorage.getItem('tidemart_pin') || '');
  const [typed, setTyped] = useState('');
  const [msg, setMsg] = useState('');
  const [form, setForm] = useState(null);
  const load = () => (auth ? listAddresses().then(setList).catch(() => {}) : Promise.resolve());
  useEffect(() => { if (open) load(); }, [open]);
  useEffect(() => {
    const h = (e) => box.current && !box.current.contains(e.target) && setOpen(false);
    document.addEventListener('mousedown', h);
    return () => document.removeEventListener('mousedown', h);
  }, []);
  const choose = (a) => {
    localStorage.setItem('tidemart_addr', String(a.id)); localStorage.setItem('tidemart_pin', a.pincode); window.dispatchEvent(new CustomEvent('tidemart-location',{detail:{addressId:String(a.id),pincode:a.pincode}}));
    setSelId(String(a.id)); setPin(a.pincode); setOpen(false); setMsg('');
  };
  const current = list.find((a) => String(a.id) === selId);
  const label = current ? `${current.city} ${current.pincode}` : pin ? `Pincode ${pin}` : 'Select location';
  const checkTyped = () => checkPincode(typed).then((r) => {
    if (r.serviceable) { localStorage.setItem('tidemart_pin', typed); localStorage.removeItem('tidemart_addr'); setSelId(null); setPin(typed); window.dispatchEvent(new CustomEvent('tidemart-location',{detail:{addressId:null,pincode:typed}})); setMsg(`Delivery available by ${r.deliveryBy}`); }
    else setMsg('We do not deliver there yet');
  }).catch(() => setMsg('Enter a 6-digit pincode'));
  const saveNew = () => saveAddress(form).then((a) => { choose(a); setForm(null); }).catch((e) => setMsg(e.response?.data?.message || 'Could not save the address'));
  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });
  const input = 'w-full border rounded-lg px-3 py-1 mb-2 text-sm';
  return (
    <div className="relative" ref={box}>
      <button className="flex items-center gap-1 text-sm px-2 py-1 border rounded-full max-w-44" onClick={() => setOpen(!open)} aria-label="Choose delivery location">
        <svg viewBox="0 0 24 24" className="w-5 h-5 shrink-0 text-brand-accent" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M12 21s7-6 7-11a7 7 0 0 0-14 0c0 5 7 11 7 11zM12 12.5a2.5 2.5 0 1 0 0-5 2.5 2.5 0 0 0 0 5z" /></svg>
        <span className="truncate">{label}</span>
      </button>
      {open && (
        <div className="absolute left-0 mt-2 w-80 max-w-[90vw] bg-white border rounded-xl shadow-lg p-3 z-30">
          <b className="text-sm">Deliver to</b>
          {auth && list.map((a) => (
            <button key={a.id} onClick={() => choose(a)} className={`block w-full text-left border rounded-lg p-2 mt-2 text-sm ${String(a.id) === selId ? 'border-brand bg-slate-50' : ''}`}>
              <b>{a.name}</b> {a.isDefault && <span className="text-xs text-brand">Default</span>}<br />{a.line1}, {a.city} {a.pincode}
            </button>
          ))}
          {auth && !form && <button className="text-brand text-sm mt-2" onClick={() => setForm(blank)}>+ Add a new address</button>}
          {auth && form && (
            <div className="mt-2">
              {['name', 'phone', 'line1', 'city', 'state', 'pincode'].map((k) => <input key={k} className={input} placeholder={k === 'line1' ? 'Address' : k} value={form[k]} onChange={set(k)} />)}
              <button className="px-3 py-1 bg-brand text-white rounded-lg text-sm mr-2" onClick={saveNew}>Save and use</button>
              <button className="text-sm" onClick={() => setForm(null)}>Cancel</button>
            </div>
          )}
          {!auth && <p className="text-xs text-slate-500 mt-2">Log in to save addresses. You can still check a pincode.</p>}
          <div className="flex gap-2 mt-3"><input className="flex-1 border rounded-lg px-3 py-1 text-sm" maxLength={6} placeholder="Or enter a pincode" value={typed} onChange={(e) => setTyped(e.target.value)} /><button className="px-3 border rounded-lg text-sm" onClick={checkTyped}>Check</button></div>
          {msg && <p className="text-xs mt-2">{msg}</p>}
        </div>
      )}
    </div>
  );
}
