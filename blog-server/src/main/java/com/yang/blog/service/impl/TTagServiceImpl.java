package com.yang.blog.service.impl;

import com.yang.blog.entity.TTagEntity;
import com.yang.blog.mapper.TTagMapper;
import com.yang.blog.service.TTagService;
import com.yang.blog.utils.CommonDate;
import com.yang.blog.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TTagServiceImpl implements TTagService {
    @Autowired
    private TTagMapper tTagMapper;
    @Override
    public RespBean getTagAll() {
        List<TTagEntity> entities = tTagMapper.getTagAll();
        return RespBean.ok("ok",entities);
    }

    @Override
    public RespBean getTagByName(String name) {
        List<TTagEntity> entities = tTagMapper.getTagByName(name);
        return RespBean.ok("ok",entities);
    }

    @Override
    public RespBean addTag(String tName) {
        TTagEntity entity = new TTagEntity();
        entity.setId(CommonDate.getStamp());
        entity.setName(tName);
        int status = tTagMapper.addTag(entity);
        if(status > 0){
            return RespBean.ok("添加标签成功");
        }else {
            return RespBean.error("添加标签失败");
        }
    }

    @Override
    public RespBean updTagById(TTagEntity entity) {
        int status = tTagMapper.updTagById(entity);
        if(status > 0){
            return RespBean.ok("修改标签成功");
        }else {
            return RespBean.error("修改标签失败");
        }
    }

    @Override
    public RespBean delTag(String id) {
        int status = tTagMapper.delTag(id);
        if(status > 0){
            return RespBean.ok("删除标签成功");
        }else {
            return RespBean.error("删除标签失败");
        }
    }
}
