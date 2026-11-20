package com.yang.blog.mapper;

import com.yang.blog.entity.DailyFlowEntity;
import com.yang.blog.entity.UConsumptionCountEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface DailyFlowMapper {
    //查询所有记录
    List<DailyFlowEntity> findAll();
    //根据登录用户查询账单
    List<DailyFlowEntity> findAllById(@Param("id")String id);
    //条件查询
    List<DailyFlowEntity> findByFactor(@Param("searchDate1")String searchDate1 , @Param("searchDate2")String searchDate2,@Param("searchType")String searchType,@Param("searchName")String searchName,@Param("minCost")String minCost,@Param("maxCost")String maxCost);
    //统计收支情况
    List<UConsumptionCountEntity> getConsumptionCount(@Param("search")String search ,@Param("consumptionType")String consumptionType , @Param("getCount")int getCount , @Param("queryId")String queryId);
    //统计最近一天的收支情况明细
    List<UConsumptionCountEntity> getConsumptionCountDInfo(@Param("consumptionType")String consumptionType ,@Param("consumptionTime")String consumptionTime,@Param("queryId")String queryId);
    //统计最近一月的收支情况明细
    List<UConsumptionCountEntity> getConsumptionCountMInfo(@Param("consumptionType")String consumptionType ,@Param("consumptionTime")String consumptionTime,@Param("queryId")String queryId);
    //统计最近一年的收支情况明细
    List<UConsumptionCountEntity> getConsumptionCountYInfo(@Param("consumptionType")String consumptionType ,@Param("consumptionTime")String consumptionTime,@Param("queryId")String queryId );
    //条件查询获取原始数据
    List<UConsumptionCountEntity> getRawData(@Param("userId")String userId,@Param("timeRange")String timeRange,@Param("year")String year,@Param("month")String month,@Param("startDate")String startDate,@Param("endDate")String endDate);
    //获取用户有记录的年份列表
    List<Integer> selectAvailableYears(@Param("userId")Long userId);
    //添加记录
    int addFlow(DailyFlowEntity entity);
    //修改记录
    int updFlow(DailyFlowEntity entity);
    //删除记录
    int delFlow(String id);
}
