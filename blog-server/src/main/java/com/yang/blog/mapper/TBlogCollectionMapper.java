package com.yang.blog.mapper;

import com.yang.blog.entity.TBlogCollectionEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface TBlogCollectionMapper {
    //批量添加收藏关系
    int insertBatch(List<TBlogCollectionEntity> users);
    //批量删除收藏关系
    int deleteBatch(List<TBlogCollectionEntity> users);
    //判断关系是否存在
    TBlogCollectionEntity isUserCollection(Long bID, Long uID);
}
