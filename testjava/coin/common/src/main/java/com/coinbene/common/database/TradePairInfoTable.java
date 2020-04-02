package com.coinbene.common.database;

import java.io.Serializable;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Uid;
import io.objectbox.relation.ToMany;

/**
 * Created by mengxiangdong on 2017/11/27.
 */
@Entity
public class TradePairInfoTable implements Serializable{
    @Id
    public long id;

    public String localBaseAsset;//基础资产本地名
    public String englishBaseAsset;//基础资产英文名
    @Uid(2529274713634411487L)
    public int pricePrecision;
    public int sort;//排序字段
    public String tradePair;//交易对名称,"BTCUSDT"
    public String tradePairName;//交易对显示名,"BTC/USDT"
    @Uid(6548845286997851081L)
    public int volumePrecision;//价格精度,2--> 0.01

    public String minVolume;
    public String priceChangeScale;
    public String takeFee;
    public String makeFee;

    public String groupName;//分组信息
    public int group_sort;//分组排序
    public boolean isOptional;//是否是自选
    public int sellDisabled;//0,正常的交易对，1，IEO交易对

    public boolean isLatest;    //是否是最新币种
    public boolean isHot;       //是否是hot币种

    @Backlink(to = "tradePairInfoTable")
    public ToMany<TagsTable> tags;
}
