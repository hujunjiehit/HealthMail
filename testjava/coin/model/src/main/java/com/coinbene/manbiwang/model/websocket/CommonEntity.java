package com.coinbene.manbiwang.model.websocket;

/**
 * @author huyong
 */
public class CommonEntity<T> {
    private String topic;
    private String ts;

    private T data;

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    //    private int code;//10开头成功
//    private CommandBean command;
//
//    public String getMsg() {
//        return msg;
//    }
//
//    public void setMsg(String msg) {
//        this.msg = msg;
//    }
//
//    public T getData() {
//        return data;
//    }
//
//    public void setData(T data) {
//        this.data = data;
//    }
//
//    public int getCode() {
//        return code;
//    }
//
//    public void setCode(int code) {
//        this.code = code;
//    }
//
//    public CommandBean getCommand() {
//        return command;
//    }
//
//    public void setCommand(CommandBean command) {
//        this.command = command;
//    }
//
//    public static class CommandBean {
//
//        private String path;
//        private String unique;
//
//        public String getPath() {
//            return path;
//        }
//
//        public void setPath(String path) {
//            this.path = path;
//        }
//
//        public String getUnique() {
//            return unique;
//        }
//
//        public void setUnique(String unique) {
//            this.unique = unique;
//        }
//    }
}
