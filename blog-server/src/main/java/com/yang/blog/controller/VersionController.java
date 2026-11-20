package com.yang.blog.controller;

import com.yang.blog.entity.AppVersionEntity;
import com.yang.blog.service.AppVersionService;
import com.yang.blog.service.FileService;
import com.yang.blog.utils.CommonDate;
import com.yang.blog.utils.RespBean;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/app")
public class VersionController {
    @Autowired
    private AppVersionService appVersionService;

    @PostMapping("/upload")
    public RespBean uploadWgtFile(@RequestParam("file") MultipartFile file,
                                  @RequestParam("updateTitle") String updateTitle,
                                  @RequestParam("updateContent") String updateContent,
                                  @RequestParam(value = "forceUpdate", defaultValue = "false") Boolean forceUpdate,
                                  @RequestParam("versionInfo") String  versionInfoStr) {
        try {
            AppVersionEntity version = appVersionService.processWgtUpload(file, updateTitle, updateContent, forceUpdate,versionInfoStr);
            return RespBean.ok("上传成功", version);
        } catch (Exception e) {
            return RespBean.error("上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public RespBean getVersionList(@RequestParam(value = "versionName") String versionName,@RequestParam(value = "versionCode") String versionCode,
                                   @RequestParam(value = "startTime") String startTime,@RequestParam(value = "endTime") String endTime) {
        // 返回版本列表
         List<AppVersionEntity> list = appVersionService.getRawData(versionName,versionCode,startTime,endTime);
         return RespBean.ok(list);
    }

    @GetMapping("/version")
    public RespBean getLatestVersion() {
        AppVersionEntity version = new AppVersionEntity();
        version.setVersionCode(125);  // 比当前版本高
        version.setVersionName("1.2.5");
        version.setDownloadUrl("http://180.76.189.135/app/__UNI__70D2920.wgt");
        version.setUpdateTitle("发现新版本");
        version.setUpdateContent("1. 修复已知问题\n2. 优化用户体验\n3. 新增功能");
        version.setForceUpdate(false); // 是否强制更新
        version.setFileSize(getFileSize("D:\\MyProject\\yang-blog\\blog-app\\unpackage\\release\\__UNI__70D2920.wgt"));
        version.setPublishTime(CommonDate.getTime24().toString());

        return RespBean.ok(version);
    }

    // 获取文件大小的方法
    private String getFileSize(String filePath) {
        try {
            File file = new File(filePath);
            long size = file.length();
            if (size < 1024) {
                return size + "B";
            } else if (size < 1024 * 1024) {
                return String.format("%.1fKB", size / 1024.0);
            } else {
                return String.format("%.1fMB", size / (1024.0 * 1024.0));
            }
        } catch (Exception e) {
            return "未知大小";
        }
    }

    //操作
    @PostMapping("/update")
    public RespBean operationDailyFlow(AppVersionEntity entity){
        return appVersionService.update(entity);
    }

    //删除记录
    @DeleteMapping("/delete")
    public RespBean delete(@RequestParam("id") String id){
        return appVersionService.delete(id);
    }
}