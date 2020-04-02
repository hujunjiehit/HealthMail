package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.io.Serializable;

/**
 * Created by mxd on 2018/6/8.
 */

public class NoteModel extends BaseRes {

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        public String notice;
        public String url;
        public String title;
    }
}
