package com.yang.blog.service.impl;

import com.yang.blog.entity.UEventEntity;
import com.yang.blog.mapper.UEventMapper;
import com.yang.blog.service.UEventService;
import com.yang.blog.utils.CommonDate;
import com.yang.blog.utils.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        List<Map<String,Object>> tBlogPage = eventMapper.getEvenByPage(type,eTitle,uName,createName);
        return RespBean.ok("ok",tBlogPage);
    }

    @Override
    public RespBean addEvent(HashMap<String, Object> entity) {
        UEventEntity eventEntity = new UEventEntity();
        eventEntity.setId(CommonDate.getStamp());
        eventEntity.seteTitle((String) entity.get("scheduleTitle"));
        eventEntity.seteContent((String) entity.get("content"));
        eventEntity.setStartDate((String) entity.get("startDate"));
        eventEntity.setEndDate((String) entity.get("endDate"));
        eventEntity.seteAddress((String) entity.get("scheduleAddress"));
        eventEntity.setNote((String) entity.get("note"));
        eventEntity.setuName((String) entity.get("name"));

        eventEntity.setCreateDate((String) entity.get("createDate"));
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
        eventEntity.seteTitle((String) entity.get("scheduleTitle"));
        eventEntity.seteContent((String) entity.get("content"));
        eventEntity.setStartDate((String) entity.get("startDate"));
        eventEntity.setEndDate((String) entity.get("endDate"));
        eventEntity.seteAddress((String) entity.get("scheduleAddress"));
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
        System.out.println(id);
        int status = eventMapper.delEvent(id);
        if(status > 0){
            return RespBean.ok("成功");
        }else{
            return RespBean.error("失败");
        }
    }
}
