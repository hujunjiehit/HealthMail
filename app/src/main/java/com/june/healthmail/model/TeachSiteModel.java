package com.june.healthmail.model;

import java.io.Serializable;

/**
 * Created by june on 2017/7/7.
 */

public class TeachSiteModel implements Serializable{
  private static final long serialVersionUID = 1L;
  private int isvenue;
  private String sitename;
  private HmGbcCoordinate coordinate;

  public int getIsvenue() {
    return isvenue;
  }

  public void setIsvenue(int isvenue) {
    this.isvenue = isvenue;
  }

  public String getSitename() {
    return sitename;
  }

  public void setSitename(String sitename) {
    this.sitename = sitename;
  }

  public HmGbcCoordinate getCoordinate() {
    return coordinate;
  }

  public void setCoordinate(HmGbcCoordinate coordinate) {
    this.coordinate = coordinate;
  }
}
