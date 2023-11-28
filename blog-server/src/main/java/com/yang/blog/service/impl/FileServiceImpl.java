package com.yang.blog.service.impl;

import com.yang.blog.entity.FileEntity;
import com.yang.blog.mapper.FileMapper;
import com.yang.blog.service.FileService;
import com.yang.blog.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl implements FileService {
    @Autowired
    private FileMapper fileMapper;
    @Override
    public RespBean uploadFile(FileEntity entity) {
        int status = fileMapper.addFile(entity);
        if(status > 0){
            return RespBean.ok("ok");
        }else{
            return RespBean.error("error");
        }
    }
}
