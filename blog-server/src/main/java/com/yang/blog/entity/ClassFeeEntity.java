package com.yang.blog.entity;

import io.swagger.annotations.ApiModel;

@ApiModel(value="ClassFeeEntity", description="t_classFee 课时费")
public class ClassFeeEntity {
    private Long id;    //ID
    private Integer teacherLevel;   //教师等级
    private String grade;   //年级
    private String classType;   //授课类型
    private Integer studentCount;   //学生人数
    private String classCategory;   //班课类型
    private String fee; //课时费

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTeacherLevel() {
        return teacherLevel;
    }

    public void setTeacherLevel(Integer teacherLevel) {
        this.teacherLevel = teacherLevel;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }

    public String getClassCategory() {
        return classCategory;
    }

    public void setClassCategory(String classCategory) {
        this.classCategory = classCategory;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }
}
