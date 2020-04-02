package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.ArrayList;

/**
 */

public class AdResponse extends BaseRes {

    /**
     * data : {"des":"string","downUrl":"string","forcedUpdate":true}
     * timezone : 0
     */

    public DataBean data;

    public static class DataBean {
        public ArrayList<PicUrlData> picInfos;
        public String clickUrl;
        public int showTime;
        public int advertFormat;//0是图片广告，1是gif 2是视频
    }

    public static class PicUrlData {
        //分辨率类型：3:android  4: iphone4或4s  5: iphone5或5s 6:iphone6或7或8 7: iphone6或7或8plus 8: iphoneX
        public int type;
        public String picUrl;
    }
}
