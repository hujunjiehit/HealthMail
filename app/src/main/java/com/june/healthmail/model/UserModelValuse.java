package com.june.healthmail.model;

/**
 * Created by bjhujunjie on 2017/3/9.
 */

public class UserModelValuse {
  private String HM_U_NickName;
  private UserModel UserModel;
  private TrainerModel PTrainerModel;

  public TrainerModel getPTrainerModel() {
    return PTrainerModel;
  }

  public void setPTrainerModel(TrainerModel PTrainerModel) {
    this.PTrainerModel = PTrainerModel;
  }

  public com.june.healthmail.model.UserModel getUserModel() {
    return UserModel;
  }

  public void setUserModel(com.june.healthmail.model.UserModel userModel) {
    UserModel = userModel;
  }

  public String getHM_U_NickName() {
    return HM_U_NickName;
  }

  public void setHM_U_NickName(String HM_U_NickName) {
    this.HM_U_NickName = HM_U_NickName;
  }
}
