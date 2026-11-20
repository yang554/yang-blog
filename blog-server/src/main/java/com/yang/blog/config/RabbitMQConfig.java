package com.yang.blog.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {
    private static final String DELAY_EXCHANGE = "delay.exchange";
    private static final String DELAY_QUEUE = "delay.queue";
    private static final String DELAY_ROUTING_KEY = "delay.routing.key";

    private static final String LIKE_QUEUE = "like.queue";
    private static final String COLLECTION_QUEUE = "collection.queue";
    private static final String FOOD_COLLECTION_QUEUE = "foodCol.queue";

    // 定义 Exchange
    @Bean
    public DirectExchange interactionExchange() {
        return new DirectExchange("interaction.exchange", true, true); // 持久化非自动删除
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 可选配置：忽略未知字段、允许空值等
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    // 定义队列（点赞、收藏） 持久化队列
    @Bean
    public Queue likeQueue() {
        return new Queue(LIKE_QUEUE, true);
    }

    @Bean
    public Queue collectionQueue() {
        return new Queue(COLLECTION_QUEUE, true);
    }
    //食谱收藏
    @Bean
    public Queue foodCollectionQueue() {
        return new Queue(FOOD_COLLECTION_QUEUE, true);
    }

    // 绑定 Exchange 和 Queue
    @Bean
    public Binding bindLikeQueue(DirectExchange exchange, Queue likeQueue) {
        return BindingBuilder.bind(likeQueue).to(exchange).with("like");
    }

    @Bean
    public Binding bindCollectionQueue(DirectExchange exchange, Queue collectionQueue) {
        return BindingBuilder.bind(collectionQueue).to(exchange).with("collection");
    }

    @Bean
    public Binding bindFoodCollectionQueue(DirectExchange exchange, Queue foodCollectionQueue) {
        return BindingBuilder.bind(foodCollectionQueue).to(exchange).with("foodCol");
    }

//    @Bean
//    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
//        RabbitTemplate template = new RabbitTemplate(connectionFactory);
//        template.setMessageConverter(jsonMessageConverter());
//        // 开启确认模式（确保消息到达 Broker）
//        template.setConfirmCallback((correlationData, ack, cause) -> {
//            if (!ack) {
////                log.error("消息发送失败: {}", cause);
//            }
//        });
//        return template;
//    }
}
