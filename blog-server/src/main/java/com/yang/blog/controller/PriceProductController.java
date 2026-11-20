
package com.yang.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yang.blog.entity.PriceProductEntity;
import com.yang.blog.service.PriceProductService;
import com.yang.blog.utils.RespBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品信息Controller
 */
@RestController
@RequestMapping("/product")
@Slf4j
@Api(tags = "商品管理")
public class PriceProductController {

    @Autowired
    private PriceProductService productService;

    /**
     * 添加商品
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加商品", notes = "添加一个新的商品")
    public RespBean addProduct(@RequestBody PriceProductEntity product) {
        log.info("添加商品: {}", product.getName());
        return productService.addProduct(product);
    }

    /**
     * 编辑商品
     */
    @PutMapping("/edit")
    @ApiOperation(value = "编辑商品", notes = "编辑商品信息")
    public RespBean editProduct(@RequestBody PriceProductEntity product) {
        log.info("编辑商品: {}", product.getId());
        return productService.editProduct(product);
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除商品", notes = "根据ID删除商品(软删除)")
    public RespBean deleteProduct(@PathVariable Long id) {
        log.info("删除商品: {}", id);
        return productService.deleteProduct(id);
    }

    /**
     * 根据ID查询商品
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "查询商品", notes = "根据ID查询商品详情")
    public RespBean getProductById(@PathVariable Long id) {
        PriceProductEntity product = productService.getProductById(id);
        if (product != null) {
            return RespBean.ok("查询成功", product);
        }
        return RespBean.error("商品不存在");
    }

    /**
     * 查询所有商品
     */
    @GetMapping("/all")
    @ApiOperation(value = "查询所有商品", notes = "查询所有未删除的商品")
    public RespBean getAllProducts() {
        List<PriceProductEntity> products = productService.getAllProducts();
        return RespBean.ok("查询成功", products);
    }

    /**
     * 根据商品名称模糊查询
     */
    @GetMapping("/search/name")
    @ApiOperation(value = "按名称查询商品", notes = "根据商品名称模糊查询")
    public RespBean getProductsByName(
            @ApiParam(value = "商品名称", required = true) @RequestParam String name) {
        List<PriceProductEntity> products = productService.getProductsByName(name);
        return RespBean.ok("查询成功", products);
    }

    /**
     * 根据品牌查询商品
     */
    @GetMapping("/search/brand")
    @ApiOperation(value = "按品牌查询商品", notes = "根据品牌查询商品")
    public RespBean getProductsByBrand(
            @ApiParam(value = "品牌", required = true) @RequestParam String brand) {
        List<PriceProductEntity> products = productService.getProductsByBrand(brand);
        return RespBean.ok("查询成功", products);
    }

    /**
     * 根据评分查询商品
     */
    @GetMapping("/search/rating")
    @ApiOperation(value = "按评分查询商品", notes = "根据星级评分查询商品")
    public RespBean getProductsByRating(
            @ApiParam(value = "评分(1-5)", required = true) @RequestParam Integer rating) {
        List<PriceProductEntity> products = productService.getProductsByRating(rating);
        return RespBean.ok("查询成功", products);
    }

    /**
     * 分页查询商品
     */
    @GetMapping("/page")
    @ApiOperation(value = "分页查询商品", notes = "分页查询商品列表，支持按名称、品牌、规格、评分筛选")
    public RespBean getProductsByPage(
            @ApiParam(value = "页码", required = true) @RequestParam(defaultValue = "1") Integer current,
            @ApiParam(value = "每页数量", required = true) @RequestParam(defaultValue = "10") Integer size,
            @ApiParam(value = "商品名称") @RequestParam(required = false) String name,
            @ApiParam(value = "品牌") @RequestParam(required = false) String brand,
            @ApiParam(value = "评分") @RequestParam(required = false) Integer rating) {

        Page<PriceProductEntity> page = new Page<>(current, size);
        IPage<PriceProductEntity> result = productService.getProductsByPage(page, name, brand, rating);
        return RespBean.ok("查询成功", result);
    }
}
