package com.yang.blog.controller;

import com.yang.blog.entity.NutritionalRecipesEntity;
import com.yang.blog.service.NutritionalRecipesService;
import com.yang.blog.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/NutritionalRecipes")
public class NutritionalRecipesController {
    Logger log = Logger.getLogger(String.valueOf(NutritionalRecipesController.class));
    @Autowired
    private NutritionalRecipesService service;

    //获取所有记录
    @GetMapping("/all")
    public List<NutritionalRecipesEntity> findAll(){
        return service.findAll();
    }

    //根据条件获取记录
    @GetMapping("/findByFactor")
    public List<NutritionalRecipesEntity> findByFactor(@RequestParam(value = "name") String name,@RequestParam(value = "type") String type,@RequestParam(value = "target") String target){
        return service.findByFactor(name,type,target);
    }

    //根据ID获取记录
    @GetMapping("/findById")
    public RespBean findById(@RequestParam("id") String id){
        return service.findById(id);
    }

    //操作
    @PostMapping("/operationRecipes")
    public RespBean operationRecipes(@RequestBody HashMap<String,Object> params){
        return service.operationRecipes(params);
    }

    //删除记录
    @DeleteMapping("/delRecipes")
    public RespBean delRecipes(@RequestParam("id") String id){
        return service.delRecipes(id);
    }

}
