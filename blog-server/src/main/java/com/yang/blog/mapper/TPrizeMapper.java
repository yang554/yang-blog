package com.yang.blog.mapper;

import com.yang.blog.entity.TPrizeEntity;
import com.yang.blog.entity.TTagEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface TPrizeMapper {
    //查询所有记录
    List<TPrizeEntity> getAll();
    //根据ID查询单条记录
    TPrizeEntity findByID(String id);
    //批量添加记录
    int addPrize(List<TPrizeEntity> entity);
    //修改记录
    int updPrize(List<TPrizeEntity> entity);
    //根据ID奖品数量-1
    int updNumberByID(@Param("id")Long id,@Param("number")int number);
    //批量删除记录
    int delPrize(List<TPrizeEntity> entity);
}
