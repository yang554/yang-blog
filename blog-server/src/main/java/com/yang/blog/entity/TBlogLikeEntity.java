package com.yang.blog.entity;

import io.swagger.annotations.ApiModel;

@ApiModel(value="t_blog_like", description="博客点赞表")
public class TBlogLikeEntity {
    private Long Id;
    private Long blogId;
    private Long userId;
    private Integer isLike;

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

    public Integer getIsLike() {
        return isLike;
    }

    public void setIsLike(Integer isLike) {
        this.isLike = isLike;
    }
}
