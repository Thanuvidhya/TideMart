import { useState } from 'react';

export default function useVoiceSearch() {
  const [listening, setListening] = useState(false);
  const Speech = window.SpeechRecognition || window.webkitSpeechRecognition;
  const start = (onText) => {
    if (!Speech) return alert('Voice search is not supported in this browser. Try Chrome.');
    const r = new Speech();
    r.lang = 'en-IN';
    r.onstart = () => setListening(true);
    r.onend = () => setListening(false);
    r.onresult = (e) => onText(e.results[0][0].transcript);
    r.start();
  };
  return { listening, supported: !!Speech, start };
}
