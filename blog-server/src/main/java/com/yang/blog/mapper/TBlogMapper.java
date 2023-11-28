package com.yang.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yang.blog.entity.TBlogEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Repository
@Transactional
public interface TBlogMapper {

    //查询
    List<Map<String,Object>> getBlogByPage(@Param(value = "ispublic") Boolean ispublic,
                                           @Param(value = "isoriginal") String isoriginal,
                                           @Param(value = "issue") Boolean issue,
                                           @Param(value = "isdel") Boolean isdel);
    //根据博客标题模糊查询
    List<Map<String,Object>> getBlogByPageTitle(@Param(value = "ispublic") Boolean ispublic,
                                               @Param(value = "isoriginal") String isoriginal,
                                               @Param(value = "issue") Boolean issue,
                                               @Param(value = "isdel") Boolean isdel,
                                               @Param(value = "title") String title);
    //根据博客ID查询
    TBlogEntity getBlogByID(String bID);
    //添加博客
    int addBlog(TBlogEntity entity);
    //编辑博客
    int saveBlog(TBlogEntity entity);
    //删除博客(永久)
    int delBlog(String bID);
    //删除至回收站
    int delBlogH(String bID);
}
