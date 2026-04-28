import Vue from 'vue'
import App from './App.vue'

import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import VueRouter from 'vue-router'
import Vuex from 'vuex'
import router from "@/router";
import store from "@/store";
import * as echarts from "echarts";

Vue.use(VueRouter)
Vue.use(Vuex)
Vue.use(ElementUI)

Vue.config.productionTip = false

new Vue({
  render: h => h(App),
  beforeCreate() {
    Vue.prototype.$bus = this
    Vue.prototype.$echarts = echarts;
  },
  router,
  store
}).$mount('#app')
