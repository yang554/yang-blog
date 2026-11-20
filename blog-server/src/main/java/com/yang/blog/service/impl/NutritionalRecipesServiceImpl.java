package com.yang.blog.service.impl;

import com.yang.blog.entity.NutritionalRecipesEntity;
import com.yang.blog.mapper.NutritionalRecipesMapper;
import com.yang.blog.service.NutritionalRecipesService;
import com.yang.blog.utils.CommonDate;
import com.yang.blog.utils.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class NutritionalRecipesServiceImpl implements NutritionalRecipesService {
    @Autowired
    private NutritionalRecipesMapper mapper;

    @Override
    public List<NutritionalRecipesEntity> findAll() {
        return mapper.findAll();
    }

    @Override
    public List<NutritionalRecipesEntity> findByFactor(String name,String type,String target) {
        return mapper.findByFactor(name,type,target);
    }

    @Override
    public RespBean findById(String id) {
        NutritionalRecipesEntity entity = mapper.findById(id);
        if(entity != null){
            return RespBean.ok("ok",entity);
        }else{
            return RespBean.error("未查询到相关内容！！！");
        }
    }

    @Override
    public RespBean operationRecipes(HashMap<String,Object> hashMap) {
        NutritionalRecipesEntity entity = new NutritionalRecipesEntity();
        entity.setfName((String) hashMap.get("fName"));
        entity.setfIntro((String) hashMap.get("fIntro"));
        entity.setfType((String) hashMap.get("fType"));
        entity.setzMeal((String) hashMap.get("zMeal"));
        entity.setzSnack((String) hashMap.get("zSnack"));
        entity.setaMeal((String) hashMap.get("aMeal"));
        entity.setaSnack((String) hashMap.get("aSnack"));
        entity.setnMeal((String) hashMap.get("nMeal"));
        entity.setnSnack((String) hashMap.get("nSnack"));
        entity.setfTarget((String) hashMap.get("fTarget"));
        entity.setfUpdateTime(CommonDate.getTime24());
        entity.setfNote((String) hashMap.get("fNote"));

        String op = String.valueOf(hashMap.get("op"));
        if("add".equals(op)){
            entity.setfID(CommonDate.getStamp());
            entity.setuID(Long.valueOf(String.valueOf(hashMap.get("uID"))));
            entity.setfCreateTime(CommonDate.getTime24());
            int status = mapper.addRecipes(entity);
            if(status >=0){
                return RespBean.ok("添加成功！");
            }else{
                return RespBean.error("创建失败！");
            }
        }else if("upd".equals(op)){
            entity.setfID(Long.valueOf(String.valueOf(hashMap.get("fID"))) );
            int status = mapper.updRecipes(entity);
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
    public RespBean delRecipes(String id) {
        int status = mapper.delRecipes(id);

        if(status > 0){
            return RespBean.ok("删除成功");
        }else {
            return RespBean.error("删除失败");
        }
    }
}
