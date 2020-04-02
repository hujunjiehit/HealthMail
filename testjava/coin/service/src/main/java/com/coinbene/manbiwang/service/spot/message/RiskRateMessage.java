package com.coinbene.manbiwang.service.spot.message;

/**
 * Created by june
 * on 2019-12-09
 */
public class RiskRateMessage {
	private String forceClosePrice;
	private String riskRate;

	public RiskRateMessage(String forceClosePrice, String riskRate) {
		this.forceClosePrice = forceClosePrice;
		this.riskRate = riskRate;
	}

	public String getForceClosePrice() {
		return forceClosePrice;
	}

	public void setForceClosePrice(String forceClosePrice) {
		this.forceClosePrice = forceClosePrice;
	}

	public String getRiskRate() {
		return riskRate;
	}

	public void setRiskRate(String riskRate) {
		this.riskRate = riskRate;
	}
}
