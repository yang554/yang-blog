import { createRouter, createWebHashHistory,createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import LoginView from '@/views/LoginView.vue'


const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'login',
    meta: { title: '登录', hidden: true },
    component: LoginView
  },
  {
    path: '/home',
    name: 'home',
    meta: { title: '首页', hidden: true },
    component: () => import('@/views/HomeView.vue'),
    children: [
      { path: "/BlogView", name: "BlogView", meta: { title: '文章' }, component: () => import('@/views/BlogView.vue') },
      { path: "/FamilySource", name: "FamilySource", meta: { title: '族谱' }, component: () => import('@/views/FamilySource.vue') },
    ]
  },
]

const router = createRouter({
  history:createWebHashHistory(),
  routes
})

export default router
