import { createContext, useState } from 'react';

export const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [auth, setAuth] = useState(() => JSON.parse(localStorage.getItem('tidemart_auth') || 'null'));
  const signIn = (data) => {
    localStorage.setItem('tidemart_token', data.token);
    localStorage.setItem('tidemart_auth', JSON.stringify(data));
    setAuth(data);
  };
  const signOut = () => {
    localStorage.removeItem('tidemart_token');
    localStorage.removeItem('tidemart_auth');
    setAuth(null);
  };
  return <AuthContext.Provider value={{ auth, signIn, signOut }}>{children}</AuthContext.Provider>;
}

export default AuthProvider;
