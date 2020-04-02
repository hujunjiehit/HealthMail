package com.coinbene.common.websocket.model;

import java.util.List;

/**
 * Created by june
 * on 2020-01-13
 */
public class WsRequest {

	public static final String OPERATION_SUB = "subscribe";
	public static final String OPERATION_UNSUB = "unsubscribe";
	public static final String OPERATION_LOGIN = "login";

	private String op;

	private List<String> args;

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public List<String> getArgs() {
		return args;
	}

	public void setArgs(List<String> args) {
		this.args = args;
	}
}
