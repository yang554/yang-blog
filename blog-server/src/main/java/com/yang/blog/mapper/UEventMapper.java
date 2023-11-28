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
    List<Map<String,Object>> getEvenByPage(@Param(value = "id") Boolean id,
                                           @Param(value = "eTitle") String eTitle,
                                           @Param(value = "uName") Boolean uName,
                                           @Param(value = "createName") Boolean createName);

    //添加数据
    int addEven(UEventEntity entity);
}
