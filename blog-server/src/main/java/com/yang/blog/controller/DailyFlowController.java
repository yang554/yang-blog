package com.yang.blog.controller;

import com.yang.blog.entity.DailyFlowEntity;
import com.yang.blog.entity.UConsumptionCountEntity;
import com.yang.blog.service.DailyFlowService;
import com.yang.blog.utils.RespBean;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/DailyFlowController")
public class DailyFlowController {
    Logger log = Logger.getLogger(String.valueOf(DailyFlowController.class));
    @Autowired
    private DailyFlowService service;

    //获取所有记录
    @GetMapping("/all")
    public List<DailyFlowEntity> findAll(){
        return service.findAll();
    }

    //获取所有记录
    @GetMapping("/findAllById")
    public List<DailyFlowEntity> findAllById(@RequestParam(value = "id") String id){
        return service.findAllById(id);
    }

    //根据条件获取记录
    @GetMapping("/findByFactor")
    public List<DailyFlowEntity> findByFactor(@RequestParam(value = "searchDate1") String searchDate1,@RequestParam(value = "searchDate2") String searchDate2,@RequestParam(value = "searchType") String searchType,@RequestParam(value = "searchName") String searchName,@RequestParam(value = "queryCost") String queryCost){
        return service.findByFactor(searchDate1,searchDate2,searchType,searchName,queryCost);
    }

    //统计收支情况
    @GetMapping("/getConsumptionCount")
    public List<UConsumptionCountEntity> getConsumptionCount(@RequestParam(value = "search") String search, @RequestParam(value = "consumptionType") String consumptionType, @RequestParam(value = "getCount") String getCount, @RequestParam(value = "queryId") String queryId){
        return service.getConsumptionCount(search,consumptionType,getCount,queryId);
    }

    //统计最近一天的收支情况明细
    @GetMapping("/getConsumptionCountDInfo")
    public List<UConsumptionCountEntity> getConsumptionCountDInfo( @RequestParam(value = "consumptionType") String consumptionType,@RequestParam(value = "consumptionTime") String consumptionTime, @RequestParam(value = "queryId") String queryId){
        return service.getConsumptionCountDInfo(consumptionType,consumptionTime,queryId);
    }

    //统计最近一月的收支情况明细
    @GetMapping("/getConsumptionCountMInfo")
    public List<UConsumptionCountEntity> getConsumptionCountMInfo( @RequestParam(value = "consumptionType") String consumptionType,@RequestParam(value = "consumptionTime") String consumptionTime, @RequestParam(value = "queryId") String queryId){
        return service.getConsumptionCountMInfo(consumptionType,consumptionTime,queryId);
    }

    //统计最近一年的收支情况明细
    @GetMapping("/getConsumptionCountYInfo")
    public List<UConsumptionCountEntity> getConsumptionCountYInfo( @RequestParam(value = "consumptionType") String consumptionType,@RequestParam(value = "consumptionTime") String consumptionTime, @RequestParam(value = "queryId") String queryId){
        return service.getConsumptionCountYInfo(consumptionType,consumptionTime,queryId);
    }

    //条件查询获取原始数据
    @GetMapping("/getRawData")
    public List<UConsumptionCountEntity> getRawData( @RequestParam(value = "userId") String userId,@RequestParam(value = "timeRange") String timeRange,
                                                     @RequestParam(value = "year") String year,@RequestParam(value = "month") String month,
                                                     @RequestParam(value = "startDate") String startDate,@RequestParam(value = "endDate") String endDate){
        return service.getRawData(userId,timeRange,year,month,startDate,endDate);
    }

    // 获取可用年份
    @GetMapping("/years/{userId}")
    public RespBean getAvailableYears(@PathVariable Long userId) {
        try {
            List<Integer> years = service.selectAvailableYears(userId);
            return RespBean.ok(years);
        } catch (Exception e) {
            log.info("获取可用年份失败");
            return RespBean.error(e.getMessage());
        }
    }

    //操作
    @PostMapping("/operationDailyFlow")
    public RespBean operationDailyFlow(@RequestBody HashMap<String,Object> params){
        return service.operationDailyFlow(params);
    }

    //删除记录
    @DeleteMapping("/delFlow")
    public RespBean delFlow(@RequestParam("id") String id){
        return service.delFlow(id);
    }
}
