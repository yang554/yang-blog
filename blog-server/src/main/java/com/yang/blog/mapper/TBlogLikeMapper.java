package com.yang.blog.mapper;

import com.yang.blog.entity.TBlogLikeEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface TBlogLikeMapper {
    //添加点赞
    int addLike(TBlogLikeEntity user);
}
