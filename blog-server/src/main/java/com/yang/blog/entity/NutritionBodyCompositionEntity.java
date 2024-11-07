package com.yang.blog.entity;

import io.swagger.annotations.ApiModel;

import java.time.LocalDateTime;

@ApiModel(value="NutritionBodyCompositionEntity", description="营养体成分分析表")
public class NutritionBodyCompositionEntity {
    private Long id;    //编码
    private String uID; //用户ID
    private String uName;   //用户名称
    private String age; //年龄
    private String sex; //性别
    private String height;  //身高
    private String weight;  //体重
    private String basalMetabolism; //基础代谢
    private String BMI; //BMI
    private String foodIntake;  //进食量
    private String fat; //体脂
    private String visceralFat; //内脏脂肪
    private String muscle;  //肌肉
    private String boneMass;    //骨重
    private String waterContent;    //水分
    private String protein; //蛋白质
    private String fatFreeBodyWeight;//去脂体重
    private String muscleMass;  //肌肉量
    private String skeletalMuscle;  //骨骼肌
    private String waterContent_1;  //含水量
    private String fatMass; //脂肪量
    private String subcutaneousFatPercentage;   //皮下脂肪率
    private String weightControlAmount; //体重控制量
    private String fatControlAmount;    //脂肪控制量
    private String muscleControlVolume; //肌肉控制量
    private String standardWeight;  //标准体重
    private String IdealWeight; //理想体重
    private String physiologicalAge;   //生理年龄
    private LocalDateTime createTime;  //创建时间
    private LocalDateTime updateTime;  //最后一次修改时间
    private String recordTime;   //记录时间
    private String note;    //建议
    private String obesityLevel;    //肥胖等级

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getBasalMetabolism() {
        return basalMetabolism;
    }

    public void setBasalMetabolism(String basalMetabolism) {
        this.basalMetabolism = basalMetabolism;
    }

    public String getBMI() {
        return BMI;
    }

    public void setBMI(String BMI) {
        this.BMI = BMI;
    }

    public String getFoodIntake() {
        return foodIntake;
    }

    public void setFoodIntake(String foodIntake) {
        this.foodIntake = foodIntake;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getVisceralFat() {
        return visceralFat;
    }

    public void setVisceralFat(String visceralFat) {
        this.visceralFat = visceralFat;
    }

    public String getMuscle() {
        return muscle;
    }

    public void setMuscle(String muscle) {
        this.muscle = muscle;
    }

    public String getBoneMass() {
        return boneMass;
    }

    public void setBoneMass(String boneMass) {
        this.boneMass = boneMass;
    }

    public String getWaterContent() {
        return waterContent;
    }

    public void setWaterContent(String waterContent) {
        this.waterContent = waterContent;
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public String getFatFreeBodyWeight() {
        return fatFreeBodyWeight;
    }

    public void setFatFreeBodyWeight(String fatFreeBodyWeight) {
        this.fatFreeBodyWeight = fatFreeBodyWeight;
    }

    public String getMuscleMass() {
        return muscleMass;
    }

    public void setMuscleMass(String muscleMass) {
        this.muscleMass = muscleMass;
    }

    public String getSkeletalMuscle() {
        return skeletalMuscle;
    }

    public void setSkeletalMuscle(String skeletalMuscle) {
        this.skeletalMuscle = skeletalMuscle;
    }

    public String getWaterContent_1() {
        return waterContent_1;
    }

    public void setWaterContent_1(String waterContent_1) {
        this.waterContent_1 = waterContent_1;
    }

    public String getFatMass() {
        return fatMass;
    }

    public void setFatMass(String fatMass) {
        this.fatMass = fatMass;
    }

    public String getSubcutaneousFatPercentage() {
        return subcutaneousFatPercentage;
    }

    public void setSubcutaneousFatPercentage(String subcutaneousFatPercentage) {
        this.subcutaneousFatPercentage = subcutaneousFatPercentage;
    }

    public String getWeightControlAmount() {
        return weightControlAmount;
    }

    public void setWeightControlAmount(String weightControlAmount) {
        this.weightControlAmount = weightControlAmount;
    }

    public String getFatControlAmount() {
        return fatControlAmount;
    }

    public void setFatControlAmount(String fatControlAmount) {
        this.fatControlAmount = fatControlAmount;
    }

    public String getMuscleControlVolume() {
        return muscleControlVolume;
    }

    public void setMuscleControlVolume(String muscleControlVolume) {
        this.muscleControlVolume = muscleControlVolume;
    }

    public String getStandardWeight() {
        return standardWeight;
    }

    public void setStandardWeight(String standardWeight) {
        this.standardWeight = standardWeight;
    }

    public String getIdealWeight() {
        return IdealWeight;
    }

    public void setIdealWeight(String idealWeight) {
        IdealWeight = idealWeight;
    }

    public String getPhysiologicalAge() {
        return physiologicalAge;
    }

    public void setPhysiologicalAge(String physiologicalAge) {
        this.physiologicalAge = physiologicalAge;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getObesityLevel() {
        return obesityLevel;
    }

    public void setObesityLevel(String obesityLevel) {
        this.obesityLevel = obesityLevel;
    }
}
