package com.coinbene.manbiwang.model.http;

import androidx.annotation.NonNull;

/**
 * Created by mengxiangdong on 2017/11/27.
 */

public class OrderModel  implements  Comparable<OrderModel>{
    public String cnt;
    public String price;
    public boolean isSell;//显示用

    public String tradeType;
    public boolean isFalse;//假的，补充数据

    public String addUpCnt; //当前价格对应的数量累加值，绘制深度图用的
    public double percent; //当前价格占比，绘制深度图用的

    @Override
    public int compareTo(@NonNull OrderModel o) {
        return Double.valueOf(o.price).compareTo(Double.valueOf(this.price));
    }
}
