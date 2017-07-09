package com.june.healthmail.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by june on 2017/7/9.
 */

public class SijiaoModel extends BmobObject {
  private String userName;
  private String sijiaoName;
  private String IdCard;
  private String College;
  private Double CourseCost;
  private String UserId;
  private String UserAccount;
  private String UserPwd;

  public String getUserName() {
    return userName;
  }

  public String getUserAccount() {
    return UserAccount;
  }

  public void setUserAccount(String userAccount) {
    UserAccount = userAccount;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getSijiaoName() {
    return sijiaoName;
  }

  public void setSijiaoName(String sijiaoName) {
    this.sijiaoName = sijiaoName;
  }

  public String getIdCard() {
    return IdCard;
  }

  public void setIdCard(String idCard) {
    IdCard = idCard;
  }

  public String getCollege() {
    return College;
  }

  public void setCollege(String college) {
    College = college;
  }

  public Double getCourseCost() {
    return CourseCost;
  }

  public void setCourseCost(Double courseCost) {
    CourseCost = courseCost;
  }

  public String getUserId() {
    return UserId;
  }

  public void setUserId(String userId) {
    UserId = userId;
  }

  public String getUserPwd() {
    return UserPwd;
  }

  public void setUserPwd(String userPwd) {
    UserPwd = userPwd;
  }
}
