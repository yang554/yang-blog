package com.yang.blog.service;

public interface TBlogLikeService {
    boolean toggleLikeOrCollection(Long bID, Long uID, String type);
    Long getLikeCount(Long bID, String type);
    boolean isLiked(Long bID, Long uID, String type);

//    void syncLikesOrCollectionToDB();//每5分钟执行一次,同步文章点赞量or收藏量
}
