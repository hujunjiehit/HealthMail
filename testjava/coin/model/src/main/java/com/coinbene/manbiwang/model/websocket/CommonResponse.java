package com.coinbene.manbiwang.model.websocket;

/**
 * @author huyong
 */
public class CommonResponse implements Response<CommonEntity> {

    private String responseText;
    private CommonEntity<String> responseEntity;

    public CommonResponse() {
    }

    public CommonResponse(String responseText, CommonEntity<String> responseEntity) {
        this.responseText = responseText;
        this.responseEntity = responseEntity;
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
    public CommonEntity<String> getResponseEntity() {
        return responseEntity;
    }

    @Override
    public void setResponseEntity(CommonEntity responseEntity) {
        this.responseEntity = responseEntity;
    }
}
