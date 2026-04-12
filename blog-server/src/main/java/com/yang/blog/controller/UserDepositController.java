package com.yang.blog.controller;

import com.yang.blog.entity.UserDepositEntity;
import com.yang.blog.service.UserDepositService;
import com.yang.blog.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/UserDeposit")
public class UserDepositController {
    Logger log = Logger.getLogger(String.valueOf(UserDepositController.class));

    @Autowired
    private UserDepositService userDepositService;

    @GetMapping("/findAll")
    private List<UserDepositEntity> findAll(){
        return userDepositService.findAll();
    }

    @GetMapping("/getUserDepositById")
    private UserDepositEntity getUserDepositById(@RequestParam("id") String id){
        return userDepositService.findByUserId(id);
    }

    @GetMapping("/getUserDepositByName")
    private List<UserDepositEntity> getUserDepositByName(@RequestParam("name") String name){
        return userDepositService.findByName(name);
    }

    //批量添加
    @PostMapping("/updUserDepositById")
    public RespBean updUserDepositById(@RequestBody UserDepositEntity entities){
        return userDepositService.updUserDepositById(entities);
    }

}
