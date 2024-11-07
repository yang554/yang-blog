package com.yang.blog.service.impl;

import com.yang.blog.entity.NutritionBodyCompositionEntity;
import com.yang.blog.mapper.NutritionBodyCompositionMapper;
import com.yang.blog.service.NutritionBodyCompositionService;
import com.yang.blog.utils.CommonDate;
import com.yang.blog.utils.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.border.EtchedBorder;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

@Service
@Slf4j
public class NutritionBodyCompositionServiceImpl implements NutritionBodyCompositionService {
    Logger log = Logger.getLogger(String.valueOf(NutritionBodyCompositionServiceImpl.class));
    @Autowired
    private NutritionBodyCompositionMapper mapper;

    @Override
    public List<NutritionBodyCompositionEntity> findAll() {
        return mapper.findAll();
    }

    @Override
    public RespBean findById(String id) {
        NutritionBodyCompositionEntity entity = mapper.findById(id);
        if(entity != null){
            System.out.println(entity);
            return RespBean.ok("ok",entity);
        }else{
           return RespBean.error("未查询到相关内容！！！");
        }
    }

    @Override
    public List<NutritionBodyCompositionEntity> findByUser(String uID) {
        return mapper.findByUser(uID);
    }

    @Override
    public RespBean operationNBC(HashMap<String,Object> hashMap) {
        NutritionBodyCompositionEntity entity = new NutritionBodyCompositionEntity();
        entity.setuID((String) hashMap.get("uID"));
        entity.setAge((String) hashMap.get("age"));
        entity.setSex((String) hashMap.get("sex"));
        entity.setHeight((String) hashMap.get("height"));
        entity.setWeight((String) hashMap.get("weight"));
        entity.setBasalMetabolism(String.valueOf(hashMap.get("basalMetabolism")));
        entity.setBMI(String.valueOf(hashMap.get("bmi")));
        entity.setFoodIntake(String.valueOf(hashMap.get("foodIntake")));
        entity.setFat(String.valueOf(hashMap.get("fat")));
        entity.setVisceralFat(String.valueOf(hashMap.get("visceralFat")));
        entity.setMuscle(String.valueOf(hashMap.get("muscle")));
        entity.setBoneMass(String.valueOf(hashMap.get("boneMass")));
        entity.setWaterContent(String.valueOf(hashMap.get("waterContent")));
        entity.setProtein(String.valueOf(hashMap.get("protein")));
        entity.setFatFreeBodyWeight(String.valueOf(hashMap.get("fatFreeBodyWeight")));
        entity.setMuscleMass(String.valueOf(hashMap.get("muscleMass")));
        entity.setSkeletalMuscle(String.valueOf(hashMap.get("skeletalMuscle")));
        entity.setWaterContent_1(String.valueOf(hashMap.get("waterContent_1")));
        entity.setFatMass(String.valueOf(hashMap.get("fatMass")));
        entity.setSubcutaneousFatPercentage(String.valueOf(hashMap.get("subcutaneousFatPercentage")));
        entity.setWeightControlAmount(String.valueOf(hashMap.get("weightControlAmount")));
        entity.setFatControlAmount(String.valueOf(hashMap.get("fatControlAmount")));
        entity.setMuscleControlVolume(String.valueOf(hashMap.get("muscleControlVolume")));
        entity.setStandardWeight(String.valueOf(hashMap.get("standardWeight")));
        entity.setIdealWeight(String.valueOf(hashMap.get("idealWeight")));
        entity.setNote((String) hashMap.get("note"));
        entity.setObesityLevel((String) hashMap.get("obesityLevel"));
        entity.setRecordTime(String.valueOf(hashMap.get("recordTime")));

        entity.setUpdateTime(CommonDate.getTime24());

        String op = String.valueOf(hashMap.get("op"));
        if("add".equals(op)){
            entity.setId(CommonDate.getStamp());
            entity.setCreateTime(CommonDate.getTime24());
            int status = mapper.addNBC(entity);
            if(status >=0){
                return RespBean.ok("添加成功！");
            }else{
                return RespBean.error("创建失败！");
            }
        }else if("upd".equals(op)){
            entity.setId(Long.valueOf(String.valueOf(hashMap.get("id"))) );
            int status = mapper.updNBC(entity);
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
    public RespBean delNBC(String id) {
        int status = mapper.delNBC(id);

        if(status > 0){
            return RespBean.ok("删除成功");
        }else {
            return RespBean.error("删除失败");
        }
    }
}
