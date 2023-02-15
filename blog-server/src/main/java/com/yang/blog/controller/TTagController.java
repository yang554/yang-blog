package com.yang.blog.controller;

import com.yang.blog.entity.TTagEntity;
import com.yang.blog.service.TTagService;
import com.yang.blog.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/tag")
public class TTagController {
    @Autowired
    private TTagService tagService;

    @GetMapping("/all")
    private RespBean getTagAll(){
        return tagService.getTagAll();
    }

    @GetMapping("/getTagByName/{name}")
    private RespBean getTagByName(@PathVariable("name") String name){
        return tagService.getTagByName(name);
    }
    @PostMapping("/addTag")
    private RespBean addTag(@RequestBody HashMap<String,String> tName){
        String tagName = tName.get("tName");
        return tagService.addTag(tagName);
    }
    @PostMapping("/updTagById")
    private RespBean updTagById(@RequestBody TTagEntity entity){
        return tagService.updTagById(entity);
    }
    @DeleteMapping("/delTagById")
    private RespBean delTagById(@RequestParam String id){
        return tagService.delTag(id);
    }
}
