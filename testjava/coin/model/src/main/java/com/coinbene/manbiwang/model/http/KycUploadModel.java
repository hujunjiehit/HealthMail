package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

public class KycUploadModel extends BaseRes{

    private KycUploadModel.DataBean data;

    public KycUploadModel.DataBean getData() {
        return data;
    }

    public void setData(KycUploadModel.DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * status : 1
         */

        private String path;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }


}
