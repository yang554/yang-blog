package com.yang.blog.config.shiro;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.concurrent.TimeUnit;
import javax.servlet.Filter;

@Configuration
public class ShiroConfig {
    @Bean
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        // 会话超时设置（30分钟）
        sessionManager.setGlobalSessionTimeout(TimeUnit.MINUTES.toMillis(30));
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setSessionValidationInterval(TimeUnit.MINUTES.toMillis(15));
        // 删除无效 Session
        sessionManager.setDeleteInvalidSessions(true);

        // 会话Cookie配置
        SimpleCookie sessionIdCookie = new SimpleCookie("JSESSIONID");
        sessionIdCookie.setHttpOnly(true);
        sessionIdCookie.setPath("/");
        sessionIdCookie.setMaxAge(-1); // 浏览器关闭时失效
        sessionManager.setSessionIdCookie(sessionIdCookie);
        sessionManager.setSessionIdCookieEnabled(true);

        // 防止URL中携带JSESSIONID
        sessionManager.setSessionIdUrlRewritingEnabled(false);

        return sessionManager;
    }

    /**
     * * 设置cookie 记住我生成cookie
     */
    @Bean("rememberMeManager")
    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();
        SimpleCookie rememberMeCookie = new SimpleCookie("rememberMe");
        rememberMeCookie.setHttpOnly(true);
        rememberMeCookie.setMaxAge(259200); // 30天
        rememberMeCookie.setPath("/");

        rememberMeManager.setCookie(rememberMeCookie);
        // 设置加密密钥
        rememberMeManager.setCipherKey(Base64.getDecoder().decode("Z3VucwAAAAAAAAAAAAAAAA=="));

        return rememberMeManager;
    }

    //3.创建自定义realm
    @Bean("userRealm")
    public Realm userRealm() {
        CustomRealm userRealm = new CustomRealm();
        //加盐
        // 密码匹配器
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        credentialsMatcher.setHashAlgorithmName("MD5");
        credentialsMatcher.setHashIterations(1024);
        credentialsMatcher.setStoredCredentialsHexEncoded(true);
        userRealm.setCredentialsMatcher(credentialsMatcher);

        //开启缓存管理器
        userRealm.setCachingEnabled(true);
        userRealm.setAuthenticationCachingEnabled(true);
        userRealm.setAuthorizationCachingEnabled(true);
        userRealm.setAuthenticationCacheName("authenticationCache");
        userRealm.setAuthorizationCacheName("authorizationCache");
        userRealm.setCacheManager(new EhCacheManager());

        return userRealm;
    }

    /**
     * 微信Realm
     */
    @Bean("wxRealm")
    public Realm wxRealm() {
        WxRealm wxRealm = new WxRealm();
        // 可以根据需要为微信Realm配置特定的认证器
        return wxRealm;
    }

    /**
     * 系统自带的Realm管理，主要针对多realm
     */
    @Bean("myModularRealmAuthenticator")
    public ModularRealmAuthenticator modularRealmAuthenticator() {
        //自己重写的ModularRealmAuthenticator
        UserModularRealmAuthenticator modularRealmAuthenticator = new UserModularRealmAuthenticator();
        modularRealmAuthenticator.setAuthenticationStrategy(new AtLeastOneSuccessfulStrategy());
        return modularRealmAuthenticator;
    }

    //创建安全管理器
    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager  = new DefaultWebSecurityManager();

        // 设置多Realm
//        List<Realm> realms = new ArrayList<>();
//        realms.add(userRealm());
//        realms.add(wxRealm());
//        securityManager.setRealms(realms);
        securityManager.setRealm(userRealm()); // 使用单个Realm
        // 设置认证器
//        securityManager.setAuthenticator(modularRealmAuthenticator());

        // 设置会话管理器
        securityManager.setSessionManager(sessionManager());

        // 设置记住我管理器
        securityManager.setRememberMeManager(rememberMeManager());

        return securityManager;
    }

    //创建shiroFilter负责拦截所有请求
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean() {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        //给filter设置安全管理器
        shiroFilter.setSecurityManager(securityManager());

        // 自定义过滤器
        Map<String, Filter> filters = new HashMap<>();
        filters.put("authc", new CustomShiroAuthcFilter());
        filters.put("jwt", new JwtFilter());
        shiroFilter.setFilters(filters);

        // 拦截规则配置（按顺序）
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();

        //静态资源放行
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/css/**", "anon");
        filterChainDefinitionMap.put("/js/**", "anon");
        filterChainDefinitionMap.put("/images/**", "anon");
        filterChainDefinitionMap.put("/favicon.ico", "anon");

        //公开接口放行
        filterChainDefinitionMap.put("/wx/user/login", "anon");
        filterChainDefinitionMap.put("/wx/user/register", "anon");
        filterChainDefinitionMap.put("/user/login", "anon");
        filterChainDefinitionMap.put("/user/register", "anon");
        filterChainDefinitionMap.put("/user/getCaptcha", "anon");
        filterChainDefinitionMap.put("/api/user/getCaptcha", "anon");
        filterChainDefinitionMap.put("/file/**", "anon");

        //API文档放行
        filterChainDefinitionMap.put("/swagger-ui.html", "anon");
        filterChainDefinitionMap.put("/swagger-ui/**", "anon");
        filterChainDefinitionMap.put("/webjars/**", "anon");
        filterChainDefinitionMap.put("/swagger-resources/**", "anon");
        filterChainDefinitionMap.put("/v3/api-docs", "anon");
        filterChainDefinitionMap.put("/v3/api-docs/**", "anon");

        //配置系统受限资源
        filterChainDefinitionMap.put("/api/**", "jwt");
        filterChainDefinitionMap.put("/wx/**", "jwt");

//        filterChainDefinitionMap.put("/api/**", "authc");// Web 页面使用传统 Session 认证

        //默认页面
        filterChainDefinitionMap.put("/", "anon");
        shiroFilter.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return shiroFilter;
    }

}
