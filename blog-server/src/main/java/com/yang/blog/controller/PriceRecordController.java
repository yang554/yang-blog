
package com.yang.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yang.blog.entity.PriceRecordEntity;
import com.yang.blog.service.PriceRecordService;
import com.yang.blog.utils.RespBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 价格记录Controller
 */
@RestController
@RequestMapping("/price")
@Slf4j
@Api(tags = "价格记录管理")
public class PriceRecordController {

    @Autowired
    private PriceRecordService recordService;

    /**
     * 添加价格记录
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加价格记录", notes = "添加商品的价格记录，如果该商品在指定日期已有记录则更新")
    public RespBean addRecord(@RequestBody PriceRecordEntity record) {
        log.info("添加价格记录: 商品ID={}, 日期={}, 价格={}", 
                record.getProductId(), record.getRecordDate(), record.getPrice());
        return recordService.addRecord(record);
    }

    /**
     * 编辑价格记录
     */
    @PutMapping("/edit")
    @ApiOperation(value = "编辑价格记录", notes = "编辑价格记录信息")
    public RespBean editRecord(@RequestBody PriceRecordEntity record) {
        log.info("编辑价格记录: {}", record.getId());
        return recordService.editRecord(record);
    }

    /**
     * 删除价格记录
     */
    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除价格记录", notes = "根据ID删除价格记录")
    public RespBean deleteRecord(@PathVariable Long id) {
        log.info("删除价格记录: {}", id);
        return recordService.deleteRecord(id);
    }

    /**
     * 根据ID查询价格记录
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "查询价格记录", notes = "根据ID查询价格记录详情")
    public RespBean getRecordById(@PathVariable Long id) {
        PriceRecordEntity record = recordService.getRecordById(id);
        if (record != null) {
            return RespBean.ok("查询成功", record);
        }
        return RespBean.error("价格记录不存在");
    }

    /**
     * 根据商品ID查询价格记录
     */
    @GetMapping("/product/{productId}")
    @ApiOperation(value = "查询商品价格记录", notes = "根据商品ID查询所有价格记录")
    public RespBean getRecordsByProductId(@PathVariable Long productId) {
        List<PriceRecordEntity> records = recordService.getRecordsByProductId(productId);
        return RespBean.ok("查询成功", records);
    }

    /**
     * 根据日期范围查询价格记录
     */
    @GetMapping("/date/range")
    @ApiOperation(value = "按日期范围查询价格记录", notes = "查询指定日期范围内的价格记录")
    public RespBean getRecordsByDateRange(
            @ApiParam(value = "开始日期", required = true) 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam(value = "结束日期", required = true) 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<PriceRecordEntity> records = recordService.getRecordsByDateRange(startDate, endDate);
        return RespBean.ok("查询成功", records);
    }

    /**
     * 查询商品在指定日期的价格
     */
    @GetMapping("/product/{productId}/date/{recordDate}")
    @ApiOperation(value = "查询商品指定日期价格", notes = "查询商品在指定日期的价格记录")
    public RespBean getRecordByProductAndDate(
            @PathVariable Long productId,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate recordDate) {
        PriceRecordEntity record = recordService.getRecordByProductAndDate(productId, recordDate);
        if (record != null) {
            return RespBean.ok("查询成功", record);
        }
        return RespBean.error("该日期无价格记录");
    }

    /**
     * 分页查询价格记录
     */
    @GetMapping("/page")
    @ApiOperation(value = "分页查询价格记录", notes = "分页查询价格记录列表，支持按商品ID、日期范围筛选")
    public RespBean getRecordsByPage(
            @ApiParam(value = "页码", required = true) @RequestParam(defaultValue = "1") Integer current,
            @ApiParam(value = "每页数量", required = true) @RequestParam(defaultValue = "10") Integer size,
            @ApiParam(value = "商品ID") @RequestParam(required = false) Long productId,
            @ApiParam(value = "开始日期") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam(value = "结束日期") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        Page<PriceRecordEntity> page = new Page<>(current, size);
        IPage<PriceRecordEntity> result = recordService.getRecordsByPage(page, productId, startDate, endDate);
        return RespBean.ok("查询成功", result);
    }

    /**
     * 查询商品的价格统计信息
     */
    @GetMapping("/product/{productId}/stats")
    @ApiOperation(value = "查询商品价格统计", notes = "查询商品的最高价、最低价和平均价")
    public RespBean getPriceStats(@PathVariable Long productId) {
        PriceRecordEntity maxPrice = recordService.getMaxPriceByProduct(productId);
        PriceRecordEntity minPrice = recordService.getMinPriceByProduct(productId);
        Double avgPrice = recordService.getAvgPriceByProduct(productId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("maxPrice", maxPrice);
        stats.put("minPrice", minPrice);
        stats.put("avgPrice", avgPrice);

        return RespBean.ok("查询成功", stats);
    }
}
