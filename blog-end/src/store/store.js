import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)
import { getToken, setToken, removeToken, getUser, isLoggedIn } from "@/utils/auth.js"

export default new Vuex.Store({
  state: {
    // HOST: "http://118.25.110.52:8090/",
    HOST: "http://127.0.0.1:8090/",
    token: getToken() || null,
    isAuthenticated: isLoggedIn(),  // 认证状态
    login_user: getUser() || {},    //后台管理员登录的信息
    date: new Date().getTime(),
    blogActiveMenuIndex: '8', // 博客菜单默认值
  },
  getters: {
    isAuthenticated: state => state.isAuthenticated,
    currentUser: state => state.login_user,
    currentToken: state => state.token
  },
  mutations: {
    // 登录成功
    loginSuccess(state, payload) {
      state.token = payload.token;
      state.login_user = payload.user;
      state.isAuthenticated = true;
      setToken(payload.token, payload.user);
    },
    // 更新存储的用户信息
    updateLoginUser(state, payload) {
      state.login_user = payload.user;
      setToken(state.token, payload.user); 
    },
    // 退出登录
    logout(state) {
      state.token = null;
      state.login_user = {};
      state.isAuthenticated = false;
      removeToken();
    },
    setActiveMenuIndex(state, index) {
      state.blogActiveMenuIndex = index;
    }
  },
  actions: {
    // 登录动作
    login({ commit }, { token, user }) {
      commit('loginSuccess', { token, user });
    },
    
    // 退出登录动作
    logout({ commit }) {
      commit('logout');
    },
    
    // 更新用户信息动作
    updateUser({ commit }, user) {
      commit('updateLoginUser', { user });
    }
  },
  modules: {
  }
})
