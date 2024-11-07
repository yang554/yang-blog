package com.yang.blog.service.impl;

import com.yang.blog.entity.NutritionalRecipeEntity;
import com.yang.blog.mapper.NutritionalRecipeMapper;
import com.yang.blog.service.NutritionalRecipeService;
import com.yang.blog.utils.CommonDate;
import com.yang.blog.utils.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class NutritionalRecipeServiceImpl  implements NutritionalRecipeService {
    @Autowired
    private NutritionalRecipeMapper mapper;

    @Override
    public List<NutritionalRecipeEntity> findAll() {
        return mapper.findAll();
    }

    @Override
    public List<NutritionalRecipeEntity> findByFactor(String name,String type) {
        return mapper.findByFactor(name,type);
    }

    @Override
    public RespBean findById(String id) {
        NutritionalRecipeEntity entity = mapper.findById(id);
        if(entity != null){
            return RespBean.ok("ok",entity);
        }else{
            return RespBean.error("未查询到相关内容！！！");
        }
    }

    @Override
    public RespBean operationRecipe(HashMap<String,Object> hashMap) {
        NutritionalRecipeEntity entity = new NutritionalRecipeEntity();
        entity.setfImg((String) hashMap.get("fImg"));
        entity.setfName((String) hashMap.get("fName"));
        entity.setfIntro((String) hashMap.get("fIntro"));
        entity.setfType((String) hashMap.get("fType"));
        entity.setfBatching((String) hashMap.get("fBatching"));
        entity.setfSeasoning((String) hashMap.get("fSeasoning"));
        entity.setfMade((String) hashMap.get("fMade"));
        entity.setfTips((String) hashMap.get("fTips"));
        entity.setfUpdateTime(CommonDate.getTime24());

        String op = String.valueOf(hashMap.get("op"));
        if("add".equals(op)){
            entity.setfID(CommonDate.getStamp());
            entity.setfCreateTime(CommonDate.getTime24());
            int status = mapper.addRecipe(entity);
            if(status >=0){
                return RespBean.ok("添加成功！");
            }else{
                return RespBean.error("创建失败！");
            }
        }else if("upd".equals(op)){
            entity.setfID(Long.valueOf(String.valueOf(hashMap.get("fID"))) );
            int status = mapper.updRecipe(entity);
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
    public RespBean delRecipe(String id) {
        int status = mapper.delRecipe(id);

        if(status > 0){
            return RespBean.ok("删除成功");
        }else {
            return RespBean.error("删除失败");
        }
    }
}
