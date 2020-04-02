package com.coinbene.common.database;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by mengxiangdong on 2017/11/27.
 */
@Entity
public class MarginSymbolTable implements Serializable {
	@Id
	public long id;
	public String symbol;//币对
	public String base;//分子
	public String quote;//分母
	public String leverage;//杠杆
	public int pricePrecision;//价格精度
	public String makeFee;//make费

	public String takeFee;//take费
	public String sellDisabled;//是否能交易
	public String minVolume;//最小交易量
	public int volumePrecision;//数量精度

	public String initialPrice;//
	public String baseInterestRate;//
	public String quoteInterestRate;//
}
