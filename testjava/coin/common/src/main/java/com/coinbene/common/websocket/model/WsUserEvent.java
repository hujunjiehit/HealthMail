package com.coinbene.common.websocket.model;

/**
 * Created by june
 * on 2020-01-16
 */
public class WsUserEvent {

	/**
	 * key : curorder_changed
	 * value :
	 */

	private String key;
	private String value;

	public WsUserEvent() {
	}

	public WsUserEvent(String key) {
		this.key = key;
		this.value = "";
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
