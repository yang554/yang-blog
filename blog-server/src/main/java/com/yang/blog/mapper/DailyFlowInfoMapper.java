package com.yang.blog.mapper;

import com.yang.blog.entity.DailyFlowInfoEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface DailyFlowInfoMapper {
    //查询所有记录
    List<DailyFlowInfoEntity> findAll();
    //条件查询
    List<DailyFlowInfoEntity> findByFactor(String fID);
    //条件查询
    List<DailyFlowInfoEntity> findByFactorInfo(@Param("searchDate1")String searchDate1 , @Param("searchDate2")String searchDate2, @Param("searchType")String searchType,@Param("searchTypeTag")String searchTypeTag,@Param("id")String id,@Param("minCost")String minCost,@Param("maxCost")String maxCost);
    //根据ID查询单条记录
    DailyFlowInfoEntity findByID(String id);
    //批量添加记录
    int insertBatch(List<DailyFlowInfoEntity> entity);
    //添加记录
    int addFlowInfo(List<DailyFlowInfoEntity> entity);
    //修改记录
    int updFlowInfo(List<DailyFlowInfoEntity> entity);
    //批量删除记录
    int delFlowInfoBatch(List<DailyFlowInfoEntity> entity);
    //删除记录
    int delFlowInfo(String id);
}
