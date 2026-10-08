import api from './axiosClient';

export const getWallet = () => api.get('/wallet').then((r) => r.data.data);
