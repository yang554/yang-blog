
package com.yang.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yang.blog.entity.PriceProductEntity;
import com.yang.blog.mapper.PriceProductMapper;
import com.yang.blog.service.PriceProductService;
import com.yang.blog.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 商品信息Service实现类
 */
@Service
public class PriceProductServiceImpl implements PriceProductService {

    @Autowired
    private PriceProductMapper productMapper;

    @Override
    public RespBean addProduct(PriceProductEntity product) {
        try {
            // 设置默认值
            if (product.getIsDeleted() == null) {
                product.setIsDeleted(0);
            }
            int result = productMapper.insert(product);
            if (result > 0) {
                return RespBean.ok("添加商品成功", product);
            }
            return RespBean.error("添加商品失败");
        } catch (Exception e) {
            return RespBean.error("添加商品异常: " + e.getMessage());
        }
    }

    @Override
    public RespBean editProduct(PriceProductEntity product) {
        try {
            int result = productMapper.updateById(product);
            if (result > 0) {
                return RespBean.ok("编辑商品成功", product);
            }
            return RespBean.error("编辑商品失败");
        } catch (Exception e) {
            return RespBean.error("编辑商品异常: " + e.getMessage());
        }
    }

    @Override
    public RespBean deleteProduct(Long id) {
        try {
            // 软删除
            PriceProductEntity product = new PriceProductEntity();
            product.setId(id);
            product.setIsDeleted(1);
            int result = productMapper.updateById(product);
            if (result > 0) {
                return RespBean.ok("删除商品成功");
            }
            return RespBean.error("删除商品失败");
        } catch (Exception e) {
            return RespBean.error("删除商品异常: " + e.getMessage());
        }
    }

    @Override
    public PriceProductEntity getProductById(Long id) {
        QueryWrapper<PriceProductEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id).eq("is_deleted", 0);
        return productMapper.selectOne(queryWrapper);
    }

    @Override
    public List<PriceProductEntity> getAllProducts() {
        QueryWrapper<PriceProductEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0).orderByDesc("updated_at");
        return productMapper.selectList(queryWrapper);
    }

    @Override
    public List<PriceProductEntity> getProductsByName(String name) {
        return productMapper.selectByName(name);
    }

    @Override
    public List<PriceProductEntity> getProductsByBrand(String brand) {
        return productMapper.selectByBrand(brand);
    }

    @Override
    public List<PriceProductEntity> getProductsByRating(Integer rating) {
        return productMapper.selectByRating(rating);
    }

    @Override
    public IPage<PriceProductEntity> getProductsByPage(Page<PriceProductEntity> page, 
                                                      String name, 
                                                      String brand, 
                                                      Integer rating) {
        return productMapper.selectPage(page, name, brand, rating);
    }
}
