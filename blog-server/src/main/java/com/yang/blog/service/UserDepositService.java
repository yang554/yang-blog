package com.yang.blog.service;

import com.yang.blog.entity.UserDepositEntity;
import com.yang.blog.utils.RespBean;

import java.util.List;

public interface UserDepositService {
    //查询所有记录
    List<UserDepositEntity> findAll();
    //根据ID查询指定用户
    UserDepositEntity findByUserId(String id);
    //根据name查询用户
    List<UserDepositEntity> findByName(String name);
    //新增用户
    RespBean addUserDeposit(UserDepositEntity entity);
    //修改用户资产
    RespBean updUserDepositById(UserDepositEntity entity);
}
