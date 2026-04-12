package com.yang.blog.service.impl;

import com.yang.blog.entity.TPrizeEntity;
import com.yang.blog.entity.TTagEntity;
import com.yang.blog.mapper.TPrizeMapper;
import com.yang.blog.mapper.TTagMapper;
import com.yang.blog.service.TPrizeService;
import com.yang.blog.service.TTagService;
import com.yang.blog.utils.CommonDate;
import com.yang.blog.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class TPrizeServiceImpl implements TPrizeService {
    @Autowired
    private TPrizeMapper prizeMapper;

    @Override
    public List<TPrizeEntity> findAll() {
        return prizeMapper.getAll();
    }

    @Override
    public RespBean operationPrize(List<TPrizeEntity> entity) {
        List<TPrizeEntity> listAll = prizeMapper.getAll();
        List<TPrizeEntity> insertData = new ArrayList<>();
        List<TPrizeEntity> updateData = new ArrayList<>();
        List<TPrizeEntity> deleteData = new ArrayList<>();
        try{
            // 首先对比两个集合的大小是否相同
            for (TPrizeEntity e : entity) {
                if(prizeMapper.findByID(String.valueOf(e.getId())) == null){
                    insertData.add(e);
                }else{
                    updateData.add(e);
                }
            }
            for (TPrizeEntity a : listAll) {
                boolean flag = false;
                for (TPrizeEntity e : entity) {
                    if(a.getId().equals(e.getId())){
                        flag = true;
                        break;
                    }
                }
                if(!flag){
                    deleteData.add(a);
                }
            }

            if(insertData.size() >0){
                prizeMapper.addPrize(insertData);
            }

            if(updateData.size() >0 ){
                prizeMapper.updPrize(updateData);
            }

            if(deleteData.size() >0){
                prizeMapper.delPrize(deleteData);
            }
            return RespBean.ok("添加成功！");
        }catch (Exception e){
            return RespBean.error("添加失败！"+e.getMessage());
        }
    }

    @Override
    public int updNumberByID(Long id, int number) {
        return prizeMapper.updNumberByID(id,number);
    }
}
