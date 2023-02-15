import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '@/store/store'

Vue.use(VueRouter)

import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import HomeView from '@/views/HomeView.vue'

//首页
import DashBoardView from '@/views/index/DashBoardView.vue'

//博客管理
import WriteView from '@/views/blogs/WriteView.vue'
import SortView from '@/views/blogs/SortView.vue'
import TagView from '@/views/blogs/TagView.vue'

//静态路由
export const constantRoutes = [
  {
    path: '/',
    name: 'LoginView',
    component: LoginView,
    hidden: true
  }, {
    path: '/login',
    name: 'LoginView',
    component: LoginView,
    meta: {title:'首页'},
    hidden: true
  }, {
    path: '/register',
    name: 'RegisterView',
    component: RegisterView,
    title:'注册',
    hidden: true
  },{
    path: '/admin/dashboard',
    name: 'register',
    component: HomeView,
    hidden:true,
    children:[
      {path:"/",name:"Home",meta: {title:'首页'},component:DashBoardView},
    ]
  }, {
    path: '/home/blog',
    name: '博客管理',
    component: HomeView,
    hidden: false,
    myIcon: "el-icon-s-management",
    meta: {
      title:'博客管理',
      roles: ['admin']
    },
    children: [
      { path: "/home/blog/write", name: "WriteView",meta: {title:'写博客'}, component: () => import('@/views/blogs/WriteView.vue'), myIcon: "el-icon-edit" },
      { path: '/home/editblog/:id', name: '编辑博客',meta: {title:'编辑博客'}, component: WriteView, props: true, hidden: true },
      { path: '/home/allblog', name: '文章管理',meta: {title:'文章管理'}, component: WriteView, myIcon: "el-icon-notebook-1" },
      { path: '/home/sortblog', name: 'SortView',meta: {title:'分类专栏'}, component: SortView, myIcon: "el-icon-document" },
      { path: '/home/tagblog', name: 'TagView',meta: {title:'云标签管理'}, component: TagView, myIcon: "el-icon-collection-tag" },
      { path: '/home/commentblog', name: '评论管理',meta: {title:'首页'}, component: WriteView, myIcon: "el-icon-tickets" },
    ]
  },{
    path: '/home/user',
    name: '用户管理',
    component: HomeView,
    hidden: false,
    meta: {
      title:'用户管理',
      roles: ['admin', 'user_manager']
    },
    myIcon: "el-icon-user-solid",
    children: [
      { path: "/home/user/list", name: "用户列表",meta: {title:'首页'}, component: WriteView, myIcon: "el-icon-user" },
      { path: "/home/user/collection/:uid", name: "用户收藏",meta: {title:'首页'}, component: WriteView, hidden: true },
      { path: "/home/user/add", name: "新增用户",meta: {title:'首页'}, component: WriteView, myIcon: "el-icon-plus" },
      { path: "/home/user/update", name: "编辑用户",meta: {title:'首页'}, component: WriteView, hidden: true },
    ]
  }, {
    path: '/test',
    name: '其他模块',
    component: HomeView,
    meta: {title:'其他模块'},
    hidden: false,
    myIcon: "el-icon-setting",
    children: [
      { path: "/test/vue-aplayer", name: "aplayer播放器",meta: {title:'首页'}, component: WriteView, myIcon: "el-icon-s-data" },
      { path: "/test/v-emoji-picker", name: "emoji测试",meta: {title:'首页'}, component: WriteView, myIcon: "el-icon-s-data" },
      { path: "/test/kkfileview", name: "Kkfileview测试",meta: {title:'首页'}, component: WriteView, myIcon: "el-icon-s-data" },
    ]
  }
]

//动态路由
export const asyncRoutes = [

]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: constantRoutes
})

// router.beforeEach(async (to, from, next) => {
//   router.addRoute(v)
// })

// let isToken = true;

// router.beforeEach(async (to, from, next) => {
//   //定义isToken为true和vuex不为空时添加路由
//   if (isToken) {
//     //从vuex中获取动态路由
//     const accessRouteses = await asyncRoutes;
//     // console.log(accessRouteses);
//     //动态路由循环解析和添加
//     accessRouteses.forEach(v => {
//       // v.children = routerChildren(v.children);
//       v.component = routerCom(v.component);
//       console.log(v);
//       router.addRoute(v); //添加
//     })
//     isToken = false //将isToken赋为 false ，否则会一直循环，崩溃
//     next({...to,replace: true,})
//   } else {
//     if (to.name == null) {
//       next("/404")
//     } else {
//       if (to.meta.title) { //判断是否有标题
//         document.title = to.meta.title //给相应页面添加标题
//       }
//       next()
//     }
//   }
// })

// function routerCom(path) { //对路由的component解析
//   // console.log(path);
//   return require('@/views/blogs/WriteView.vue');
// }
// function routerChildren(children) { //对子路由的component解析
//   children.forEach(v => {
//     v.component = routerCom(v.component);
//     if (v.children != undefined) {
//       v.children = routerChildren(v.children)
//     }
//   })
//   return children
// }

export default router
