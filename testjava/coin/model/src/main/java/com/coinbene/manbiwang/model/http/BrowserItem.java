package com.coinbene.manbiwang.model.http;

import java.io.Serializable;

/**
 */

public class BrowserItem extends BaseModel implements Serializable {//

    public static final String KEY_ID = "_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_URL = "url";
    public static final String KEY_CODE = "code";
    public static final String KEY_MARKET = "market";
    public static final String KEY_FROM = "from";
    public static final String KEY_GROUP_ID = "id";
    public static final String KEY_PRICE_PRECISION = "pricePrecision";
    public static final String KEY_NOT_NEED_URLFILTER = "not_needUrlFilter";

    public String _id;
    public String title;
    public String url;
    public String code;
    public String market;
    public String from;
    public String id;//分组id
    public int pricePrecision;//精度
    public boolean not_needUrlFilter;//true,不过滤url；false, 过滤url

    @Override
    public String toString() {
        return "BrowserItem{" +
                "_id='" + _id + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
