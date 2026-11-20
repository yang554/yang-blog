import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export const useCounterStore = defineStore('counter', () => {
  const count = ref(0)
  const doubleCount = computed(() => count.value * 2)
  function increment() {
    count.value++
  }

  return { count, doubleCount, increment }
})


// import { createStore } from 'vuex';
 
// export default createStore({
//   state: {
//     isAuthenticated: false,
//     user: null
//   },
//   mutations: {
//     setAuthentication(state, status) {
//       state.isAuthenticated = status;
//     },
//     setUser(state, user) {
//       state.user = user;
//     }
//   },
//   actions: {
//     login({ commit }, user) {
//       // 模拟登录过程，实际开发中这里应该是 API 调用
//       commit('setAuthentication', true);
//       commit('setUser', user);
//     },
//     logout({ commit }) {
//       commit('setAuthentication', false);
//       commit('setUser', null);
//     }
//   }
// });