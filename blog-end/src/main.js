import Vue from 'vue'
import router from './router/router'
import store from './store/store'
import App from './App.vue'


Vue.config.productionTip = false

//滚动条
import VueSmoothScroll from "vue2-smooth-scroll";
Vue.use(VueSmoothScroll)

//使用element-ui
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
Vue.use(ElementUI);

//mavonEditor富文本
import mavonEditor from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
Vue.use(mavonEditor)

//粒子特效
import VueParticles from 'vue-particles'
Vue.use(VueParticles);

/*JavaScript时间日期格式化*/
import Moment from 'moment'
Vue.prototype.$moment = Moment;



// //全局请求
import { request, postKeyValueRequest, getRequest, postRequest } from "@/api/request.js"
Vue.prototype.$request = request;
// Vue.prototype.$postKeyValueRequest = postKeyValueRequest;
// Vue.prototype.$getRequest = getRequest;
// Vue.prototype.$postRequest = postRequest;

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
