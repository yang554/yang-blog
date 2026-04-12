package com.yang.blog.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 写入缓存设置时效时间
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value, Long expireTime , TimeUnit timeUnit) {
        boolean result = false;
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, timeUnit);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 批量删除对应的value
     * @param keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }
    /**
     * 批量删除key
     * @param pattern
     */
    public void removePattern(final String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0){
            redisTemplate.delete(keys);
        }
    }

    /**
     * 删除对应的value
     * @param key
     */
    public void remove(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }
    /**
     * 判断缓存中是否有对应的value
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        try {
            Boolean boo = redisTemplate.hasKey(key);
            return boo != null && boo;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 哈希 添加
     * @param key
     * @param hashKey
     * @param value
     */
    public void hmSet(String key, Object hashKey, Object value){
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        hash.put(key,hashKey,value);
    }
    /**
     * 哈希获取数据
     * @param key
     * @param hashKey
     * @return
     */
    public Object hmGet(String key, Object hashKey){
        HashOperations<String, Object, Object>  hash = redisTemplate.opsForHash();
        return hash.get(key,hashKey);
    }
    /**
     * 列表添加
     * @param k
     * @param v
     */
    public void lPush(String k,Object v){
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.rightPush(k,v);
    }
    /**
     * 列表获取
     * @param k
     * @param l
     * @param l1
     * @return
     */
    public List<Object> lRange(String k, long l, long l1){
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.range(k,l,l1);
    }
    /**
     * 集合添加
     * @param key
     * @param value
     */
    public void add(String key,Object value){
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        set.add(key,value);
    }

    /**
     * 集合删除
     * @param key
     * @param value
     */
    public void remove(String key,Object value){
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        set.remove(key,value);
    }

    /**
     * 集合获取
     * @param key
     * @return
     */
    public Set<Object> setMembers(String key){
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        return set.members(key);
    }

    /**
     * 判断集合是否存在
     * @param key
     * @return
     */
    public boolean isMember(String key,Object o){
        Boolean isMember = redisTemplate.opsForSet().isMember(key, o.toString());
        return isMember;
    }



    /**
     * 有序集合添加
     * @param key
     * @param value
     * @param scoure
     */
    public void zAdd(String key,Object value,double scoure){
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        zset.add(key,value,scoure);
    }
    /**
     * 有序集合获取
     * @param key
     * @param scoure
     * @param scoure1
     * @return
     */
    public Set<Object> rangeByScore(String key, double scoure, double scoure1){
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        return zset.rangeByScore(key, scoure, scoure1);
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key, long time , TimeUnit timeUnit) {
        try {
            if (time > 0) {
                Boolean boo = redisTemplate.expire(key, time, timeUnit);
                return boo != null && boo;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    public boolean del(String... key) {
        try {
            if (key != null && key.length > 0) {
                if (key.length == 1) {
                    redisTemplate.delete(key[0]);
                } else {
                    redisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(key));

                }
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据前缀删除key
     * @param prex
     */
    public boolean deleteByPrex(String prex) {
        try {
            prex = prex+"**";
            Set<String> keys = redisTemplate.keys(prex);
            redisTemplate.delete(keys);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        try{
            return redisTemplate.opsForValue().get(key);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取hash所有键值对
     *
     * @param key 键
     * @return 值
     */
    public Map<Object,Object> getHash(String key){
//        Map<Object,Object> map = redisTemplate.opsForHash().entries(key);
        return  redisTemplate.opsForHash().entries(key);
    }

    /**
     * 删除hash某一个key
     *
     * @param key 键
     * @return 值
     */
    public boolean delHashKey(String key,String fieldKey) {
        try {
            redisTemplate.opsForHash().delete(key,fieldKey);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 加锁
     *
     * @param key         键
     * @param value         值
     * @param releaseTime 锁过期时间 秒
     * @return 结果
     */
    public boolean lock(String key, Object value, long releaseTime) {
        try {
            Boolean boo = redisTemplate.opsForValue().setIfAbsent(key, value, releaseTime, TimeUnit.SECONDS);
            return boo != null && boo;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 解锁
     *
     * @param key key
     */
    public boolean unLock(String key) {
        try {
            Boolean boo = redisTemplate.delete(key);
            return boo != null && boo;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    /**
     * 向指定list的队列头部批量添加value
     *
     * @param key key
     */
    public boolean rightPush(String key, String value) {
        try {
            Long aLong = redisTemplate.opsForList().rightPush(key, value);
            return aLong != 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 向指定list的队列头部批量添加value
     *
     * @param key key
     */
    public boolean leftPush(String key, String value) {
        try {
            Long aLong = redisTemplate.opsForList().leftPush(key, value);
            return aLong != 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    /**
     * 移除并获取指定list中(队列-头部/尾部)第一个元素
     *
     * @param key key
     */
    public Object leftPop(String key) {
        try {
            return redisTemplate.opsForList().leftPop(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    /**
     * 指定key+！
     *
     * @param key key
     */
    public Object incr(String key,Long num) {
        try {
            return redisTemplate.opsForValue().increment(key,num);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 增加文章浏览量（返回最新值可用于实时展示）
     *
     * @param key key
     */
    public Long incr(String key) {
        try {
            return redisTemplate.opsForValue().increment(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 指定key-！
     *
     * @param key key
     */
    public Long decr(String key) {
        try {
            return redisTemplate.opsForValue().decrement(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据参数查询相关KEY集合
     */
    public Set<String> getKeyListByStr(String key){
        key = key+"**";
        return redisTemplate.keys(key);
    }


//    @Scheduled(fixedRate = 5000)
//    public void distributedTask() {
//        String lockKey = "task:distributed:lock";
//        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", 30, TimeUnit.MINUTES);
//        if (locked != null && locked) {
//            try {
//                System.out.println("执行分布式任务...");
//            } finally {
//                redisTemplate.delete(lockKey);
//            }
//        }
//    }
}
