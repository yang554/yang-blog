package com.yang.blog.controller;

import com.yang.blog.entity.TTagEntity;
import com.yang.blog.entity.UConsumptionTypeEntity;
import com.yang.blog.service.TTagService;
import com.yang.blog.service.UConsumptionTypeService;
import com.yang.blog.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/consumption")
public class UConsumptionTypeController {
    @Autowired
    private UConsumptionTypeService consumptionTypeService;

    @GetMapping("/getConsumptionTypeAll")
    private RespBean getConsumptionTypeAll(){
        return consumptionTypeService.getConsumptionTypeAll();
    }

    @GetMapping("/getConsumptionTypeByName")
    private RespBean getConsumptionTypeByName(@RequestParam String name){
        return consumptionTypeService.getConsumptionTypeByName(name);
    }

    @GetMapping("/getMatchSource")
    public RespBean getMatchSource(@RequestParam String purpose) {
        List<String> categories = consumptionTypeService.matchCategory(purpose);
        return RespBean.ok(categories);
    }

    @PostMapping("/addConsumptionType")
    private RespBean addConsumptionType(@RequestBody HashMap<String,String> hashMap){
        return consumptionTypeService.addConsumptionType(hashMap);
    }
    @PostMapping("/updConsumptionTypeById")
    private RespBean updConsumptionTypeById(@RequestBody UConsumptionTypeEntity entity){
        return consumptionTypeService.updConsumptionTypeById(entity);
    }
    @DeleteMapping("/delConsumptionType")
    private RespBean delConsumptionType(@RequestParam String id){
        return consumptionTypeService.delConsumptionType(id);
    }
}
