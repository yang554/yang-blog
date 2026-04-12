package com.yang.blog.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InteractionEvent {
    private Long eventId;    //唯一事件ID（防重）
    private Long userId; //用户ID
    private Long articleId; //文章ID
    private String action; //操作类型（like/unlike、collect/uncollect）
    private Long timestamp; //操作时间戳

    @JsonCreator
    public InteractionEvent(@JsonProperty("eventId") Long eventId, @JsonProperty("userId") Long userId, @JsonProperty("articleId") Long articleId, @JsonProperty("action") String action, @JsonProperty("timestamp") Long timestamp) {
        this.eventId = eventId;
        this.userId = userId;
        this.articleId = articleId;
        this.action = action;
        this.timestamp = timestamp;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
