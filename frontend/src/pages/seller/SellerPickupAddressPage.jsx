import { useEffect, useState } from 'react';
import { getSellerMe, savePickup } from '../../api/sellerApi.js';
import SellerLayout from '../../layouts/SellerLayout.jsx';

export default function SellerPickupAddressPage() {
  const [f, setF] = useState({ line1: '', city: '', state: '', pincode: '' });
  const [msg, setMsg] = useState('');
  useEffect(() => { getSellerMe().then((d) => d.pickup && setF(d.pickup)); }, []);
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value });
  const save = () => savePickup(f).then(() => setMsg('Saved')).catch((e) => setMsg(e.response?.data?.message || 'Failed'));
  return (
    <SellerLayout title="Pickup address">
      {['line1', 'city', 'state', 'pincode'].map((k) => <input key={k} className="w-full border rounded-lg px-3 py-2 mb-2" placeholder={k === 'line1' ? 'Address' : k} value={f[k] || ''} onChange={set(k)} />)}
      <button className="w-full bg-brand text-white py-2 rounded-lg" onClick={save}>Save</button>
      {msg && <p className="mt-2 text-sm">{msg}</p>}
    </SellerLayout>
  );
}
