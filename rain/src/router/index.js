// 该文件专门用于创建整个应用的路由器
import VueRouter from 'vue-router'
//引入组件
import Index from "@/components/Index.vue";
import Streaming from "@/components/Streaming.vue";
import Recovery from "@/components/Recovery.vue";
import Record from "@/components/Record.vue";

//创建并暴露一个路由器
const router =  new VueRouter({
        routes: [
            {
                name: 'recovery',
                path: '/recovery',
                component: Recovery,
            },
            {
                name: 'index',
                path: '/index',
                component: Index,
            },
            {
                name: 'index',
                path: '/',
                component: Index,
            },
            {
                name: 'streaming',
                path: '/streaming',
                component: Streaming
            },
            {
                name: 'record',
                path: '/record',
                component: Record
            }
        ]
    })

export default router
