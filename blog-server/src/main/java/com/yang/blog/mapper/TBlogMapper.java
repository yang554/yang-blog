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
    List<Map<String,String>> getBlogByPage(@Param(value = "ispublic") Boolean ispublic,
                                           @Param(value = "isoriginal") String isoriginal,
                                           @Param(value = "issue") Boolean issue,
                                           @Param(value = "isdel") Boolean isdel);

    //添加博客
    int addBlog(TBlogEntity entity);
}
