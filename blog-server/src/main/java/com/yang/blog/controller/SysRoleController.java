package com.yang.blog.controller;

import com.yang.blog.entity.SysRoleEntity;
import com.yang.blog.service.SysRoleService;
import com.yang.blog.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/role")
public class SysRoleController {

    @Autowired
    private SysRoleService roleService;

    //获取所有用户信息
    @GetMapping("/all")
    public RespBean findAll(){
        List<SysRoleEntity> roleList = roleService.getRoleAll();
        if(roleList == null){
            return RespBean.ok("error");
        }else{
            return RespBean.ok("ok",roleList);
        }
    }

    //新增
    @PostMapping("/addRole")
    public RespBean addRole(@RequestBody HashMap<String,String> RoleName){
        String name = RoleName.get("roleName");
        return roleService.addRole(name);
    }

    //修改
    @PostMapping("/updRole")
    public RespBean updRole(@RequestBody HashMap<String,Object> params){
        return roleService.updRole(params);
    }

    //删除
    @DeleteMapping("/delRole")
    public RespBean delRole(@RequestParam("id") String id){
        return roleService.delRole(id);
    }
}
