import Vue from 'vue'
import router from './router/router'
import store from './store/store'
import App from './App.vue'


Vue.config.productionTip = false

//使用element-ui
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
Vue.use(ElementUI);

//粒子特效
import VueParticles from 'vue-particles'
Vue.use(VueParticles);

// //全局请求
// import { request, postKeyValueRequest, getRequest, postRequest } from "@/api/request.js"
// Vue.prototype.$request = request;
// Vue.prototype.$postKeyValueRequest = postKeyValueRequest;
// Vue.prototype.$getRequest = getRequest;
// Vue.prototype.$postRequest = postRequest;

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
