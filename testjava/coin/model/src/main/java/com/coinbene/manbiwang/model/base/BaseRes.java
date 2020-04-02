package com.coinbene.manbiwang.model.base;

import java.io.Serializable;

/**
 * Created by zhangle on 2018/4/6.
 */

public class BaseRes implements Serializable {

    public int code;
    public String message;

    public boolean isSuccess() {
        return code == 200;
    }

    public boolean isExpired() {
        return code == 401;
    }

    /**
     * 订单在撤单中  需要单独处理   不需要toast提示用户操作错误
     *
     * @return
     */
    public boolean isOrderCanceling() {
        return code == 10325;
    }

}
