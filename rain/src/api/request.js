//对axios进行二次封装
import axios from 'axios';

axios.defaults.withCredentials=true;
// 利用axios方法去创建axios实例
const requests=axios.create({
    baseURL:'/api',
    timeout:5000,
    headers:{},
    withCredentials:true,
    crossDomain:true,
});

export default requests;