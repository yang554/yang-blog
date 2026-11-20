package com.yang.blog.controller;

import com.yang.blog.entity.DietPlanInfoEntity;
import com.yang.blog.service.DietPlanInfoService;
import com.yang.blog.utils.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dietPlanInfo")
@Slf4j
public class DietPlanInfoController {
    @Autowired
    private DietPlanInfoService service;

    @GetMapping("/getDietPlanByPlanID")
    public List<DietPlanInfoEntity> getDietPlanByPlanID(@RequestParam(value = "planID") String planID){
        return service.findByFactor(planID);
    }
    //批量操作记录
    @PostMapping("/operationDietPlan")
    public RespBean operationDietPlan(@RequestBody List<DietPlanInfoEntity> hashMap){
        return service.operationData(hashMap);
    }
}
