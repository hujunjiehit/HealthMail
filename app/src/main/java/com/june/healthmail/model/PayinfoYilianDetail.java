package com.june.healthmail.model;

/**
 * Created by june on 2017/3/15.
 */

public class PayinfoYilianDetail {


    /**
     * retCode : 0000
     * retMsg : 交易成功
     * ylModel : {"Version":"2.0.0","TradeCode":"PlaceOrder","MerchantId":"502050002097",
     * "MerchOrderId":"M1801182317319266853","Amount":"1000.00","TradeTime":"20180118231732",
     * "OrderId":"502018011826825888","Sign":"O3H7cHpaZKhE0VG6Z7GM2lkmpcdQL8WS2tsEojEDRv3rVVQpCx2GtncIzb6RWYfApVd+iI2aH
     * NJlbaeujxgx7LWL9A6brTkUVu4TnPEqmsCYm3cfoZJORtPH1gHdtMTLk5f9b589mRRtYbFrdTbRJQmjKSTrAR3D3Xl3DNBOx0Y="}
     */

    private String retCode;
    private String retMsg;
    private YlModelBean ylModel;

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }

    public YlModelBean getYlModel() {
        return ylModel;
    }

    public void setYlModel(YlModelBean ylModel) {
        this.ylModel = ylModel;
    }

    public static class YlModelBean {
        /**
         * Version : 2.0.0
         * TradeCode : PlaceOrder
         * MerchantId : 502050002097
         * MerchOrderId : M1801182317319266853
         * Amount : 1000.00
         * TradeTime : 20180118231732
         * OrderId : 502018011826825888
         * Sign : O3H7cHpaZKhE0VG6Z7GM2lkmpcdQL8WS2tsEojEDRv3rVVQpCx2GtncIzb6RWYfApVd+iI2aHNJlbaeujxgx7LWL9A6brTkUVu4TnPEqmsCYm3cfoZJORtPH1gHdtMTLk5f9b589mRRtYbFrdTbRJQmjKSTrAR3D3Xl3DNBOx0Y=
         */

        private String Version;
        private String TradeCode;
        private String MerchantId;
        private String MerchOrderId;
        private String Amount;
        private String TradeTime;
        private String OrderId;
        private String Sign;

        public String getVersion() {
            return Version;
        }

        public void setVersion(String Version) {
            this.Version = Version;
        }

        public String getTradeCode() {
            return TradeCode;
        }

        public void setTradeCode(String TradeCode) {
            this.TradeCode = TradeCode;
        }

        public String getMerchantId() {
            return MerchantId;
        }

        public void setMerchantId(String MerchantId) {
            this.MerchantId = MerchantId;
        }

        public String getMerchOrderId() {
            return MerchOrderId;
        }

        public void setMerchOrderId(String MerchOrderId) {
            this.MerchOrderId = MerchOrderId;
        }

        public String getAmount() {
            return Amount;
        }

        public void setAmount(String Amount) {
            this.Amount = Amount;
        }

        public String getTradeTime() {
            return TradeTime;
        }

        public void setTradeTime(String TradeTime) {
            this.TradeTime = TradeTime;
        }

        public String getOrderId() {
            return OrderId;
        }

        public void setOrderId(String OrderId) {
            this.OrderId = OrderId;
        }

        public String getSign() {
            return Sign;
        }

        public void setSign(String Sign) {
            this.Sign = Sign;
        }
    }
}
