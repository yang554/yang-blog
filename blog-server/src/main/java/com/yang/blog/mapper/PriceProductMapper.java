
package com.yang.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yang.blog.entity.PriceProductEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品信息Mapper接口
 */
@Repository
@Transactional
public interface PriceProductMapper extends BaseMapper<PriceProductEntity> {
    /**
     * 根据商品名称模糊查询
     * @param name 商品名称
     * @return 商品列表
     */
    List<PriceProductEntity> selectByName(@Param("name") String name);

    /**
     * 根据品牌查询商品
     * @param brand 品牌
     * @return 商品列表
     */
    List<PriceProductEntity> selectByBrand(@Param("brand") String brand);

    /**
     * 根据评分查询商品
     * @param rating 评分
     * @return 商品列表
     */
    List<PriceProductEntity> selectByRating(@Param("rating") Integer rating);

    /**
     * 分页查询商品列表
     * @param page 分页对象
     * @param name 商品名称(可选)
     * @param brand 品牌(可选)
     * @param rating 评分(可选)
     * @return 分页结果
     */
    IPage<PriceProductEntity> selectPage(Page<PriceProductEntity> page,
                                        @Param("name") String name,
                                        @Param("brand") String brand,
                                        @Param("rating") Integer rating);
}
