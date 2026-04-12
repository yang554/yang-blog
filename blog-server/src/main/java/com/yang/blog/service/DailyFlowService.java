package com.yang.blog.service;

import com.yang.blog.entity.DailyFlowEntity;
import com.yang.blog.entity.UConsumptionCountEntity;
import com.yang.blog.utils.RespBean;

import java.util.HashMap;
import java.util.List;

public interface DailyFlowService {
    //查询所有记录
    List<DailyFlowEntity> findAll();
    //根据登录用户查询账单
    List<DailyFlowEntity> findAllById(String id);
    //条件查询
    List<DailyFlowEntity> findByFactor(String searchDate1,String searchDate2,String searchType,String searchName,String queryCost);
    //统计收支情况
    List<UConsumptionCountEntity> getConsumptionCount(String search ,String consumptionType ,String getCount ,String queryId);
    //统计最近一天的收支情况明细
    List<UConsumptionCountEntity> getConsumptionCountDInfo(String consumptionType ,String consumptionTime,String queryId);
    //统计最近一月的收支情况明细
    List<UConsumptionCountEntity> getConsumptionCountMInfo(String consumptionType ,String consumptionTime,String queryId);
    //统计最近一年的收支情况明细
    List<UConsumptionCountEntity> getConsumptionCountYInfo(String consumptionType ,String consumptionTime,String queryId);
    //条件查询获取原始数据
    List<UConsumptionCountEntity> getRawData(String userId,String timeRange,String year,String month,String startDate,String endDate);
    //获取用户有记录的年份列表
    List<Integer> selectAvailableYears(Long userId);
    //添加记录
    RespBean operationDailyFlow(HashMap<String,Object> entity);
    //删除记录
    RespBean delFlow(String id);
}
