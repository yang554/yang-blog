package com.yang.blog.controller;

import com.yang.blog.service.NuReColService;
import com.yang.blog.service.TBlogLikeService;
import com.yang.blog.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/food/likeOrCol")
public class NuReColController {
    @Autowired
    private TBlogLikeService likeService;

    @PostMapping("/toggle")
    public RespBean toggleLike(@RequestParam("fID")  Long fID, @RequestParam("uID") Long uID, @RequestParam("type") String type) {
        boolean result = likeService.toggleLikeOrCollection(fID, uID, type);
        return RespBean.ok("foodCol", result);
    }

    @GetMapping("/count")
    public RespBean getLikeCount(@RequestParam("fID") Long fID, @RequestParam("type") String type) {
        return RespBean.ok("count", likeService.getLikeCount(fID, type));
    }

    @GetMapping("/status")
    public RespBean getLikeStatus(@RequestParam("fID") Long fID, @RequestParam("uID") Long uID, @RequestParam("type") String type) {
        return RespBean.ok("status", likeService.isLiked(fID, uID, type));
    }
}
