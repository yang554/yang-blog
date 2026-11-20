package com.yang.blog.config.shiro;

import com.yang.blog.controller.SysUserController;
import com.yang.blog.entity.RoleListEntity;
import com.yang.blog.entity.SysRoleEntity;
import com.yang.blog.entity.SysUserEntity;
import com.yang.blog.service.SysUserService;
import com.yang.blog.utils.JwtToken;
import com.yang.blog.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class CustomRealm extends AuthorizingRealm {
    @Autowired
    private SysUserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 支持 JWT Token 和 UsernamePasswordToken
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken || token instanceof UsernamePasswordToken;
    }

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("======授权=======");
        String username;
        // 从 principals 中获取用户名（可能来自 JWT 或 Session）
        if (principals.getPrimaryPrincipal() instanceof String) {
            username = (String) principals.getPrimaryPrincipal();
        } else {
            return null;
        }

        SysUserEntity user = userService.selectRolesByUsername(username);
        if (user == null || CollectionUtils.isEmpty(user.getRoles())) {
            return null;
        }

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

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        log.info("======开始用户认证=======");

        if (token instanceof JwtToken) {
            // JWT Token 认证
            return authenticateWithJwt((JwtToken) token);
        } else if (token instanceof UsernamePasswordToken) {
            // 用户名密码认证
            return authenticateWithUsernamePassword((UsernamePasswordToken) token);
        }

        throw new AuthenticationException("不支持的认证方式");
    }

    /**
     * JWT Token 认证
     */
    private AuthenticationInfo authenticateWithJwt(JwtToken jwtToken) {
        String token = jwtToken.getToken();
        log.info("JWT Token 认证: " + token.substring(0, Math.min(token.length(), 20)) + "...");

        try {
            // 验证 JWT Token
            String username = jwtUtil.getUsernameFromToken(token);
            if (username == null) {
                throw new AuthenticationException("Token无效");
            }

            if (jwtUtil.isTokenExpired(token)) {
                throw new ExpiredCredentialsException("Token已过期");
            }

            // 获取用户信息
            SysUserEntity user = userService.selectRolesByUsername(username);
            if (user == null) {
                throw new UnknownAccountException("用户不存在");
            }

            log.info("JWT认证成功: " + username);
            return new SimpleAuthenticationInfo(
                    username,  // principal
                    token,     // credentials
                    getName()  // realm name
            );

        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("JWT认证异常: " + e.getMessage());
            throw new AuthenticationException("JWT认证失败", e);
        }
    }

    /**
     * 用户名密码认证
     */
    private AuthenticationInfo authenticateWithUsernamePassword(UsernamePasswordToken upToken) {
        String username = upToken.getUsername();
        String password = new String(upToken.getPassword());

        log.info("用户名密码认证: " + username);

        try {
            if (username == null || username.trim().isEmpty()) {
                log.warn("用户名为空");
                throw new UnknownAccountException("用户名不能为空");
            }

            SysUserEntity user = userService.selectRolesByUsername(username);
            if (user == null) {
                log.warn("用户不存在: " + username);
                throw new UnknownAccountException("用户不存在");
            }

            // 验证密码（这里需要你的密码验证逻辑）
            // 注意：你的 MyByteSource 使用了盐值
            if (!user.getUsername().equals(username)) {
                log.warn("用户名不匹配");
                throw new IncorrectCredentialsException("用户名或密码错误");
            }

            log.info("用户认证成功: " + user.getUsername());
            return new SimpleAuthenticationInfo(
                    username,
                    user.getPassword(),
                    new MyByteSource(user.getSalt()),
                    this.getName()
            );

        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("用户认证异常: " + e.getMessage());
            throw new AuthenticationException("系统异常，认证失败", e);
        }
    }

}



