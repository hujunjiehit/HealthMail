package com.coinbene.manbiwang.model.websocket;

import com.coinbene.manbiwang.model.websocket.Response;

/**
 * 默认的消息响应事件包装类，
 * 只包含文本，不包含数据实体
 * @author huyong
 */
public class TextResponse implements Response<String> {

    private String responseText;

    public TextResponse(String responseText) {
        this.responseText = responseText;
    }

    @Override
    public String getResponseText() {
        return responseText;
    }

    @Override
    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    @Override
    public String getResponseEntity() {
        return null;
    }

    @Override
    public void setResponseEntity(String responseEntity) {
    }
}
