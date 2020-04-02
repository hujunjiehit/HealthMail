package com.coinbene.manbiwang.model.websocket;

import java.util.HashMap;

public class WsExchangeRate extends WsBaseResponse {


    /**
     * ts : 1550826247074
     * data : {"op":"exchangeRate","btcRates":{"BTCKRW":["7315.1000","\u20a9"],"BTCCNY":["43.6885","￥"],"BTCBRL":["24.2145","R$"],"BTCUSDT":["6.5000","$"]}}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * op : exchangeRate
         * btcRates : {"BTCKRW":["7315.1000","\u20a9"],"BTCCNY":["43.6885","￥"],"BTCBRL":["24.2145","R$"],"BTCUSDT":["6.5000","$"]}
         */

        private String op;
        private HashMap<String ,String[]> rates;

        public String getOp() {
            return op;
        }

        public void setOp(String op) {
            this.op = op;
        }

        public HashMap<String, String[]> getRates() {
            return rates;
        }

        public void setRates(HashMap<String, String[]> rates) {
            this.rates = rates;
        }
    }
}
