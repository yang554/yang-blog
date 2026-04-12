package com.yang.blog.config.shiro;

import com.yang.blog.entity.SysUserWxEntity;
import com.yang.blog.service.SysUserWxService;
import com.yang.blog.utils.JwtToken;
import com.yang.blog.utils.JwtUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;


public class WxRealm extends AuthorizingRealm {

    // 支持JWT Token
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }
    @Autowired
    private SysUserWxService wxService;

    @Autowired
    private JwtUtil jwtUtil;

    // 认证（登录验证）
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("======微信登录认证=======");
        String jwtToken = (String) token.getPrincipal();
        String openid = jwtUtil.getTokenType(jwtToken);
        if (wxService == null){
            System.out.println("service是空的");
        }else{
            // 验证用户是否存在
            SysUserWxEntity user = wxService.findByOpenid(openid);
            if (user == null) {
                throw new UnknownAccountException("用户不存在");
            }
        }
        System.out.println(getName());
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(jwtToken, jwtToken, getName());
        return simpleAuthenticationInfo;
    }

    // 授权（权限验证）
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String openid = jwtUtil.getTokenType(principals.toString());
        // 验证用户是否存在
        SysUserWxEntity user = wxService.findByOpenid(openid);

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        //1、给认证用户添加角色信息

        return info;
    }
}
