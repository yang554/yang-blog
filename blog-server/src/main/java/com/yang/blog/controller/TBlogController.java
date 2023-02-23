package com.yang.blog.controller;

import com.yang.blog.entity.TBlogEntity;
import com.yang.blog.service.TBlogService;
import com.yang.blog.utils.RespBean;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.logging.Logger;

@RestController
@RequestMapping("/blog")
public class TBlogController {
    Logger log = Logger.getLogger(String.valueOf(TBlogController.class));
    @Autowired
    private TBlogService blogService;

    @GetMapping("/getBlogByPage")
    @ApiOperation("博客分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ispublic",value = "是否公开"),
            @ApiImplicitParam(name = "isoriginal",value = "原创或转载"),
            @ApiImplicitParam(name = "issue",value = "草稿"),
            @ApiImplicitParam(name = "isdel",value = "是否已删除"),
    })
    public RespBean getBlogByPage(@RequestParam(value = "ispublic",required = false) Boolean ispublic,
                                   @RequestParam(value = "isoriginal",required = false) String isoriginal,
                                   @RequestParam(value = "issue",required = false) Boolean issue,
                                   @RequestParam(value = "isdel",required = false) Boolean isdel){
        System.out.println(ispublic+"========="+isoriginal+"======1111111======"+issue+"============"+isdel);
        return blogService.getBlogByPage(ispublic,isoriginal,issue,isdel);
    }

    @PostMapping("/addBlog")
    private RespBean addBlog(@RequestBody HashMap<String,Object> entity){
        System.out.println(entity);
        return blogService.addBlog(entity);
    }
}
