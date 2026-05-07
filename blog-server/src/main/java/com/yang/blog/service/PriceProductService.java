
package com.yang.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yang.blog.entity.PriceProductEntity;
import com.yang.blog.utils.RespBean;

import java.util.List;

/**
 * 商品信息Service接口
 */
public interface PriceProductService {
    /**
     * 添加商品
     * @param product 商品实体
     * @return 操作结果
     */
    RespBean addProduct(PriceProductEntity product);

    /**
     * 编辑商品
     * @param product 商品实体
     * @return 操作结果
     */
    RespBean editProduct(PriceProductEntity product);

    /**
     * 删除商品(软删除)
     * @param id 商品ID
     * @return 操作结果
     */
    RespBean deleteProduct(Long id);

    /**
     * 根据ID查询商品
     * @param id 商品ID
     * @return 商品实体
     */
    PriceProductEntity getProductById(Long id);

    /**
     * 查询所有商品
     * @return 商品列表
     */
    List<PriceProductEntity> getAllProducts();

    /**
     * 根据商品名称模糊查询
     * @param name 商品名称
     * @return 商品列表
     */
    List<PriceProductEntity> getProductsByName(String name);

    /**
     * 根据品牌查询商品
     * @param brand 品牌
     * @return 商品列表
     */
    List<PriceProductEntity> getProductsByBrand(String brand);

    /**
     * 根据评分查询商品
     * @param rating 评分
     * @return 商品列表
     */
    List<PriceProductEntity> getProductsByRating(Integer rating);

    /**
     * 分页查询商品
     * @param page 分页对象
     * @param name 商品名称(可选)
     * @param brand 品牌(可选)
     * @param rating 评分(可选)
     * @return 分页结果
     */
    IPage<PriceProductEntity> getProductsByPage(Page<PriceProductEntity> page, 
                                                String name, 
                                                String brand, 
                                                Integer rating);
}
