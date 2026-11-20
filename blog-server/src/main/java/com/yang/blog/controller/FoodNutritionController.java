package com.yang.blog.controller;

import com.yang.blog.entity.FoodNutritionEntity;
import com.yang.blog.service.FoodNutritionService;
import com.yang.blog.utils.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/foodNutrition")
@Slf4j
public class FoodNutritionController {
    @Autowired
    private FoodNutritionService foodNutritionService;

    //获取所有食物
    @GetMapping("/all")
    public List<FoodNutritionEntity> findAll(){
        long startTime = System.currentTimeMillis();
        log.info("开始处理请求: {}"+startTime);
        long serviceStart = System.currentTimeMillis();
        List<FoodNutritionEntity> list =  foodNutritionService.findAll();
        long serviceEnd = System.currentTimeMillis();
        log.info("[PERF] Service层耗时: {}ms, 数据量: {}",serviceEnd - serviceStart, list.size());

        return list;
    }

    //根据条件筛选食物
    @GetMapping("/findByFactor")
    public List<FoodNutritionEntity> findByFactor(@RequestParam("param") String param){
        long startTime = System.currentTimeMillis();
        log.info("开始处理请求: {}"+startTime);
        long serviceStart = System.currentTimeMillis();
        List<FoodNutritionEntity> list = foodNutritionService.findByFactor(param);
        long serviceEnd = System.currentTimeMillis();
        log.info("[PERF] Service层耗时: {}ms, 数据量: {}",serviceEnd - serviceStart, list.size());

        return list;
    }

    //批量添加
    @PostMapping("/insertBatch")
    public RespBean insertBatch(@RequestBody List<FoodNutritionEntity> entities){
        return foodNutritionService.insertBatch(entities);
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
