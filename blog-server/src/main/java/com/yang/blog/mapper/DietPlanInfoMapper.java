package com.yang.blog.mapper;

import com.yang.blog.entity.DietPlanInfoEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Repository
@Transactional
public interface DietPlanInfoMapper {
    //查询所有记录
    List<DietPlanInfoEntity> findAll();
    //条件查询
    List<DietPlanInfoEntity> findByFactor(@Param("planID") String planID);
    //批量添加
    int insertBatch(List<DietPlanInfoEntity> entity);
    //批量修改
    int updateBatch(List<DietPlanInfoEntity> entity);
    //批量删除
    int deleteBatch(List<DietPlanInfoEntity> entity);
}
