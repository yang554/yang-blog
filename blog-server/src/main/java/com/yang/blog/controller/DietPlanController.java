package com.yang.blog.controller;

import com.yang.blog.entity.DietPlanEntity;
import com.yang.blog.entity.DietPlanInfoEntity;
import com.yang.blog.service.DietPlanInfoService;
import com.yang.blog.service.DietPlanService;
import com.yang.blog.utils.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dietPlan")
@Slf4j
public class DietPlanController {
    @Autowired
    private DietPlanService service;

    @Autowired
    private DietPlanInfoService serviceInfo;

    @GetMapping("/getDietPlanByUid")
    public DietPlanEntity getDietPlanByUid( @RequestParam(value = "createID") String createID,@RequestParam(value = "createDate") String createDate){
        return service.findByIdAndDate(createID,createDate);
    }

    @GetMapping("/getDietPlanByDateScope")
    public List<DietPlanEntity> getDietPlanByDateScope(@RequestParam(value = "createID") String createID, @RequestParam(value = "startDate") String startDate, @RequestParam(value = "endDate") String endDate){
        return service.getRawData(createID,startDate,endDate);
    }
    //批量操作记录
    @PostMapping("/operationDietPlan")
    public RespBean operationDietPlan(@RequestBody DietPlanEntity request ){
        try {
            String op = String.valueOf(request.getOp());

            int status = 0;
            if("add".equals(op)){
                status = service.insertData(request);
            }else if("upd".equals(op)){
                status = service.updateData(request);
            }
            if(status > 0 && request.getPlanInfoEntities().size() > 0){
                serviceInfo.operationData(request.getPlanInfoEntities());
            }

            log.info(String.valueOf(request));
            return RespBean.ok("饮食计划");
        }catch (Exception e){
            return RespBean.error(e.getMessage());
        }
    }
}
