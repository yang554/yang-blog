package com.yang.blog.mapper;

import com.yang.blog.entity.UConsumptionTypeEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface UConsumptionTypeMapper {
    //查询所有标签
    List<UConsumptionTypeEntity> getConsumptionTypeAll();
    //根据标签名称查询标签（模糊）
    List<UConsumptionTypeEntity> getConsumptionTypeByName(String name);
    //根据标签ID查询
    List<UConsumptionTypeEntity> getConsumptionTypeById(String[] tID);
    //添加标签
    int addConsumptionType(UConsumptionTypeEntity entity);
    //根据编码修改标签
    int updConsumptionTypeById(UConsumptionTypeEntity entity);
    //根据name修改标签
    int updConsumptionTypeByName(@Param(value = "name") String name, @Param(value = "key")String key);
    //根据编码删除标签
    int delConsumptionType(String id);

}
