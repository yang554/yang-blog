package com.yang.blog.controller;

import com.yang.blog.entity.NutritionBodyCompositionEntity;
import com.yang.blog.service.NutritionBodyCompositionService;
import com.yang.blog.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/nutrition")
public class NutritionBodyCompositionController {
    Logger log = Logger.getLogger(String.valueOf(NutritionBodyCompositionController.class));
    @Autowired
    private NutritionBodyCompositionService nutritionBodyCompositionService;

    //获取所有体成分记录
    @GetMapping("/all")
    public List<NutritionBodyCompositionEntity> findAll(){
        return nutritionBodyCompositionService.findAll();
    }

    //根据ID获取体成分记录
    @GetMapping("/findById")
    public RespBean findById(@RequestParam("id") String id){
        return nutritionBodyCompositionService.findById(id);
    }

    //根据ID获取体成分记录
    @GetMapping("/findByUser")
    public List<NutritionBodyCompositionEntity> findByUser(@RequestParam("uID") String uID){
        return nutritionBodyCompositionService.findByUser(uID);
    }

    //操作体成分记录
    @PostMapping("/operationNBC")
    public RespBean operationNBC(@RequestBody HashMap<String,Object> params){
        return nutritionBodyCompositionService.operationNBC(params);
    }

    //删除体成分记录
    @DeleteMapping("/delNBC")
    public RespBean delNBC(@RequestParam("id") String id){
        return nutritionBodyCompositionService.delNBC(id);
    }
}
