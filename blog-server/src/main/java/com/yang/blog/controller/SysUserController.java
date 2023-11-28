package com.yang.blog.controller;

import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import com.wf.captcha.utils.CaptchaUtil;
import com.yang.blog.entity.SysRoleEntity;
import com.yang.blog.entity.SysUserEntity;
import com.yang.blog.service.SysUserService;
import com.yang.blog.utils.RespBean;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
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
        //1、用户名密码登录
        String username = params.get("username");
        String password = params.get("password");
        String verify_code = params.get("verify_code");
        if(StringUtils.hasText(username) && StringUtils.hasText(password)){
            try {
                //比较验证码
                String codes = (String) session.getAttribute("verify_code");
                if (codes.equalsIgnoreCase(verify_code)){
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
    //根据账号查找用户
    @PostMapping("findUser")
    public RespBean findUser(@RequestBody HashMap<String,String> params){
        String username = params.get("username").trim();
        boolean isTrue = userService.findUser(username);
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
        String description = params.get("description").trim();
        String role = params.get("role").trim();

        userEntity.setUsername(username);
        userEntity.setPassword(password);
        userEntity.setNickname(nickname);
        userEntity.setAvatar(avatar);
        userEntity.setEmail(email);
        userEntity.setPhone(phone);
        userEntity.setAddress(address);
        userEntity.setDescription(description);
        userEntity.setEtx01(role);


        return userService.addUser(userEntity);
    }
    //获取验证码
    @RequestMapping("/getCaptcha")
    public void getCaptchaImg(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        SpecCaptcha specCaptcha = new SpecCaptcha();
        specCaptcha.setLen(4);
        specCaptcha.setHeight(58);
        specCaptcha.setCharType(Captcha.TYPE_ONLY_NUMBER);
        String text = specCaptcha.text();
        log.info(" 4C5V67B8N9M,0-.12+6541："+text);
        session.setAttribute("verify_code",text);
        CaptchaUtil.out(specCaptcha,request,response);
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
    //根据用户名查询用户信息
    @GetMapping("/selectUserByName")
    public RespBean selectUserByName(@RequestParam("name") String name){
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
}
