package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

public class QueryPswStateModel extends BaseRes {


    /**
     * "code": 200,
     * "message": "",
     * "data": true
     */

    private boolean data;

    public boolean getData() {
        return data;
    }

    public void setData(boolean data) {
        this.data = data;
    }

}
