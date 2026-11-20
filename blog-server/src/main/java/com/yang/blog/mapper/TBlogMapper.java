package com.yang.blog.mapper;

import com.yang.blog.entity.TBlogEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
@Transactional
public interface TBlogMapper {

    //查询
    List<Map<String,Object>> getBlogByPage(@Param(value = "page") int page,@Param(value = "size") int size,@Param(value = "ispublic") Boolean ispublic,
                                           @Param(value = "isoriginal") String isoriginal,
                                           @Param(value = "issue") Boolean issue,
                                           @Param(value = "isdel") Boolean isdel,
                                           @Param(value = "title") String title,
                                           @Param(value = "uID") String uID,
                                           @Param(value = "isLiked") String isLiked,
                                           @Param(value = "isCollection") String isCollection,
                                           @Param(value = "authorID") String authorID,
                                           @Param(value = "type") String type);
    //根据博客标题模糊查询
    List<Map<String,Object>> getBlogByPageTitle(@Param(value = "ispublic") Boolean ispublic,
                                               @Param(value = "isoriginal") String isoriginal,
                                               @Param(value = "issue") Boolean issue,
                                               @Param(value = "isdel") Boolean isdel,
                                               @Param(value = "title") String title,
                                                @Param(value = "uID") String uID,
                                                @Param(value = "isLiked") String isLiked,
                                                @Param(value = "isCollection") String isCollection,
                                                @Param(value = "type") String type);
    //条件查询
    List<Map<String,Object>> getBlogByParams(@Param(value = "title") String title,@Param(value = "uID") String uID,@Param(value = "tags") List<String> tags,@Param(value = "type") String type);
    //根据博客ID查询
    TBlogEntity getBlogByID(String bID);
    //根据用户查询博客
    List<TBlogEntity> getBlogByUser(String uID);
    //添加博客
    int addBlog(TBlogEntity entity);
    //编辑博客
    int saveBlog(TBlogEntity entity);
    //保存博客封面
    int saveBlogCover(@Param(value = "cover") String cover,@Param(value = "id") String id);
    //文章浏览量
    int addBlogBrowseCount(@Param(value = "bID")Object bID ,@Param(value = "value")Object value);
    //文章点赞量
    int addBlogLikesCount(@Param(value = "bID")Object bID ,@Param(value = "value")Object value);
    //文章收藏量
    int addBlogCollectionCount(@Param(value = "bID")Object bID ,@Param(value = "value")Object value);
    //删除博客(永久)
    int delBlog(String bID);
    //删除至回收站\还原
    int delBlogH(@Param(value = "bID")String bID,@Param(value = "isdel")Boolean isdel);
}
