package com.yang.blog.mapper;

import com.yang.blog.entity.AppVersionEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface AppVersionMapper {
    List<AppVersionEntity> getRawData(@Param("appName")String appName, @Param("versionCode")String versionCode, @Param("startTime")String startTime, @Param("endTime")String endTime);
    int insert(AppVersionEntity entity);
    int update(AppVersionEntity entity);
    int delete(String id);
}
