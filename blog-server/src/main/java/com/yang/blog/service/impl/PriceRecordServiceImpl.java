
package com.yang.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yang.blog.entity.PriceRecordEntity;
import com.yang.blog.mapper.PriceRecordMapper;
import com.yang.blog.service.PriceRecordService;
import com.yang.blog.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 价格记录Service实现类
 */
@Service
public class PriceRecordServiceImpl implements PriceRecordService {

    @Autowired
    private PriceRecordMapper recordMapper;

    @Override
    public RespBean addRecord(PriceRecordEntity record) {
        try {
            // 检查是否已存在该商品在指定日期的价格记录
            PriceRecordEntity existingRecord = recordMapper.selectByProductAndDate(
                record.getProductId(), record.getRecordDate());

            if (existingRecord != null) {
                // 如果已存在，则更新
                existingRecord.setPrice(record.getPrice());
                int result = recordMapper.updateById(existingRecord);
                if (result > 0) {
                    return RespBean.ok("更新价格记录成功", existingRecord);
                }
                return RespBean.error("更新价格记录失败");
            } else {
                // 如果不存在，则新增
                int result = recordMapper.insert(record);
                if (result > 0) {
                    return RespBean.ok("添加价格记录成功", record);
                }
                return RespBean.error("添加价格记录失败");
            }
        } catch (Exception e) {
            return RespBean.error("添加价格记录异常: " + e.getMessage());
        }
    }

    @Override
    public RespBean editRecord(PriceRecordEntity record) {
        try {
            int result = recordMapper.updateById(record);
            if (result > 0) {
                return RespBean.ok("编辑价格记录成功", record);
            }
            return RespBean.error("编辑价格记录失败");
        } catch (Exception e) {
            return RespBean.error("编辑价格记录异常: " + e.getMessage());
        }
    }

    @Override
    public RespBean deleteRecord(Long id) {
        try {
            int result = recordMapper.deleteById(id);
            if (result > 0) {
                return RespBean.ok("删除价格记录成功");
            }
            return RespBean.error("删除价格记录失败");
        } catch (Exception e) {
            return RespBean.error("删除价格记录异常: " + e.getMessage());
        }
    }

    @Override
    public PriceRecordEntity getRecordById(Long id) {
        return recordMapper.selectById(id);
    }

    @Override
    public List<PriceRecordEntity> getRecordsByProductId(Long productId) {
        return recordMapper.selectByProductId(productId);
    }

    @Override
    public List<PriceRecordEntity> getRecordsByDateRange(LocalDate startDate, LocalDate endDate) {
        return recordMapper.selectByDateRange(startDate, endDate);
    }

    @Override
    public PriceRecordEntity getRecordByProductAndDate(Long productId, LocalDate recordDate) {
        return recordMapper.selectByProductAndDate(productId, recordDate);
    }

    @Override
    public IPage<PriceRecordEntity> getRecordsByPage(Page<PriceRecordEntity> page,
                                                     Long productId,
                                                     LocalDate startDate,
                                                     LocalDate endDate) {
        return recordMapper.selectPage(page, productId, startDate, endDate);
    }

    @Override
    public PriceRecordEntity getMaxPriceByProduct(Long productId) {
        return recordMapper.selectMaxPriceByProduct(productId);
    }

    @Override
    public PriceRecordEntity getMinPriceByProduct(Long productId) {
        return recordMapper.selectMinPriceByProduct(productId);
    }

    @Override
    public Double getAvgPriceByProduct(Long productId) {
        return recordMapper.selectAvgPriceByProduct(productId);
    }
}
