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
import BlogView from '@/views/blogs/BlogView.vue'

//静态路由
export const constantRoutes = [
  {
    path: '/',
    name: 'LoginView',
    component: LoginView,
    meta: { title: '登录' },
    hidden: true
  }, {
    path: '/register',
    name: 'RegisterView',
    component: RegisterView,
    title: '注册',
    hidden: true
  }, {
    path: '/admin/dashboard',
    component: HomeView,
    hidden: true,
    children: [
      { path: "/", name: "Home", meta: { title: '' }, component: DashBoardView },
    ]
  }, {
    path: '/home/blog',
    name: '博客管理',
    component: HomeView,
    hidden: false,
    myIcon: "el-icon-s-management",
    meta: {
      title: '博客管理',
      roles: ['admin']
    },
    children: [
      { path: "/home/blog/write", name: "WriteView", meta: { title: '写博客' }, component: () => import('@/views/blogs/WriteView.vue'), myIcon: "el-icon-edit" },
      { path: '/home/editblog/:id', name: 'EditBlog', meta: { title: '编辑博客' }, component: () => import('@/views/blogs/EditBlog.vue'), props: true, hidden: true },
      { path: '/home/allblog', name: 'BlogView', meta: { title: '文章管理' }, component: BlogView, myIcon: "el-icon-notebook-1" },
      { path: '/home/sortblog', name: 'SortView', meta: { title: '分类专栏' }, component: SortView, myIcon: "el-icon-document" },
      { path: '/home/tagblog', name: 'TagView', meta: { title: '云标签管理' }, component: TagView, myIcon: "el-icon-collection-tag" },
      { path: '/home/commentblog', name: '评论管理', meta: { title: '评论管理' }, component: () => import('@/views/blogs/RemarkView.vue'), props: true, myIcon: "el-icon-tickets" },
    ]
  }, {
    path: '/home/user',
    name: '用户管理',
    component: HomeView,
    hidden: false,
    meta: {
      title: '用户管理',
      roles: ['admin', 'user_manager']
    },
    myIcon: "el-icon-user-solid",
    children: [
      { path: "/home/user/list", name: "UserListView", meta: { title: '用户列表' }, component: () => import('@/views/users/UserListView.vue'), myIcon: "el-icon-user" },
      { path: "/home/user/collection/:uid", name: "用户收藏", meta: { title: '用户收藏' }, component: WriteView, hidden: true },
      { path: "/home/user/add", name: "UserAddView", meta: { title: '新增用户' }, component: () => import('@/views/users/UserAddView.vue'), myIcon: "el-icon-plus" },
      { path: "/home/user/update", name: "EditUser", meta: { title: '编辑用户' }, component: () => import('@/views/users/EditUser.vue'), hidden: true },
    ]
  }, {
    path: '/home/aunt',
    name: 'AUNT模块',
    component: HomeView,
    meta: {
      title: 'aunt管理',
      roles: ['admin']
    },
    hidden: false,
    myIcon: "el-icon-s-grid",
    children: [
      { path: "/home/aunt/AuntDashBoard", name: "AuntDashBoard", meta: { title: 'aunt看板' }, component: () => import('@/views/aunt/AuntDashBoard.vue'), myIcon: "el-icon-s-data" },
      { path: "/home/aunt/AuntAdd", name: "AuntAdd", meta: { title: '新增aunt' }, component: () => import('@/views/aunt/AuntAdd.vue'), myIcon: "el-icon-plus" },
      { path: "/home/aunt/CreateEven/:data", name: "CreateEven", meta: { title: '创建事件' }, component: () => import('@/views/aunt/CreateEven.vue'), props: true, hidden: true },
      { path: "/home/aunt/EvenList", name: "EvenList", meta: { title: '事件列表' }, component: () => import('@/views/aunt/EvenList.vue'), myIcon: "el-icon-s-order" },
    ]
  }, {
    path: '/home/family',
    name: '族谱管理',
    component: HomeView,
    meta: { title: '族谱管理', roles: ['admin', 'user_manage', 'blog_manager', 'user'] },
    hidden: false,
    myIcon: "el-icon-s-home",
    children: [
      { path: "/home/family/FamilyDashBoard", name: "FamilyDashBoard", meta: { title: '族谱仪表板' }, component: () => import('@/views/family/FamilyDashBoard.vue'), myIcon: "el-icon-office-building" },
      { path: "/home/family/FamilySource", name: "FamilySource", meta: { title: '朔源一览图' }, component: () => import('@/views/family/FamilySource.vue'), myIcon: "el-icon-s-data" },
      { path: "/home/family/FamilyListView", name: "FamilyListView", meta: { title: '成员列表' }, component: () => import('@/views/family/FamilyListView.vue'), myIcon: "el-icon-s-custom" },
      { path: "/home/family/FamilyEditRank", name: "FamilyEditRank", meta: { title: '编辑家族成员信息' }, component: () => import('@/views/family/FamilyEditRank.vue'), hidden: true },
    ]
  }, {
    path: '/test',
    name: '其他模块',
    component: HomeView,
    meta: { title: '其他模块' },
    hidden: false,
    myIcon: "el-icon-setting",
    children: [
    ]
  }
]

//动态路由
export const asyncRoutes = [

]

const router = new VueRouter({
  mode: 'history',
  // mode: 'hash',
  base: process.env.BASE_URL,
  routes: constantRoutes
})

router.beforeEach((to, from, next) => {
  // console.log(to)
  // console.log(from)
  // console.log(next)
  // console.log(this.$store.state.login_user)
  // const isAdmin = checkAdminPermission() // 检查用户是否具有管理员权限
  // const isUser = checkUserPermission() // 检查用户是否具有用户权限
  if (to.matched.some(record => record.meta.requiresAdmin)) {
    // if (!isAdmin) {
    //   next({ path: '/' }) // 如果用户没有管理员权限，则重定向到首页
    // } else {
    //   next() // 如果用户具有管理员权限，则继续访问路由
    // }
  } else if (to.matched.some(record => record.meta.requiresUser)) {
    // if (!isUser) {
    //   next({ path: '/' }) // 如果用户没有用户权限，则重定向到首页
    // } else {
    //   next() // 如果用户具有用户权限，则继续访问路由
    // }
  } else {
    next() // 如果路由不需要权限控制，则继续访问路由
  }
})

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
