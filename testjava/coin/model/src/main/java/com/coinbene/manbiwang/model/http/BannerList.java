package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

/**
 * Create by huyong
 * on 2018/8/6
 */
public class BannerList extends BaseRes {


    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 45
         * img_url : http://res.mobilecoinabcbene.nlren.cn/coinbene-banner/ee917ba5f4940647.jpg
         * link_url : www.sina.com
         * sort : 2
         * status : 1
         * create_time : 2018-08-01 16:22:06
         * user_id : 1
         * des : testtest测试
         * site : MAIN
         * lang : zh_CN
         * gid : 15
         * name : 默认组
         * start_time : 2018-08-10 10:23:56
         * end_time : 9999-12-31 00:00:00
         * position : PC
         * last_updtime : 2018-04-28 15:02:28
         */

        private int id;
        private String img_url;
        private String link_url;
        private int sort;
        private int status;
        private String create_time;
        private int user_id;
        private String des;
        private String site;
        private String lang;
        private int gid;
        private String name;
        private String start_time;
        private String end_time;
        private String position;
        private String last_updtime;

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof DataBean)) {
                return false;
            }
            if (this.img_url.equals(((DataBean) obj).img_url)) {
                return true;
            }
            return false;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

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

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }

        public String getSite() {
            return site;
        }

        public void setSite(String site) {
            this.site = site;
        }

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public int getGid() {
            return gid;
        }

        public void setGid(int gid) {
            this.gid = gid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getLast_updtime() {
            return last_updtime;
        }

        public void setLast_updtime(String last_updtime) {
            this.last_updtime = last_updtime;
        }
    }
}
