package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.io.Serializable;

public class SomePicUrlData extends BaseRes implements Serializable {


    /**
     * data : {"img_url":"http://res.mobilecoinabcbene.nlren.cn/coinbene-banner/fc797c60e6b7770.png","link_url":"www.sina.com"}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * img_url : http://res.mobilecoinabcbene.nlren.cn/coinbene-banner/fc797c60e6b7770.png
         * link_url : www.sina.com
         */

        public String img_url;
        public String link_url;
        public String id;

        public String getImg_url() {
            return img_url;
        }

        public void setImg_url(String img_url) {
            this.img_url = img_url;
        }

        public String getLink_url() {
            return link_url;
        }

        public void setLink_url(String link_url) {
            this.link_url = link_url;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
