package com.yang.blog.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private int status = 0;
    private String msg;
    private Object data;

    // 显式定义全参构造器
//    public RespBean(int status, String msg, Object data) {
//        this.status = status;
//        this.msg = msg;
//        this.data = data;
//    }

    public static RespBean ok(String msg){
        return new RespBean(200,msg,null);
    }

    public static RespBean ok(String msg, Object data){
        return new RespBean(200,msg,data);
    }

    public static RespBean ok(Object data){
        return new RespBean(200,"ok",data);
    }

    public static RespBean warn(String msg){
        return new RespBean(204,msg,null);
    }

    public static RespBean warn(String msg, Object data){
        return new RespBean(204,msg,data);
    }

    public static RespBean error(String msg){
        return new RespBean(500,msg,null);
    }

    public static RespBean error(String msg, Object data){
        return new RespBean(500,msg,data);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
