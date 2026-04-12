package com.yang.blog.consumer;

import com.rabbitmq.client.Channel;
import com.yang.blog.entity.InteractionEvent;
import com.yang.blog.entity.NuReColEntity;
import com.yang.blog.entity.TBlogCollectionEntity;
import com.yang.blog.entity.TBlogLikeEntity;
import com.yang.blog.mapper.NuReColMapper;
import com.yang.blog.mapper.TBlogCollectionMapper;
import com.yang.blog.mapper.TBlogLikeMapper;
import com.yang.blog.mapper.TBlogMapper;
import com.yang.blog.utils.CommonDate;
import com.yang.blog.utils.RedisUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static java.util.logging.Level.SEVERE;

@Service
public class InteractionEventConsumer {
    Logger log = Logger.getLogger(String.valueOf(InteractionEventConsumer.class));
    @Autowired
    private TBlogLikeMapper likeMapper ;
    @Autowired
    private TBlogCollectionMapper collectionMapper ;
    @Autowired
    private NuReColMapper nuReColMapper ;
    @Autowired
    private TBlogMapper blogMapper ;
    @Autowired
    private RedisUtils redisUtils ;

    /**
     * 处理文章点赞事件
     */
    @RabbitListener(queues = "like.queue")
    public void handleLikeEvent(InteractionEvent event, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            // 幂等性检查（Redis）
            String eventKey = "event:processed:" + event.getEventId();
            if (redisUtils.get(eventKey) != null) {
                channel.basicAck(tag, false);
                return;
            }
            List<TBlogLikeEntity> listLike = new ArrayList<>();
            TBlogLikeEntity likeEntity = new TBlogLikeEntity(CommonDate.getStamp(),event.getArticleId(), event.getUserId());
            listLike.add(likeEntity);
            // 处理点赞/取消点赞
            if ("like".equals(event.getAction())) {
                likeMapper.insertBatch(listLike);
                blogMapper.addBlogLikesCount(event.getArticleId(),1);
            } else if ("unlike".equals(event.getAction())) {
                likeMapper.deleteBatch(listLike);
                blogMapper.addBlogLikesCount(event.getArticleId(),-1);
            }
            // 标记事件已处理（1小时过期）
            redisUtils.set(eventKey, "true", 1L, TimeUnit.HOURS);
            // 确认消息
            channel.basicAck(tag, false);
            redisUtils.deleteByPrex("blogListCache::");//清空list缓存
        } catch (Exception e) {
            //处理失败：拒绝消息并重新入队（可设置重试次数）
            try {
                channel.basicNack(tag, false, true);
            } catch (IOException ex) {
                log.log(SEVERE,"处理点赞事件失败: {}"+event, e.getMessage());
            }
        }
    }

    /**
     * 处理文章收藏事件
     */
    @RabbitListener(queues = "collection.queue")
    public void handleCollectionEvent(InteractionEvent event, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            // 幂等性检查（Redis）
            String eventKey = "event:processed:" + event.getEventId();
            if (redisUtils.get(eventKey) != null) {
                channel.basicAck(tag, false);
                return;
            }
            List<TBlogCollectionEntity> listCollection = new ArrayList<>();
            TBlogCollectionEntity likeEntity = new TBlogCollectionEntity(CommonDate.getStamp(),event.getArticleId(), event.getUserId());
            listCollection.add(likeEntity);
            // 处理点赞/取消点赞
            if ("collection".equals(event.getAction())) {
                collectionMapper.insertBatch(listCollection);
                blogMapper.addBlogCollectionCount(event.getArticleId(),1);
            } else if ("uncollection".equals(event.getAction())) {
                collectionMapper.deleteBatch(listCollection);
                blogMapper.addBlogCollectionCount(event.getArticleId(),-1);
            }
            // 标记事件已处理（1小时过期）
            redisUtils.set(eventKey, "true", 1L, TimeUnit.HOURS);
            // 确认消息
            channel.basicAck(tag, false);
            redisUtils.deleteByPrex("blogListCache::");//清空list缓存
        } catch (Exception e) {
            //处理失败：拒绝消息并重新入队（可设置重试次数）
            try {
                channel.basicNack(tag, false, true);
            } catch (IOException ex) {
                log.log(SEVERE,"处理点赞事件失败: {}"+event, e.getMessage());
            }
        }
    }

    /**
     * 处理食谱收藏事件
     */
    @RabbitListener(queues = "foodCol.queue")
    public void handleFoodCollectionEvent(InteractionEvent event, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            // 幂等性检查（Redis）
            String eventKey = "event:processed:" + event.getEventId();
            if (redisUtils.get(eventKey) != null) {
                channel.basicAck(tag, false);
                return;
            }
            List<NuReColEntity> nuReColEntities = new ArrayList<>();
            NuReColEntity entity = new NuReColEntity(CommonDate.getStamp(),event.getArticleId(), event.getUserId());
            nuReColEntities.add(entity);
            // 处理收藏/取消收藏
            if ("foodCol".equals(event.getAction())) {
                nuReColMapper.insertBatch(nuReColEntities);
            } else if ("unfoodCol".equals(event.getAction())) {
                nuReColMapper.deleteBatch(nuReColEntities);
            }
            // 标记事件已处理（1小时过期）
            redisUtils.set(eventKey, "true", 1L, TimeUnit.HOURS);
            // 确认消息
            channel.basicAck(tag, false);
        } catch (Exception e) {
            //处理失败：拒绝消息并重新入队（可设置重试次数）
            try {
                channel.basicNack(tag, false, true);
            } catch (IOException ex) {
                log.log(SEVERE,"处理食谱收藏事件失败: {}"+event, e.getMessage());
            }
        }
    }

}
