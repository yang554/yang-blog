package com.yang.blog.service.impl;

import com.yang.blog.entity.DietPlanEntity;
import com.yang.blog.mapper.DietPlanMapper;
import com.yang.blog.service.DietPlanService;
import com.yang.blog.utils.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
public class DietPlanServiceImpl implements DietPlanService {
    @Autowired
    private DietPlanMapper mapper;

    @Override
    public List<DietPlanEntity> findAll() {
        return null;
    }

    @Override
    public DietPlanEntity findByIdAndDate(String createID,String createDate) {
        return mapper.findByIdAndDate(createID,createDate);
    }

    @Override
    public  List<DietPlanEntity> getRawData(String createID, String startDate, String endDate) {
        return mapper.getRawData(createID,startDate,endDate);
    }

    @Override
    public int insertData(DietPlanEntity entity) {
        return mapper.insertData(entity);
    }

    @Override
    public int updateData(DietPlanEntity entity) {
        return  mapper.updateData(entity);
    }
}
