package com.yang.blog.controller;

import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import com.wf.captcha.utils.CaptchaUtil;
import com.yang.blog.entity.SysRoleEntity;
import com.yang.blog.entity.SysUserEntity;
import com.yang.blog.service.SysUserService;
import com.yang.blog.utils.JwtUtil;
import com.yang.blog.utils.RedisUtils;
import com.yang.blog.utils.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@RestController
@RequestMapping("/user")
@Slf4j
public class SysUserController {
    @Autowired
    private SysUserService userService;
    @Autowired
    private RedisUtils redisUtils ;
    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.token-header:Authorization}")
    private String tokenHeader;

    @Value("${jwt.token-prefix:Bearer }")
    private String tokenPrefix;

    // 会话超时时间（分钟）
    private static final long SESSION_TIMEOUT = 30;
    // 验证码超时时间（分钟）
    private static final long CAPTCHA_TIMEOUT = 5;
    // 记住我超时时间（天）
    private static final long REMEMBER_ME_TIMEOUT = 30;

    //获取所有用户信息
    @GetMapping("/all")
    public List<SysUserEntity> findAll(){
        return userService.findAll();
    }

    /**
     * 存储记住我Token到Redis
     */
    private void storeRememberMeToken(SysUserEntity user, String rememberMeToken,
                                      HttpServletRequest request) {
        try {
            String tokenKey = "remember_me:" + rememberMeToken;
            HashMap<String, Object> tokenInfo = new HashMap<>();
            tokenInfo.put("userId", user.getId());
            tokenInfo.put("username", user.getUsername());
            tokenInfo.put("loginTime", System.currentTimeMillis());
            tokenInfo.put("ip", request.getRemoteAddr());
            tokenInfo.put("userAgent", request.getHeader("User-Agent"));

            // 存储90天（与JWT配置一致）
            redisUtils.set(tokenKey, tokenInfo, Long.valueOf(90), TimeUnit.DAYS);

            // 建立用户名到Token的映射（用于清理）
            String userTokenKey = "user_remember_me:" + user.getUsername();
            redisUtils.set(userTokenKey, rememberMeToken);
            redisUtils.expire(userTokenKey, 90, TimeUnit.DAYS);

        } catch (Exception e) {
            log.error("存储记住我Token失败: " + e.getMessage());
        }
    }

    /**
     * 清理用户的记住我Token
     */
    private void clearUserRememberMeTokens(String username) {
        try {
            String userTokenKey = "user_remember_me:" + username;
            Object tokens = redisUtils.get(userTokenKey);
            if (tokens != null) {
                redisUtils.del("remember_me:" + tokens.toString());
            }
            redisUtils.del(userTokenKey);
        } catch (Exception e) {
            log.error("清理记住我Token失败: " + e.getMessage());
        }
    }

    /**
     * 验证记住我Token
     */
    private RespBean validateRememberMeToken(String token) {
        try {
            String tokenKey = "remember_me:" + token;
            Object tokenInfo = redisUtils.get(tokenKey);

            if (tokenInfo == null) {
                return RespBean.error("记住我Token已失效");
            }

            // 验证JWT Token是否有效
            String username = jwtUtil.getUsernameFromToken(token);
            String tokenType = jwtUtil.getTokenType(token);

            if (username == null || !"REMEMBER_ME".equals(tokenType)) {
                return RespBean.error("无效的记住我Token");
            }

            if (jwtUtil.isTokenExpired(token)) {
                redisUtils.del(tokenKey);
                return RespBean.error("记住我Token已过期");
            }

            return RespBean.ok("Token验证成功", tokenInfo);
        } catch (Exception e) {
            log.error("验证记住我Token异常: " + e.getMessage());
            return RespBean.error("Token验证失败");
        }
    }

    /**
     * 参数验证
     */
    private boolean validateLoginParams(String username, String password, String verifyCode, String captchaKey) {
        if ("".equals(username) || "".equals(password)) {
            return false;
        }
        if ("".equals(verifyCode) || "".equals(captchaKey)) {
            return false;
        }
        // 简单的SQL注入防护
        if (username.contains("'") || username.contains("\"") || username.contains(";")) {
            return false;
        }
        return true;
    }

    /**
     * 验证码校验
     */
    private RespBean validateCaptcha(String captchaKey, String verifyCode) {
        try {
            String storedCode = (String) redisUtils.get("captcha:" + captchaKey);
            if (storedCode == null) {
                return RespBean.warn("验证码已过期，请刷新");
            }
            if (!storedCode.equalsIgnoreCase(verifyCode)) {
                return RespBean.warn("验证码错误");
            }
            return RespBean.ok("验证码正确");
        } catch (Exception e) {
            log.error("验证码校验异常: "+ e.getMessage());
            return RespBean.error("验证码校验失败");
        }
    }

    @PostMapping("/login")
    public RespBean login(@RequestBody HashMap<String,String> params, HttpServletRequest request,
                          HttpServletResponse response) {
        try {
            String username = params.get("username");
            String password = params.get("password");
            String verifyCode = params.get("verify_code");
            String captchaKey = params.get("captcha_key");
            boolean rememberMe = "true".equals(params.get("rememberMe"));

            if (!validateLoginParams(username, password, verifyCode, captchaKey)) {
                return RespBean.error("参数不完整，请检查输入");
            }

            // 验证码校验
            RespBean captchaResult = validateCaptcha(captchaKey, verifyCode);
            if (captchaResult.getStatus() != 200) {
                return captchaResult;
            }

            // 先清理可能存在的 Session
            SecurityUtils.getSubject().logout();

            // Shiro认证
            Subject subject = SecurityUtils.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);

            // 设置Shiro的记住我
            token.setRememberMe(rememberMe);
            // 执行登录
            subject.login(token);

            // 获取用户信息
            SysUserEntity user = userService.selectRolesByUsername(username);
            if (user == null) {
                return RespBean.error("用户信息获取失败");
            }

            // 生成不同时效的Token
            String accessToken = jwtUtil.generateAccessToken(username);
            String refreshToken = jwtUtil.generateRefreshToken(username);
            String rememberMeToken = rememberMe ? jwtUtil.generateRememberMeToken(username) : null;


            // 存储登录状态到Redis
            String userKey = "user:login:" + username;
            HashMap<String, Object> loginInfo = new HashMap<>();
            loginInfo.put("userId", user.getId());
            loginInfo.put("username", username);
            loginInfo.put("loginTime", System.currentTimeMillis());
            loginInfo.put("accessToken", accessToken);
            loginInfo.put("refreshToken", refreshToken);
            loginInfo.put("rememberMe", rememberMe);

            // 根据是否记住我设置不同的过期时间
            if (rememberMe) {
                loginInfo.put("rememberMeToken", rememberMeToken);
                redisUtils.set(userKey, loginInfo, REMEMBER_ME_TIMEOUT, TimeUnit.DAYS);

                // 存储记住我Token
                storeRememberMeToken(user, rememberMeToken, request);

                // 设置记住我Cookie（HttpOnly, Secure）
                Cookie rememberMeCookie = new Cookie("remember_me_token", rememberMeToken);
                rememberMeCookie.setHttpOnly(true);
                rememberMeCookie.setSecure(request.isSecure()); // 生产环境应为true
                rememberMeCookie.setPath("/");
                rememberMeCookie.setMaxAge(90 * 24 * 60 * 60); // 90天
                response.addCookie(rememberMeCookie);
            } else {
                redisUtils.set(userKey, loginInfo, SESSION_TIMEOUT, TimeUnit.MINUTES);
            }

            // 存储Token映射
            redisUtils.set("token:access:" + accessToken, username, (long)30, TimeUnit.MINUTES);
            redisUtils.set("token:refresh:" + refreshToken, username, (long)30, TimeUnit.DAYS);

            // 清理验证码
            redisUtils.del("captcha:" + captchaKey);

            // 返回登录结果
            HashMap<String, Object> result = new HashMap<>();
            result.put("user", user);
            result.put("accessToken", accessToken);
            result.put("refreshToken", refreshToken);
            result.put("rememberMeToken", rememberMeToken);
            result.put("expireTime", System.currentTimeMillis() + SESSION_TIMEOUT * 60 * 1000);
            result.put("rememberMe", rememberMe);

            log.info("用户登录成功: " + username + (rememberMe ? " [记住我]" : ""));
            return RespBean.ok("登录成功", result);

        } catch (UnknownAccountException e) {
            log.warn("登录失败 - 用户不存在: " + params.get("username"));
            return RespBean.error("用户名或密码错误");
        } catch (IncorrectCredentialsException e) {
            log.warn("登录失败 - 密码错误: " + params.get("username"));
            return RespBean.error("用户名或密码错误");
        } catch (Exception e) {
            log.error("登录异常: " + e.getMessage());
            e.printStackTrace();
            return RespBean.error("登录失败: " + e.getMessage());
        }
    }

    /**
     * 自动登录（通过记住我Token）
     */
    @PostMapping("/autoLogin")
    public RespBean autoLogin(HttpServletRequest request,
                              HttpServletResponse response) {
        try {
            // 从Cookie中获取记住我Token
            Cookie[] cookies = request.getCookies();
            String rememberMeToken = null;

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("remember_me_token".equals(cookie.getName())) {
                        rememberMeToken = cookie.getValue();
                        break;
                    }
                }
            }

            if (rememberMeToken == null) {
                return RespBean.error("未找到记住我Token");
            }

            // 验证Token
            RespBean tokenResult = validateRememberMeToken(rememberMeToken);
            if (tokenResult.getStatus() != 200) {
                // 清除无效的Cookie
                Cookie cookie = new Cookie("remember_me_token", null);
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
                return tokenResult;
            }

            // 获取用户信息
            HashMap<String, Object> tokenInfo = (HashMap<String, Object>) tokenResult.getData();
            String username = (String) tokenInfo.get("username");

            SysUserEntity user = userService.selectRolesByUsername(username);
            if (user == null) {
                return RespBean.error("用户不存在");
            }

            // 生成新的访问Token
            String newAccessToken = jwtUtil.generateAccessToken(username);
            String newRefreshToken = jwtUtil.generateRefreshToken(username);

            // 更新Redis中的登录状态
            String userKey = "user:login:" + username;
            HashMap<String, Object> loginInfo = new HashMap<>();
            loginInfo.put("userId", user.getId());
            loginInfo.put("username", username);
            loginInfo.put("loginTime", System.currentTimeMillis());
            loginInfo.put("accessToken", newAccessToken);
            loginInfo.put("refreshToken", newRefreshToken);
            loginInfo.put("rememberMe", true);
            loginInfo.put("rememberMeToken", rememberMeToken);

            redisUtils.set(userKey, loginInfo, REMEMBER_ME_TIMEOUT, TimeUnit.DAYS);

            // 更新Token映射
            redisUtils.set("token:access:" + newAccessToken, username, (long)(30), TimeUnit.MINUTES);
            redisUtils.set("token:refresh:" + newRefreshToken, username, (long) 30, TimeUnit.DAYS);

            // 更新记住我Token的最后使用时间
            tokenInfo.put("lastUsed", System.currentTimeMillis());
            redisUtils.set("remember_me:" + rememberMeToken, tokenInfo, (long)90, TimeUnit.DAYS);

            // 返回结果
            HashMap<String, Object> result = new HashMap<>();
            result.put("user", user);
            result.put("accessToken", newAccessToken);
            result.put("refreshToken", newRefreshToken);
            result.put("expireTime", System.currentTimeMillis() + SESSION_TIMEOUT * 60 * 1000);

            log.info("自动登录成功: " + username);
            return RespBean.ok("自动登录成功", result);

        } catch (Exception e) {
            log.error("自动登录异常: " + e.getMessage());
            return RespBean.error("自动登录失败");
        }
    }

    //用户注册
    @PostMapping("/register")
    public RespBean register(@RequestBody HashMap<String,String> params){
        String username = params.get("username").trim();
        String password = params.get("password").trim();
        SysUserEntity userEntity = new SysUserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword(password);
        try {
            return userService.register(userEntity);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("创建失败",e.getMessage());
        }
    }
    //根据身份证号和账户名查找用户
    @PostMapping("findUser")
    public RespBean findUser(@RequestBody HashMap<String,String> params){
        String ext02 = params.get("ext02").trim();
        String userName = params.get("username").trim();
        boolean isTrue = userService.findUser(ext02,userName);
        if(isTrue){
            return RespBean.error("error",isTrue);
        }else {
            return RespBean.ok("ok",isTrue);
        }
    }
    //添加用户
    @PostMapping("/addUser")
    public RespBean addUser(@RequestBody HashMap<String,String> params){
        SysUserEntity userEntity = new SysUserEntity();
        String username = params.get("username").trim();
        String password = params.get("password").trim();
        String nickname = params.get("nickname").trim();
        String avatar = params.get("avatar").trim();
        String email = params.get("email").trim();
        String phone = params.get("phone").trim();
        String address = params.get("address").trim();
        String description = params.get("role").trim();
        String note = params.get("note").trim();
        String ext01 = params.get("ext01").trim();
        String ext02 = params.get("ext02").trim();

        userEntity.setUsername(username);
        userEntity.setPassword(password);
        userEntity.setNickname(nickname);
        userEntity.setAvatar(avatar);
        userEntity.setEmail(email);
        userEntity.setPhone(phone);
        userEntity.setAddress(address);
        userEntity.setDescription(description);
        userEntity.setNote(note);
        userEntity.setExt01(ext01);
        userEntity.setExt02(ext02);


        return userService.addUser(userEntity);
    }

    /**
     * 获取验证码
     */
    @GetMapping(value = "/getCaptcha", produces = MediaType.IMAGE_GIF_VALUE)
    public void getCaptchaImg(HttpServletRequest request,
                              HttpServletResponse response,
                              @RequestParam(required = false) String refresh) throws IOException {

        // 设置响应头，禁用缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setDateHeader("Expires", 0);
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setContentType("image/gif");

        try {
            // 生成验证码
            SpecCaptcha captcha = new SpecCaptcha(130, 48, 4);
            captcha.setCharType(Captcha.TYPE_ONLY_NUMBER);
            String text = captcha.text();

            // 生成验证码key
            String captchaKey;
            if ("true".equals(refresh) || request.getSession().getAttribute("captcha_key") == null) {
                captchaKey = UUID.randomUUID().toString();
                request.getSession().setAttribute("captcha_key", captchaKey);
            } else {
                captchaKey = (String) request.getSession().getAttribute("captcha_key");
            }

            // 存储到Redis
            redisUtils.set("captcha:" + captchaKey, text, CAPTCHA_TIMEOUT, TimeUnit.MINUTES);

            // 通过响应头返回captcha_key
            response.setHeader("X-Captcha-Key", captchaKey);
            response.setHeader("X-Captcha-Expire", String.valueOf(CAPTCHA_TIMEOUT * 60));

            // 输出图片
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                captcha.out(os);
                response.getOutputStream().write(os.toByteArray());
                response.getOutputStream().flush();
            }

            log.info("生成验证码: {"+text+"}, key: {"+captchaKey+"}");


        } catch (Exception e) {
            log.warn("生成验证码异常: {}"+ e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    //获取所有权限
    @GetMapping("/getRoleAll")
    public RespBean getRoleAll(){
        List<SysRoleEntity> roleList = userService.getRoleAll();
        if(roleList == null){
            return RespBean.ok("error");
        }else{
            return RespBean.ok("ok",roleList);
        }
    }
    //修改用户信息
    @PostMapping("/editUser")
    public RespBean editUser(@RequestBody HashMap<String,Object> params){
        return userService.editUser(params);
    }
    //修改用户密码
    @PostMapping("/resetUserPwd")
    public RespBean resetUserPwd(@RequestBody HashMap<String,Object> params){
        return userService.resetUserPwd(params);
    }
    //修改头像
    @PostMapping("/saveAvatar")
    public RespBean saveAvatar(@RequestParam String avatar,@RequestParam String id){
        return userService.saveAvatar(avatar,id);
    }
    //删除
    @DeleteMapping("/delUser")
    public RespBean delUser(@RequestParam("id") String id){
        return userService.delUser(id);
    }
    //根据用户名模糊查询用户信息
    @GetMapping("/selectUserByName")
    public RespBean selectUserByName(@RequestParam("name") String name){
        List<SysUserEntity> user = userService.selectByUsername(name);
        if(user != null){
            return RespBean.ok("ok",user);
        }else {
            return RespBean.error("error");
        }
    }

    //根据用户名查询用户信息
    @GetMapping("/getUserByName")
    public RespBean getUserByName(@RequestParam("name") String name){
        SysUserEntity user = userService.selectRolesByUsername(name);
        if(user != null){
            return RespBean.ok("ok",user);
        }else {
            return RespBean.error("error");
        }
    }
    //获取角色对应的用户数量
    @GetMapping("/count/role")
    public RespBean getUserRoleAll(){
        return userService.getUserRoleAll();
    }


    /**
     * 用户注销
     */
    @PostMapping("/logout")
    public RespBean logout(HttpServletRequest request,
                           HttpServletResponse response) {
        try {
            Subject subject = SecurityUtils.getSubject();
            String username = (String) subject.getPrincipal();

            // Shiro注销
            subject.logout();

            // 清理Session
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            // 清理Redis
            if (username != null) {
                // 清理登录状态
                redisUtils.del("user:login:" + username);

                // 清理记住我Token
                clearUserRememberMeTokens(username);
            }

            // 清理Token映射
            String token = request.getHeader(tokenHeader);
            if (token != null && token.startsWith(tokenPrefix)) {
                token = token.substring(tokenPrefix.length());
                redisUtils.del("token:access:" + token);
                redisUtils.del("token:refresh:" + token);
            }

            // 清理Cookie
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("remember_me_token".equals(cookie.getName()) ||
                            "refresh_token".equals(cookie.getName())) {
                        Cookie clearCookie = new Cookie(cookie.getName(), null);
                        clearCookie.setMaxAge(0);
                        clearCookie.setPath("/");
                        response.addCookie(clearCookie);
                    }
                }
            }

            log.info("用户注销成功: " + username);
            return RespBean.ok("注销成功");

        } catch (Exception e) {
            log.error("注销异常: " + e.getMessage());
            return RespBean.error("注销失败");
        }
    }

    /**
     * 刷新Token
     */
    @PostMapping("/refreshToken")
    public RespBean refreshToken(HttpServletRequest request,
                                 HttpServletResponse response) {
        try {
            String refreshToken = null;
            // 尝试从请求头获取
            String authHeader = request.getHeader(tokenHeader);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                refreshToken = authHeader.substring(7);
            }
            // 如果请求头没有，尝试从Cookie获取
            if (refreshToken == null) {
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if ("refresh_token".equals(cookie.getName())) {
                            refreshToken = cookie.getValue();
                            break;
                        }
                    }
                }
            }
            if (refreshToken == null) {
                return RespBean.error("未找到刷新Token");
            }
            // 验证Token类型
            String tokenType = jwtUtil.getTokenType(refreshToken);
            if (!"REFRESH".equals(tokenType) && !"REMEMBER_ME".equals(tokenType)) {
                return RespBean.error("无效的刷新Token类型");
            }

            // 验证Token有效性
            String username = jwtUtil.getUsernameFromToken(refreshToken);
            if (username == null || jwtUtil.isTokenExpired(refreshToken)) {
                return RespBean.error("Token已过期");
            }

            // 验证Redis中是否存在
            String redisKey = "token:refresh:" + refreshToken;
            if (!redisUtils.exists(redisKey)) {
                redisKey = "remember_me:" + refreshToken;
                if (!redisUtils.exists(redisKey)) {
                    return RespBean.error("Token无效");
                }
            }

            // 生成新的访问Token
            String newAccessToken = jwtUtil.generateAccessToken(username);

            // 如果使用的是记住我Token，也刷新它
            if ("REMEMBER_ME".equals(tokenType)) {
                String newRememberMeToken = jwtUtil.refreshToken(refreshToken);

                // 更新Redis中的记住我Token
                Object tokenInfo = redisUtils.get("remember_me:" + refreshToken);
                if (tokenInfo != null) {
                    redisUtils.set("remember_me:" + newRememberMeToken, tokenInfo, (long) 90, TimeUnit.DAYS);
                    redisUtils.del("remember_me:" + refreshToken);

                    // 更新Cookie
                    Cookie cookie = new Cookie("remember_me_token", newRememberMeToken);
                    cookie.setHttpOnly(true);
                    cookie.setSecure(request.isSecure());
                    cookie.setPath("/");
                    cookie.setMaxAge(90 * 24 * 60 * 60);
                    response.addCookie(cookie);

                    refreshToken = newRememberMeToken;
                }
            }

            // 更新Redis中的Token映射
            redisUtils.set("token:access:" + newAccessToken, username, (long) 30, TimeUnit.MINUTES);

            HashMap<String, Object> result = new HashMap<>();
            result.put("accessToken", newAccessToken);
            result.put("refreshToken", refreshToken);
            result.put("expireTime", System.currentTimeMillis() + SESSION_TIMEOUT * 60 * 1000);

            return RespBean.ok("Token刷新成功", result);

        } catch (Exception e) {
            log.error("刷新Token异常: " + e.getMessage());
            return RespBean.error("Token刷新失败");
        }
    }
}
