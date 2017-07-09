package com.june.healthmail.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by june on 2017/7/7.
 */

public class TrainerModel implements Serializable{

  private static final long serialVersionUID = 1L;

  private String User_Id;
  private String HM_PT_Name;
  private String HM_PT_IDCard;
  private int HM_PT_Education;
  private int HM_PT_Qualification;
  private String HM_PT_College;
  private double HM_PT_CourseCost;
  private String HM_PT_TeachingProgram;
  private List<TeachSiteModel> HM_PT_TeachingSite;
  private String HM_PT_Introduction;
  private String HM_PT_InviteCode;
  private int HM_PT_Level;
  private String headImageTrainer;
  private List<ImageModel> hm_ptwi_images;

  public String getUser_Id() {
    return User_Id;
  }

  public void setUser_Id(String user_Id) {
    User_Id = user_Id;
  }

  public String getHM_PT_Name() {
    return HM_PT_Name;
  }

  public void setHM_PT_Name(String HM_PT_Name) {
    this.HM_PT_Name = HM_PT_Name;
  }

  public String getHM_PT_IDCard() {
    return HM_PT_IDCard;
  }

  public void setHM_PT_IDCard(String HM_PT_IDCard) {
    this.HM_PT_IDCard = HM_PT_IDCard;
  }

  public int getHM_PT_Education() {
    return HM_PT_Education;
  }

  public void setHM_PT_Education(int HM_PT_Education) {
    this.HM_PT_Education = HM_PT_Education;
  }

  public int getHM_PT_Qualification() {
    return HM_PT_Qualification;
  }

  public void setHM_PT_Qualification(int HM_PT_Qualification) {
    this.HM_PT_Qualification = HM_PT_Qualification;
  }

  public String getHM_PT_College() {
    return HM_PT_College;
  }

  public void setHM_PT_College(String HM_PT_College) {
    this.HM_PT_College = HM_PT_College;
  }

  public double getHM_PT_CourseCost() {
    return HM_PT_CourseCost;
  }

  public void setHM_PT_CourseCost(double HM_PT_CourseCost) {
    this.HM_PT_CourseCost = HM_PT_CourseCost;
  }

  public String getHM_PT_TeachingProgram() {
    return HM_PT_TeachingProgram;
  }

  public void setHM_PT_TeachingProgram(String HM_PT_TeachingProgram) {
    this.HM_PT_TeachingProgram = HM_PT_TeachingProgram;
  }

  public List<TeachSiteModel> getHM_PT_TeachingSite() {
    return HM_PT_TeachingSite;
  }

  public void setHM_PT_TeachingSite(List<TeachSiteModel> HM_PT_TeachingSite) {
    this.HM_PT_TeachingSite = HM_PT_TeachingSite;
  }

  public String getHM_PT_Introduction() {
    return HM_PT_Introduction;
  }

  public void setHM_PT_Introduction(String HM_PT_Introduction) {
    this.HM_PT_Introduction = HM_PT_Introduction;
  }

  public String getHM_PT_InviteCode() {
    return HM_PT_InviteCode;
  }

  public void setHM_PT_InviteCode(String HM_PT_InviteCode) {
    this.HM_PT_InviteCode = HM_PT_InviteCode;
  }

  public int getHM_PT_Level() {
    return HM_PT_Level;
  }

  public void setHM_PT_Level(int HM_PT_Level) {
    this.HM_PT_Level = HM_PT_Level;
  }

  public String getHeadImageTrainer() {
    return headImageTrainer;
  }

  public void setHeadImageTrainer(String headImageTrainer) {
    this.headImageTrainer = headImageTrainer;
  }

  public List<ImageModel> getHm_ptwi_images() {
    return hm_ptwi_images;
  }

  public void setHm_ptwi_images(List<ImageModel> hm_ptwi_images) {
    this.hm_ptwi_images = hm_ptwi_images;
  }
}
