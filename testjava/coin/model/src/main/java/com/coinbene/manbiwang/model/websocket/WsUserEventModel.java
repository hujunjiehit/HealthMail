package com.coinbene.manbiwang.model.websocket;

import java.util.List;

/**
 * @author huyong
 */
public class WsUserEventModel extends WsBaseResponse {


    /**
     * ts : 1552287201946
     * data : {"events":[{"key":"positions_changed","value":""},{"key":"curorder_changed","value":""},{"key":"hisorder_changed","value":""}]}
     * full : false
     */

    private DataBean data;
    private boolean full;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public boolean isFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
    }

    public static class DataBean {
        private List<EventsBean> events;

        public List<EventsBean> getEvents() {
            return events;
        }

        public void setEvents(List<EventsBean> events) {
            this.events = events;
        }

        public static class EventsBean {
            /**
             * key : positions_changed
             * value :
             */

            private String key;
            private String value;

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }
    }
}
