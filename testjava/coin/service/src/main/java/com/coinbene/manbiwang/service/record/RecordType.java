package com.coinbene.manbiwang.service.record;

/**
 * Created by june
 * on 2019-09-20
 *
 * 记录类型的枚举
 */
public enum  RecordType {

	RECHARGE,	//充币

	WITHDRAW,	//提币

	PLATFORM_TRANSFER,	//平台内转账

	TRANSFER, 	//账户之间划转

	OTHER, //其它记录

	SPOT_CURRENT_ORDER,	//币币当前委托

	SPOT_HISTORY_ORDER,	//币币历史委托

	SPOT_HIGH_LEVER_ORDER,	//币币高级委托

	MARGIN_CURRENT_ORDER,	//杠杆当前委托

	MARGIN_HISTORY_ORDER,	//杠杆历史委托

	MARGIN_CURRENT_BORROW,	//杠杆当前借币

	MARGIN_HISTORY_BORROM,	//杠杆历史借币

	USDT_CONTRACT,	//USDT合约记录

	BTC_CONTRACT,	//BTC合约记录

	MINING,		//挖矿明细

	OPTIONS,	//猜涨跌交易记录
}
