package com.yang.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yang.blog.entity.DailyFlowInfoEntity;
import com.yang.blog.mapper.DailyFlowInfoMapper;
import com.yang.blog.mapper.UConsumptionTypeMapper;
import com.yang.blog.service.DailyFlowInfoService;
import com.yang.blog.utils.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class DailyFlowServiceInfoImpl implements DailyFlowInfoService {
    @Autowired
    private DailyFlowInfoMapper mapper;
    @Autowired
    private UConsumptionTypeMapper typeMapper;

    @Override
    public List<DailyFlowInfoEntity> findAll() {
        return mapper.findAll();
    }

    @Override
    public List<DailyFlowInfoEntity> findByFactor(String fID) {
        return mapper.findByFactor(fID);
    }

    @Override
    public List<DailyFlowInfoEntity> findByFactor(String searchDate1, String searchDate2, String searchType, String searchTypeTag,String id,String queryCost) {
        JSONObject jsonObject = JSON.parseObject(queryCost);
        String minCost = jsonObject.getString("minCost");
        String maxCost = jsonObject.getString("maxCost");
        return mapper.findByFactorInfo(searchDate1,searchDate2,searchType,searchTypeTag,id,minCost,maxCost);
    }

    @Override
    public RespBean operationDailyFlowInfo(HashMap<String,Object> entity) {
        return null;
    }

    @Override
    public RespBean insertBatch(List<DailyFlowInfoEntity> entity) {
        List<DailyFlowInfoEntity> listAll = mapper.findByFactor(String.valueOf(entity.get(0).getfID()));
        List<DailyFlowInfoEntity> insertData = new ArrayList<>();
        List<DailyFlowInfoEntity> updateData = new ArrayList<>();
        List<DailyFlowInfoEntity> deleteData = new ArrayList<>();

        // 首先对比两个集合的大小是否相同
        for (DailyFlowInfoEntity e : entity) {
            if("x".equals(e.getNote())){
                String tag   = e.getTypeTagID();
                String key  =  e.getPurpose();
                typeMapper.updConsumptionTypeByName(tag,key);
                e.setNote("");
            }
            if(mapper.findByID(String.valueOf(e.getId())) == null){
                insertData.add(e);
            }else{
                updateData.add(e);
            }
        }
        for (DailyFlowInfoEntity a : listAll) {
            boolean flag = false;
            for (DailyFlowInfoEntity e : entity) {
                if(a.getId().equals(e.getId())){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                deleteData.add(a);
            }
        }

        if(insertData.size() >0){
            mapper.insertBatch(insertData);
        }

        if(updateData.size() >0 ){
            mapper.updFlowInfo(updateData);
        }

        if(deleteData.size() >0){
            mapper.delFlowInfoBatch(deleteData);
        }

        if(true){
            return RespBean.ok("添加成功！");
        }else{
            return RespBean.error("添加失败！");
        }
    }

    @Override
    public RespBean delFlowInfo(String id) {
        return null;
    }
}
