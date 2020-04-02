package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

/**
 * @author huyong
 */
public class Balance30DayModel extends BaseRes {


    /**
     * data : {"preestimateType":"USD","currencySymbol":"$","current":{"date":"2018-12-21 00:00:00","btcPreestimate":"2265672.30151732","localPreestimate":"14785596185.91"},"list":[{"date":"2018-12-21 00:00:00","btcPreestimate":"2265672.30151732","localPreestimate":"14785596185.91"},{"date":"2018-12-21 00:00:00","btcPreestimate":"2265672.30151732","localPreestimate":"14785596185.91"}]}
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
         * preestimateType : USD
         * currencySymbol : $
         * current : {"date":"2018-12-21 00:00:00","btcPreestimate":"2265672.30151732","localPreestimate":"14785596185.91"}
         * list : [{"date":"2018-12-21 00:00:00","btcPreestimate":"2265672.30151732","localPreestimate":"14785596185.91"},{"date":"2018-12-21 00:00:00","btcPreestimate":"2265672.30151732","localPreestimate":"14785596185.91"}]
         */

        private String preestimateType;
        private String currencySymbol;
        private CurrentBean current;
        private List<ListBean> list;

        public String getPreestimateType() {
            return preestimateType;
        }

        public void setPreestimateType(String preestimateType) {
            this.preestimateType = preestimateType;
        }

        public String getCurrencySymbol() {
            return currencySymbol;
        }

        public void setCurrencySymbol(String currencySymbol) {
            this.currencySymbol = currencySymbol;
        }

        public CurrentBean getCurrent() {
            return current;
        }

        public void setCurrent(CurrentBean current) {
            this.current = current;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class CurrentBean {
            /**
             * date : 2018-12-21 00:00:00
             * btcPreestimate : 2265672.30151732
             * localPreestimate : 14785596185.91
             */

            private String date;
            private String btcPreestimate;
            private String localPreestimate;

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getBtcPreestimate() {
                return btcPreestimate;
            }

            public void setBtcPreestimate(String btcPreestimate) {
                this.btcPreestimate = btcPreestimate;
            }

            public String getLocalPreestimate() {
                return localPreestimate;
            }

            public void setLocalPreestimate(String localPreestimate) {
                this.localPreestimate = localPreestimate;
            }
        }

        public static class ListBean {
            /**
             * date : 2018-12-21 00:00:00
             * btcPreestimate : 2265672.30151732
             * localPreestimate : 14785596185.91
             */

            private String date;
            private String btcPreestimate;
            private String localPreestimate;

            public ListBean(String date, String btcPreestimate, String localPreestimate) {
                this.date = date;
                this.btcPreestimate = btcPreestimate;
                this.localPreestimate = localPreestimate;
            }

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getBtcPreestimate() {
                return btcPreestimate;
            }

            public void setBtcPreestimate(String btcPreestimate) {
                this.btcPreestimate = btcPreestimate;
            }

            public String getLocalPreestimate() {
                return localPreestimate;
            }

            public void setLocalPreestimate(String localPreestimate) {
                this.localPreestimate = localPreestimate;
            }
        }
    }
}
