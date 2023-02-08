package com.yang.blog.config.shiro;

import com.yang.blog.controller.SysUserController;
import com.yang.blog.entity.RoleListEntity;
import com.yang.blog.entity.SysRoleEntity;
import com.yang.blog.entity.SysUserEntity;
import com.yang.blog.service.SysUserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;


public class CustomRealm extends AuthorizingRealm {
    Logger log = Logger.getLogger(String.valueOf(SysUserController.class));
    @Autowired
    private SysUserService userService;

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("======授权=======");

        String principal = (String) principals.getPrimaryPrincipal();
        SysUserEntity user = userService.selectRolesByUsername(principal);

        if(!CollectionUtils.isEmpty(user.getRoles())){
            SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
            //1、给认证用户添加角色信息
            for (SysRoleEntity role : user.getRoles()) {
                authorizationInfo.addRole(role.getName());
                //2、给认证用户添加权限信息
                List<RoleListEntity> permsList = userService.findPermsListByRoleId(role.getId());
                if(!CollectionUtils.isEmpty(permsList) && permsList.get(0) != null){
                    for (RoleListEntity perms : permsList) {
                        authorizationInfo.addStringPermission(perms.getName());
                    }
                }
            }
            return authorizationInfo;
        }
        return null;
    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("======认证=======");

        //获取用户名
        String principal = (String) token.getPrincipal();
        log.info("====用户名===="+principal);

        //假设是从数据库获得的 用户名，密码
        SysUserEntity tUser = userService.selectRolesByUsername(principal);
        log.info("====认证的tUser===="+tUser);

        if (tUser.getUsername().equals(principal)){
            log.info("====是否成功===="+tUser.getUsername()+"======"+tUser.getPassword()+"================"+new MyByteSource(tUser.getSalt())+"=========="+this.getName());

            SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(
                    principal,
                    tUser.getPassword(),
                    new MyByteSource(tUser.getSalt()),
                    this.getName());
            return simpleAuthenticationInfo;
        }
        return null;
    }
}
