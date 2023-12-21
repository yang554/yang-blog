package com.yang.blog.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yang.blog.entity.RoleListEntity;
import com.yang.blog.entity.SysRoleEntity;
import com.yang.blog.entity.SysUserEntity;
import com.yang.blog.entity.SysUserRoleEntity;
import com.yang.blog.mapper.SysRoleMapper;
import com.yang.blog.mapper.SysUserMapper;
import com.yang.blog.mapper.SysUserRoleMapper;
import com.yang.blog.service.SysUserService;
import com.yang.blog.utils.CommonDate;
import com.yang.blog.utils.RespBean;
import lombok.AccessLevel;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
@Slf4j
public class SysUserServiceImpl implements SysUserService {
    Logger log = Logger.getLogger(String.valueOf(SysUserServiceImpl.class));
    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private SysRoleMapper roleMapper;
    @Autowired
    private SysUserRoleMapper userRoleMapper;

    private SysUserEntity getUser(SysUserEntity user){
        //1、先检查用户名是否已存在
        if(!ObjectUtils.isEmpty(this.selectRolesByUsername(user.getUsername()))){
            return null;
        }
        //2、产生随机盐
        String ss = RandomUtil.randomString(6);
        log.info("随机字符串为==="+ss);
        user.setSalt(ss);
        Md5Hash md5Hash = new Md5Hash(user.getPassword(), ss, 1024);
        user.setPassword(md5Hash.toHex());

        return  user;
    }

    @Override
    public boolean findUser(String name) {
        if(ObjectUtils.isEmpty(this.selectRolesByUsername(name))){
            return true;
        }
        return false;
    }

    @Override
    public List<SysUserEntity> findAll() {
        return userMapper.findAll();
    }

    @Override
    public SysUserEntity selectRolesByUsername(String username) {
        return userMapper.selectRolesByUsername(username);
    }

    @Override
    public List<SysUserEntity> selectByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public RespBean register(SysUserEntity user) {
        //1、先检查用户名是否已存在
        if(!ObjectUtils.isEmpty(this.selectRolesByUsername(user.getUsername()))){
            return RespBean.warn("用户名已存在请重新输入");
        }
        //2、产生随机盐
        String ss = RandomUtil.randomString(6);
        user.setSalt(ss);
        Md5Hash md5Hash = new Md5Hash(user.getPassword(), ss, 1024);
        user.setPassword(md5Hash.toHex());

        int insert = userMapper.register(user);
        System.out.println(user);
        if(insert >=0){
            SysUserRoleEntity userRoleEntity = new SysUserRoleEntity();
            userRoleEntity.setId(CommonDate.getStamp());
            userRoleEntity.setUserid(user.getId());
            userRoleEntity.setRoleid(4);
            userRoleMapper.add(userRoleEntity);//添加普通用户权限
            return RespBean.ok("用户创建成功username="+user.getUsername(),user);
        }else{
            return RespBean.error("创建失败！");
        }
    }

    @Override
    public RespBean addUser(SysUserEntity user) {
        user = getUser(user);
        user.setId(CommonDate.getStamp());
        user.setCreateTime(CommonDate.getTime24());
        int insert = userMapper.addUser(user);
        if(insert >=0){
            SysUserRoleEntity userRoleEntity = new SysUserRoleEntity();
            userRoleEntity.setId(CommonDate.getStamp());
            userRoleEntity.setUserid(user.getId());
            if("".equals(user.getEtx01())){
                userRoleEntity.setRoleid(4);
            }else{
                userRoleEntity.setRoleid(Integer.valueOf(user.getEtx01()));
            }
            userRoleMapper.add(userRoleEntity);//添加用户权限
            return RespBean.ok("用户创建成功username="+user.getUsername(),user);
        }else{
            return RespBean.error("创建失败！");
        }
    }

    @Override
    public List<RoleListEntity> findPermsListByRoleId(Long id) {
        return null;
    }

    @Override
    public List<SysRoleEntity> getRoleAll() {
        return roleMapper.getRoleAll();
    }

    @Override
    public RespBean editUser(HashMap<String,Object> userEntity) {
        SysUserEntity user = new SysUserEntity();
        String id = (String) userEntity.get("id");
        String name = (String) userEntity.get("username");
        String nickname = (String) userEntity.get("nickname");
        String email = (String) userEntity.get("email");
        String phone = (String) userEntity.get("phone");
        String description = (String) userEntity.get("description");

        user.setId(Long.valueOf(id));
        user.setUsername(name);
        user.setNickname(nickname);
        user.setEmail(email);
        user.setPhone(phone);
        user.setDescription(description);
        user.setUpdateTime(CommonDate.getTime24());

        int status = userMapper.editUser(user);
        if(status > 0){
            return RespBean.ok("保存成功");
        }else {
            return RespBean.error("保存失败");
        }
    }

    @Override
    public RespBean resetUserPwd(HashMap<String, Object> userEntity) {
        SysUserEntity user = new SysUserEntity();
        String id = (String) userEntity.get("id");
        String pwd = (String) userEntity.get("pwd");

        //2、产生随机盐
        String ss = RandomUtil.randomString(6);
        user.setSalt(ss);
        Md5Hash md5Hash = new Md5Hash(pwd, ss, 1024);
        user.setPassword(md5Hash.toHex());
        user.setId(Long.parseLong(id));

        int count = userMapper.resetUserPwd(user);

        if(count == 1) return RespBean.ok("修改成功！");
        else return RespBean.error("未知错误！");

    }

    @Override
    public RespBean saveAvatar(String avatar, String id) {
        int status = userMapper.saveAvatar(avatar,id);
        if(status > 0){
            return RespBean.ok("头像上传成功",avatar);
        }else {
            return RespBean.error("头像上传失败");
        }
    }

    @Override
    public RespBean delUser(String id) {
        int status = userMapper.delUser(id);
        if(status > 0){
            return RespBean.ok("删除成功");
        }else {
            return RespBean.error("删除失败");
        }
    }

    @Override
    public RespBean getUserRoleAll() {
        List<Map<String, Integer>> ret = roleMapper.getUserRoleAll();
        if(ObjectUtils.isEmpty(ret)){
            return RespBean.error("查询失败");
        }else{
            return RespBean.ok("ok",ret);
        }
    }


}
