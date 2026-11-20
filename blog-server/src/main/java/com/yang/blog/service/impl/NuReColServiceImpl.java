package com.yang.blog.service.impl;

import com.yang.blog.service.NuReColService;
import org.springframework.stereotype.Service;

@Service
public class NuReColServiceImpl implements NuReColService {
    @Override
    public boolean toggleLikeOrCollection(Long bID, Long uID, String type) {
        return false;
    }

    @Override
    public Long getLikeCount(Long bID, String type) {
        return null;
    }

    @Override
    public boolean isLiked(Long bID, Long uID, String type) {
        return false;
    }
}
