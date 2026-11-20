package com.yang.blog.service;

import com.yang.blog.entity.TPrizeEntity;
import com.yang.blog.utils.RespBean;

import java.util.HashMap;
import java.util.List;

public interface TPrizeService {
    //查询所有记录
    List<TPrizeEntity> findAll();
    //操作记录
    RespBean operationPrize(List<TPrizeEntity> entity);
    //根据ID奖品数量-1
    int updNumberByID(Long id,int number);
}
