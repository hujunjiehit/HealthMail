package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

public class KycStatusModel extends BaseRes {
    /**
     * data : {"status":1}
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
         * status : 1
         */

        private int status;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

    /*
    0是审核中

    1是已认证

    9是认证失败

    8是之前实名认证的用户需要重新进行实名认证

    -1,该用户还未进行实名认证
     */


}
