package com.yang.blog.config.shiro;
import io.micrometer.core.instrument.util.StringUtils;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomShiroAuthcFilter extends FormAuthenticationFilter {
    /**
     * 当访问被拒绝时（未认证），根据请求类型返回 401 或重定向
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 统一设置响应头
        httpResponse.setContentType("application/json; charset=utf-8");

        if (isAjaxRequest(httpRequest)) {
            // AJAX请求返回JSON
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write("{\"code\": 401, \"msg\": \"会话已过期，请重新登录\", \"data\": null}");
        } else {
            // 普通请求重定向到登录页
            httpResponse.setStatus(HttpServletResponse.SC_FOUND);
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/user/login");
        }
        return false;
    }

    /**
     * 判断是否为AJAX请求
     */
    private boolean isAjaxRequest(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        String requestedWith = request.getHeader("X-Requested-With");
        String contentType = request.getHeader("Content-Type");

        return (StringUtils.isNotBlank(requestedWith) &&
                "XMLHttpRequest".equalsIgnoreCase(requestedWith)) ||
                (StringUtils.isNotBlank(accept) &&
                        accept.contains("application/json")) ||
                (StringUtils.isNotBlank(contentType) &&
                        contentType.contains("application/json"));
    }
}
