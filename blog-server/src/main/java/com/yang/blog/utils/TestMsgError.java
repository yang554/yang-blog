package com.yang.blog.utils;

public class TestMsgError {
    private String start_error="000000";
    private String phone;
    private String code;
    //手机号码(数据库)是否存在
    private String phoneExists;

    public String getPhoneExists() {
        return phoneExists;
    }

    public void setPhoneExists(String phoneExists) {
        this.phoneExists = phoneExists;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStart_error() {
        return start_error;
    }

    public void setStart_error(String start_error) {
        this.start_error = start_error;
    }
}
