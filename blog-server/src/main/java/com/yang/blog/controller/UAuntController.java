package com.yang.blog.controller;

import com.yang.blog.service.UAuntService;
import com.yang.blog.utils.RespBean;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/aunt")
public class UAuntController {
    Logger log = Logger.getLogger(String.valueOf(UAuntController.class));
    @Autowired
    private UAuntService auntService;

    @GetMapping("/all")
    private RespBean getTypeAll(){
        return auntService.findNameAll();
    }

    @GetMapping("/selectByNameAll")
    private Map<String,Object> selectByNameAll(@RequestParam("uName") String uName,@RequestParam("uStatus") String uStatus){
        return auntService.selectByNameAll(uName,uStatus);
    }

    @PostMapping("/addAunt")
    @ApiOperation("add-aunt")
    private RespBean addAunt(@RequestBody HashMap<String,Object> entity){
        return auntService.addAunt(entity);
    }

    @PostMapping("/updAunt")
    @ApiOperation("upd-aunt")
    private RespBean updAunt(@RequestBody HashMap<String,Object> entity){
        return auntService.updAunt(entity);
    }
}
