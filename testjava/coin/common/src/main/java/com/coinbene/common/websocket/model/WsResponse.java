package com.coinbene.common.websocket.model;

import androidx.annotation.NonNull;

import com.coinbene.common.context.CBRepository;

import java.util.List;

/**
 * Created by june
 * on 2020-01-13
 */
public class WsResponse<T> {

	private String event;

	private String topic;

	private String action;

	private List<T> data;

	private int code;

	private String message;

	private boolean success;

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	@NonNull
	@Override
	public String toString() {
		return CBRepository.gson.toJson(this);
	}
}
