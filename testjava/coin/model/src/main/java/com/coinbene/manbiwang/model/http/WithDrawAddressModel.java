package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhangle on 2018/4/10.
 */

public class WithDrawAddressModel extends BaseRes{


    /**
     * code : 0
     * data : {"list":[{"address":"string","asset":"string","id":0,"label":"string","tag":"string"}],"pages":0,"total":0}
     * message : string
     * timezone : 0
     */

    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class DataBean {
        /**
         * list : [{"address":"string","asset":"string","id":0,"label":"string","tag":"string"}]
         * pages : 0
         * total : 0
         */

        private int pages;
        private int total;
        private List<ListBean> list;

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean  implements Serializable{
            /**
             * address : string
             * asset : string
             * id : 0
             * label : string
             * tag : string
             */

            private String address;
            private String asset;
            private int id;
            private String label;
            private String tag;
            private String chain;

            private String  protocolName;
            public boolean hideDelete;

            public String getProtocolName() {
                return protocolName;
            }

            public void setProtocolName(String protocolName) {
                this.protocolName = protocolName;
            }

            public String getChain() {
                return chain;
            }

            public void setChain(String chain) {
                this.chain = chain;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getAsset() {
                return asset;
            }

            public void setAsset(String asset) {
                this.asset = asset;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getLabel() {
                return label;
            }

            public void setLabel(String label) {
                this.label = label;
            }

            public String getTag() {
                return tag;
            }

            public void setTag(String tag) {
                this.tag = tag;
            }
        }
    }
}
