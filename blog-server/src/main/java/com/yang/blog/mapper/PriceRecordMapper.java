
package com.yang.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yang.blog.entity.PriceRecordEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 价格记录Mapper接口
 */
@Repository
@Transactional
public interface PriceRecordMapper extends BaseMapper<PriceRecordEntity> {
    /**
     * 根据商品ID查询价格记录
     * @param productId 商品ID
     * @return 价格记录列表
     */
    List<PriceRecordEntity> selectByProductId(@Param("productId") Long productId);

    /**
     * 根据日期范围查询价格记录
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 价格记录列表
     */
    List<PriceRecordEntity> selectByDateRange(@Param("startDate") LocalDate startDate, 
                                              @Param("endDate") LocalDate endDate);

    /**
     * 查询商品在指定日期的价格
     * @param productId 商品ID
     * @param recordDate 记录日期
     * @return 价格记录
     */
    PriceRecordEntity selectByProductAndDate(@Param("productId") Long productId, 
                                             @Param("recordDate") LocalDate recordDate);

    /**
     * 分页查询价格记录
     * @param page 分页对象
     * @param productId 商品ID(可选)
     * @param startDate 开始日期(可选)
     * @param endDate 结束日期(可选)
     * @return 分页结果
     */
    IPage<PriceRecordEntity> selectPage(Page<PriceRecordEntity> page,
                                       @Param("productId") Long productId,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

    /**
     * 查询商品的最高价格记录
     * @param productId 商品ID
     * @return 最高价格记录
     */
    PriceRecordEntity selectMaxPriceByProduct(@Param("productId") Long productId);

    /**
     * 查询商品的最低价格记录
     * @param productId 商品ID
     * @return 最低价格记录
     */
    PriceRecordEntity selectMinPriceByProduct(@Param("productId") Long productId);

    /**
     * 查询商品的平均价格
     * @param productId 商品ID
     * @return 平均价格
     */
    Double selectAvgPriceByProduct(@Param("productId") Long productId);
}
