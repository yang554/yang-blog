package com.yang.blog.service;

import com.yang.blog.entity.USourceEntity;
import com.yang.blog.utils.RespBean;

import java.util.HashMap;
import java.util.List;

public interface USourceService {
    //查询所有用户
    List<USourceEntity> selectSourceAll();
    //根据name查询用户
    List<USourceEntity> selectUserByName(String s_name);
    //根据key查询用户姓名
    RespBean selectUserByKey(String key);
    //根据性别查询男女比例
    RespBean getUserCountBySex();
    //根据性别查询男女比例（嫡系）
    RespBean getUserCountBySexDX();
    //根据代系获取人口数量
    RespBean getUserCountByLove();
    //根据代系获取男女比例
    RespBean getUserCountByLoveAndSex();
    //根据key查询用户
    RespBean selectSourceByKeys(List<String> list);
    //分页查询用户
    RespBean selectSourceAllFY(Integer start, Integer length, String search);
    //条件过滤后的数量
    RespBean selectRecordsFiltered(String search);
    //查询总数量
    RespBean selectRecordsTotal();
    //添加用户
    RespBean insertUserSource(USourceEntity user);
    //根据key修改用户PID
    RespBean updatePidByKey(String key,String Pid);
    //根据key修改用户MID
    RespBean updateMidByKey(String key,String Pid);
    //根据key修改用户parents
    RespBean updateParentsByKey(String key,String Pid,String Mid);
    //根据key修改用户MATE
    RespBean updateMateByKey(String id,String key);
    //根据key修改用户头像
    RespBean updateImgByKey(String imgUrl,String key);
    //根据key修改用户信息
    RespBean updateUserByKey(HashMap<String,Object> user);
    //根据key删除用户信息
    RespBean deleteUserByKey(List<String> list);
}
