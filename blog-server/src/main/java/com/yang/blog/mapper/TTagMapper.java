package com.yang.blog.mapper;

import com.yang.blog.entity.TTagEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface TTagMapper {
    //查询所有标签
    List<TTagEntity> getTagAll();
    //根据标签名称查询标签（模糊）
    List<TTagEntity> getTagByName(String name);
    //添加标签
    int addTag(TTagEntity entity);
    //根据编码修改标签
    int updTagById(TTagEntity entity);
    //根据编码删除标签
    int delTag(String id);

}
