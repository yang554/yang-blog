package com.yang.blog.service.impl;

import com.yang.blog.entity.FoodNutritionEntity;
import com.yang.blog.mapper.FoodNutritionMapper;
import com.yang.blog.service.FoodNutritionService;
import com.yang.blog.utils.CommonDate;
import com.yang.blog.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class FoodNutritionServiceImpl implements FoodNutritionService {
    @Autowired
    private FoodNutritionMapper mapper;

    @Override
    public List<FoodNutritionEntity> findAll() {
        return mapper.findAll();
    }

    @Override
    public List<FoodNutritionEntity> findByFactor(String param) {
        return mapper.findByFactor(param);
    }

    @Override
    public RespBean operationFood(HashMap<String, Object> hashMap) {
        FoodNutritionEntity entity = new FoodNutritionEntity();
        entity.setfType((String) hashMap.get("fType"));
        entity.setfName((String) hashMap.get("fName"));
        entity.setfImg((String) hashMap.get("fImg"));
        entity.setfEdibleSection((String) hashMap.get("fEdibleSection"));
        entity.setfEnergy((String) hashMap.get("fEnergy"));
        entity.setfCarbonWater(String.valueOf(hashMap.get("fCarbonWater")));
        entity.setfFat(String.valueOf(hashMap.get("fFat")));
        entity.setfProtein(String.valueOf(hashMap.get("fProtein")));
        entity.setfDietaryFiber(String.valueOf(hashMap.get("fDietaryFiber")));
        entity.setfVitaminA(String.valueOf(hashMap.get("fVitaminA")));
        entity.setfVitaminC(String.valueOf(hashMap.get("fVitaminC")));
        entity.setfVitaminD(String.valueOf(hashMap.get("fVitaminD")));
        entity.setfVitaminE(String.valueOf(hashMap.get("fVitaminE")));
        entity.setfVitaminK(String.valueOf(hashMap.get("fVitaminK")));
        entity.setfVitaminB1(String.valueOf(hashMap.get("fVitaminB1")));
        entity.setfVitaminB2(String.valueOf(hashMap.get("fVitaminB2")));
        entity.setfVitaminB3(String.valueOf(hashMap.get("fVitaminB3")));
        entity.setfVitaminB6(String.valueOf(hashMap.get("fVitaminB6")));
        entity.setfVitaminB9(String.valueOf(hashMap.get("fVitaminB9")));
        entity.setfVitaminB12(String.valueOf(hashMap.get("fVitaminB12")));
        entity.setfK(String.valueOf(hashMap.get("fK")));
        entity.setfNa(String.valueOf(hashMap.get("fNa")));
        entity.setfCa(String.valueOf(hashMap.get("fCa")));
        entity.setfZn(String.valueOf(hashMap.get("fZn")));
        entity.setfSe(String.valueOf(hashMap.get("fSe")));
        entity.setfFe((String) hashMap.get("fFe"));
        entity.setfRole((String) hashMap.get("fRole"));
        entity.setfNote((String) hashMap.get("fNote"));
        entity.setfUpdateTime(CommonDate.getTime24());
        String op = String.valueOf(hashMap.get("op"));
        if("add".equals(op)){
            entity.setfID(CommonDate.getStamp());
            entity.setfCreateTime(CommonDate.getTime24());
            int status = mapper.addFood(entity);
            if(status >=0){
                return RespBean.ok("添加成功！");
            }else{
                return RespBean.error("创建失败！");
            }
        }else if("upd".equals(op)){
            entity.setfID(Long.valueOf(String.valueOf(hashMap.get("fID"))) );
            int status = mapper.updFood(entity);
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
    public RespBean delFood(String id) {
        int status = mapper.delFood(id);

        if(status > 0){
            return RespBean.ok("删除成功");
        }else {
            return RespBean.error("删除失败");
        }
    }
}
