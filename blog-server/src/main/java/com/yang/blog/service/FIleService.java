package com.yang.blog.service;

import com.yang.blog.entity.FileEntity;
import com.yang.blog.utils.RespBean;

public interface FileService {
    RespBean uploadFile(FileEntity entity);
}
