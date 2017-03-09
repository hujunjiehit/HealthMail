package com.june.healthmail.model;

/**
 * Created by bjhujunjie on 2017/3/9.
 */

public class UserModel {
  private String User_Id;
  private int HM_UI_Sex;
  private int HM_UI_Age;
  private String HM_UI_Province;
  private String HM_UI_City;
  private String HM_UI_Job;
  private String HM_UI_CaseHistory;
  private int HM_UI_Height;
  private int HM_UI_Weight;
  private String HM_UI_Signature;
  private int HM_UI_TrainerStatus;
  private double distance;
  private String headImageUser;

  public String getUser_Id() {
    return User_Id;
  }

  public void setUser_Id(String user_Id) {
    User_Id = user_Id;
  }

  public int getHM_UI_Sex() {
    return HM_UI_Sex;
  }

  public void setHM_UI_Sex(int HM_UI_Sex) {
    this.HM_UI_Sex = HM_UI_Sex;
  }

  public int getHM_UI_Age() {
    return HM_UI_Age;
  }

  public void setHM_UI_Age(int HM_UI_Age) {
    this.HM_UI_Age = HM_UI_Age;
  }

  public String getHM_UI_Province() {
    return HM_UI_Province;
  }

  public void setHM_UI_Province(String HM_UI_Province) {
    this.HM_UI_Province = HM_UI_Province;
  }

  public String getHM_UI_City() {
    return HM_UI_City;
  }

  public void setHM_UI_City(String HM_UI_City) {
    this.HM_UI_City = HM_UI_City;
  }

  public String getHM_UI_Job() {
    return HM_UI_Job;
  }

  public void setHM_UI_Job(String HM_UI_Job) {
    this.HM_UI_Job = HM_UI_Job;
  }

  public String getHM_UI_CaseHistory() {
    return HM_UI_CaseHistory;
  }

  public void setHM_UI_CaseHistory(String HM_UI_CaseHistory) {
    this.HM_UI_CaseHistory = HM_UI_CaseHistory;
  }

  public int getHM_UI_Height() {
    return HM_UI_Height;
  }

  public void setHM_UI_Height(int HM_UI_Height) {
    this.HM_UI_Height = HM_UI_Height;
  }

  public int getHM_UI_Weight() {
    return HM_UI_Weight;
  }

  public void setHM_UI_Weight(int HM_UI_Weight) {
    this.HM_UI_Weight = HM_UI_Weight;
  }

  public String getHM_UI_Signature() {
    return HM_UI_Signature;
  }

  public void setHM_UI_Signature(String HM_UI_Signature) {
    this.HM_UI_Signature = HM_UI_Signature;
  }

  public int getHM_UI_TrainerStatus() {
    return HM_UI_TrainerStatus;
  }

  public void setHM_UI_TrainerStatus(int HM_UI_TrainerStatus) {
    this.HM_UI_TrainerStatus = HM_UI_TrainerStatus;
  }

  public double getDistance() {
    return distance;
  }

  public void setDistance(double distance) {
    this.distance = distance;
  }

  public String getHeadImageUser() {
    return headImageUser;
  }

  public void setHeadImageUser(String headImageUser) {
    this.headImageUser = headImageUser;
  }
}
