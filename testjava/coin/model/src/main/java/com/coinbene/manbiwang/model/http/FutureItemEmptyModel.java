package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

/**
 * Created by mxd on 2019/3/5.
 */

public class FutureItemEmptyModel extends BaseRes {

    public FutureItemEmptyModel(boolean isLogin) {
        this.isLogin = isLogin;
    }

    public boolean isLogin;//是否是登录登录状态

}
