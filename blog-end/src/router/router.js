import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '@/store/store'

const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location, onResolve, onReject) {
  if (onResolve || onReject) return originalPush.call(this, location, onResolve, onReject)
  return originalPush.call(this, location).catch(err => err)
}

Vue.use(VueRouter)

import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import HomeView from '@/views/HomeView.vue'

//首页
import DashBoardView from '@/views/index/DashBoardView.vue'

//静态路由
export const constantRoutes = [
  {
    path: '/login',
    name: 'LoginView',
    component: LoginView,
    meta: { title: '登录' },
    hidden: true
  }, {
    path: '/register',
    name: 'RegisterView',
    component: RegisterView,
    meta: { title: '注册' },
    hidden: true
  }, {
    path: '/admin/dashboard',
    component: HomeView,
    hidden: true,
    meta: { title: '首页' },
    children: [
      { path: "/", name: "Home", meta: { title: '数据面板' }, component: DashBoardView },
    ]
  }, {
    path: '/home/blog',
    name: '博客管理',
    component: HomeView,
    hidden: false,
    myIcon: "el-icon-s-management",
    meta: {
      title: '博客管理',
      roles: ['admin', 'user','visitor']
    },
    children: [
      { path: "/home/blog/write", name: "WriteView", meta: { title: '写博客' }, component: () => import('@/views/blogs/WriteView.vue'), myIcon: "el-icon-edit" },
      { path: '/home/editblog/:id', name: 'EditBlog', meta: { title: '编辑博客' }, component: () => import('@/views/blogs/EditBlog.vue'), props: true, hidden: true },
      { path: '/home/allblog', name: 'BlogView', meta: { title: '文章管理' }, component: () => import('@/views/blogs/BlogView.vue'), myIcon: "el-icon-notebook-1" },
      { path: '/home/sortblog', name: 'SortView', meta: { title: '分类专栏' }, component: () => import('@/views/blogs/SortView.vue'), myIcon: "el-icon-document" },
      { path: '/home/tagblog', name: 'TagView', meta: { title: '云标签管理' }, component: () => import('@/views/blogs/TagView.vue'), myIcon: "el-icon-collection-tag" },
      { path: '/home/commentblog', name: '评论管理', meta: { title: '评论管理' }, component: () => import('@/views/blogs/RemarkView.vue'), props: true, myIcon: "el-icon-tickets" },
    ]
  },{
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
      { path: "/home/user/collection/:uid", name: "用户收藏", meta: { title: '用户收藏' }, component: null, hidden: true },
      { path: "/home/user/add", name: "UserAddView", meta: { title: '新增用户' }, component: () => import('@/views/users/UserAddView.vue'), myIcon: "el-icon-plus" },
      { path: "/home/user/role", name: "RoleView", meta: { title: '权限管理' }, component: () => import('@/views/users/RoleView.vue'), myIcon: "el-icon-key" },
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
    myIcon: "el-icon-date",
    children: [
      { path: "/home/aunt/AuntList", name: "AuntList", meta: { title: 'aunt列表' }, component: () => import('@/views/aunt/AuntList.vue'), myIcon: "el-icon-s-operation" },
      { path: "/home/aunt/AuntDashBoard", name: "AuntDashBoard", meta: { title: 'aunt看板' }, component: () => import('@/views/aunt/AuntDashBoard.vue'), myIcon: "el-icon-s-data" },
      { path: "/home/aunt/AuntAdd", name: "AuntAdd", meta: { title: '新增aunt' }, component: () => import('@/views/aunt/AuntAdd.vue'), myIcon: "el-icon-plus" },
      { path: "/home/aunt/CreateEven/:data", name: "CreateEven", meta: { title: '创建事件' }, component: () => import('@/views/aunt/CreateEven.vue'), props: true, hidden: true },
      { path: "/home/aunt/EvenList", name: "EvenList", meta: { title: '事件列表' }, component: () => import('@/views/aunt/EvenList.vue'), myIcon: "el-icon-s-order" },
    ]
  }, {
    path: '/home/family',
    name: '族谱管理',
    component: HomeView,
    meta: { title: '族谱管理', roles: ['admin', 'user_manager', 'blog_manager', 'user','visitor'] },
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

const whiteList = ["/login","/register"]; //无需令牌白名单

router.beforeEach(async (to, from, next) => {
  // 获取令牌判断用户是否登录
  const hasToken = store.state.login_user.roles;
  if (hasToken) {
    //已登录
    if (to.path === "/login") {
      //若以登录没有必要显示登录页，重定向回首页
      next({ path: "/login" });
    } else {
      // 去其他路由
      const hasRoles = store.state.login_user.roles && store.state.login_user.roles.length > 0;
      if (hasRoles) {
        // 若用户角色已付加则说明权限以判定，动态路由已添加
        next();
      } else {
        next(`/login`);
        alert(error || "未知错误");
      }
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      // 白名单中的路由路过
      next();
    } else {
      // 重定向至登录页
      next(`/login`);
    }
  }
})

export default router
