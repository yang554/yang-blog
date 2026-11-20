// 权限验证工具

/**
 * 检查路由权限
 * @param {Object} route - 路由对象
 * @param {Array} userRoles - 用户角色数组
 * @returns {boolean} 是否有权限
 */
export function checkRoutePermission(route, userRoles = []) {
    // 如果没有meta或没有roles，默认允许访问
    if (!route.meta || !route.meta.roles) {
      return true
    }
    
    // 如果用户没有角色，拒绝访问
    if (!userRoles || userRoles.length === 0) {
      return false
    }
    
    // 检查用户是否有其中一个角色
    return route.meta.roles.some(requiredRole => 
      userRoles.some(userRole => userRole === requiredRole)
    )
  }
  
  /**
   * 过滤有权限的路由
   * @param {Array} routes - 路由数组
   * @param {Array} userRoles - 用户角色数组
   * @returns {Array} 过滤后的路由
   */
  export function filterRoutes(routes, userRoles = []) {
    const result = []
    
    routes.forEach(route => {
      // 检查当前路由是否有权限
      const hasPermission = checkRoutePermission(route, userRoles)
      
      if (hasPermission) {
        // 创建路由副本
        const routeCopy = { ...route }
        
        // 如果有子路由，递归过滤
        if (route.children && route.children.length > 0) {
          routeCopy.children = filterRoutes(route.children, userRoles)
          
          // 如果过滤后子路由为空，且当前路由有redirect，保留
          if (routeCopy.children.length === 0 && !routeCopy.redirect) {
            return // 跳过这个路由
          }
        }
        
        result.push(routeCopy)
      }
    })
    
    return result
  }
  
  /**
   * 获取扁平化的菜单项
   * @param {Array} routes - 路由数组
   * @returns {Array} 扁平化的菜单项
   */
  export function getFlatMenuItems(routes) {
    const items = []
    
    function traverse(routesList) {
      routesList.forEach(route => {
        if (!route.hidden && route.meta && route.meta.title) {
          items.push({
            path: route.path,
            title: route.meta.title,
            icon: route.myIcon || route.meta.icon,
            children: []
          })
        }
        
        if (route.children && route.children.length > 0) {
          traverse(route.children)
        }
      })
    }
    
    traverse(routes)
    return items
  }
  
  export default {
    checkRoutePermission,
    filterRoutes,
    getFlatMenuItems
  }