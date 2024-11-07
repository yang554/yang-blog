package com.yang.blog.controller;

import com.yang.blog.entity.FoodNutritionEntity;
import com.yang.blog.service.FoodNutritionService;
import com.yang.blog.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/foodNutrition")
public class FoodNutritionController {
    Logger log = Logger.getLogger(String.valueOf(FoodNutritionController.class));
    @Autowired
    private FoodNutritionService foodNutritionService;

    //获取所有食物
    @GetMapping("/all")
    public List<FoodNutritionEntity> findAll(){
        return foodNutritionService.findAll();
    }

    //根据条件筛选食物
    @GetMapping("/findByFactor")
    public List<FoodNutritionEntity> findByFactor(@RequestParam("param") String param){
        return foodNutritionService.findByFactor(param);
    }

    //添加、修改记录
    @PostMapping("/operationFood")
    public RespBean operationFood(@RequestBody HashMap<String,Object> params){
        return foodNutritionService.operationFood(params);
    }

    //删除体成分记录
    @DeleteMapping("/delFood")
    public RespBean delFood(@RequestParam("id") String id){
        return foodNutritionService.delFood(id);
    }
}
