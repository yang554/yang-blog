package com.yang.blog.controller;

import com.yang.blog.entity.TypeEntity;
import com.yang.blog.service.TypeService;
import com.yang.blog.utils.RespBean;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/type")
public class TypeController {
    @Autowired
    private TypeService typeService;

    @GetMapping("/all")
    private RespBean getTypeAll(){
        return typeService.getTypeAll();
    }

    @GetMapping("/getTypeByName")
    @ApiOperation("通过name查询分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name",value = "分类name"),
    })
    private RespBean getTypeByName(@RequestParam String name){
        return typeService.getTypeByName(name);
    }

    @PostMapping("/add")
    private RespBean addType(@RequestBody HashMap<String,String> tName){
        String typeName = tName.get("tName");
        return typeService.addType(typeName);
    }

    @PostMapping("/updTypeById")
    private RespBean updTypeById(@RequestBody TypeEntity entity){
        return typeService.updTypeById(entity);
    }

    @DeleteMapping("/delTypeById")
    private RespBean delTypeById(@RequestParam String id){
        return typeService.delTypeById(id);
    }

    @GetMapping("/count/type")
    private RespBean getUserRoleAll(){
        return typeService.getCountByType();
    }
}
