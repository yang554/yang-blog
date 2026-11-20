package com.yang.blog.controller;

import com.yang.blog.entity.DailyFlowInfoEntity;
import com.yang.blog.service.DailyFlowInfoService;
import com.yang.blog.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/DailyFlowInfoController")
public class DailyFlowInfoController {
    Logger log = Logger.getLogger(String.valueOf(DailyFlowInfoController.class));
    @Autowired
    private DailyFlowInfoService service;

    //获取所有记录
    @GetMapping("/all")
    public List<DailyFlowInfoEntity> findAll(){
        return service.findAll();
    }

    //根据条件获取记录
    @GetMapping("/findByFactor")
    public List<DailyFlowInfoEntity> findByFactor(@RequestParam(value = "fID") String fID){
        return service.findByFactor(fID);
    }

    //根据条件获取记录
    @GetMapping("/findByFactorInfo")
    public List<DailyFlowInfoEntity> findByFactorInfo(@RequestParam(value = "searchDate1") String searchDate1, @RequestParam(value = "searchDate2") String searchDate2, @RequestParam(value = "searchType") String searchType,@RequestParam(value = "searchTypeTag") String searchTypeTag,@RequestParam(value = "id") String id,@RequestParam(value = "queryCost") String queryCost){
        return service.findByFactor(searchDate1,searchDate2,searchType,searchTypeTag,id,queryCost);
    }

    //批量添加
    @PostMapping("/insertBatch")
    public RespBean insertBatch(@RequestBody List<DailyFlowInfoEntity> entities){
        return service.insertBatch(entities);
    }
    //操作
    @PostMapping("/operationDailyFlowInfo")
    public RespBean operationDailyFlowInfo(@RequestBody HashMap<String,Object> params){
        return service.operationDailyFlowInfo(params);
    }

    //删除记录
    @DeleteMapping("/delFlowInfo")
    public RespBean delFlow(@RequestParam("id") String id){
        return service.delFlowInfo(id);
    }
}
