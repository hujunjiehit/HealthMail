package com.coinbene.common.database;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by mengxiangdong on 2017/11/27.
 */
@Entity
public class TradePairOptionalTable implements Serializable{
    @Id
    public long id;

    public String site;
    public String tradePair;//"BTCUSDT"
    public String tradePairName;//"BTC/USDT"
    public int sort;//倒序排序
}
