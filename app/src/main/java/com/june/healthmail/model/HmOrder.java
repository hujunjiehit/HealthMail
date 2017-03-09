package com.june.healthmail.model;

/**
 * Created by june on 2017/3/9.
 */

public class HmOrder {
    private String HM_OrderId;
    private int HM_OrderType;
    private String User_Id;
    private String HM_PTrainerId;
    private String HM_ServerProgram;
    private String HM_OrderDate;
    private float HM_OrderPrice;
    private int HM_PayStatus;
    private int HM_OrderStatus;
    private String TrainerNick;
    private String HM_ServerDate;
    private String HM_GBC_ServerTime;

    public String getHM_OrderId() {
        return HM_OrderId;
    }

    public void setHM_OrderId(String HM_OrderId) {
        this.HM_OrderId = HM_OrderId;
    }

    public int getHM_OrderType() {
        return HM_OrderType;
    }

    public void setHM_OrderType(int HM_OrderType) {
        this.HM_OrderType = HM_OrderType;
    }

    public String getUser_Id() {
        return User_Id;
    }

    public void setUser_Id(String user_Id) {
        User_Id = user_Id;
    }

    public String getHM_PTrainerId() {
        return HM_PTrainerId;
    }

    public void setHM_PTrainerId(String HM_PTrainerId) {
        this.HM_PTrainerId = HM_PTrainerId;
    }

    public String getHM_ServerProgram() {
        return HM_ServerProgram;
    }

    public void setHM_ServerProgram(String HM_ServerProgram) {
        this.HM_ServerProgram = HM_ServerProgram;
    }

    public String getHM_OrderDate() {
        return HM_OrderDate;
    }

    public void setHM_OrderDate(String HM_OrderDate) {
        this.HM_OrderDate = HM_OrderDate;
    }

    public float getHM_OrderPrice() {
        return HM_OrderPrice;
    }

    public void setHM_OrderPrice(float HM_OrderPrice) {
        this.HM_OrderPrice = HM_OrderPrice;
    }

    public int getHM_PayStatus() {
        return HM_PayStatus;
    }

    public void setHM_PayStatus(int HM_PayStatus) {
        this.HM_PayStatus = HM_PayStatus;
    }

    public int getHM_OrderStatus() {
        return HM_OrderStatus;
    }

    public void setHM_OrderStatus(int HM_OrderStatus) {
        this.HM_OrderStatus = HM_OrderStatus;
    }

    public String getTrainerNick() {
        return TrainerNick;
    }

    public void setTrainerNick(String trainerNick) {
        TrainerNick = trainerNick;
    }

    public String getHM_ServerDate() {
        return HM_ServerDate;
    }

    public void setHM_ServerDate(String HM_ServerDate) {
        this.HM_ServerDate = HM_ServerDate;
    }

    public String getHM_GBC_ServerTime() {
        return HM_GBC_ServerTime;
    }

    public void setHM_GBC_ServerTime(String HM_GBC_ServerTime) {
        this.HM_GBC_ServerTime = HM_GBC_ServerTime;
    }
}
