import api from './axiosClient';

const unwrap = (p) => p.then((r) => r.data.data);

export const getMe = () => unwrap(api.get('/users/me'));
export const updateMe = (body) => unwrap(api.put('/users/me', body));
export const deleteMe = () => unwrap(api.delete('/users/me'));
export const listAddresses = () => unwrap(api.get('/addresses'));
export const saveAddress = (a) => unwrap(a.id ? api.put(`/addresses/${a.id}`, a) : api.post('/addresses', a));
export const deleteAddress = (id) => unwrap(api.delete(`/addresses/${id}`));
