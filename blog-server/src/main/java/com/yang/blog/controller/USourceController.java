package com.yang.blog.controller;

import com.yang.blog.entity.USourceEntity;
import com.yang.blog.service.USourceService;
import com.yang.blog.utils.CodeUtil;
import com.yang.blog.utils.PinYinUtil;
import com.yang.blog.utils.RespBean;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/source")
public class USourceController {
    Logger log = Logger.getLogger(String.valueOf(USourceController.class));

    @Autowired
    private USourceService sourceService;

    @GetMapping("/getSourceAll")
    @ApiOperation("获取所有成员")
    private List<USourceEntity> getSourceAll(){
        return sourceService.selectSourceAll();
    }

    @GetMapping("/getSourceCount")
    @ApiOperation("获取所有成员数量")
    private RespBean getSourceCount(){
        return sourceService.selectRecordsTotal();
    }

    @GetMapping("/getSourceCountByDX")
    @ApiOperation("获取嫡系成员数量")
    private RespBean getSourceCountByDX(@RequestParam("search") String search){
        return sourceService.selectRecordsFiltered(search);
    }

    @GetMapping("/getSourceByName")
    private List<USourceEntity> getSourceByName(@RequestParam("s_name") String s_name){
        return sourceService.selectUserByName(s_name);
    }
    @GetMapping("/getUserCountBySex")
    @ApiOperation("获取成员男女比例")
    private RespBean getUserCountBySex(){
        return sourceService.getUserCountBySex();
    }
    @GetMapping("/getUserCountBySexDX")
    @ApiOperation("获取成员男女比例")
    private RespBean getUserCountBySexDX(){
        return sourceService.getUserCountBySexDX();
    }
    @GetMapping("/getUserCountByLove")
    @ApiOperation("根据代系获取人口数量")
    private RespBean getUserCountByLove(){
        return sourceService.getUserCountByLove();
    }
    @GetMapping("/getUserCountByLoveAndSex")
    @ApiOperation("根据代系获取男女比例")
    private RespBean getUserCountByLoveAndSex(){
        return sourceService.getUserCountByLoveAndSex();
    }
    //添加配偶、子女
    @PostMapping("/addSource")
    public RespBean addSource(@RequestBody HashMap<String,String> params){
        USourceEntity userEntity = new USourceEntity();
        String id = CodeUtil.getRandomCode(10);
        String userID = params.get("userId").trim();
        String name = params.get("userName").trim();
        String sex = params.get("userSex").trim();
        String phone = params.get("userPhone").trim();
        String emil = params.get("userEmil").trim();
        String birthDay = params.get("userBirthDay").trim();
        String addressNew = params.get("userAddressNew").trim();
        String addressOld = params.get("userAddressOld").trim();
        String note = params.get("userNote").trim();
        String dieDay = params.get("userDieDay").trim();
        String isSurvival = params.get("isSurvival").trim();
        String userLove = params.get("userLove").trim();
        String type = params.get("type").trim();

        userEntity.setUserId(id);
        userEntity.setUserName(name);
        userEntity.setUserPinYin(PinYinUtil.getPinyin(name));
        userEntity.setUserSex(sex);
        userEntity.setUserPhone(phone);
        userEntity.setUserEmil(emil);
        userEntity.setUserBirthDay(birthDay);
        userEntity.setUserAddressNew(addressNew);
        userEntity.setUserAddressOld(addressOld);
        userEntity.setUserNote(note);
        userEntity.setUserDieDay(dieDay);
        if("是".equals(isSurvival)) userEntity.setIsSurvival("1");
        else userEntity.setIsSurvival("0");

        if("addMate".equals(type)){
            userEntity.setUserLove(userLove);
            userEntity.setUserMateId(userID);
            RespBean updStatus = sourceService.updateMateByKey(id,userID);
            RespBean addStatus = sourceService.insertUserSource(userEntity);
            return RespBean.ok("addMate is ok");
        }else if("addChildren".equals(type)){
            int love = Integer.parseInt(userLove) + 1;
            userEntity.setUserLove(Integer.toString(love));
            String PID = params.get("userPId").trim();
            String MID = params.get("userMID").trim();

            userEntity.setUserPId(PID);
            userEntity.setUserMID(MID);
            RespBean addStatus = sourceService.insertUserSource(userEntity);
            return RespBean.ok("addChildren is ok");
        }else {
            return RespBean.error("未知异常");
        }
    }

    //添加父母
    @RequestMapping("/addParents")
    @ResponseBody
    public RespBean addParents(@RequestBody List<USourceEntity> parentsList){
        USourceEntity userInfo;
        if(parentsList.size() == 2){
            List<USourceEntity> list = JSON.parseArray(JSON.toJSONString(parentsList),USourceEntity.class);
            String key = list.get(0).getUserId();//用户ID
            String pId = CodeUtil.getRandomCode(10);//父ID
            String mId = CodeUtil.getRandomCode(11);//母ID
            String userLove = list.get(0).getUserLove();
            int love = Integer.parseInt(userLove) - 1;

            list.get(0).setUserId(pId);
            list.get(0).setUserPinYin(PinYinUtil.getPinyin(list.get(0).getUserName()));
            list.get(0).setUserMateId(mId);
            list.get(0).setUserLove(Integer.toString(love));
            userInfo = list.get(0);
            sourceService.insertUserSource(userInfo);

            list.get(1).setUserId(mId);
            list.get(1).setUserPinYin(PinYinUtil.getPinyin(list.get(1).getUserName()));
            list.get(1).setUserMateId(pId);
            list.get(1).setUserLove(Integer.toString(love));
            userInfo = list.get(1);
            sourceService.insertUserSource(userInfo);

            sourceService.updateParentsByKey(key,pId,mId);
            return RespBean.ok("addParents is ok");
        }
        return null;

    }

    @PostMapping("/saveSource")
    @ApiOperation("保存成员")
    private RespBean saveSource(@RequestBody HashMap<String,Object> entity){
        return sourceService.updateUserByKey(entity);
    }

    @RequestMapping("/delSource")
    @ResponseBody
    @ApiOperation("删除成员")
    private RespBean delSource(@RequestBody  List<String> list){
        return sourceService.deleteUserByKey(list);
    }
}
