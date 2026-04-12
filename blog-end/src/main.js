import Vue from 'vue'
import router from './router/router'
import store from './store/store'
import App from './App.vue'
import { isLoggedIn } from '@/utils/auth'
import './assets/icon/iconfont.js' //引入阿里巴巴图标库js
import './assets/icon/iconfont.css'//引入阿里巴巴图标库css

import './assets/less/common.less' //引入全局样式


//gojs
import gojs from "./utils/go";
Vue.prototype.go = gojs

//树形图
import Vue2OrgTree from 'vue2-org-tree'
Vue.use(Vue2OrgTree)


Vue.config.productionTip = false

//转盘
import VueLuckyCanvas from '@lucky-canvas/vue'
Vue.use(VueLuckyCanvas)

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

/*农历转换*/
import * as nlCalendar from 'chinese-lunar-calendar'
Vue.prototype.$nlCalendar = nlCalendar;

/*qs*/
import qs from "qs"
Vue.prototype.$qs = qs;

/*引入echarts*/
import * as echarts from 'echarts';
Vue.prototype.$echarts = echarts;

Vue.prototype.$echarts_l = function (el) {
  return echarts.init(el, null, {renderer: 'svg'})
}

//窗口抖动
import VueDebounce from 'debounce';
Vue.prototype.$debounce = VueDebounce;

//使用Vuetify
import Vuetify from 'vuetify';
import 'vuetify/dist/vuetify.min.css';
Vue.use(Vuetify);

// //全局请求
import { request, postKeyValueRequest, getRequest, postRequest } from "@/api/request.js"
Vue.prototype.$request = request;
Vue.prototype.$getRequest = getRequest;

// 应用启动时恢复认证状态
if (isLoggedIn()) {
  // 状态已经在 store 的初始化中恢复了
  // 可以在这里添加 token 验证逻辑
  console.log('检测到已登录状态，自动恢复');
}

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
