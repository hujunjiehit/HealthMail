package com.coinbene.manbiwang.kline.bean;

/**
 * Created by loro on 2017/2/8.
 */
public class KLineBean {
    public String date;
    public float open;
    public float close;
    public float high;
    public float low;
    public float vol;

    public String time;//原始的时间
    public float ma5;
    public float ma10;
    public float ma20;

    public float amountVol;//算分时图的总和
    public float total;//分时图
    public float ave;//分时图

    public String change; //涨跌幅
}
