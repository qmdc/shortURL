// 该文件专门用于创建整个应用的路由器
import VueRouter from 'vue-router'
//引入组件
import Index from "@/components/Index.vue";
import Streaming from "@/components/Streaming.vue";
import Recovery from "@/components/Recovery.vue";
import Record from "@/components/Record.vue";
import Login from "@/components/Login.vue";
import Register from "@/components/Register.vue";
import store from "@/store";

//创建并暴露一个路由器
const router =  new VueRouter({
        routes: [
            {
                name: 'login',
                path: '/login',
                component: Login,
                meta: { requiresAuth: false }
            },
            {
                name: 'register',
                path: '/register',
                component: Register,
                meta: { requiresAuth: false }
            },
            {
                name: 'recovery',
                path: '/recovery',
                component: Recovery,
                meta: { requiresAuth: true }
            },
            {
                name: 'index',
                path: '/index',
                component: Index,
                meta: { requiresAuth: true }
            },
            {
                name: 'index',
                path: '/',
                component: Index,
                meta: { requiresAuth: true }
            },
            {
                name: 'streaming',
                path: '/streaming',
                component: Streaming,
                meta: { requiresAuth: true }
            },
            {
                name: 'record',
                path: '/record',
                component: Record,
                meta: { requiresAuth: true }
            }
        ]
    })

router.beforeEach((to, from, next) => {
    const isLoggedIn = store.getters['user/isLoggedIn'];
    
    if (to.meta.requiresAuth && !isLoggedIn) {
        next('/login');
    } else if ((to.path === '/login' || to.path === '/register') && isLoggedIn) {
        next('/');
    } else {
        next();
    }
})

export default router
