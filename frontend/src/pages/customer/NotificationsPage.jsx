import { useEffect, useState } from 'react';
import { getNotifications, readAllNotifications } from '../../api/supportApi.js';
import Navbar from '../../components/common/Navbar.jsx';

export default function NotificationsPage() {
  const [d, setD] = useState({ unread: 0, items: [] });
  const load = () => getNotifications().then(setD);
  useEffect(() => { load(); }, []);
  return (
    <>
      <Navbar />
      <main className="max-w-xl mx-auto p-4">
        <div className="flex justify-between items-center mb-3"><h1 className="text-2xl font-bold">Alerts</h1><button className="text-sm text-brand" onClick={() => readAllNotifications().then(load)}>Mark all read</button></div>
        {!d.items.length && <p className="text-slate-500">No alerts yet. Order updates show up here.</p>}
        {d.items.map((n) => (
          <div key={n.id} className={`border rounded-xl p-3 mb-2 ${n.isRead ? 'bg-white' : 'bg-orange-50 border-brand-accent'}`}>
            <b>{n.title}</b><div className="text-sm">{n.body}</div><div className="text-xs text-slate-400">{n.createdAt.slice(0, 16).replace('T', ' ')}</div>
          </div>
        ))}
      </main>
    </>
  );
}
