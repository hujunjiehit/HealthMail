package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

/**
 * @author ding
 * 划转记录Model
 */
public class TransferRecordModel extends BaseRes {


    /**
     * message : null
     * timezone : null
     * data : {"pageNum":1,"total":6,"pageSize":1,"list":[{"id":83,"asset":"BTC","amount":"10.0000","from":"币币账户","to":"合约账户","time":1551682667000},{"id":87,"asset":"BTC","amount":"11.0000","from":"币币账户","to":"合约账户","time":1551683261000},{"id":95,"asset":"BTC","amount":"10.0000","from":"币币账户","to":"合约账户","time":1551685511000},{"id":96,"asset":"BTC","amount":"20.0000","from":"币币账户","to":"合约账户","time":1551686174000},{"id":97,"asset":"BTC","amount":"19.0000","from":"币币账户","to":"合约账户","time":1551686180000},{"id":98,"asset":"BTC","amount":"10.0000","from":"币币账户","to":"合约账户","time":1551687170000}]}
     */


    private Object timezone;
    private DataBean data;


    public Object getTimezone() {
        return timezone;
    }

    public void setTimezone(Object timezone) {
        this.timezone = timezone;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * pageNum : 1
         * total : 6
         * pageSize : 1
         * list : [{"id":83,"asset":"BTC","amount":"10.0000","from":"币币账户","to":"合约账户","time":1551682667000},{"id":87,"asset":"BTC","amount":"11.0000","from":"币币账户","to":"合约账户","time":1551683261000},{"id":95,"asset":"BTC","amount":"10.0000","from":"币币账户","to":"合约账户","time":1551685511000},{"id":96,"asset":"BTC","amount":"20.0000","from":"币币账户","to":"合约账户","time":1551686174000},{"id":97,"asset":"BTC","amount":"19.0000","from":"币币账户","to":"合约账户","time":1551686180000},{"id":98,"asset":"BTC","amount":"10.0000","from":"币币账户","to":"合约账户","time":1551687170000}]
         */

        private int pageNum;
        private int total;
        private int pageSize;
        private List<ListBean> list;

        public int getPageNum() {
            return pageNum;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            //           "id": 37,
            //                   "asset": "BTC",
            //                   "amount": "10.0000",
            //                   "from": "Spot",
            //                   "to": "Futures",
            //                   "time": 1555647888000

            private int id;
            private String asset;
            private String amount;
            private String from;
            private String to;
            private long time;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getAsset() {
                return asset;
            }

            public void setAsset(String asset) {
                this.asset = asset;
            }

            public String getAmount() {
                return amount;
            }

            public void setAmount(String amount) {
                this.amount = amount;
            }

            public String getFrom() {
                return from;
            }

            public void setFrom(String from) {
                this.from = from;
            }

            public String getTo() {
                return to;
            }

            public void setTo(String to) {
                this.to = to;
            }

            public long getTime() {
                return time;
            }

            public void setTime(long time) {
                this.time = time;
            }
        }
    }
}
