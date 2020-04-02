package com.coinbene.common.websocket.model;

/**
 * Created by june
 * on 2020-01-16
 */
public enum  UserEventType {
	ACCOUNT_CHANGED("account_changed"),
	POSITIONS_CHANGED("positions_changed"),
	CURORDER_CHANGED("curorder_changed"),
	HISORDER_CHANGED("hisorder_changed"),
	PLANORDER_CHANGED("planorder_changed"),
	LIQUIDATION("liquidation");

	public final String value;

	UserEventType(String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}

	public static UserEventType getUserEvent(String value){
		for(UserEventType type : values()) {
			if (type.value.equals(value)) {
				return type;
			}
		}
		return null;
	}
}
