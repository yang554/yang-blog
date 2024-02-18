import router from "./router/router";
import store from "./store/store";
 
const whiteList = ["/login"]; //无需令牌白名单
 
router.beforeEach(async (to, from, next) => {
  //to and from are Route Object,next() must be called to resolve the hook
  // 获取令牌判断用户是否登录
  const hasToken = localStorage.getItem("token");
  if (hasToken) {
    //已登录
    if (to.path === "/login") {
      //若以登录没有必要显示登录页，重定向回首页
      next({ path: "/" });
    } else {
      // 去其他路由
      const hasRoles =
        store.state.user.roles && store.state.user.roles.length > 0;
      if (hasRoles) {
        // 若用户角色已付加则说明权限以判定，动态路由已添加
        next();
      } else {
        try {
          // 请求获取用户信息
          const roles = await store.dispatch("user/getInfo");
          console.log(roles);
          // 根据当前用户角色动态生成路由
          const accessRoutes = await store.dispatch(
            "permission/generateRoutes",
            roles
          );
          console.log(accessRoutes);
          // 添加这些至路由器
          router.addRoutes(accessRoutes);
          // 继续路由切换，确保addRoutes完成
          next({ ...to });
        } catch (error) {
          // 出错需要重置令牌（令牌过期，网络错误等原因）
          //await store.dispatch('user/resetToken')
          next(`/login?redirect=${to.path}`);
          alert(error || "未知错误");
        }
      }
    }
  } else {
    //未登录
    if (whiteList.indexOf(to.path) !== -1) {
      // 白名单中的路由路过
      next();
    } else {
      // 重定向至登录页
      next(`/login?redirect=${to.path}`);
    }
  }
});