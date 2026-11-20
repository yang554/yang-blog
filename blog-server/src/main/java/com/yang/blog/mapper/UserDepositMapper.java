package com.yang.blog.mapper;

import com.yang.blog.entity.UserDepositEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface UserDepositMapper {
    //查询所有记录
    List<UserDepositEntity> findAll();
    //根据ID查询指定用户
    UserDepositEntity findByUserId(@Param(value = "id")String id);
    //根据name查询用户
    List<UserDepositEntity> findByName(@Param(value = "name")String name);
    //新增资产用户
    int addUserDeposit(UserDepositEntity entity);
    //修改用户名
    int updUserNameById(@Param(value = "id")String id,@Param(value = "userName")String userName);
    //修改用户资产
    int updUserDepositById(@Param(value = "id")String id,@Param(value = "deposit")String deposit);
}
