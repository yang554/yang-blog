package com.yang.blog.mapper;

import com.yang.blog.entity.FoodNutritionEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface FoodNutritionMapper {
    //查询所有记录
    List<FoodNutritionEntity> findAll();
    //条件查询
    List<FoodNutritionEntity> findByFactor(String param);
    //添加食物
    int addFood(FoodNutritionEntity entity);
    //修改食物
    int updFood(FoodNutritionEntity entity);
    //删除食物
    int delFood(String id);
}
