package com.yang.blog.service;

import com.yang.blog.entity.AppVersionEntity;
import com.yang.blog.utils.RespBean;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AppVersionService {
    AppVersionEntity processWgtUpload(MultipartFile file, String updateTitle, String updateContent, Boolean forceUpdate, String versionInfo);
    List<AppVersionEntity> getRawData(String appName, String versionCode, String startTime, String endTime);
    RespBean insert(AppVersionEntity entity);
    RespBean update(AppVersionEntity entity);
    RespBean delete(String id);
}
