package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

public class CoinInfoModel extends BaseRes{


    /**
     * data : {"asset":"ETH","issueTime":"2014/07/24","totalAmount":"96311500","circulation":"96311500","whitePaper":"https://github.com/ethereum/wiki/wiki/%5BEnglish%5D-White-Paper","blockExplorer":"https://etherscan.io/","officialWebsite":"https://www.ethereum.org/","initialPrice":"0.31$","introduction":"Ethereum is a decentralized platform that runs smart contracts: applications that run exactly as programmed without any possibility of downtime, censorship, fraud or third party interference. These apps run on a custom built blockchain, an enormously powerful shared global infrastructure that can move value around and represent the ownership of property.","name":"Ether"}
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
         * asset : ETH
         * issueTime : 2014/07/24
         * totalAmount : 96311500
         * circulation : 96311500
         * whitePaper : https://github.com/ethereum/wiki/wiki/%5BEnglish%5D-White-Paper
         * blockExplorer : https://etherscan.io/
         * officialWebsite : https://www.ethereum.org/
         * initialPrice : 0.31$
         * introduction : Ethereum is a decentralized platform that runs smart contracts: applications that run exactly as programmed without any possibility of downtime, censorship, fraud or third party interference. These apps run on a custom built blockchain, an enormously powerful shared global infrastructure that can move value around and represent the ownership of property.
         * name : Ether
         */

        private String asset;
        private String issueTime;
        private String totalAmount;
        private String circulation;
        private String whitePaper;
        private String blockExplorer;
        private String officialWebsite;
        private String initialPrice;
        private String introduction;
        private String name;

        public String getAsset() {
            return asset;
        }

        public void setAsset(String asset) {
            this.asset = asset;
        }

        public String getIssueTime() {
            return issueTime;
        }

        public void setIssueTime(String issueTime) {
            this.issueTime = issueTime;
        }

        public String getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(String totalAmount) {
            this.totalAmount = totalAmount;
        }

        public String getCirculation() {
            return circulation;
        }

        public void setCirculation(String circulation) {
            this.circulation = circulation;
        }

        public String getWhitePaper() {
            return whitePaper;
        }

        public void setWhitePaper(String whitePaper) {
            this.whitePaper = whitePaper;
        }

        public String getBlockExplorer() {
            return blockExplorer;
        }

        public void setBlockExplorer(String blockExplorer) {
            this.blockExplorer = blockExplorer;
        }

        public String getOfficialWebsite() {
            return officialWebsite;
        }

        public void setOfficialWebsite(String officialWebsite) {
            this.officialWebsite = officialWebsite;
        }

        public String getInitialPrice() {
            return initialPrice;
        }

        public void setInitialPrice(String initialPrice) {
            this.initialPrice = initialPrice;
        }

        public String getIntroduction() {
            return introduction;
        }

        public void setIntroduction(String introduction) {
            this.introduction = introduction;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
