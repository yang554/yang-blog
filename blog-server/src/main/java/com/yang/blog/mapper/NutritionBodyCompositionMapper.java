package com.yang.blog.mapper;

import com.yang.blog.entity.NutritionBodyCompositionEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface NutritionBodyCompositionMapper {
    //查询所有记录
    List<NutritionBodyCompositionEntity> findAll();
    //根据ID查询
    NutritionBodyCompositionEntity findById(String id);
    //根据用户查询
    List<NutritionBodyCompositionEntity> findByUser(String uID);
    //添加体成分记录
    int addNBC(NutritionBodyCompositionEntity entity);
    //修改体成分记录
    int updNBC(NutritionBodyCompositionEntity entity);
    //删除体成分记录
    int delNBC(String id);
}
