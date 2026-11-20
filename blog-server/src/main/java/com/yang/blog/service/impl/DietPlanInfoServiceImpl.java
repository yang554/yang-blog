package com.yang.blog.service.impl;

import com.yang.blog.entity.DietPlanInfoEntity;
import com.yang.blog.mapper.DietPlanInfoMapper;
import com.yang.blog.service.DietPlanInfoService;
import com.yang.blog.utils.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class DietPlanInfoServiceImpl implements DietPlanInfoService {
    @Autowired
    private DietPlanInfoMapper mapper;

    @Override
    public List<DietPlanInfoEntity> findAll() {
        return null;
    }

    @Override
    public List<DietPlanInfoEntity> findByFactor(String planID) {
        return mapper.findByFactor(planID);
    }

    @Override
    public RespBean operationData(List<DietPlanInfoEntity> entity) {
        try {
            List<DietPlanInfoEntity> listAll = mapper.findByFactor(String.valueOf(entity.get(0).getPlanID()));
            List<DietPlanInfoEntity> insertData = new ArrayList<>();
            List<DietPlanInfoEntity> updateData = new ArrayList<>();
            List<DietPlanInfoEntity> deleteData = new ArrayList<>();

            // 将listAll转换为Map以便快速查找
            Map<Long, DietPlanInfoEntity> mapAll = new HashMap<>();
            for (DietPlanInfoEntity e : listAll) {
                mapAll.put(e.getId(), e);
            }

            Set<Long> processedIds = new HashSet<>();
            for (DietPlanInfoEntity e : entity) {
                Long id = e.getId();
                DietPlanInfoEntity itemAll = mapAll.get(id);
                if (itemAll != null) {
                    // 相同ID的数据
                    updateData.add(e);
                    processedIds.add(id);
                } else {
                    insertData.add(e);
                }
            }

            // 需要删除的数据
            for (DietPlanInfoEntity e : listAll) {
                if (!processedIds.contains(e.getId())) {
                    deleteData.add(e);
                }
            }

            if(insertData.size() >0){
                mapper.insertBatch(insertData);
            }

            if(updateData.size() >0 ){
                mapper.updateBatch(updateData);
            }

            if(deleteData.size() >0){
                mapper.deleteBatch(deleteData);
            }
            return RespBean.ok("添加饮食计划！");
        }catch (Exception e){
            return RespBean.error("添加饮食计划出错啦！"+e.getMessage());
        }

    }
}
