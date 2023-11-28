package com.yang.blog.entity;

import io.swagger.annotations.ApiModel;

@ApiModel(value="UAuntEntity", description="user-aunt")
public class UAuntEntity {
    private Long id;
    private String uName;
    private String startDate;
    private String endDate;
    private String cycle;
    private String duration;
    private String bloodVolume;
    private String symptom;
    private String note;
    private String uStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getBloodVolume() {
        return bloodVolume;
    }

    public void setBloodVolume(String bloodVolume) {
        this.bloodVolume = bloodVolume;
    }

    public String getSymptom() {
        return symptom;
    }

    public void setSymptom(String symptom) {
        this.symptom = symptom;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getuStatus() {
        return uStatus;
    }

    public void setuStatus(String uStatus) {
        this.uStatus = uStatus;
    }
}
