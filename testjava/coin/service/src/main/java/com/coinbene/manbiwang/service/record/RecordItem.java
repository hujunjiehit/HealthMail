package com.coinbene.manbiwang.service.record;

/**
 * Created by june
 * on 2019-09-20
 */
public class RecordItem {

	private String recordName;
	private RecordType recordType;

	public RecordItem(String recordName, RecordType tag) {
		this.recordName = recordName;
		this.recordType = tag;
	}

	public String getRecordName() {
		return recordName;
	}

	public void setRecordName(String recordName) {
		this.recordName = recordName;
	}

	public RecordType getRecordType() {
		return recordType;
	}

	public void setRecordType(RecordType tag) {
		this.recordType = tag;
	}
}

