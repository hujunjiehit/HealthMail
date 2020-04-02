package com.coinbene.common.database;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class ContractUsdtConfigTable {

	@Id
	public long id;

	public String makerFeeRate;
	public String takerFeeRate;
	public String showPrecision;
	public String maintainMarginRate;
	public String leverages;
	public String marketOrderFloatRate;
	public String maxOrder;
}
