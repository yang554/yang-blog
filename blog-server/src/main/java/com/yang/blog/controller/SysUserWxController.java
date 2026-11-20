package com.yang.blog.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yang.blog.entity.SysUserWxEntity;
import com.yang.blog.service.SysUserWxService;
import com.yang.blog.utils.JwtUtil;
import com.yang.blog.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/wx/user")
public class SysUserWxController {
    @Autowired
    private SysUserWxService wxService;

    @Autowired
    private RestTemplate restTemplate;

    //微信登录
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> params) {
        String code = params.get("code");

        // 1. 调用微信接口换取 openid
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("appid", "wx4424cd9a76720106");
        queryParams.put("secret", "2b36c1d1ac24a25a50f42ee42e91efbf");
        queryParams.put("js_code", code);
        queryParams.put("grant_type", "authorization_code");
        Map<String, Object> res = new HashMap<>();
        try{
            ResponseEntity<String> response = restTemplate.getForEntity(
                    url + "?appid={appid}&secret={secret}&js_code={js_code}&grant_type={grant_type}",
                    String.class, queryParams
            );
            // 2. 解析响应
            JSONObject wxData = JSON.parseObject(response.getBody());
            if (wxData.containsKey("errcode")) {
//            throw new RuntimeException("微信登录失败: " + wxData.getString("errmsg"));
                if (wxData.get("errcode") == "40163") {
                    res.put("code","40163");
                    res.put("msg","code无效，请重新登录");
                    // 返回特定错误码，提示前端重新获取 code
                }else{
                    res.put("code",wxData.get("errcode"));
                    res.put("msg",wxData.getString("errmsg"));
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(res);

            }else{
                String openid = wxData.getString("openid");
                String sessionKey = wxData.getString("session_key");

                SysUserWxEntity user = wxService.register(openid);
                // 3. 生成 Token (基于 openid)
                String token = null;
                res.put("code","200");
                res.put("msg","登录成功");
                res.put("token",token);
                res.put("user",user);
                return ResponseEntity.ok(res);
            }
        }catch (Exception e){
            res.put("code","400");
            res.put("msg","服务器异常");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

    }
    //微信用户注册
    @PostMapping("/register")
    public RespBean register(@RequestBody Map<String, String> params){
        String code = params.get("code");
        if(!"".equals(code)){
            return RespBean.ok(wxService.register(code));
        }else {
            return RespBean.error("微信注册失败！");
        }
    }

}
