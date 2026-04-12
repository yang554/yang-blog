package com.yang.blog.service;

public interface NuReColService {
    boolean toggleLikeOrCollection(Long bID, Long uID, String type);
    Long getLikeCount(Long bID, String type);
    boolean isLiked(Long bID, Long uID, String type);
}
