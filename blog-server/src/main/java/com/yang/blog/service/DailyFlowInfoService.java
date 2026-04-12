package com.yang.blog.service;

import com.yang.blog.entity.DailyFlowInfoEntity;
import com.yang.blog.utils.RespBean;

import java.util.HashMap;
import java.util.List;

public interface DailyFlowInfoService {
    //查询所有记录
    List<DailyFlowInfoEntity> findAll();
    //条件查询
    List<DailyFlowInfoEntity> findByFactor(String fID);
    //条件查询
    List<DailyFlowInfoEntity> findByFactor(String searchDate1,String searchDate2,String searchType,String searchTypeTag,String id,String queryCost);
    //操作记录
    RespBean operationDailyFlowInfo(HashMap<String,Object> entity);
    //批量添加记录
    RespBean insertBatch(List<DailyFlowInfoEntity> entity);
    //删除记录
    RespBean delFlowInfo(String id);
}
