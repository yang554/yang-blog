package com.yang.blog.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespBean {
    private int status = 0;
    private String msg;
    private Object obj;

    public static RespBean build(){
        return new RespBean();
    }

    public static RespBean ok(String msg){
        return new RespBean(200,msg,null);
    }

    public static RespBean ok(String msg, Object obj){
        return new RespBean(200,msg,obj);
    }

    public static RespBean ok(Object obj){
        return new RespBean(200,"ok",obj);
    }

    public static RespBean warn(String msg){
        return new RespBean(204,msg,null);
    }

    public static RespBean warn(String msg, Object obj){
        return new RespBean(204,msg,obj);
    }

    public static RespBean error(String msg){
        return new RespBean(500,msg,null);
    }

    public static RespBean error(String msg, Object obj){
        return new RespBean(500,msg,obj);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
