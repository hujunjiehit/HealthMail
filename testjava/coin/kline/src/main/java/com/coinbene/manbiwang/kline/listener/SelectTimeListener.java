package com.coinbene.manbiwang.kline.listener;

/**
 * Created by mengxiangdong on 2017/11/30.
 */

public interface SelectTimeListener {
    String MA_HIDE = "MA_HIDE";

    String MACD = "MACD";
    String KDJ = "KDJ";
    String RSI = "RSI";
    String SUB2_HIDE = "SUB2_HIDE";

    String BOLL = "BOLL";
    String MA = "MA";

    void selectTimeType(int clickId, String txt);

    //附图2
    void selectZhibiaoType(String zhibiao_type, String txt);

    //主图
    void selectMasterType(int zhibiao_type, String txt);

    void cancelDimiss();
}
