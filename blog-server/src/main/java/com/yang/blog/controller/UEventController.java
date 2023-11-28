package com.yang.blog.controller;

import com.yang.blog.service.UEventService;
import com.yang.blog.utils.RespBean;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.logging.Logger;

@RestController
@RequestMapping("/event")
public class UEventController {
    Logger log = Logger.getLogger(String.valueOf(UAuntController.class));

    @Autowired
    private UEventService eventService;

    @PostMapping("/addEvent")
    @ApiOperation("添加事件")
    private RespBean addEvent(@RequestBody HashMap<String,Object> entity){
        return eventService.addEvent(entity);
    }
}
