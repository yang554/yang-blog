package com.yang.blog.controller;

import com.yang.blog.entity.SysUserEntity;
import com.yang.blog.mapper.SysUserMapper;
import com.yang.blog.service.SysUserService;
import com.yang.blog.service.impl.SysUserServiceImpl;
import com.yang.blog.utils.RespBean;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/user")
public class SysUserController {
    Logger log = Logger.getLogger(String.valueOf(SysUserController.class));
    @Autowired
    private SysUserService userService;
    //获取所有用户信息
    @GetMapping("/all")
    public List<SysUserEntity> findAll(){
        return userService.findAll();
    }
    //博主登录
    @PostMapping("/login")
    public RespBean login(@RequestBody HashMap<String,String> params, HttpSession session) {
        System.out.println(params);
        //1、用户名密码登录
        String username = params.get("username");
        String password = params.get("password");
//        String verify_code = params.get("verify_code");
        if(StringUtils.hasText(username) && StringUtils.hasText(password)){
            try {
                //比较验证码
                String codes = (String) session.getAttribute("verify_code");
                log.info("session中保存的验证码："+codes);
//                log.info("用户输入的验证码："+verify_code);
//codes.equalsIgnoreCase(verify_code)
                if (true){
                    //获取主体对象
                    Subject subject = SecurityUtils.getSubject();

                    UsernamePasswordToken token = new UsernamePasswordToken(username, password);
                    subject.login(token);

                    //用户登录成功，将tUser返回给前端
                    SysUserEntity tUser = userService.selectRolesByUsername((String) subject.getPrincipal());

                    //将登陆用户存到session中
                    session.setAttribute("login_user",tUser);

                    return RespBean.ok("登录成功！",tUser);
                }else{
                    log.info("验证码错误!");
                    return RespBean.warn("验证码错误！");
                }
            } catch (UnknownAccountException e) {
                e.printStackTrace();
                return RespBean.error("用户名错误！");
            } catch (IncorrectCredentialsException e) {
                e.printStackTrace();
                System.out.println("密码错误!");
                return RespBean.error("密码错误！");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
                return RespBean.error(e.getMessage());
            }
        }else{
            return RespBean.error("请检查是否正确输入");
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
}
