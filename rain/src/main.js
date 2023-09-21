import Vue from 'vue'
import App from './App.vue'

import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import VueRouter from 'vue-router'
import router from "@/router";
import * as echarts from "echarts";

Vue.use(VueRouter)
Vue.use(ElementUI)

Vue.config.productionTip = false

new Vue({
  render: h => h(App),
  beforeCreate() {
    Vue.prototype.$bus = this
    Vue.prototype.$echarts = echarts;
  },
  router
}).$mount('#app')
