import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

const app = createApp(App)

//粒子特效
import Particles from 'vue3-particles'
app.use(Particles);

app.config.globalProperties.$productionTip = false

//Element-Plus
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import 'element-plus/dist/index.css'
app.use(ElementPlus, {
  locale: zhCn,
});

//svg图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

//mavonEditor富文本
import mavonEditor from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
app.use(mavonEditor)

//echarts
// import * as echarts from 'echarts';
// app.use(echarts)

/*农历转换*/
import * as nlCalendar from 'chinese-lunar-calendar'
app.config.globalProperties.$nlCalendar = nlCalendar

app.use(createPinia())
app.use(router)

app.mount('#app')
