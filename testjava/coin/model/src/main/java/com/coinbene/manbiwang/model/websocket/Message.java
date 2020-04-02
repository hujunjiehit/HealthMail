package com.coinbene.manbiwang.model.websocket;

/**
 * WebSocket消息
 * <p>
 *
 * @author huyong
 */
public class Message {

    public enum MessageType {
        //连接Socket
        CONNECT,
        //断开连接，主动关闭或被动关闭
        DISCONNECT,
        //结束线程
        QUIT,
        //通过Socket连接发送数据
        SEND_MESSAGE,
        //通过Socket获取到数据
        RECEIVE_MESSAGE
    }

    private MessageType messageType;
    private String requestText;
    private String responseText;

    public Message() {
    }

    public Message(MessageType messageType) {
        this.messageType = messageType;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getRequestText() {
        return requestText;
    }

    public void setRequestText(String requestText) {
        this.requestText = requestText;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }
}
