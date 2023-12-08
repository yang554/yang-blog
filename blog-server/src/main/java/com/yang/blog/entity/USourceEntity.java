package com.yang.blog.entity;

import java.util.List;

public class USourceEntity {
    private String UserId;         //ID
    private String UserPinYin;         //姓名拼音
    private String UserName;         //姓名
    private String UserBirthDay;         //出生日期
    private String UserSex;         //性别
    private String UserAddressNew;         //现地址
    private String UserAddressOld;         //户口地址
    private String UserPhone;         //电话
    private String UserEmil;         //邮箱
    private String UserLove;         //用户代数
    private String UserPId;    //父ID
    private String UserMID;     //母ID
    private String UserImgUrl;  //头像链接
    private String IsSurvival;  //是否存活
    private String UserDieDay;  //死亡日期
    private String UserMateId;     //配偶ID
    private String UserNote;     //座右铭

    private String pName;   //父亲姓名
    private String mName;   //母亲姓名
    private String mateName;   //配偶姓名
    private String styleNo; //模板编号

    private List<USourceEntity> list;

    public List<USourceEntity> getList() {
        return list;
    }

    public void setList(List<USourceEntity> list) {
        this.list = list;
    }

    public String getStyleNo() {
        return styleNo;
    }

    public void setStyleNo(String styleNo) {
        this.styleNo = styleNo;
    }

    public String getMateName() {
        return mateName;
    }

    public void setMateName(String mateName) {
        this.mateName = mateName;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getUserPinYin() {
        return UserPinYin;
    }

    public void setUserPinYin(String userPinYin) {
        UserPinYin = userPinYin;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserBirthDay() {
        return UserBirthDay;
    }

    public void setUserBirthDay(String userBirthDay) {
        UserBirthDay = userBirthDay;
    }

    public String getUserSex() {
        return UserSex;
    }

    public void setUserSex(String userSex) {
        UserSex = userSex;
    }

    public String getUserAddressNew() {
        return UserAddressNew;
    }

    public void setUserAddressNew(String userAddressNew) {
        UserAddressNew = userAddressNew;
    }

    public String getUserAddressOld() {
        return UserAddressOld;
    }

    public void setUserAddressOld(String userAddressOld) {
        UserAddressOld = userAddressOld;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }

    public String getUserEmil() {
        return UserEmil;
    }

    public void setUserEmil(String userEmil) {
        UserEmil = userEmil;
    }

    public String getUserLove() {
        return UserLove;
    }

    public void setUserLove(String userLove) {
        UserLove = userLove;
    }

    public String getUserPId() {
        return UserPId;
    }

    public void setUserPId(String userPId) {
        UserPId = userPId;
    }

    public String getUserMID() {
        return UserMID;
    }

    public void setUserMID(String userMID) {
        UserMID = userMID;
    }

    public String getUserImgUrl() {
        return UserImgUrl;
    }

    public void setUserImgUrl(String userImgUrl) {
        UserImgUrl = userImgUrl;
    }

    public String getIsSurvival() {
        return IsSurvival;
    }

    public void setIsSurvival(String isSurvival) {
        IsSurvival = isSurvival;
    }

    public String getUserDieDay() {
        return UserDieDay;
    }

    public void setUserDieDay(String userDieDay) {
        UserDieDay = userDieDay;
    }

    public String getUserMateId() {
        return UserMateId;
    }

    public void setUserMateId(String userMateId) {
        UserMateId = userMateId;
    }

    public String getUserNote() {
        return UserNote;
    }

    public void setUserNote(String userNote) {
        UserNote = userNote;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }
}
