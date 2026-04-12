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
import HomePageView from '@/views/index/HomePageView.vue'
//静态路由
export const constantRoutes = [
  {
    path: '/',
    name: 'LoginView',
    component: LoginView,
    meta: { title: '登录' },
    hidden: true
  },
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
    meta: { title: '财富轨迹' },
    children: [
      { path: "/admin/dashboard", name: "Home", meta: { title: '财富轨迹' }, component: HomePageView },
    ]
  }, {
    path: '/home/life',
    name: '财富管理',
    component: HomeView,
    meta: { title: '财富管理', roles: ['root', 'admin', 'user_manager', 'blog_manager', 'user'] },
    hidden: false,
    myIcon: "iconfont icon-caifuguanli",
    children: [
      { path: '/home/life/WealthReportView', name: 'WealthReportView', meta: { title: '财富报表' }, component: () => import('@/views/life/WealthReportView.vue'), myIcon: "el-icon-data-line" },
      { path: '/home/life/UserDepositView', name: 'UserDepositView', meta: { title: '用户资产' }, component: () => import('@/views/life/UserDepositView.vue'), myIcon: "el-icon-coin" },
      { path: '/home/life/ConsumptionTypeView', name: 'ConsumptionTypeView', meta: { title: '流水类型' }, component: () => import('@/views/life/ConsumptionTypeView.vue'), myIcon: "iconfont icon-fuwuleixing" },
      { path: "/home/life/DailyFlowView", name: "DailyFlowView", meta: { title: '账簿' }, component: () => import('@/views/life/DailyFlowView.vue'), myIcon: "iconfont icon-taizhangzhangbumingxichaxun" },
      { path: "/home/life/DailyFlowInfoShowView", name: "DailyFlowInfoShowView", meta: { title: '账簿明细' }, component: () => import('@/views/life/DailyFlowInfoShowView.vue'), myIcon: "iconfont icon-a-feiyongliushui3x" },
      { path: "/home/life/AddDailyFlowInfoView/:type", name: "AddDailyFlowInfoView", meta: { title: '新增流水明细' }, component: () => import('@/views/life/DailyFlowInfoView.vue'), hidden: true },
      { path: "/home/life/EditDailyFlowInfoView/:data", name: "EditDailyFlowInfoView", meta: { title: '编辑流水明细' }, component: () => import('@/views/life/DailyFlowInfoView.vue'), props: true, hidden: true },
    ]
  }, {
    path: '/home/blog',
    name: '博客管理',
    component: HomeView,
    hidden: false,
    myIcon: "el-icon-s-management",
    meta: {
      title: '博客管理',
      roles: ['root', 'admin', 'blog_manager', 'visitor']
    },
    children: [
      { path: "/home/blog/BlogDashBoardView", name: "BlogDashBoardView", meta: { title: '博客看板' }, component: () => import('@/views/index/BlogDashBoardView.vue'), myIcon: "el-icon-s-marketing" },
      { path: "/home/blog/write", name: "WriteView", meta: { title: '发布博客' }, component: () => import('@/views/blogs/WriteView.vue'), myIcon: "el-icon-edit" },
      { path: '/home/editblog/:id', name: 'EditBlog', meta: { title: '编辑博客' }, component: () => import('@/views/blogs/EditBlog.vue'), props: true, hidden: true },
      { path: '/home/allblog', name: 'BlogView', meta: { title: '文章管理' }, component: () => import('@/views/blogs/BlogView.vue'), myIcon: "el-icon-notebook-1" },
      { path: '/home/sortblog', name: 'SortView', meta: { title: '分类专栏' }, component: () => import('@/views/blogs/SortView.vue'), myIcon: "el-icon-document" },
      { path: '/home/tagblog', name: 'TagView', meta: { title: '云标签管理' }, component: () => import('@/views/blogs/TagView.vue'), myIcon: "el-icon-collection-tag" },
      { path: '/home/commentblog', name: '评论管理', meta: { title: '评论管理' }, component: () => import('@/views/blogs/RemarkView.vue'), props: true, myIcon: "el-icon-tickets" },
    ]
  }, {
    path: '/home/user',
    name: '用户管理',
    component: HomeView,
    hidden: false,
    meta: {
      title: '用户管理',
      roles: ['root', 'admin', 'user_manager']
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
      title: '经期管理',
      roles: ['root', 'admin', 'user_manager', 'blog_manager', 'user']
    },
    hidden: false,
    myIcon: "el-icon-date",
    children: [
      { path: "/home/aunt/AuntList", name: "AuntList", meta: { title: '历史经期记录' }, component: () => import('@/views/aunt/AuntList.vue'), myIcon: "el-icon-s-operation" },
      { path: "/home/aunt/AuntDashBoard", name: "AuntDashBoard", meta: { title: '经期看板' }, component: () => import('@/views/aunt/AuntDashBoard.vue'), myIcon: "el-icon-s-data" },
      { path: "/home/aunt/AuntAdd", name: "AuntAdd", meta: { title: '新增经期记录' }, component: () => import('@/views/aunt/AuntAdd.vue'), myIcon: "el-icon-plus" },
      { path: "/home/aunt/CreateEven/:data", name: "CreateEven", meta: { title: '创建事件' }, component: () => import('@/views/aunt/CreateEven.vue'), props: true, hidden: true },
      { path: "/home/aunt/EvenList", name: "EvenList", meta: { title: '事件列表' }, component: () => import('@/views/aunt/EvenList.vue'), myIcon: "el-icon-s-order" },
    ]
  }, {
    path: '/home/family',
    name: '族谱管理',
    component: HomeView,
    meta: { title: '族谱管理', roles: ['root', 'admin', 'user_manager', 'blog_manager', 'user'] },
    hidden: false,
    myIcon: "el-icon-s-home",
    children: [
      { path: "/home/family/FamilyDashBoard", name: "FamilyDashBoard", meta: { title: '族谱数据大屏' }, component: () => import('@/views/family/FamilyDashBoard.vue'), myIcon: "el-icon-office-building" },
      { path: "/home/family/FamilyTree", name: "FamilyTree", meta: { title: '族谱' }, component: () => import('@/views/family/FamilyTree.vue'), myIcon: "el-icon-s-data" },
      { path: "/home/family/FamilySource/:pTree", name: "FamilySource", meta: { title: '朔源一览图' }, component: () => import('@/views/family/FamilySource.vue'), hidden: true },
      { path: "/home/family/FamilyListView", name: "FamilyListView", meta: { title: '成员列表' }, component: () => import('@/views/family/FamilyListView.vue'), myIcon: "el-icon-s-custom" },
      { path: "/home/family/FamilyEditRank", name: "FamilyEditRank", meta: { title: '编辑家族成员信息' }, component: () => import('@/views/family/FamilyEditRank.vue'), hidden: true },
    ]
  }, {
    path: '/home/nutrition',
    name: '营养管理',
    component: HomeView,
    meta: { title: '营养管理' },
    hidden: false,
    myIcon: "el-icon-fork-spoon",
    children: [
      { path: "/home/nutrition/BodyComposition", name: "BodyComposition", meta: { title: '体成分分析' }, component: () => import('@/views/nutrition/BodyComposition.vue'), myIcon: "el-icon-first-aid-kit" },
      { path: "/home/nutrition/AddBodyComposition", name: "AddBodyComposition", meta: { title: '新增体成分' }, component: () => import('@/views/nutrition/OpBodyComposition.vue'), hidden: true },
      { path: '/home/nutrition/EditBodyComposition/:id', name: 'EditBodyComposition', meta: { title: '编辑体成分' }, component: () => import('@/views/nutrition/OpBodyComposition.vue'), props: true, hidden: true },
      { path: "/home/nutrition/FoodNutrition", name: "FoodNutrition", meta: { title: '食物营养' }, component: () => import('@/views/nutrition/FoodNutrition.vue'), myIcon: "el-icon-food" },
      { path: "/home/nutrition/AddFoodNutrition", name: "AddFoodNutrition", meta: { title: '新增食物' }, component: () => import('@/views/nutrition/OpFoodNutrition.vue'), hidden: true },
      { path: '/home/nutrition/EditFoodNutrition/:id', name: 'EditFoodNutrition', meta: { title: '编辑食物' }, component: () => import('@/views/nutrition/OpFoodNutrition.vue'), props: true, hidden: true },
      { path: "/home/nutrition/NutritionalRecipe", name: "NutritionalRecipe", meta: { title: '营养菜谱' }, component: () => import('@/views/nutrition/NutritionalRecipe.vue'), myIcon: "el-icon-dish" },
      { path: "/home/nutrition/AddNutritionRecipe", name: "AddNutritionRecipe", meta: { title: '新增菜谱' }, component: () => import('@/views/nutrition/OpNutritionRecipe.vue'), hidden: true },
      { path: '/home/nutrition/EditNutritionRecipe/:id', name: 'EditNutritionRecipe', meta: { title: '编辑菜谱' }, component: () => import('@/views/nutrition/OpNutritionRecipe.vue'), props: true, hidden: true },
      { path: "/home/nutrition/NutritionalRecipes", name: "NutritionalRecipes", meta: { title: '营养方案' }, component: () => import('@/views/nutrition/NutritionalRecipes.vue'), myIcon: "el-icon-dish-1" },
      { path: "/home/nutrition/AddNutritionRecipes", name: "AddNutritionRecipes", meta: { title: '新增食谱' }, component: () => import('@/views/nutrition/OpNutritionRecipes.vue'), hidden: true },
      { path: '/home/nutrition/EditNutritionRecipes/:id', name: 'EditNutritionRecipes', meta: { title: '编辑食谱' }, component: () => import('@/views/nutrition/OpNutritionRecipes.vue'), props: true, hidden: true },
      { path: "/home/nutrition/RecommendedRecipes", name: "RecommendedRecipes", meta: { title: '食谱推荐' }, component: () => import('@/views/nutrition/RecommendedRecipes.vue'), myIcon: "el-icon-tableware" },
      { path: "/home/nutrition/NutritionalDynamics", name: "NutritionalDynamics", meta: { title: '营养动态' }, component: () => import('@/views/nutrition/NutritionalDynamics.vue'), myIcon: "el-icon-data-analysis" },

      { path: "/home/nutrition/NutritionalAssessment", name: "NutritionalAssessment", meta: { title: '营养评估' }, component: () => import('@/views/nutrition/NutritionalAssessment.vue'), myIcon: "iconfont icon-yingyangpinggu" },
      { path: "/home/nutrition/NutritionalKnowledge", name: "NutritionalKnowledge", meta: { title: '营养知识' }, component: () => import('@/views/nutrition/NutritionalKnowledge.vue'), myIcon: "iconfont icon-zhishiku" },
      { path: "/home/nutrition/FoodSafety", name: "FoodSafety", meta: { title: '食品安全' }, component: () => import('@/views/nutrition/FoodSafety.vue'), myIcon: "iconfont icon-shipinanquan" },
    ]
  }, {
    path: '/home/prize',
    name: '奖品管理',
    component: HomeView,
    meta: { title: '奖品管理', roles: ['root', 'admin', 'user_manager', 'blog_manager', 'user'] },
    hidden: false,
    myIcon: "iconfont icon-jiangpinguanli",
    children: [
      { path: '/home/prize/PrizeSetting', name: 'PrizeSetting', meta: { title: '奖品设置' }, component: () => import('@/views/prizes/PrizeSetting.vue'), myIcon: "iconfont icon-youhuipeizhi" },
      { path: '/home/prize/Prizes', name: 'Prizes', meta: { title: '幸运转盘' }, component: () => import('@/views/prizes/Prizes.vue'), myIcon: "iconfont icon-xingyunzhuanpan" },
    ]
  }, {
    path: '/home/class',
    name: '课时费管理',
    component: HomeView,
    meta: { title: '课时费管理', roles: ['root', 'admin', 'user_manager', 'blog_manager', 'user', 'visitor'] },
    hidden: false,
    myIcon: "iconfont icon-keshifeiguanli",
    children: [
      { path: '/home/class/classFeeRule', name: 'classFeeRule', meta: { title: '课时费计算规则' }, component: () => import('@/views/class/ClassFeeRule.vue'), myIcon: "iconfont icon-jisuanguize" },
      { path: '/home/class/BasicFee', name: 'BasicFee', meta: { title: '课时费计算' }, component: () => import('@/views/class/BasicFee.vue'), myIcon: "iconfont icon-baojiadan" },
    ]
  }, {
    path: '/home/app',
    name: 'app管理',
    component: HomeView,
    meta: { title: 'app管理', roles: ['admin', 'root'] },
    hidden: false,
    myIcon: "iconfont icon-keshifeiguanli",
    children: [
      { path: '/home/app/appUpload', name: 'appUpload', meta: { title: 'app上传' }, component: () => import('@/views/app/appUpload.vue'), myIcon: "iconfont icon-jisuanguize" },
      { path: '/home/app/appList', name: 'appList', meta: { title: '历史更新' }, component: () => import('@/views/app/appList.vue'), myIcon: "iconfont icon-baojiadan" },
    ]
  }
]

//动态路由
export const asyncRoutes = [

]

const router = new VueRouter({
  mode: 'history',
  base: '/',
  routes: constantRoutes,
  scrollBehavior(to, from, savedPosition) {
    // 返回一个 Promise 延迟执行
    return new Promise((resolve) => {
      setTimeout(() => {
        if (savedPosition) {
          resolve(savedPosition);
        } else {
          // 强制滚动所有可能的容器
          const containers = [
            '.content-container',
            '.main-container',
            '.clan-dashboard'
          ];

          containers.forEach(selector => {
            const el = document.querySelector(selector);
            if (el) {
              el.scrollTop = 0;
            }
          });

          // 也滚动 window
          window.scrollTo(0, 0);

          resolve({ x: 0, y: 0 });
        }
      }, 100);
    });
  }
})

const whiteList = ["/login", "/register"]; //无需令牌白名单

router.beforeEach(async (to, from, next) => {
  // 检查用户是否已认证
  const isAuthenticated = store.getters.isAuthenticated;
  if (isAuthenticated) {
    //已登录
    if (to.path === "/login" || to.path === "/") {
      //若以登录没有必要显示登录页，重定向回首页
      next({ path: "/admin/dashboard" });
    } else {
      // 检查路由是否需要特定角色
      if (to.meta.roles) {
        const userRoles = store.state.login_user.roles || [];
        const hasPermission = to.meta.roles.some(role => userRoles.includes(role));

        if (hasPermission) {
          next();
        } else {
          // 没有权限，跳转到无权限页面或首页
          next({ path: "/admin/dashboard" });
        }
      } else {
        // 不需要特定角色的路由，直接放行
        next();
      }
    }
  } else {
    // 未登录
    if (whiteList.includes(to.path)) {
      // 白名单中的路由直接放行
      next();
    } else if (to.meta.requiresAuth) {
      // 需要认证的路由，重定向到登录页
      next({
        path: "/login",
        query: { redirect: to.fullPath } // 保存目标路径，登录后跳转
      });
    } else {
      // 不需要认证的路由，直接放行
      next();
    }

  }
})

export default router
