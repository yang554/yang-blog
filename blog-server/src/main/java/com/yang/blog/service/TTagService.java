package com.yang.blog.service;

import com.yang.blog.entity.TTagEntity;
import com.yang.blog.utils.RespBean;

public interface TTagService {
    RespBean getTagAll();
    RespBean getTagByName(String name);
    //添加标签
    RespBean addTag(String tName);
    //根据编码修改标签
    RespBean updTagById(TTagEntity entity);
    //根据编码删除标签
    RespBean delTag(String id);
}
