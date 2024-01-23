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
        return blogService.getBlogByPage(ispublic,isoriginal,issue,isdel);
    }

    @GetMapping("/getBlogByPageTitle")
    @ApiOperation("博客模糊查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ispublic",value = "是否公开"),
            @ApiImplicitParam(name = "isoriginal",value = "原创或转载"),
            @ApiImplicitParam(name = "issue",value = "草稿"),
            @ApiImplicitParam(name = "isdel",value = "是否已删除"),
            @ApiImplicitParam(name = "title",value = "标题"),
    })
    public RespBean getBlogByPageTitle(@RequestParam(value = "ispublic",required = false) Boolean ispublic,
                                  @RequestParam(value = "isoriginal",required = false) String isoriginal,
                                  @RequestParam(value = "issue",required = false) Boolean issue,
                                  @RequestParam(value = "isdel",required = false) Boolean isdel,
                                  @RequestParam(value = "title",required = false) String title){
        return blogService.getBlogByPageTitle(ispublic,isoriginal,issue,isdel,title);
    }


    @PostMapping("/addBlog")
    @ApiOperation("添加博客")
    private RespBean addBlog(@RequestBody HashMap<String,Object> entity){
        return blogService.addBlog(entity);
    }

    @DeleteMapping("/delBlog")
    @ApiOperation("删除博客（永久删除）")
    private RespBean delBlog(@RequestParam String bID){
        return blogService.delBlog(bID);
    }

    @DeleteMapping("/delBlogH")
    @ApiOperation("删除博客进回收站")
    private RespBean delBlogH(@RequestParam(value = "bID") String bID,@RequestParam(value = "isdel") Boolean isdel){
        return blogService.delBlogH(bID,isdel);
    }

    @GetMapping("getBlogByID")
    @ApiImplicitParam(name = "bID",value = "通过bid查询")
    @ApiOperation("博客分页查询")
    private RespBean getBlogByID(@RequestParam("bID") String bID){
        return blogService.getBlogByID(bID);
    }

    @PostMapping("/saveBlog")
    @ApiOperation("保存博客")
    private RespBean saveBlog(@RequestBody HashMap<String,Object> entity){
        return blogService.saveBlog(entity);
    }

}
