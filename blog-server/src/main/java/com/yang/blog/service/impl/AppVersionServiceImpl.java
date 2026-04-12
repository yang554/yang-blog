package com.yang.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.yang.blog.entity.AppVersionEntity;
import com.yang.blog.mapper.AppVersionMapper;
import com.yang.blog.service.AppVersionService;
import com.yang.blog.utils.CommonDate;
import com.yang.blog.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class AppVersionServiceImpl implements AppVersionService {
    @Value("${app.upload.dir:}")
    private String uploadDir;

    @Value("${app.download.base-url}")
    private String downloadBaseUrl;

    @Autowired
    private AppVersionMapper appVersionMapper;

    /**
     * 处理 wgt 文件上传并提取版本信息
     */
    @Override
    public AppVersionEntity processWgtUpload(MultipartFile file, String updateTitle, String updateContent, Boolean forceUpdate, String versionInfoStr) {
        try {
            // 保存文件到服务器
            String fileName = file.getOriginalFilename();

            // 使用 NIO Path
            Path uploadPath = Paths.get(uploadDir);
            Path destPath = uploadPath.resolve(fileName);

            // 确保上传目录存在
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Files.copy(file.getInputStream(), destPath, StandardCopyOption.REPLACE_EXISTING);


            // 构建版本实体
            AppVersionEntity version = new AppVersionEntity();
            version = JSON.parseObject(versionInfoStr, AppVersionEntity.class);
            version.setId(CommonDate.getStamp());
            version.setDownloadUrl(downloadBaseUrl + "/app/" + fileName);
            version.setUpdateTitle(updateTitle);
            version.setUpdateContent(updateContent);
            version.setForceUpdate(forceUpdate != null ? forceUpdate : false);
            version.setPublishTime(CommonDate.getDateTimeFormat());


            // 保存到数据库
            appVersionMapper.insert(version);

            return version;

        } catch (Exception e) {
            throw new RuntimeException("处理 wgt 文件失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<AppVersionEntity> getRawData(String versionName, String versionCode, String startTime, String endTime) {
        return appVersionMapper.getRawData(versionName,versionCode,startTime,endTime);
    }

    @Override
    public RespBean insert(AppVersionEntity entity) {
        return null;
    }

    @Override
    public RespBean update(AppVersionEntity entity) {
        int s = appVersionMapper.update(entity);
        if(s > 0)
            return RespBean.ok("修改成功");
        else
            return RespBean.error("修改失败");
    }

    @Override
    public RespBean delete(String id) {
        int s = appVersionMapper.delete(id);
        if(s > 0)
            return RespBean.ok("删除成功");
        else
            return RespBean.error("删除失败");
    }
}
