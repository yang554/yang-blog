package com.yang.blog.service.impl;

import com.yang.blog.entity.TBlogEntity;
import com.yang.blog.entity.TTagEntity;
import com.yang.blog.mapper.TBlogMapper;
import com.yang.blog.mapper.TTagMapper;
import com.yang.blog.service.TBlogLikeService;
import com.yang.blog.service.TBlogService;
import com.yang.blog.utils.CommonDate;
import com.yang.blog.utils.RedisUtils;
import com.yang.blog.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class TBlogServiceImpl implements TBlogService {
    @Autowired
    private TBlogMapper blogMapper;
    @Autowired
    private RedisUtils redisUtils ;
    @Autowired
    private TTagMapper tagMapper;

    @Override
    @Cacheable(
            value = "blogListCache",
            key = "#page + '-' + #size + '-' + #ispublic + '-' + #isoriginal + '-' + #issue + '-' + #isdel+ '-' + #title+ '-' + #uID+ '-' + #isLiked+ '-' + #isCollection+ '-' + #authorID+ '-' + #type"
    )
    public RespBean getBlogByPage( int page,int size,Boolean ispublic, String isoriginal, Boolean issue, Boolean isdel, String title,String uID,String isLiked,String isCollection,String authorID, String type) {
        List<Map<String,Object>> tBlogPage = blogMapper.getBlogByPage(page,size,ispublic,isoriginal,issue,isdel,title,uID,isLiked,isCollection,authorID,type);

        // 组装标签数据
        for (Map<String, Object> blog : tBlogPage) {
            if (blog.get("tagjson") == null) continue;
            String tags = blog.get("tagjson").toString().replaceAll(" ", "");
            String tagIds = tags.substring(1, tags.length() - 1);
            String[] t = tagIds.split(",");
            List<TTagEntity> l = tagMapper.getTagById(t);
            blog.put("blogTags", l);
        }
        return RespBean.ok("ok", tBlogPage);
    }

    @Override
    public RespBean getBlogByPageTitle(Boolean ispublic, String isoriginal, Boolean issue, Boolean isdel, String title,String uID,String isLiked,String isCollection, String type) {
        List<Map<String,Object>> tBlogPage = blogMapper.getBlogByPageTitle(ispublic,isoriginal,issue,isdel,title,uID,isLiked,isCollection,type);
        for (int j=0;j<tBlogPage.size();j++){
            List<TTagEntity> blog = new ArrayList<>();
            if(tBlogPage.get(j).get("tagjson") == null)
                break;
            String tags = (String) tBlogPage.get(j).get("tagjson").toString().replaceAll(" ", "");
            int a = tags.length();
            String tag = tags.substring(1,a-1);
            String[] t = tag.split(",");
            List<TTagEntity> l = tagMapper.getTagById(t);
            tBlogPage.get(j).put("blogTags",l);
        }
        return RespBean.ok("ok",tBlogPage);
    }

    @Override
    public RespBean getBlogByParams(String title, String uID, List<String> tags, String type) {
        List<Map<String,Object>> tBlogPage = blogMapper.getBlogByParams(title,uID,tags,type);

        // 组装标签数据
        for (Map<String, Object> blog : tBlogPage) {
            if (blog.get("tagjson") == null) continue;
            String tagjson = blog.get("tagjson").toString().replaceAll(" ", "");
            String tagIds = tagjson.substring(1, tagjson.length() - 1);
            String[] t = tagIds.split(",");
            List<TTagEntity> l = tagMapper.getTagById(t);
            blog.put("blogTags", l);
        }
        return RespBean.ok("ok", tBlogPage);
    }

    @Override
    public RespBean getBlogByUser(String uID) {
        List<TBlogEntity> list = blogMapper.getBlogByUser(uID);
        if(list.size() > 0)
            return RespBean.ok("ok",list);
        else
            return RespBean.error("getBlogByUser");
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
        boolean flag = (Boolean)  entity.get("isoriginal");
        if(flag){
            blogEntity.setIsoriginal("原创");
        }else{
            blogEntity.setIsoriginal("转载");
        }
        blogEntity.setIspublic((Boolean) entity.get("ispublic"));
        blogEntity.setIscomment(true);
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
            redisUtils.deleteByPrex("blogListCache::");//清空list缓存
            return RespBean.ok("添加博客成功");
        }else{
            return RespBean.error("添加博客失败");
        }
    }

    @Override
    @Transactional
//    @CacheEvict(value = {"blogListCache"}, key = "#blog.id") // 清除详情和标签缓存
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
        boolean flag = (Boolean)  entity.get("isoriginal");
        if(flag){
            blogEntity.setIsoriginal("原创");
        }else{
            blogEntity.setIsoriginal("转载");
        }
        blogEntity.setIspublic((Boolean) entity.get("ispublic"));
        blogEntity.setIscomment(true);
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
            String pattern = "blogListCache::" ;
            Set<String> keys = redisUtils.getKeyListByStr(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisUtils.deleteByPrex(pattern);
            }
            return RespBean.ok("博客保存成功");
        }else{
            return RespBean.error("博客保存失败");
        }
    }

    // 数据库基准值缓存（防止频繁查库）
    @Cacheable(value = "blogViews", key = "#blogID")
    public Long getBaseViewsFromCacheOrDB(String blogID) {
        String v = blogMapper.getBlogByID(blogID).getBrowsecount();
        return Long.parseLong(v);
    }

    @Override
    public RespBean addBlogBrowseCount(String bID) {
        try{
            // 1. 查询数据库基准值
            Long baseViews = getBaseViewsFromCacheOrDB(bID);
            // 2. 查询 Redis 增量值
            String key = "article:views:" + bID;
            redisUtils.incr(key);
            redisUtils.expire(key,60,TimeUnit.SECONDS);
            String redisViews = String.valueOf(redisUtils.get(key));
            Long increment = (!"".equals(redisViews)) ? Long.parseLong(redisViews) : 0L;

            return RespBean.ok("addBlogBrowseCount",baseViews + increment);
        }catch (Exception e){
            return RespBean.error(e.getMessage());
        }

    }

    // 每5分钟执行一次,同步文章浏览量
    @Scheduled(cron = "0 */1 * * * ?")
    @Transactional
    public void syncClicksToDB() {
        // 扫描所有文章浏览量的 Redis Key
        Set<String> keys = redisUtils.getKeyListByStr("article:views:");
        if (keys == null || keys.isEmpty()) return;
        for (String key : keys) {
            Long blogID = Long.parseLong(key.split(":")[2]);
            if(redisUtils.exists(key)){
                Object delta = redisUtils.get(key);
                if (delta == null) continue;
                redisUtils.deleteByPrex("article:views:");
                // 更新数据库
                blogMapper.addBlogBrowseCount(blogID,delta);
            }
        }
        redisUtils.deleteByPrex("blogListCache::");//清空list缓存
    }

    @Override
    public RespBean saveBlogCover(String cover, String id) {
        int status = blogMapper.saveBlogCover(cover,id);
        if(status > 0){
            redisUtils.deleteByPrex("blogListCache::");//清空list缓存
            return RespBean.ok("头像上传成功",cover);
        }else {
            return RespBean.error("头像上传失败");
        }
    }

    @Override
    public RespBean delBlog(String bID) {
        int status = blogMapper.delBlog(bID);
        String key = "article:views:" + bID;
        if(redisUtils.exists(key)){
            redisUtils.del(key);//删除缓存
            redisUtils.deleteByPrex("blogListCache::");//清空list缓存
        }
        if(status > 0){
            return RespBean.ok("删除博客成功");
        }else{
            return RespBean.error("删除博客失败");
        }
    }

    @Override
    public RespBean delBlogH(String bID,Boolean isdel) {
        int status = blogMapper.delBlogH(bID,isdel);
        if(status > 0){
            redisUtils.deleteByPrex("blogListCache::");//清空list缓存
            return RespBean.ok("删除博客成功");
        }else{
            return RespBean.error("删除博客失败");
        }
    }
}
