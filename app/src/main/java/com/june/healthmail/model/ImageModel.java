package com.june.healthmail.model;

import java.io.Serializable;

/**
 * Created by june on 2017/7/7.
 */

public class ImageModel implements Serializable{

  private static final long serialVersionUID = 1L;


  private String iurl;
  private String uploaddate;

  public String getIurl() {
    return iurl;
  }

  public void setIurl(String iurl) {
    this.iurl = iurl;
  }

  public String getUploaddate() {
    return uploaddate;
  }

  public void setUploaddate(String uploaddate) {
    this.uploaddate = uploaddate;
  }
}
