package com.yang.blog.entity;

import io.swagger.annotations.ApiModel;

@ApiModel(value="t_blog_like", description="博客点赞表")
public class TBlogLikeEntity {
    private static final long serialVersionUID = 1L;
    private Long Id;
    private Long blogId;
    private Long userId;
    private String createTime;

    public TBlogLikeEntity(Long id, Long blogId, Long userId) {
        Id = id;
        this.blogId = blogId;
        this.userId = userId;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public Long getBlogId() {
        return blogId;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
