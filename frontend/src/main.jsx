import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App.jsx';
import './i18n/i18n.js';
import './index.css';

if (localStorage.getItem('tidemart_dark')) document.documentElement.classList.add('dark');

ReactDOM.createRoot(document.getElementById('root')).render(<App />);
