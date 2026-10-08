import { useEffect, useState } from 'react';
import { answerQuestion, askQuestion, getQuestions } from '../../api/productApi.js';

export default function QuestionsAnswers({ productId }) {
  const [list, setList] = useState([]);
  const [text, setText] = useState('');
  const [msg, setMsg] = useState('');
  const load = () => getQuestions(productId).then(setList);
  useEffect(() => { load(); }, [productId]);
  const fail = (e) => setMsg(e.response?.data?.message || 'Log in to do this');
  const ask = () => askQuestion(productId, text).then((l) => { setList(l); setText(''); setMsg(''); }).catch(fail);
  const answer = (id) => { const a = window.prompt('Your answer'); if (a) answerQuestion(id, a).then(load).catch(fail); };
  return (
    <section className="mt-8">
      <h2 className="font-bold mb-3">Questions and answers</h2>
      {!list.length && <p className="text-sm text-slate-500 mb-3">No questions yet.</p>}
      {list.map((q) => (
        <div key={q.id} className="bg-white border rounded-xl p-3 mb-2 text-sm">
          <b>Q: {q.question}</b> <span className="text-slate-400">{q.user}, {q.createdAt}</span>
          {q.answers.map((a, i) => <div key={i} className="mt-1">A: {a.answer} <span className="text-slate-400">{a.user}</span></div>)}
          <button className="text-brand mt-1" onClick={() => answer(q.id)}>Answer</button>
        </div>
      ))}
      <div className="flex gap-2 mt-3"><input className="flex-1 border rounded-lg px-3 py-2" placeholder="Ask about this product" value={text} onChange={(e) => setText(e.target.value)} /><button className="px-4 bg-brand text-white rounded-lg" onClick={ask}>Ask</button></div>
      {msg && <p className="text-sm text-red-600 mt-2">{msg}</p>}
    </section>
  );
}
