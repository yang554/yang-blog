package com.yang.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yang.blog.entity.TypeEntity;
import com.yang.blog.mapper.TypeMapper;
import com.yang.blog.service.TypeService;
import com.yang.blog.utils.CommonDate;
import com.yang.blog.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

@Service
public class TypeServiceImpl implements TypeService {
    @Autowired
    private TypeMapper typeMapper;
    @Override
    public RespBean getTypeAll() {
        List<TypeEntity> typeEntities = typeMapper.getTypeAll();
        return RespBean.ok("ok",typeEntities);
    }

    @Override
    public RespBean getTypeByPage(Integer currentPage, Integer pageSize) {
        Page<TypeEntity> pager = new Page<>(currentPage, pageSize);
        IPage<TypeEntity> tTypeIPage = null;
        List<TypeEntity> records = tTypeIPage.getRecords();
        long total = tTypeIPage.getTotal();
        long size = tTypeIPage.getSize();
        long pages = tTypeIPage.getPages();

        return RespBean.ok(tTypeIPage);
    }

    @Override
    public RespBean getTypeById(String id) {
        return null;
    }

    @Override
    public RespBean getTypeByName(String name) {
        List<TypeEntity> typeEntities = typeMapper.getTypeByName(name);
        return RespBean.ok("ok",typeEntities);
    }

    @Override
    public RespBean addType(String name) {
        TypeEntity entity = new TypeEntity();
        System.out.println(CommonDate.getStamp());
        entity.setId(CommonDate.getStamp());
        entity.setName(name);
        int status = typeMapper.addType(entity);
        if(status > 0){
            return RespBean.ok("分类 添加成功");
        }else {
            return RespBean.error("分类 添加失败");
        }
    }

    @Override
    public RespBean updTypeById(TypeEntity entity) {
        int status = typeMapper.updTypeById(entity);
        if(status > 0){
            return RespBean.ok("修改分类成功");
        }else {
            return RespBean.error("修改分类失败");
        }
    }

    @Override
    public RespBean delTypeById(String id) {
        int status = typeMapper.delTypeById(id);
        if(status > 0){
            return RespBean.ok("删除分类成功");
        }else {
            return RespBean.error("删除分类失败");
        }
    }

    @Override
    public RespBean delType() {
        return null;
    }

    @Override
    public RespBean getCountByType() {
        List<Map<String, Integer>> ret = typeMapper.getCountByType();
        if(ObjectUtils.isEmpty(ret)){
            return RespBean.error("查询失败");
        }else{
            return RespBean.ok("ok",ret);
        }
    }
}
