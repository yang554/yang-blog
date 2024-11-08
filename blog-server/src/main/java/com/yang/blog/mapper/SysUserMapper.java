package com.yang.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yang.blog.entity.SysUserEntity;
import com.yang.blog.utils.RespBean;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface SysUserMapper extends BaseMapper {
    //查询所有用户信息
    List<SysUserEntity> findAll();
    //根据身份证号查找用户
    SysUserEntity selectRolesByUsername(String ext02);
    //根据用户名模糊查询
    List<SysUserEntity> selectByUsername(String username);
    //用户注册
    int register(SysUserEntity user);
    //添加用户
    int addUser(SysUserEntity user);
    //编辑用户
    int editUser(SysUserEntity userEntity);
    //修改密码
    int resetUserPwd(SysUserEntity userEntity);
    //头像保存
    int saveAvatar(@Param(value = "avatar") String avatar,@Param(value = "id") String id);
    //删除用户
    int delUser(String id);
}
