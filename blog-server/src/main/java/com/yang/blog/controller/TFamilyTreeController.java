package com.yang.blog.controller;

import com.yang.blog.entity.TFamilyTreeEntity;
import com.yang.blog.service.TFamilyTreeService;
import com.yang.blog.utils.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/TFamilyTreeController")
@Slf4j
public class TFamilyTreeController {
    @Autowired
    private TFamilyTreeService treeService;

    @GetMapping("/getFamilyTree")
    public List<TFamilyTreeEntity> getFamilyTree(){
        return treeService.getFamilyTree();
    }

    @GetMapping("/getAll")
    public List<TFamilyTreeEntity> getAll(){
        return treeService.getAll();
    }

    //批量操作记录
    @PostMapping("/operationData")
    public RespBean operationData(@RequestBody HashMap<String,Object> entity){
        return treeService.operationData(entity);
    }

    //删除记录
    @DeleteMapping("/delTree")
    public RespBean delTree(@RequestParam("id") String id){
        return treeService.delTree(Long.valueOf(id));
    }
}
