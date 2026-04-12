package com.yang.blog.controller;

import com.yang.blog.service.TBlogLikeService;
import com.yang.blog.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/blog/like")
public class TBlogLikeController {
    @Autowired
    private TBlogLikeService likeService;

    @PostMapping("/toggle")
    public RespBean toggleLike(@RequestParam("bID")  Long bID, @RequestParam("uID") Long uID, @RequestParam("type") String type) {
        boolean result = likeService.toggleLikeOrCollection(bID, uID, type);
        return RespBean.ok("liked", result);
    }

    @GetMapping("/count")
    public RespBean getLikeCount(@RequestParam("bID") Long bID, @RequestParam("type") String type) {
        return RespBean.ok("count", likeService.getLikeCount(bID, type));
    }

    @GetMapping("/status")
    public RespBean getLikeStatus(@RequestParam("bID") Long bID, @RequestParam("uID") Long uID, @RequestParam("type") String type) {
        return RespBean.ok("status", likeService.isLiked(bID, uID, type));
    }
}
