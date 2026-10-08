import api from './axiosClient';

const unwrap = (p) => p.then((r) => r.data.data);

export const getReferrals = () => unwrap(api.get('/referrals'));
export const applyReferral = (code) => unwrap(api.post('/referrals/apply', null, { params: { code } }));
