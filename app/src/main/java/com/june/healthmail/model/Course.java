package com.june.healthmail.model;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by june on 2017/3/5.
 */

public class Course implements Comparable<Course>{
    private List<HmGbciImage> hm_gbci_image;
    private String groupbuy_id;
    private String hm_gbc_title;
    private String hm_gbc_date;
    private int hm_gbc_status;
    private String hm_gbc_publishdate;
    private String user_id;
    private String hm_gbc_enddate;
    private String hm_gbc_time;
    private float hm_gbc_avgprice;
    private double distance;
    private int applynumber;

    public List<HmGbciImage> getHm_gbci_image() {
        return hm_gbci_image;
    }

    public void setHm_gbci_image(List<HmGbciImage> hm_gbci_image) {
        this.hm_gbci_image = hm_gbci_image;
    }

    public String getGroupbuy_id() {
        return groupbuy_id;
    }

    public void setGroupbuy_id(String groupbuy_id) {
        this.groupbuy_id = groupbuy_id;
    }

    public String getHm_gbc_title() {
        return hm_gbc_title;
    }

    public void setHm_gbc_title(String hm_gbc_title) {
        this.hm_gbc_title = hm_gbc_title;
    }

    public String getHm_gbc_date() {
        return hm_gbc_date;
    }

    public void setHm_gbc_date(String hm_gbc_date) {
        this.hm_gbc_date = hm_gbc_date;
    }

    public int getHm_gbc_status() {
        return hm_gbc_status;
    }

    public void setHm_gbc_status(int hm_gbc_status) {
        this.hm_gbc_status = hm_gbc_status;
    }

    public String getHm_gbc_publishdate() {
        return hm_gbc_publishdate;
    }

    public void setHm_gbc_publishdate(String hm_gbc_publishdate) {
        this.hm_gbc_publishdate = hm_gbc_publishdate;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getHm_gbc_enddate() {
        return hm_gbc_enddate;
    }

    public void setHm_gbc_enddate(String hm_gbc_enddate) {
        this.hm_gbc_enddate = hm_gbc_enddate;
    }

    public String getHm_gbc_time() {
        return hm_gbc_time;
    }

    public void setHm_gbc_time(String hm_gbc_time) {
        this.hm_gbc_time = hm_gbc_time;
    }

    public float getHm_gbc_avgprice() {
        return hm_gbc_avgprice;
    }

    public void setHm_gbc_avgprice(float hm_gbc_avgprice) {
        this.hm_gbc_avgprice = hm_gbc_avgprice;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getApplynumber() {
        return applynumber;
    }

    public void setApplynumber(int applynumber) {
        this.applynumber = applynumber;
    }

    @Override
    public int compareTo(@NonNull Course o) {
        return this.getHm_gbc_time().compareTo(o.getHm_gbc_time());
    }
}
