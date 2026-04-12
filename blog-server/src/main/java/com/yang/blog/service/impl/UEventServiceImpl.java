package com.yang.blog.service.impl;

import com.yang.blog.entity.UEventEntity;
import com.yang.blog.mapper.UEventMapper;
import com.yang.blog.service.UEventService;
import com.yang.blog.utils.CommonDate;
import com.yang.blog.utils.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UEventServiceImpl implements UEventService {
    @Autowired
    private UEventMapper eventMapper;

    @Override
    public RespBean getEventByPage(String type, String eTitle, String uName, String createName) {
        List<UEventEntity> list = eventMapper.getEvenByPage(type,eTitle,uName,createName);
        return RespBean.ok("ok",list);
    }

    @Override
    public RespBean addEvent(HashMap<String, Object> entity) {
        UEventEntity eventEntity = new UEventEntity();
        eventEntity.setId(CommonDate.getStamp());
        eventEntity.setTitle((String) entity.get("scheduleTitle"));
        eventEntity.setContent((String) entity.get("content"));
        // 创建 DateTimeFormatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 解析字符串为 LocalDateTime
        LocalDateTime startDate = LocalDateTime.parse(String.valueOf(entity.get("startDate")), formatter);
        eventEntity.setStartDate(startDate);

        LocalDateTime endDate = LocalDateTime.parse(String.valueOf(entity.get("endDate")), formatter);
        eventEntity.setEndDate(endDate);

        eventEntity.setAddress((String) entity.get("scheduleAddress"));
        eventEntity.setNote((String) entity.get("note"));
        eventEntity.setName((String) entity.get("name"));

        eventEntity.setCreateDate(CommonDate.getTime24());
        eventEntity.setCreateNameID((String) entity.get("createNameID"));
        eventEntity.setIsNotice((String) entity.get("isNotice"));
        eventEntity.setType((String) entity.get("type"));

        int status = eventMapper.addEven(eventEntity);
        if(status > 0){
            return RespBean.ok("成功");
        }else{
            return RespBean.error("失败");
        }
    }

    @Override
    public RespBean updEvent(HashMap<String, Object> entity) {
        UEventEntity eventEntity = new UEventEntity();
        eventEntity.setId((Long) entity.get("id"));
        eventEntity.setTitle((String) entity.get("scheduleTitle"));
        eventEntity.setContent((String) entity.get("content"));
        // 创建 DateTimeFormatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 解析字符串为 LocalDateTime
        LocalDateTime startDate = LocalDateTime.parse(String.valueOf(entity.get("startDate")), formatter);
        eventEntity.setStartDate(startDate);

        LocalDateTime endDate = LocalDateTime.parse(String.valueOf(entity.get("endDate")), formatter);
        eventEntity.setEndDate(endDate);
        eventEntity.setAddress((String) entity.get("scheduleAddress"));
        eventEntity.setNote((String) entity.get("note"));
        eventEntity.setIsNotice((String) entity.get("isNotice"));

        int status = eventMapper.updEvent(eventEntity);
        if(status > 0){
            return RespBean.ok("成功");
        }else{
            return RespBean.error("失败");
        }
    }

    @Override
    public RespBean delEvent(String id) {
        int status = eventMapper.delEvent(id);
        if(status > 0){
            return RespBean.ok("成功");
        }else{
            return RespBean.error("失败");
        }
    }
}
