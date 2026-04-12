package com.yang.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yang.blog.entity.DailyFlowEntity;
import com.yang.blog.entity.UConsumptionCountEntity;
import com.yang.blog.mapper.DailyFlowInfoMapper;
import com.yang.blog.mapper.DailyFlowMapper;
import com.yang.blog.service.DailyFlowService;
import com.yang.blog.utils.CommonDate;
import com.yang.blog.utils.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class DailyFlowServiceImpl implements DailyFlowService {
    @Autowired
    private DailyFlowMapper mapper;
    @Autowired
    private DailyFlowInfoMapper mapperInfo;

    @Override
    public List<DailyFlowEntity> findAll() {
        return mapper.findAll();
    }

    @Override
    public List<DailyFlowEntity> findAllById(String id) {
        return mapper.findAllById(id);
    }

    @Override
    public List<DailyFlowEntity> findByFactor(String searchDate1,String searchDate2,String searchType,String searchName,String queryCost) {
        JSONObject jsonObject = JSON.parseObject(queryCost);
        String minCost = jsonObject.getString("minCost");
        String maxCost = jsonObject.getString("maxCost");
        return mapper.findByFactor(searchDate1,searchDate2,searchType,searchName,minCost,maxCost);
    }

    @Override
    public List<UConsumptionCountEntity> getConsumptionCount(String search, String consumptionType, String getCount,String queryId) {
        if("year".equals(search)){
            search = "%Y";
        }else if ("month".equals(search)){
            search = "%Y-%m";
        }else{
            search = "%Y-%m-%d";
        }
        List<UConsumptionCountEntity> list = mapper.getConsumptionCount(search,consumptionType, Integer.parseInt(getCount),queryId);
        Collections.reverse(list);
        return list;
    }

    @Override
    public List<UConsumptionCountEntity> getConsumptionCountDInfo(String consumptionType,String consumptionTime,String queryId) {
        if("0".equals(consumptionType)){
            consumptionType = "支出";
        }else{
            consumptionType = "收入";
        }
        return mapper.getConsumptionCountDInfo(consumptionType,consumptionTime,queryId);
    }

    @Override
    public List<UConsumptionCountEntity> getConsumptionCountMInfo(String consumptionType,String consumptionTime,String queryId) {
        if("0".equals(consumptionType)){
            consumptionType = "支出";
        }else{
            consumptionType = "收入";
        }
        return mapper.getConsumptionCountMInfo(consumptionType,consumptionTime,queryId);
    }

    @Override
    public List<UConsumptionCountEntity> getConsumptionCountYInfo(String consumptionType ,String consumptionTime,String queryId) {
        if("0".equals(consumptionType)){
            consumptionType = "支出";
        }else{
            consumptionType = "收入";
        }
        return mapper.getConsumptionCountYInfo(consumptionType,consumptionTime,queryId);
    }

    @Override
    public List<UConsumptionCountEntity> getRawData(String userId, String timeRange, String year, String month, String startDate, String endDate) {
        return mapper.getRawData(userId,timeRange,year,month,startDate,endDate);
    }

    @Override
    public List<Integer> selectAvailableYears(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return mapper.selectAvailableYears(userId);
    }

    @Override
    public RespBean operationDailyFlow(HashMap<String, Object> hashMap) {
        DailyFlowEntity dailyFlowEntity = new DailyFlowEntity();
        dailyFlowEntity.setId(Long.valueOf(String.valueOf(hashMap.get("id"))) );
        dailyFlowEntity.setType(String.valueOf(hashMap.get("type")));
        dailyFlowEntity.setCost(new BigDecimal(String.valueOf(hashMap.get("cost"))));
        dailyFlowEntity.setRecordTime(String.valueOf(hashMap.get("recordTime")));
        dailyFlowEntity.setNote(String.valueOf(hashMap.get("note")));
        dailyFlowEntity.setMade(Long.valueOf(String.valueOf(hashMap.get("made"))) );

        dailyFlowEntity.setfUpdateTime(CommonDate.getTime24());
        String op = String.valueOf(hashMap.get("op"));
        if("add".equals(op)){
            dailyFlowEntity.setfCreateTime(CommonDate.getTime24());
            int status = mapper.addFlow(dailyFlowEntity);
            if(status >=0){
                return RespBean.ok("添加成功！");
            }else{
                return RespBean.error("创建失败！");
            }
        }else if("upd".equals(op)){
            // 创建 DateTimeFormatter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // 解析字符串为 LocalDateTime
            LocalDateTime dateTime = LocalDateTime.parse(String.valueOf(hashMap.get("fCreateTime")), formatter);
            dailyFlowEntity.setfCreateTime(dateTime);
            int status = mapper.updFlow(dailyFlowEntity);
            if(status >=0){
                return RespBean.ok("修改成功！");
            }else{
                return RespBean.error("修改失败！");
            }
        }else{
            return RespBean.error("未知操作！！！");
        }
    }

    @Override
    public RespBean delFlow(String id) {
        int status = mapperInfo.delFlowInfo(id);
        status += mapper.delFlow(id);
        if(status >=1){
            return RespBean.ok("删除成功！");
        }else{
            return RespBean.error("删除失败！");
        }
    }
}
