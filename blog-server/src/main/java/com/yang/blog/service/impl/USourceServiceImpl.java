package com.yang.blog.service.impl;

import com.yang.blog.entity.USourceEntity;
import com.yang.blog.mapper.USourceMapper;
import com.yang.blog.service.USourceService;
import com.yang.blog.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class USourceServiceImpl implements USourceService {
    @Autowired
    private USourceMapper sourceMapper;

    /**
     * 查询所有用户
     * @return
     */
    @Override
    public List<USourceEntity> selectSourceAll() {
        List<USourceEntity> sourceEntities = sourceMapper.selectSourceAll();

        for (int i=0;i<sourceEntities.size();i++){
            if("男".equals(sourceEntities.get(i).getUserSex()) && "0".equals(sourceEntities.get(i).getIsSurvival())){
                sourceEntities.get(i).setStyleNo("亡男");
            }else if ("男".equals(sourceEntities.get(i).getUserSex()) && "1".equals(sourceEntities.get(i).getIsSurvival())){
                sourceEntities.get(i).setStyleNo("活男");
            }else if ("女".equals(sourceEntities.get(i).getUserSex()) && "0".equals(sourceEntities.get(i).getIsSurvival())){
                sourceEntities.get(i).setStyleNo("亡女");
            }else if ("女".equals(sourceEntities.get(i).getUserSex()) && "1".equals(sourceEntities.get(i).getIsSurvival())){
                sourceEntities.get(i).setStyleNo("活女");
            }else{
                sourceEntities.get(i).setStyleNo("未知");
            }
            String mateIds = "";
            List<USourceEntity> list = null;
            if(!"".equals(sourceEntities.get(i).getUserMateId()) && sourceEntities.get(i).getUserMateId() != null){
                mateIds =  sourceEntities.get(i).getUserMateId();
                List<String> result = Arrays.stream(mateIds.split(","))
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
                list = sourceMapper.selectSourceByKeys(result);
            }
            sourceEntities.get(i).setList(list);
        }

        return sourceEntities;
    }

    /**
     * 根据name查询用户
     * @param s_name
     * @return
     */
    @Override
    public RespBean selectUserByName(String s_name) {
        USourceEntity sourceEntity = sourceMapper.selectUserByName(s_name);
        return RespBean.ok("ok",sourceEntity);
    }

    /**
     * 根据key查询用户姓名
     * @param key
     * @return
     */
    @Override
    public RespBean selectUserByKey(String key) {
        String name = sourceMapper.selectUserByKey(key);
        return RespBean.ok("ok",name);
    }

    /**
     * 根据key查询用户
     * @param list
     * @return
     */
    @Override
    public RespBean selectSourceByKeys(List<String> list) {
        List<USourceEntity> sourceEntities = sourceMapper.selectSourceByKeys(list);
        return RespBean.ok("ok",sourceEntities);
    }

    /**
     * 分页查询用户
     * @param start
     * @param length
     * @param search
     * @return
     */
    @Override
    public RespBean selectSourceAllFY(Integer start, Integer length, String search) {
        List<USourceEntity> sourceEntities = sourceMapper.selectSourceAllFY(start,length,search);
        return RespBean.ok("ok",sourceEntities);
    }

    /**
     * 条件过滤后的数量
     * @param search
     * @return
     */
    @Override
    public RespBean selectRecordsFiltered(String search) {
        int count = sourceMapper.selectRecordsFiltered(search);
        return RespBean.ok("ok",count);
    }

    /**
     * 查询总数量
     * @return
     */
    @Override
    public RespBean selectRecordsTotal() {
        int count = sourceMapper.selectRecordsTotal();
        return RespBean.ok("ok",count);
    }

    /**
     * 添加用户
     * @param user
     * @return
     */
    @Override
    public RespBean insertUserSource(USourceEntity user) {
        int count = sourceMapper.insertUserSource(user);
        return RespBean.ok("ok",count);
    }

    /**
     * 根据key修改用户PID
     * @param key
     * @param Pid
     * @return
     */
    @Override
    public RespBean updatePidByKey(String key, String Pid) {
        int count = sourceMapper.updatePidByKey(key,Pid);
        return RespBean.ok("ok",count);
    }

    /**
     * 根据key修改用户MID
     * @param key
     * @param Mid
     * @return
     */
    @Override
    public RespBean updateMidByKey(String key, String Mid) {
        int count = sourceMapper.updateMidByKey(key,Mid);
        return RespBean.ok("ok",count);
    }

    /**
     * 根据key修改用户parents
     * @param key
     * @param Pid
     * @param Mid
     * @return
     */
    @Override
    public RespBean updateParentsByKey(String key, String Pid, String Mid) {
        int count =  sourceMapper.updateParentsByKey(key,Pid,Mid);
        return RespBean.ok("ok",count);
    }

    /**
     * 根据key修改用户MATE
     * @param id
     * @param key
     * @return
     */
    @Override
    public RespBean updateMateByKey(String id, String key) {
        int count = sourceMapper.updateMateByKey(id,key);
        return RespBean.ok("ok",count);
    }

    /**
     *根据key修改用户头像
     * @param imgUrl
     * @param key
     * @return
     */
    @Override
    public RespBean updateImgByKey(String imgUrl, String key) {
        int count = sourceMapper.updateImgByKey(imgUrl,key);
        return RespBean.ok("ok",count);
    }

    /**
     * 根据key修改用户信息
     * @param user
     * @return
     */
    @Override
    public RespBean updateUserByKey(USourceEntity user) {
        int count = sourceMapper.updateUserByKey(user);
        return RespBean.ok("ok",count);
    }

    /**
     * 根据key删除用户信息
     * @param key
     * @return
     */
    @Override
    public RespBean deleteUserByKey(String key) {
        int count = sourceMapper.deleteUserByKey(key);
        return RespBean.ok("ok",count);
    }
}
