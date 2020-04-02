package com.coinbene.manbiwang.model.websocket;

public class WsBaseResponse {
    String topic;
    String ts;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }
}
