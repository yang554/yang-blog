package com.yang.blog.mapper;

import com.yang.blog.entity.TBlogLikeEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface TBlogLikeMapper {
    //批量添加点赞关系
    int insertBatch(List<TBlogLikeEntity> users);
    //批量删除点赞关系
    int deleteBatch(List<TBlogLikeEntity> users);
    //判断关系是否存在
    TBlogLikeEntity isUserLike(Long bID,Long uID);
}
