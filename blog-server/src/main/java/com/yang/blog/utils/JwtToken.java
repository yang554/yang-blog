package com.yang.blog.utils;

import org.apache.shiro.authc.AuthenticationToken;

public class JwtToken implements AuthenticationToken {
    private final String token; // 存储JWT字符串

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token; // 通常返回Token本身
    }

    @Override
    public Object getCredentials() {
        return token; // 凭证即为Token本身
    }

    public String getToken() {
        return token;
    }
}
