import at from 'array.prototype.at'; 
at.shim();
import App from './App'
import {
	STORAGE_KEYS,
	PAGE_PATHS
} from '@/common/constants.js'

// import uView from 'uview-ui';
// Vue.use(uView);

// #ifndef VUE3
import Vue from 'vue'
Vue.config.productionTip = false
App.mpType = 'app'
const app = new Vue({
	...App
})
app.$mount()
// #endif

Vue.prototype.$constants = STORAGE_KEYS
Vue.prototype.$PATHS = PAGE_PATHS

// #ifdef VUE3
import {
	createSSRApp
} from 'vue'
export function createApp() {
	const app = createSSRApp(App)
	return {
		app
	}
}
// #endif