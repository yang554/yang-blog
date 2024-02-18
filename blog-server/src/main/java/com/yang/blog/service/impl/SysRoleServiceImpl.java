package com.yang.blog.service.impl;

import com.yang.blog.entity.SysRoleEntity;
import com.yang.blog.mapper.SysRoleMapper;
import com.yang.blog.service.SysRoleService;
import com.yang.blog.utils.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SysRoleServiceImpl implements SysRoleService {
    @Autowired
    private SysRoleMapper roleMapper;

    @Override
    public List<SysRoleEntity> getRoleAll() {
        return roleMapper.getRoleAll();
    }

    @Override
    public List<Map<String, Integer>> getUserRoleAll() {
        return roleMapper.getUserRoleAll();
    }

    @Override
    public RespBean addRole(String RoleName) {
        int status = roleMapper.addRole(RoleName);
        if(status > 0){
            return RespBean.ok("成功");
        }else {
            return RespBean.error("失败");
        }
    }

    @Override
    public RespBean updRole(HashMap<String,Object> roleEntities) {
        SysRoleEntity role = new SysRoleEntity();
        String id = (String) roleEntities.get("id");
        String nameZH = (String) roleEntities.get("nameZH");
        String name = (String) roleEntities.get("name");

        role.setId(Long.valueOf(id));
        role.setName(name);
        role.setNameZh(nameZH);
        int status = roleMapper.updRole(role);
        if(status > 0){
            return RespBean.ok("保存成功");
        }else {
            return RespBean.error("保存失败");
        }
    }

    @Override
    public RespBean delRole(String RoleId) {
        int status = roleMapper.delRole(RoleId);
        if(status > 0){
            return RespBean.ok("删除成功");
        }else {
            return RespBean.error("删除失败");
        }
    }
}
