package com.yang.blog.config.shiro;

import com.yang.blog.utils.JwtToken;
import com.yang.blog.utils.JwtUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtFilter extends AccessControlFilter {

    @Autowired
    private JwtUtil jwtUtil;

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        // 对于 OPTIONS 请求直接放行（CORS 预检请求）
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        return false; // 交由 onAccessDenied 处理
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 获取Token
        String token = getTokenFromRequest(httpRequest);

        if (StringUtils.isEmpty(token)) {
            // 尝试自动登录（如果有记住我Cookie）
            if (tryAutoLogin(httpRequest, httpResponse)) {
                return true;
            }
            sendUnauthorized(httpResponse, "请先登录");
            return false;
        }

        try {
            // 验证Token
            if (!isValidToken(token)) {
                // 尝试刷新Token
                if (!tryRefreshToken(token, httpRequest, httpResponse)) {
                    sendUnauthorized(httpResponse, "Token无效或已过期");
                    return false;
                }
                return true;
            }

            // Shiro认证
            JwtToken jwtToken = new JwtToken(token);

            // 获取 Subject
            Subject subject = getSubject(request, response);
            // 禁用 Session 创建
            subject.getSession(false);
            // 执行登录认证
            subject.login(jwtToken);
//            getSubject(request, response).login(jwtToken);
            return true;

        } catch (AuthenticationException e) {
            sendUnauthorized(httpResponse, "认证失败: " + e.getMessage());
            return false;
        } catch (Exception e) {
            sendUnauthorized(httpResponse, "系统异常: " + e.getMessage());
            return false;
        }

    }

    /**
     * 从请求中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 1. 从Authorization头获取
        String authHeader = request.getHeader(TOKEN_HEADER);
        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            return authHeader.substring(TOKEN_PREFIX.length());
        }

        // 2. 从参数获取
        String tokenParam = request.getParameter("token");
        if (tokenParam != null) {
            return tokenParam;
        }

        // 3. 从 Cookie 获取
        javax.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (javax.servlet.http.Cookie cookie : cookies) {
                if ("remember_me_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    /**
     * 验证Token有效性
     */
    private boolean isValidToken(String token) {
        try {
            String username = jwtUtil.getUsernameFromToken(token);
            String tokenType = jwtUtil.getTokenType(token);

            if (username == null || tokenType == null) {
                return false;
            }

            // 检查Token是否过期
            if (jwtUtil.isTokenExpired(token)) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 尝试自动登录
     */
    private boolean tryAutoLogin(HttpServletRequest request,
                                 HttpServletResponse response) {
        try {
            // 检查是否有记住我Cookie
            javax.servlet.http.Cookie[] cookies = request.getCookies();
            String rememberMeToken = null;

            if (cookies != null) {
                for (javax.servlet.http.Cookie cookie : cookies) {
                    if ("remember_me_token".equals(cookie.getName())) {
                        rememberMeToken = cookie.getValue();
                        break;
                    }
                }
            }

            if (rememberMeToken != null && isValidToken(rememberMeToken)) {
                // TODO: 这里可以调用自动登录接口
                return true;
            }
        } catch (Exception e) {
            // 忽略自动登录异常
        }
        return false;
    }

    /**
     * 尝试刷新Token
     */
    private boolean tryRefreshToken(String oldToken,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
        // TODO: 实现Token刷新逻辑
        // 可以调用/refreshToken接口获取新Token
        return false;
    }

    private void sendUnauthorized(ServletResponse response, String message) throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setStatus(401);
        httpResponse.setContentType("application/json;charset=UTF-8");
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.getWriter().write("{\"code\":401, \"msg\":\"" + message + "\"}");
    }
}
