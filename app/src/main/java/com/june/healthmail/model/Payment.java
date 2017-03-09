package com.june.healthmail.model;

/**
 * Created by june on 2017/3/10.
 */

public class Payment {
    private int hm_p_id;
    private String hm_p_name;
    private int hm_p_enable;
    private int hm_p_sort;
    private String hm_icon;
    private int cxpandcount;
    private int hm_isdefault;
    private boolean isusable;
    private double remainamount;
    private double channelamount;
    private String hm_gary_icon;

    public int getHm_p_id() {
        return hm_p_id;
    }

    public void setHm_p_id(int hm_p_id) {
        this.hm_p_id = hm_p_id;
    }

    public String getHm_p_name() {
        return hm_p_name;
    }

    public void setHm_p_name(String hm_p_name) {
        this.hm_p_name = hm_p_name;
    }

    public int getHm_p_enable() {
        return hm_p_enable;
    }

    public void setHm_p_enable(int hm_p_enable) {
        this.hm_p_enable = hm_p_enable;
    }

    public int getHm_p_sort() {
        return hm_p_sort;
    }

    public void setHm_p_sort(int hm_p_sort) {
        this.hm_p_sort = hm_p_sort;
    }

    public String getHm_icon() {
        return hm_icon;
    }

    public void setHm_icon(String hm_icon) {
        this.hm_icon = hm_icon;
    }

    public int getCxpandcount() {
        return cxpandcount;
    }

    public void setCxpandcount(int cxpandcount) {
        this.cxpandcount = cxpandcount;
    }

    public int getHm_isdefault() {
        return hm_isdefault;
    }

    public void setHm_isdefault(int hm_isdefault) {
        this.hm_isdefault = hm_isdefault;
    }

    public boolean isusable() {
        return isusable;
    }

    public void setIsusable(boolean isusable) {
        this.isusable = isusable;
    }

    public double getRemainamount() {
        return remainamount;
    }

    public void setRemainamount(double remainamount) {
        this.remainamount = remainamount;
    }

    public double isChannelamount() {
        return channelamount;
    }

    public void setChannelamount(double channelamount) {
        this.channelamount = channelamount;
    }

    public String getHm_gary_icon() {
        return hm_gary_icon;
    }

    public void setHm_gary_icon(String hm_gary_icon) {
        this.hm_gary_icon = hm_gary_icon;
    }
}
