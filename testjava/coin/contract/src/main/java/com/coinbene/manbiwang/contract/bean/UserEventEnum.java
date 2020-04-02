package com.coinbene.manbiwang.contract.bean;

public enum UserEventEnum {
    //clientEvent
    cancelOrder,openOrder,closeOrder,markPriceChange,

    //ServerEvent
    positionChange,accountChange,curOrderChange,hisChange,liquidation,planorder_changed
}
