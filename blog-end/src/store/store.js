import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)
import { getToken, setToken, removeToken ,getRouter} from "@/utils/auth.js"

export default new Vuex.Store({
  state: {
    // HOST: "http://118.25.110.52:8090/",
    HOST: "http://127.0.0.1:8090/",
    Router: getRouter() || {},
    login_user: getToken() || {},    //后台管理员登录的信息
    date: new Date().getTime()
  },
  getters: {
    // roles: state => {
    //   return state.user.roles;
    // }

  },
  mutations: {
    updateLoginUser(state, payload) {
      setToken(payload);
      state.login_user = payload;
    },
    removeLoginUser(state) {
      removeToken();
      state.login_user = {};
    },
  },
  actions: {
  },
  modules: {
  }
})
