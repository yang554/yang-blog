package com.yang.blog.mapper;

import com.yang.blog.entity.FileEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface FileMapper {
    int addFile(FileEntity entity);
}
