package com.yang.blog.mapper;

import com.yang.blog.entity.UEventEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
@Transactional
public interface UEventMapper {
    //查询
    List<Map<String,Object>> getEvenByPage(@Param(value = "type") String type,
                                           @Param(value = "eTitle") String eTitle,
                                           @Param(value = "uName") String uName,
                                           @Param(value = "createName") String createName);

    //添加数据
    int addEven(UEventEntity entity);
}
