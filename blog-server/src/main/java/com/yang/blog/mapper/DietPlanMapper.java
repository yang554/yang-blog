package com.yang.blog.mapper;

import com.yang.blog.entity.DietPlanEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface DietPlanMapper {
    //查询所有记录
    List<DietPlanEntity> findAll();
    //条件查询-根据创建人和创建时间
    DietPlanEntity findByIdAndDate(@Param("createID") String createID, @Param("createDate") String createDate);
    //条件查询-根据创建人和创建时间范围
    List<DietPlanEntity> getRawData(@Param("createID") String createID, @Param("startDate") String startDate, @Param("endDate") String endDate);
    //添加
    int insertData(DietPlanEntity entity);
    //修改
    int updateData(DietPlanEntity entity);
    //删除
    int deleteData(DietPlanEntity entity);
}
