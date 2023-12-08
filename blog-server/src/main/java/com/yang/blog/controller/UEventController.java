package com.yang.blog.controller;

import com.yang.blog.service.UEventService;
import com.yang.blog.utils.RespBean;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.logging.Logger;

@RestController
@RequestMapping("/event")
public class UEventController {
    Logger log = Logger.getLogger(String.valueOf(UAuntController.class));

    @Autowired
    private UEventService eventService;

    @GetMapping("/getEventAll")
    @ApiOperation("获取事件")
    private RespBean getEventAll(@RequestParam(value = "type",required = false) String type,
                                 @RequestParam(value = "eTitle",required = false) String eTitle,
                                 @RequestParam(value = "uName",required = false) String uName,
                                 @RequestParam(value = "createName",required = false) String createName){
        return eventService.getEventByPage(type,eTitle,uName,createName);
    }

    @PostMapping("/addEvent")
    @ApiOperation("添加事件")
    private RespBean addEvent(@RequestBody HashMap<String,Object> entity){
        return eventService.addEvent(entity);
    }
}
