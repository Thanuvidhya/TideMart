import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { deleteAddress, listAddresses, saveAddress } from '../../api/userApi.js';

const blank = { name: '', phone: '', line1: '', city: '', state: '', pincode: '', isDefault: false };

export default function AddressesPage() {
  const [list, setList] = useState([]);
  const [a, setA] = useState(blank);
  const [err, setErr] = useState('');
  const load = () => listAddresses().then(setList);
  useEffect(() => { load(); }, []);
  const set = (k) => (e) => setA({ ...a, [k]: e.target.value });
  const add = async () => {
    setErr('');
    try { await saveAddress(a); setA(blank); load(); }
    catch (e) { setErr(e.response?.data?.message || 'Could not save address'); }
  };
  const field = 'w-full border rounded-lg px-3 py-2 mb-2';
  return (
    <div className="max-w-md mx-auto p-6">
      <Link to="/" className="text-brand text-sm">Back</Link>
      <h1 className="text-2xl font-bold my-3">My addresses</h1>
      {list.map((x) => (
        <div key={x.id} className="border rounded-xl p-3 mb-2 flex justify-between gap-3">
          <div className="text-sm">
            <b>{x.name}</b> {x.isDefault && <span className="text-xs text-brand">Default</span>}<br />
            {x.line1}, {x.city}, {x.state} {x.pincode}<br />{x.phone}
          </div>
          <button className="text-red-600 text-sm" onClick={() => deleteAddress(x.id).then(load)}>Delete</button>
        </div>
      ))}
      <h2 className="font-semibold mt-6 mb-2">Add a new address</h2>
      {['name', 'phone', 'line1', 'city', 'state', 'pincode'].map((k) => (
        <input key={k} className={field} placeholder={k === 'line1' ? 'Address' : k} value={a[k]} onChange={set(k)} />
      ))}
      <label className="text-sm flex gap-2 mb-3"><input type="checkbox" checked={a.isDefault} onChange={(e) => setA({ ...a, isDefault: e.target.checked })} />Make default</label>
      <button className="w-full bg-brand text-white py-2 rounded-lg" onClick={add}>Save address</button>
      {err && <p className="text-red-600 mt-2 text-sm">{err}</p>}
    </div>
  );
}
