package com.yang.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yang.blog.entity.TBlogEntity;
import com.yang.blog.entity.TBlogTagEntity;
import com.yang.blog.entity.TTagEntity;
import com.yang.blog.mapper.TBlogMapper;
import com.yang.blog.mapper.TTagMapper;
import com.yang.blog.service.TBlogService;
import com.yang.blog.utils.CommonDate;
import com.yang.blog.utils.RedisUtils;
import com.yang.blog.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TBlogServiceImpl implements TBlogService {
    @Autowired
    private TBlogMapper blogMapper;

    @Autowired
    private TTagMapper tagMapper;

    @Override
    public RespBean getBlogByPage( Boolean ispublic, String isoriginal, Boolean issue, Boolean isdel) {
        List<Map<String,Object>> tBlogPage = blogMapper.getBlogByPage(ispublic,isoriginal,issue,isdel);
        for (int j=0;j<tBlogPage.size();j++){
            Map<String,Object> map = new HashMap<>();
            List<TTagEntity> blog = new ArrayList<>();
            if(tBlogPage.get(j).get("tagjson") == null)
                break;
            String tags = (String) tBlogPage.get(j).get("tagjson").toString().replaceAll(" ", "");
            int a = tags.length();
            String tag = tags.substring(1,a-1);
            String[] t = tag.split(",");
            List<TTagEntity> l = tagMapper.getTagById(t);
            map.put("t",l);
            tBlogPage.get(j).put("blogTags",map);
        }
        return RespBean.ok("ok",tBlogPage);
    }

    @Override
    public RespBean getBlogByPageTitle(Boolean ispublic, String isoriginal, Boolean issue, Boolean isdel, String title) {
        List<Map<String,Object>> tBlogPage = blogMapper.getBlogByPageTitle(ispublic,isoriginal,issue,isdel,title);
        for (int j=0;j<tBlogPage.size();j++){
            Map<String,Object> map = new HashMap<>();
            List<TTagEntity> blog = new ArrayList<>();
            if(tBlogPage.get(j).get("tagjson") == null)
                break;
            String tags = (String) tBlogPage.get(j).get("tagjson").toString().replaceAll(" ", "");
            int a = tags.length();
            String tag = tags.substring(1,a-1);
            String[] t = tag.split(",");
            List<TTagEntity> l = tagMapper.getTagById(t);
            map.put("t",l);
            tBlogPage.get(j).put("blogTags",map);
        }
        return RespBean.ok("ok",tBlogPage);
    }

    @Override
    public RespBean getBlogByID(String bID) {
        TBlogEntity blogEntity = blogMapper.getBlogByID(bID);
        if(blogEntity == null){
            return RespBean.error("未查询到博客内容");
        }else {
            String tags = blogEntity.getTagjson().replaceAll(" ", "");
            int a = tags.length();
            String tag = tags.substring(1,a-1);
            String[] t = tag.split(",");
            blogEntity.setBlogTags(tagMapper.getTagById(t));
            return RespBean.ok("ok",blogEntity);
        }
    }

    @Override
    public RespBean addBlog(HashMap<String,Object> entity) {
        TBlogEntity blogEntity = new TBlogEntity();
        blogEntity.setId(CommonDate.getStamp());
        blogEntity.setCreatetime(CommonDate.getTime24());

        blogEntity.setCover((String) entity.get("cover"));
        Integer flag = (Integer) entity.get("isoriginal");
        if(flag == 1){
            blogEntity.setIsoriginal("原创");
        }else{
            blogEntity.setIsoriginal("转载");
        }
        Integer pub = (Integer) entity.get("ispublic");
        if(pub == 1){
            blogEntity.setIspublic(true);
            blogEntity.setIscomment(true);
        }else {
            blogEntity.setIspublic(false);
            blogEntity.setIscomment(false);
        }
        blogEntity.setIssue((Boolean) entity.get("issue"));
        blogEntity.setDescription((String) entity.get("description"));
        blogEntity.setTitle((String) entity.get("title"));
        String user = (String) entity.get("userid");
        blogEntity.setUserid(Long.valueOf(user));
        String typeid = (String) entity.get("typeid");
        blogEntity.setTypeid(Long.valueOf(typeid));
        blogEntity.setContent((String) entity.get("content"));
        ArrayList<String> tag = (ArrayList<String>) entity.get("blogTags");
        blogEntity.setTagjson(tag.toString());
        int status = blogMapper.addBlog(blogEntity);
        if(status > 0){
            return RespBean.ok("添加博客成功");
        }else{
            return RespBean.error("添加博客失败");
        }
    }

    @Override
    public RespBean saveBlog(HashMap<String, Object> entity) {
        TBlogEntity blogEntity = new TBlogEntity();
        blogEntity.setUpdtime(CommonDate.getTime24());
        blogEntity.setTitle((String) entity.get("title"));
        blogEntity.setContent((String) entity.get("content"));
        blogEntity.setDescription((String) entity.get("description"));
        blogEntity.setCover((String) entity.get("cover"));
        if(entity.get("typeid") instanceof String){
            String typeid = (String) entity.get("typeid");
            blogEntity.setTypeid(Long.valueOf(typeid));
        }else{
            blogEntity.setTypeid((Long) entity.get("typeid"));
        }
        Integer flag = (Integer) entity.get("isoriginal");
        if(flag == 1){
            blogEntity.setIsoriginal("原创");
        }else{
            blogEntity.setIsoriginal("转载");
        }
        Integer ispublic = (Integer) entity.get("ispublic");
        if(ispublic == 0){
            blogEntity.setIspublic(false);
            blogEntity.setIscomment(false);
        }else {
            blogEntity.setIspublic(true);
            blogEntity.setIscomment(true);
        }
        blogEntity.setIssue((Boolean) entity.get("issue"));
        String bID = (String) entity.get("id");
        blogEntity.setId(Long.valueOf(bID));
        if(entity.containsKey("tags")){
            ArrayList<String> tag = (ArrayList<String>) entity.get("tags");
            blogEntity.setTagjson(tag.toString());
        }else{
            blogEntity.setTagjson((String) entity.get("tagjson"));
        }
        int status = blogMapper.saveBlog(blogEntity);
        if(status > 0){
            return RespBean.ok("博客保存成功");
        }else{
            return RespBean.error("博客保存失败");
        }
    }

    @Override
    public RespBean delBlog(String bID) {
        int status = blogMapper.delBlog(bID);
        if(status > 0){
            return RespBean.ok("删除博客成功");
        }else{
            return RespBean.error("添加博客失败");
        }
    }

    @Override
    public RespBean delBlogH(String bID,Boolean isdel) {
        int status = blogMapper.delBlogH(bID,isdel);
        if(status > 0){
            return RespBean.ok("删除博客成功");
        }else{
            return RespBean.error("添加博客失败");
        }
    }
}
