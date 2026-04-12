package com.yang.blog.service;

import com.yang.blog.entity.DietPlanInfoEntity;
import com.yang.blog.utils.RespBean;

import java.util.List;

public interface DietPlanInfoService {
    //查询所有记录
    List<DietPlanInfoEntity> findAll();
    //条件查询
    List<DietPlanInfoEntity> findByFactor(String planID);
    //操作记录
    RespBean operationData(List<DietPlanInfoEntity> entity);
}
