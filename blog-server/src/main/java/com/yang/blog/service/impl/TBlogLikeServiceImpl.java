package com.yang.blog.service.impl;

import com.yang.blog.enmu.LikeOrCollectionKey;
import com.yang.blog.entity.InteractionEvent;
import com.yang.blog.entity.TBlogCollectionEntity;
import com.yang.blog.entity.TBlogLikeEntity;
import com.yang.blog.mapper.TBlogCollectionMapper;
import com.yang.blog.mapper.TBlogLikeMapper;
import com.yang.blog.mapper.TBlogMapper;
import com.yang.blog.service.TBlogLikeService;
import com.yang.blog.utils.CommonDate;
import com.yang.blog.utils.RedisUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class TBlogLikeServiceImpl implements TBlogLikeService {
    @Autowired
    private RedisUtils redisUtils ;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Override
    public boolean toggleLikeOrCollection(Long bID, Long uID, String type) {
        LikeOrCollectionKey operation = LikeOrCollectionKey.fromType(type);
        String lockKey = operation.getLockKeyPrefix() + uID + ":" + bID;
        RLock lock = redissonClient.getLock(lockKey);
        String countKey = operation.getCountKeyPrefix() + bID;
        String key = operation.getKeyPrefix() + bID;
        String unKey = operation.getUnKeyPrefix() + bID;
        try {
            // 分布式锁：5秒内禁止重复操作
            if (!lock.tryLock(0, 5, TimeUnit.SECONDS)) {
                throw new RuntimeException("操作过于频繁");
            }
            // 检查是否已点赞/收藏
            Boolean isMember = redisUtils.isMember(key, uID.toString());
            if (Boolean.TRUE.equals(isMember)) {
                // 取消点赞/收藏
                redisUtils.remove(key, uID.toString());
                redisUtils.decr(countKey);
                redisUtils.add(unKey, uID.toString());
                // 发送消息到 RabbitMQ（确保消息可靠性）
                InteractionEvent event = new InteractionEvent(
                        CommonDate.getStamp(),
                        uID,
                        bID,
                        "un"+type,
                        System.currentTimeMillis()
                );
                rabbitTemplate.convertAndSend("interaction.exchange", type, event);
                return false;
            } else {
                // 新增点赞/收藏
                redisUtils.add(key, uID.toString());
                redisUtils.incr(countKey);
                redisUtils.remove(unKey, uID.toString());
                // 发送消息到 RabbitMQ（确保消息可靠性）
                InteractionEvent event = new InteractionEvent(
                        CommonDate.getStamp(),
                        uID,
                        bID,
                        type,
                        System.currentTimeMillis()
                );
                rabbitTemplate.convertAndSend("interaction.exchange", type, event);
                return true;
            }

        } catch (InterruptedException e) {
            //Redis 操作失败回滚
            if (!Boolean.TRUE.equals(redisUtils.isMember(key, uID.toString()))) {
                redisUtils.remove(key, uID.toString());
                redisUtils.decr(countKey);
                redisUtils.add(unKey, uID.toString());
            }else {
                redisUtils.add(key, uID.toString());
                redisUtils.incr(countKey);
                redisUtils.remove(unKey, uID.toString());
            }
            Thread.currentThread().interrupt();
            throw new RuntimeException("操作被中断");
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Long getLikeCount(Long bID,String type) {
        LikeOrCollectionKey operation = LikeOrCollectionKey.fromType(type);

        String countKey = operation.getCountKeyPrefix() + bID;
        String count = (String) redisUtils.get(countKey);
        return count != null ? Long.parseLong(count) : 0L;
    }

    @Override
    public boolean isLiked(Long bID, Long uID,String type) {
        LikeOrCollectionKey operation = LikeOrCollectionKey.fromType(type);
        String likeKey = operation.getKeyPrefix() + bID;
        return Boolean.TRUE.equals(redisUtils.isMember(likeKey, uID.toString()));
    }
}
