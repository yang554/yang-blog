package com.yang.blog.service.impl;

import com.yang.blog.entity.UAuntEntity;
import com.yang.blog.mapper.UAuntMapper;
import com.yang.blog.service.UAuntService;
import com.yang.blog.utils.CommonDate;
import com.yang.blog.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sound.midi.Soundbank;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UAuntServiceImpl implements UAuntService {

    @Autowired
    private UAuntMapper auntMapper;

    @Override
    public RespBean getByALL() {
        List<UAuntEntity> auntEntities = auntMapper.getByALL();
        return RespBean.ok("ok",auntEntities);
    }

    @Override
    public RespBean findNameAll() {
        List<UAuntEntity> auntEntities = auntMapper.findNameAll();
        return RespBean.ok("ok",auntEntities);
    }

    @Override
    public RespBean selectByName(String uName) {
        List<UAuntEntity> auntEntities = auntMapper.selectByName(uName);
        return RespBean.ok("ok",auntEntities);
    }

    @Override
    public Map<String,Object> selectByNameAll(String uName,String uStatus) {
        Map<String,Object> map = new HashMap<>();
        List<UAuntEntity> auntEntities = auntMapper.selectByNameAll(uName,uStatus);
        int index= 3;

        if(auntEntities.size() == 0){
            return null;
        }else if ("1".equals(auntEntities.get(0).getuStatus())){
            map.put("auntList",auntEntities);
            return map;
        }else {
            if(index > auntEntities.size()){
                index = auntEntities.size();
            }

            int cycleLengths = 0;
            String cyc = "";
            String syc1 = "";
            String per = "";
            String per1 = "";
            int periodLengths = 0;

            String lastPeriodStartDateStr = auntEntities.get(0).getStartDate();
            LocalDate lastPeriodStartDate = LocalDate.parse(lastPeriodStartDateStr);


            for (int i=0 ;i<index;i++){
                cyc = auntEntities.get(i).getCycle();
                syc1 = cyc.substring(0,cyc.length()-1);
                cycleLengths += Integer.valueOf(syc1);

                per = auntEntities.get(i).getDuration();
                per1 = per.substring(0,per.length()-1);
                periodLengths += Integer.valueOf(per1);
            }
            LocalDate nextPeriodStartDate = lastPeriodStartDate.plusDays(cycleLengths/index);

            LocalDate nextPeriodEndDate = nextPeriodStartDate.plusDays(periodLengths/index);

            long daysUntilNextPeriod = LocalDate.now().until(nextPeriodStartDate, ChronoUnit.DAYS);

            map.put("nextPeriodStartDate",nextPeriodStartDate);
            map.put("nextPeriodEndDate",nextPeriodEndDate);
            map.put("daysUntilNextPeriod",daysUntilNextPeriod);
            map.put("cycleLength",cycleLengths/index);
            map.put("periodLength",periodLengths/index);
            map.put("auntList",auntEntities);
            return map;
        }
    }

    @Override
    public RespBean addAunt(HashMap<String,Object> entity) {
        UAuntEntity auntEntity = new UAuntEntity();
        auntEntity.setId(CommonDate.getStamp());
        auntEntity.setuID(Long.valueOf(String.valueOf(entity.get("uID"))));
        if("".equals(entity.get("startDate"))){
            auntEntity.setStartDate(null);
        }else {
            auntEntity.setStartDate((String) entity.get("startDate"));
        }
        if("".equals(entity.get("endDate"))){
            auntEntity.setEndDate(null);
        }else {
            auntEntity.setEndDate((String) entity.get("endDate"));
        }
        auntEntity.setBloodVolume((String) entity.get("bloodVolume"));
        auntEntity.setCycle((String) entity.get("cycle"));
        auntEntity.setDuration((String) entity.get("duration"));
        auntEntity.setSymptom((String) entity.get("symptom"));
        auntEntity.setNote((String) entity.get("note"));
        auntEntity.setuStatus((String) entity.get("uStatus"));

        int status = auntMapper.addAunt(auntEntity);
        if(status > 0){
            return RespBean.ok("成功");
        }else{
            return RespBean.error("失败");
        }
    }

    @Override
    public RespBean updAunt(HashMap<String, Object> entity) {
        UAuntEntity auntEntity = new UAuntEntity();
        if("".equals(entity.get("startDate"))){
            auntEntity.setStartDate(null);
        }else {
            auntEntity.setStartDate((String) entity.get("startDate"));
        }
        if( entity.get("endDate") == null || "".equals(entity.get("endDate"))){
            auntEntity.setEndDate(null);
            auntEntity.setuStatus("1");
        }else {
            auntEntity.setEndDate((String) entity.get("endDate"));
            auntEntity.setuStatus("0");
        }

        auntEntity.setBloodVolume((String) entity.get("bloodVolume"));
        auntEntity.setCycle((String) entity.get("cycle"));
        auntEntity.setDuration((String) entity.get("duration"));
        auntEntity.setSymptom((String) entity.get("symptom"));
        auntEntity.setNote((String) entity.get("note"));
        auntEntity.setId((Long) entity.get("id"));

        int status = auntMapper.updAunt(auntEntity);
        if(status > 0){
            return RespBean.ok("成功");
        }else{
            return RespBean.error("失败");
        }
    }

    @Override
    public RespBean delAunt(String id) {
        int status = auntMapper.delAunt(id);
        if(status > 0){
            return RespBean.ok("成功");
        }else{
            return RespBean.error("失败");
        }
    }
}
