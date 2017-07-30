package com.june.healthmail.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by june on 2017/3/2.
 */

public class AccountInfo extends BmobObject{
    private int id;
    private String userName;
    private String phoneNumber;
    private String nickName;
    private String passWord;
    private int status;    //0 不启用  1 启用
    private String mallId;  //猫号id
    private String lastDay; //日期
    private int pingjiaTimes;    //今日评价次数
    private int yuekeTimes;    //今日约课次数

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMallId() {
        return mallId;
    }

    public void setMallId(String mallId) {
        this.mallId = mallId;
    }

    public String getLastDay() {
        return lastDay;
    }

    public void setLastDay(String lastDay) {
        this.lastDay = lastDay;
    }

    public int getPingjiaTimes() {
        return pingjiaTimes;
    }

    public void setPingjiaTimes(int pingjiaTimes) {
        this.pingjiaTimes = pingjiaTimes;
    }

    public int getYuekeTimes() {
        return yuekeTimes;
    }

    public void setYuekeTimes(int yuekeTimes) {
        this.yuekeTimes = yuekeTimes;
    }

    @Override
    public String toString() {
        return "phoneNumber = " + phoneNumber + "   mallId = " + mallId +  " lastDay = " + lastDay + "   yuekeTimes = " + yuekeTimes +
            "   pingjiaTimes = " + pingjiaTimes;
    }
}
