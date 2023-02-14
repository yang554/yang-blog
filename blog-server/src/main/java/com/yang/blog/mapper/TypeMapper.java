package com.yang.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yang.blog.entity.TypeEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface TypeMapper {
    //查询所有分类
    List<TypeEntity> getTypeAll();
    //根据分类ID查询
    TypeEntity getTypeById(String id);
    //根据分类名称查询(模糊)
    List<TypeEntity> getTypeByName(String name);
    //添加分类
    int addType(TypeEntity entity);
    //编辑分类
    int updTypeById(TypeEntity entity);
    //删除分类
    int delTypeById(String id);
    //批量删除分类
    int delTypes();
}
