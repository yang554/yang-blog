package com.yang.blog.entity;

import io.swagger.annotations.ApiModel;

@ApiModel(value="t_remark", description="博客评论")
public class TRemarkEntity {
    private Long id;    //评论ID
    private Long userID;    //评论人ID
    private String content; //评论内容
    private Long blogID;    //博客ID
    private Long parentConentId;    //评论父级ID
    private String createTime;  //评论时间
    private Long root_parent;   //一级评论

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getBlogID() {
        return blogID;
    }

    public void setBlogID(Long blogID) {
        this.blogID = blogID;
    }

    public Long getParentConentId() {
        return parentConentId;
    }

    public void setParentConentId(Long parentConentId) {
        this.parentConentId = parentConentId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Long getRoot_parent() {
        return root_parent;
    }

    public void setRoot_parent(Long root_parent) {
        this.root_parent = root_parent;
    }
}
