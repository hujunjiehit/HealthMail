package com.coinbene.common.model.http;

import com.coinbene.common.datacollection.SchemeFrom;
import com.coinbene.common.datacollection.SchemeTo;

public class DataCollectionModel {

	public static final String SPOT = "spot";
	public static final String CONTRACT = "contract";

	String order_id;
	String order_type;
	String response_time;

	int from;
	int to;

	public DataCollectionModel() {
	}

	public DataCollectionModel(String order_id, String order_type, String response_time) {
		this.order_id = order_id;
		this.order_type = order_type;
		this.response_time = response_time;
	}


	public DataCollectionModel from(SchemeFrom schemeFrom) {
		this.from = schemeFrom.ordinal();
		return this;
	}

	public DataCollectionModel to(SchemeTo schemeTo) {
		this.to = schemeTo.ordinal();
		return this;
	}
}
