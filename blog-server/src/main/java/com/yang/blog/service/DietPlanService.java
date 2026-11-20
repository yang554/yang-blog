package com.yang.blog.service;

import com.yang.blog.entity.DietPlanEntity;
import com.yang.blog.utils.RespBean;

import java.util.HashMap;
import java.util.List;

public interface DietPlanService {
    //查询所有记录
    List<DietPlanEntity> findAll();
    //条件查询[根据用户ID和创建时间]
    DietPlanEntity findByIdAndDate(String createID,String createDate);
    //条件查询[根据创建人和创建时间范围]
    List<DietPlanEntity> getRawData(String createID,String startDate,String endDate);
    //添加记录
    int insertData(DietPlanEntity entity);
    //修改记录
    int updateData(DietPlanEntity entity);
}
