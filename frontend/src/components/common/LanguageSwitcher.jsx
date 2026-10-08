import { useTranslation } from 'react-i18next';

export default function LanguageSwitcher() {
  const { i18n } = useTranslation();
  const change = (e) => { localStorage.setItem('tidemart_lang', e.target.value); i18n.changeLanguage(e.target.value); };
  return (
    <select aria-label="Language" className="border rounded px-1 py-1 text-sm bg-white" value={i18n.language} onChange={change}>
      <option value="en">English</option><option value="ta">தமிழ்</option><option value="hi">हिन्दी</option>
    </select>
  );
}
