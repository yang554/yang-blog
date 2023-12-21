package com.yang.blog.mapper;

import com.yang.blog.entity.CountEntity;
import com.yang.blog.entity.USourceEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
@Transactional
public interface USourceMapper {
    //查询所有用户
    List<USourceEntity> selectSourceAll();
    //根据name查询用户
    List<USourceEntity> selectUserByName(@Param(value = "s_name")String s_name);
    //根据key查询用户姓名
    String selectUserByKey(@Param(value = "key")String key);
    //根据keys查询用户
    List<USourceEntity> selectSourceByKeys(List<String> list);
    //分页查询所有配偶信息
    List<USourceEntity> selectSourceAllFY(@Param(value = "start")Integer start, @Param(value = "length")Integer length, @Param(value = "search") String search);
    //根据性别查询男女比例
    List<Map<String,Integer>> getUserCountBySex();
    //根据性别查询男女比例（嫡系）
    List<Map<String,Integer>> getUserCountBySexDX();
    //根据代系获取人口数量
    List<Map<String,Integer>> getUserCountByLove();
    //根据代系获取男女比例
    List<Map<String,Integer>> getUserCountByLoveAndSex();
    //条件过滤后的数量
    int selectRecordsFiltered(@Param(value = "search") String search);
    //查询总数量
    int selectRecordsTotal();
    //根据key修改用户头像
    int updateImgByKey(@Param(value = "avatar") String avatar,@Param(value = "id") String id);
    //根据key修改用户信息
    int updateUserByKey(USourceEntity user);
    //添加用户
    int insertUserSource(USourceEntity list);
    //根据key修改用户PID
    int updatePidByKey(@Param(value = "key") String key,@Param(value = "Pid") String Pid);
    //根据key修改用户MID
    int updateMidByKey(@Param(value = "key") String key,@Param(value = "Mid") String Mid);
    //根据key修改用户parents
    int updateParentsByKey(@Param(value = "key") String key,@Param(value = "Pid") String Pid,@Param(value = "Mid") String Mid);
    //根据key修改用户MATE
    int updateMateByKey(@Param(value = "id") String id,@Param(value = "key") String key);
    //根据KEY删除用户
    int deleteUserByKey(List<String> list);
}
