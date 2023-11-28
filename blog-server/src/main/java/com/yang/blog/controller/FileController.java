package com.yang.blog.controller;

import com.yang.blog.service.FileService;
import com.yang.blog.service.SysUserService;
import com.yang.blog.utils.RespBean;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequestMapping("/file")
public class FileController {
    Logger log = Logger.getLogger(String.valueOf(FileController.class));
    @Autowired
    private FileService fileService;
    @Autowired
    private SysUserService userService;

    @PostMapping("/upload")
    public RespBean upload(@RequestParam(value = "file") MultipartFile file) throws IOException {
        Long size = file.getSize();//文件大小
        String fileName = file.getOriginalFilename();//文件名
        String fileType = fileName.substring(fileName.lastIndexOf("."));//文件后缀
        String newFileName = UUID.randomUUID().toString().replace("-","") + fileType;//新文件名
        String filePath = new SimpleDateFormat("yyyy-MM-dd/").format(new Date());// 构建日期路径
        String path = "/MyItem/yang-blog/blog-end/public";
        String url = "/static/" +  filePath + fileName;
        String uploadUrl =  path +  url;// 文件上传的路径地址

        File f = new File(uploadUrl);
        if(!f.exists()){
            try {
                FileUtils.copyInputStreamToFile(file.getInputStream(),f);
            }catch (IOException e){
                System.out.println(e.getMessage());
            }
        }


//        for (int i=0;i==0;) {
//            File f = new File(uploadUrl);
//            if(!f.exists()){
//                try {
//                    FileUtils.copyInputStreamToFile(file.getInputStream(),f);
//                    continue;
//                }catch (IOException e){
//                    System.out.println(e.getMessage());
//                }
//                i = 1;
//            }else  if(f.exists()){
//                i=1;
//            }
//        }
        return RespBean.ok("ok",url);
    }

    @PostMapping("/upload/avatar")
    public RespBean uploadAvatar(@RequestParam(value = "file") MultipartFile file, HttpServletRequest request) throws IOException {
        String id = request.getParameter("id");
        String fileName = file.getOriginalFilename();//文件名
        String filePath = new SimpleDateFormat("yyyy-MM-dd/").format(new Date());// 构建日期路径
        String path = "/MyItem/yang-blog/blog-end/public";
        String url = "/static/" +  filePath + fileName;
        String uploadUrl =  path +  url;// 文件上传的路径地址
        File f = new File(uploadUrl);
        if(!f.exists()){
            try {
                FileUtils.copyInputStreamToFile(file.getInputStream(),f);
            }catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
        return userService.saveAvatar(url,id);
    }
}
