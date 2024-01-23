package com.yang.blog.mapper;

import com.yang.blog.entity.UAuntEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface UAuntMapper {
    //查询所有数据
    List<UAuntEntity> getByALL();
    //查询用户
    List<UAuntEntity> findNameAll();
    //根据用户模糊查询所有数据
    List<UAuntEntity> selectByName(@Param("uName") String uName);
    //根据用户查询所有数据
    List<UAuntEntity> selectByNameAll(@Param("uName") String uName,@Param("uStatus") String uStatus);
    //添加数据
    int addAunt(UAuntEntity entity);
    //修改数据
    int updAunt(UAuntEntity entity);
    //删除数据
    int delAunt(String id);
}
