//对axios进行二次封装
import axios from 'axios';
import router from '../router';

axios.defaults.withCredentials=true;
// 利用axios方法去创建axios实例
const requests=axios.create({
    baseURL:'/api',
    timeout:5000,
    headers:{},
    withCredentials:true,
    crossDomain:true,
});

requests.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

requests.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        if (error.response && error.response.status === 401) {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            router.push('/login');
        }
        return Promise.reject(error);
    }
);

export default requests;