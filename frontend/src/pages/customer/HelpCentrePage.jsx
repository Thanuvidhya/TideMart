import { useEffect, useState } from 'react';
import { createTicket, getChat, getFaqs, getTicket, listTickets, replyTicket, sendChat } from '../../api/supportApi.js';
import Navbar from '../../components/common/Navbar.jsx';

export default function HelpCentrePage() {
  const [faqs, setFaqs] = useState([]);
  const [tickets, setTickets] = useState([]);
  const [open, setOpen] = useState(null);
  const [chat, setChat] = useState([]);
  const [text, setText] = useState('');
  const [reply, setReply] = useState('');
  const [f, setF] = useState({ subject: '', message: '' });
  const [msg, setMsg] = useState('');
  const loadTickets = () => listTickets().then(setTickets);
  useEffect(() => { getFaqs().then(setFaqs); loadTickets(); getChat().then(setChat); }, []);
  const raise = () => createTicket(f).then(() => { setF({ subject: '', message: '' }); setMsg('Ticket raised'); loadTickets(); }).catch((e) => setMsg(e.response?.data?.message || 'Failed'));
  const say = () => { if (text.trim()) sendChat(text).then(setChat); setText(''); };
  const field = 'w-full border rounded-lg px-3 py-2 mb-2';
  return (
    <>
      <Navbar />
      <main className="max-w-2xl mx-auto p-4">
        <h1 className="text-2xl font-bold mb-3">Help centre</h1>
        {faqs.map((q) => <details key={q.id} className="bg-white border rounded-xl p-3 mb-2"><summary className="font-medium cursor-pointer">{q.question}</summary><p className="text-sm mt-2">{q.answer}</p></details>)}
        <h2 className="font-semibold mt-6 mb-2">Chat with us (demo assistant)</h2>
        <div className="bg-white border rounded-xl p-3 max-h-60 overflow-y-auto mb-2">
          {!chat.length && <p className="text-sm text-slate-400">Ask about orders, returns, refunds or payments.</p>}
          {chat.map((c) => <div key={c.id} className={`text-sm mb-1 ${c.senderRole === 'USER' ? 'text-right' : 'text-brand'}`}>{c.message}</div>)}
        </div>
        <div className="flex gap-2"><input className="flex-1 border rounded-lg px-3 py-2" value={text} onChange={(e) => setText(e.target.value)} onKeyDown={(e) => e.key === 'Enter' && say()} placeholder="Type a message" /><button className="px-4 bg-brand text-white rounded-lg" onClick={say}>Send</button></div>
        <h2 className="font-semibold mt-6 mb-2">Raise a ticket</h2>
        <input className={field} placeholder="Subject" value={f.subject} onChange={(e) => setF({ ...f, subject: e.target.value })} />
        <textarea className={field} rows="3" placeholder="Describe the problem" value={f.message} onChange={(e) => setF({ ...f, message: e.target.value })} />
        <button className="px-4 py-2 bg-brand-accent text-white rounded-lg" onClick={raise}>Submit</button>
        {msg && <span className="ml-3 text-sm">{msg}</span>}
        <h2 className="font-semibold mt-6 mb-2">My tickets</h2>
        {tickets.map((t) => (
          <div key={t.id} className="bg-white border rounded-xl p-3 mb-2">
            <button className="w-full text-left flex justify-between" onClick={() => (open?.ticket.id === t.id ? setOpen(null) : getTicket(t.id).then(setOpen))}><b>{t.subject}</b><span className="text-sm">{t.status}</span></button>
            {open?.ticket.id === t.id && (
              <div className="mt-2 text-sm">
                {open.messages.map((m) => <div key={m.id} className="mb-1">{m.message}</div>)}
                <div className="flex gap-2 mt-2"><input className="flex-1 border rounded px-2 py-1" value={reply} onChange={(e) => setReply(e.target.value)} placeholder="Add a message" /><button className="px-3 border rounded" onClick={() => replyTicket(t.id, reply).then((v) => { setOpen(v); setReply(''); })}>Send</button></div>
              </div>
            )}
          </div>
        ))}
      </main>
    </>
  );
}
