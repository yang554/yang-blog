package com.yang.blog.service;

import com.yang.blog.entity.TFamilyTreeEntity;
import com.yang.blog.utils.RespBean;

import java.util.HashMap;
import java.util.List;

public interface TFamilyTreeService {
    //查询所有族谱的统计信息
    List<TFamilyTreeEntity> getFamilyTree();
    List<TFamilyTreeEntity> getAll();
    RespBean operationData(HashMap<String,Object> entity);
    RespBean delTree(Long id);
}
