package com.yang.blog.controller;

import com.yang.blog.entity.TPrizeEntity;
import com.yang.blog.entity.TypeEntity;
import com.yang.blog.service.TPrizeService;
import com.yang.blog.service.TypeService;
import com.yang.blog.utils.RespBean;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * 奖品控制器类
 * 处理与奖品相关的HTTP请求
 */
@RestController
@RequestMapping("/prize")
public class TPrizeController {
    // 日志记录器
    Logger log = Logger.getLogger(String.valueOf(TPrizeController.class));
    // 自动注入奖品服务
    @Autowired
    private TPrizeService service;

    /**
     * 获取所有奖品信息
     * @return 返回所有奖品的列表
     */
    @GetMapping("/all")
    private List<TPrizeEntity> findAll(){
        return service.findAll();
    }

    //批量添加
    @PostMapping("/operationPrize")
    public RespBean operationPrize(@RequestBody List<TPrizeEntity> entities){
        return service.operationPrize(entities);
    }

    @GetMapping("/dram")
    private RespBean dram(){
        List<TPrizeEntity> listData = service.findAll();
        List<TPrizeEntity> newData = new ArrayList<>();
        for (TPrizeEntity e : listData) {
            if(e.getNumber() > 0){
                newData.add(e);
            }
        }
        int totalWeight = 0;
        for (TPrizeEntity e : newData) {
            log.info(e.getId()+"===="+e.getName()+"====="+e.getWeight()+"===="+e.getNumber());
            totalWeight += e.getWeight();
        }
        log.info("总权重："+totalWeight);
        // 验证权重合法性
        if (totalWeight <= 0) {
            return RespBean.error("奖品权重配置错误");
        }

        // 3. 生成随机数
        SecureRandom secureRandom = new SecureRandom();
        double randomValue = secureRandom.nextDouble() * totalWeight;
//        int randomValue = new Random().nextInt(totalWeight);
        // 4. 遍历匹配中奖奖品
        double accumulateWeight = 0.0;
        for (TPrizeEntity prize : newData) {
            accumulateWeight += prize.getWeight();
            log.info("商品名称："+prize.getName()+"======商品权重："+prize.getWeight());
            if (randomValue <= accumulateWeight) {
                log.info("随机数是："+randomValue+"=======权重是："+accumulateWeight);
                service.updNumberByID(prize.getId(),prize.getNumber()-1);
                return RespBean.ok(prize);
            }
        }
        return RespBean.error("抽奖逻辑异常");
    }
}
