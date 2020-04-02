package com.coinbene.manbiwang.model.http;

/**
 * Created by mengxiangdong on 2017/11/28.
 */

public class OrderLineItemModel extends BaseModel {
	public String newPrice;
	public String usdPrice;
	public String cnyPrice;
	public String localPrice;
	public String lastPrice;//上一次价格  用来对比涨跌
	public int riseType;
	public String upsAndDowns;//涨跌幅
}
