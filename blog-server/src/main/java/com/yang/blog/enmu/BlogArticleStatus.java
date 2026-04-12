package com.yang.blog.enmu;

public enum BlogArticleStatus {
    DRAFT,         // 草稿
    PENDING,       // 待审核
    EDITOR_REJECT, // 博客管理员拒绝
    CHIEF_REJECT,  // admin拒绝
    PUBLISHED      // 已发布
}
