package com.yang.blog.service.impl;

import com.yang.blog.entity.TFamilyTreeEntity;
import com.yang.blog.mapper.TFamilyTreeMapper;
import com.yang.blog.mapper.USourceMapper;
import com.yang.blog.service.TFamilyTreeService;
import com.yang.blog.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class TFamilyTreeServiceImpl implements TFamilyTreeService {
    @Autowired
    private TFamilyTreeMapper treeMapper;
    @Autowired
    private USourceMapper sourceMapper;

    @Override
    public List<TFamilyTreeEntity> getFamilyTree() {
        return treeMapper.getFamilyTree();
    }

    @Override
    public List<TFamilyTreeEntity> getAll() {
        return treeMapper.getAll();
    }

    @Override
    public RespBean operationData(HashMap<String,Object> hashMap) {
        TFamilyTreeEntity treeEntity = new TFamilyTreeEntity();
        treeEntity.setId(Long.valueOf(String.valueOf(hashMap.get("id"))) );
        treeEntity.setName(String.valueOf(hashMap.get("name")));
        treeEntity.setDisplayName(String.valueOf(hashMap.get("displayName")));
        treeEntity.setGeneration(String.valueOf(hashMap.get("generation")));
        treeEntity.setCreateID(Long.valueOf(String.valueOf(hashMap.get("createID"))) );
        treeEntity.setRemarks(String.valueOf(hashMap.get("remarks")));

        String op = String.valueOf(hashMap.get("op"));
        if("add".equals(op)){
            int status = treeMapper.addTree(treeEntity);
            if(status >=0){
                return RespBean.ok("添加成功！");
            }else{
                return RespBean.error("创建失败！");
            }
        }else if("upd".equals(op)){
            int status = treeMapper.saveTree(treeEntity);
            if(status >=0){
                return RespBean.ok("修改成功！");
            }else{
                return RespBean.error("修改失败！");
            }
        }else{
            return RespBean.error("未知操作！！！");
        }
    }


    @Override
    public RespBean delTree(Long id) {
        int status = treeMapper.delTree(id);
        int s = -1;
        if(status > 0){
            s = sourceMapper.deleteUserByTreeId(id);
        }

        return s >= 0 ? RespBean.ok("成功") : RespBean.error("失败");
    }
}
