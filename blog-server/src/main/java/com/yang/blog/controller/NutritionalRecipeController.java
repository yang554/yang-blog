package com.yang.blog.controller;

import com.yang.blog.entity.NutritionalRecipeEntity;
import com.yang.blog.service.NutritionalRecipeService;
import com.yang.blog.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/NutritionalRecipe")
public class NutritionalRecipeController {
    Logger log = Logger.getLogger(String.valueOf(NutritionalRecipeController.class));
    @Autowired
    private NutritionalRecipeService service;

    //获取所有记录
    @GetMapping("/all")
    public List<NutritionalRecipeEntity> findAll(){
        return service.findAll();
    }

    //根据条件获取记录
    @GetMapping("/findByFactor")
    public List<NutritionalRecipeEntity> findByFactor(@RequestParam(value = "name") String name,@RequestParam(value = "type") String type){
        return service.findByFactor(name,type);
    }

    //根据ID获取记录
    @GetMapping("/findById")
    public RespBean findById(@RequestParam("id") String id){
        return service.findById(id);
    }

    //操作
    @PostMapping("/operationRecipe")
    public RespBean operationRecipe(@RequestBody HashMap<String,Object> params){
        return service.operationRecipe(params);
    }

    //删除记录
    @DeleteMapping("/delRecipe")
    public RespBean delRecipe(@RequestParam("id") String id){
        return service.delRecipe(id);
    }

}
