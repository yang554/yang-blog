package com.yang.blog.service.impl;

import com.yang.blog.entity.NutritionRecipeFoodEntity;
import com.yang.blog.entity.NutritionalRecipeEntity;
import com.yang.blog.mapper.NutritionRecipeFoodMapper;
import com.yang.blog.mapper.NutritionalRecipeMapper;
import com.yang.blog.service.NutritionalRecipeService;
import com.yang.blog.utils.CommonDate;
import com.yang.blog.utils.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NutritionalRecipeServiceImpl  implements NutritionalRecipeService {
    @Autowired
    private NutritionalRecipeMapper mapper;

    @Autowired
    private NutritionRecipeFoodMapper foodMapper;

    @Override
    public List<NutritionalRecipeEntity> findAll() {
        return mapper.findAll();
    }

    @Override
    public List<NutritionalRecipeEntity> findAllByUserCol(String uID) {
        return mapper.findAllByUserCol(uID);
    }

    @Override
    public List<String> getFoodType() {
        List<String> types = mapper.getFoodType().stream().map(NutritionalRecipeEntity::getfType).collect(Collectors.toList());
        return types;
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
        // 处理配料数据
        List<NutritionRecipeFoodEntity> ingredients = new ArrayList<>();
        NutritionalRecipeEntity entity = new NutritionalRecipeEntity();
        entity.setfImg((String) hashMap.get("fImg"));
        entity.setfName((String) hashMap.get("fName"));
        entity.setfIntro((String) hashMap.get("fIntro"));
        entity.setfType((String) hashMap.get("fType"));
        entity.setCalories((String) hashMap.get("calories"));
        entity.setCarbs((String) hashMap.get("carbs"));
        entity.setProtein((String) hashMap.get("protein"));
        entity.setFat((String) hashMap.get("fat"));
        entity.setfBatching((String) hashMap.get("fBatching"));
        entity.setfSeasoning((String) hashMap.get("fSeasoning"));
        entity.setfMade((String) hashMap.get("fMade"));
        entity.setfTips((String) hashMap.get("fTips"));
        entity.setfUpdateTime(CommonDate.getTime24());

        long id = CommonDate.getStamp();
        String op = String.valueOf(hashMap.get("op"));
        if("add".equals(op)){
            entity.setfID(CommonDate.getStamp());
            entity.setfCreateTime(CommonDate.getTime24());
            // 1. 检查是否有食材数据
            if (hashMap.containsKey("ingredients")) {
                Object ingredientsObj = hashMap.get("ingredients");
                // 2. 处理不同类型的配料数据结构
                if (ingredientsObj instanceof List) {
                    List<?> ingredientsList = (List<?>) ingredientsObj;
                    // 3. 遍历配料列表
                    for (Object item : ingredientsList) {
                        if (item instanceof Map) {
                            // 3.1 Map类型的数据结构
                            Map<?, ?> ingredientMap = (Map<?, ?>) item;
                            NutritionRecipeFoodEntity ingredient = new NutritionRecipeFoodEntity();
                            // 3.2 从Map中提取数据并设置到实体
                            ingredient.setID(getLongValue(ingredientMap, "id"));
                            ingredient.setRecipeId(id);
                            ingredient.setFoodId(getLongValue(ingredientMap, "foodId"));
                            ingredient.setWeight(getIntValue(ingredientMap, "weight"));

                            ingredients.add(ingredient);
                        } else if (item instanceof NutritionRecipeFoodEntity) {
                            //如果已经是实体类型，直接添加
                            ingredients.add((NutritionRecipeFoodEntity) item);
                        }
                    }
                }
            }
            int f = foodMapper.insertBatch(ingredients);
            if(f > 0){
                int status = mapper.addRecipe(entity);
                if(status >=0){
                    return RespBean.ok("添加成功！");
                }else{
                    return RespBean.error("创建失败！");
                }
            }else{
                return RespBean.error("食谱食材缺失！");
            }
        }else if("upd".equals(op)){
            entity.setfID(Long.valueOf(String.valueOf(hashMap.get("fID"))) );
            // 1. 检查是否有食材数据
            if (hashMap.containsKey("ingredients")) {
                Object ingredientsObj = hashMap.get("ingredients");
                // 2. 处理不同类型的配料数据结构
                if (ingredientsObj instanceof List) {
                    List<?> ingredientsList = (List<?>) ingredientsObj;
                    // 3. 遍历配料列表
                    for (Object item : ingredientsList) {
                        if (item instanceof Map) {
                            // 3.1 Map类型的数据结构
                            Map<?, ?> ingredientMap = (Map<?, ?>) item;
                            NutritionRecipeFoodEntity ingredient = new NutritionRecipeFoodEntity();
                            // 3.2 从Map中提取数据并设置到实体
                            ingredient.setID(getLongValue(ingredientMap, "id"));
                            ingredient.setRecipeId(Long.valueOf(String.valueOf(hashMap.get("fID"))));
                            ingredient.setFoodId(getLongValue(ingredientMap, "foodId"));
                            ingredient.setWeight(getIntValue(ingredientMap, "weight"));

                            ingredients.add(ingredient);
                        } else if (item instanceof NutritionRecipeFoodEntity) {
                            //如果已经是实体类型，直接添加
                            ingredients.add((NutritionRecipeFoodEntity) item);
                        }
                    }
                }
            }
            foodMapper.delBatchByRecipeId(ingredients);//先删除
            foodMapper.insertBatch(ingredients);//在添加
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

    // 辅助方法：安全地从Map获取Long值
    private Long getLongValue(Map<?, ?> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value != null) {
            try {
                return Long.parseLong(value.toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    // 辅助方法：安全地从Map获取Double值
    private Double getDoubleValue(Map<?, ?> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value != null) {
            try {
                return Double.parseDouble(value.toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    // 辅助方法：安全地从Map获取INT值
    private Integer getIntValue(Map<?, ?> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value != null) {
            try {
                return Integer.parseInt(value.toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    // 辅助方法：安全地从Map获取String值
    private String getStringValue(Map<?, ?> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    @Override
    public RespBean delRecipe(String id) {
        int fs = foodMapper.delByRecipeId(id);
        if(fs <0)
            return RespBean.error("删除失败");
        int status = mapper.delRecipe(id);

        if(status > 0){
            return RespBean.ok("删除成功");
        }else {
            return RespBean.error("删除失败");
        }
    }
}
