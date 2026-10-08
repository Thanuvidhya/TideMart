import api from './axiosClient';

const unwrap = (p) => p.then((r) => r.data.data);

export const sendOtp = (phone) => unwrap(api.post('/auth/send-otp', { phone }));
export const verifyOtp = (phone, code) => unwrap(api.post('/auth/verify-otp', { phone, code }));
export const register = ({ name, email, password }) => unwrap(api.post('/auth/register', { name, email, password }));
export const login = ({ email, password }) => unwrap(api.post('/auth/login', { email, password }));
export const customerLogin = ({ email, password }) => unwrap(api.post('/auth/customer-login', { email, password }));
export const adminLogin = ({ email, password }) => unwrap(api.post('/auth/admin-login', { email, password }));
export const deliveryLogin = ({ email, password }) => unwrap(api.post('/auth/delivery-login', { email, password }));
