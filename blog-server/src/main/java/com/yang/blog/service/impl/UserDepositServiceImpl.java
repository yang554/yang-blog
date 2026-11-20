package com.yang.blog.service.impl;

import com.yang.blog.entity.UserDepositEntity;
import com.yang.blog.mapper.UserDepositMapper;
import com.yang.blog.service.UserDepositService;
import com.yang.blog.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDepositServiceImpl implements UserDepositService {
    @Autowired
    private UserDepositMapper userDepositMapper;

    @Override
    public List<UserDepositEntity> findAll() {
        return userDepositMapper.findAll();
    }

    @Override
    public UserDepositEntity findByUserId(String id) {
        return userDepositMapper.findByUserId(id);
    }

    @Override
    public List<UserDepositEntity> findByName(String name) {
        return userDepositMapper.findByName(name);
    }

    @Override
    public RespBean addUserDeposit(UserDepositEntity entity) {
        int s = userDepositMapper.addUserDeposit(entity);
        if(s > 0)
            return RespBean.ok("修改成功");
        else
            return RespBean.error("修改失败");
    }

    @Override
    public RespBean updUserDepositById(UserDepositEntity entity) {
        int s = userDepositMapper.updUserDepositById(String.valueOf(entity.getUserId()),entity.getTotalDeposit());
        if(s > 0)
            return RespBean.ok("修改成功");
        else
            return RespBean.error("修改失败");
    }
}
