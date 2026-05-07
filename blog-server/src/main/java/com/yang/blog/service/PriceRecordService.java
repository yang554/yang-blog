
package com.yang.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yang.blog.entity.PriceRecordEntity;
import com.yang.blog.utils.RespBean;

import java.time.LocalDate;
import java.util.List;

/**
 * 价格记录Service接口
 */
public interface PriceRecordService {
    /**
     * 添加价格记录
     * @param record 价格记录实体
     * @return 操作结果
     */
    RespBean addRecord(PriceRecordEntity record);

    /**
     * 编辑价格记录
     * @param record 价格记录实体
     * @return 操作结果
     */
    RespBean editRecord(PriceRecordEntity record);

    /**
     * 删除价格记录
     * @param id 记录ID
     * @return 操作结果
     */
    RespBean deleteRecord(Long id);

    /**
     * 根据ID查询价格记录
     * @param id 记录ID
     * @return 价格记录实体
     */
    PriceRecordEntity getRecordById(Long id);

    /**
     * 根据商品ID查询价格记录
     * @param productId 商品ID
     * @return 价格记录列表
     */
    List<PriceRecordEntity> getRecordsByProductId(Long productId);

    /**
     * 根据日期范围查询价格记录
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 价格记录列表
     */
    List<PriceRecordEntity> getRecordsByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 查询商品在指定日期的价格
     * @param productId 商品ID
     * @param recordDate 记录日期
     * @return 价格记录
     */
    PriceRecordEntity getRecordByProductAndDate(Long productId, LocalDate recordDate);

    /**
     * 分页查询价格记录
     * @param page 分页对象
     * @param productId 商品ID(可选)
     * @param startDate 开始日期(可选)
     * @param endDate 结束日期(可选)
     * @return 分页结果
     */
    IPage<PriceRecordEntity> getRecordsByPage(Page<PriceRecordEntity> page,
                                             Long productId,
                                             LocalDate startDate,
                                             LocalDate endDate);

    /**
     * 查询商品的最高价格记录
     * @param productId 商品ID
     * @return 最高价格记录
     */
    PriceRecordEntity getMaxPriceByProduct(Long productId);

    /**
     * 查询商品的最低价格记录
     * @param productId 商品ID
     * @return 最低价格记录
     */
    PriceRecordEntity getMinPriceByProduct(Long productId);

    /**
     * 查询商品的平均价格
     * @param productId 商品ID
     * @return 平均价格
     */
    Double getAvgPriceByProduct(Long productId);
}
