package com.yang.blog.entity;

import io.swagger.annotations.ApiModel;

@ApiModel(value="TBlog_tags", description="博客标签")
public class TBlogTagEntity {

    private Long userId;

    private Long tagId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }
}
