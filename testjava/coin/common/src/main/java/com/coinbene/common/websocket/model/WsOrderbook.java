package com.coinbene.common.websocket.model;

import java.util.List;

/**
 * Created by june
 * on 2020-01-15
 */
public class WsOrderbook {

	/**
	 * asks : [["5621.7","50"],["5621.8","0"],["5621.9","30"]]
	 * bids : [["5621.3","10"],["5621.2","20"],["5621.1","80"],["5621","0"],["5620.8","10"]]
	 * version : 2
	 * timestamp : 1575857539000
	 */

	private int version;
	private long timestamp;
	private List<List<String>> asks;
	private List<List<String>> bids;

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public List<List<String>> getAsks() {
		return asks;
	}

	public void setAsks(List<List<String>> asks) {
		this.asks = asks;
	}

	public List<List<String>> getBids() {
		return bids;
	}

	public void setBids(List<List<String>> bids) {
		this.bids = bids;
	}
}
