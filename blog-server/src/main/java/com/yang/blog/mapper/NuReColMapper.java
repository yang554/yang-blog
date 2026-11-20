package com.yang.blog.mapper;

import com.yang.blog.entity.NuReColEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface NuReColMapper {
    //批量添加收藏关系
    int insertBatch(List<NuReColEntity> entities);
    //批量删除收藏关系
    int deleteBatch(List<NuReColEntity> entities);
    //判断关系是否存在
    NuReColEntity isUserCol(Long fID,Long uID);
}
