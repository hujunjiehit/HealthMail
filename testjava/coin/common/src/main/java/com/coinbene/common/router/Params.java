package com.coinbene.common.router;

import android.os.Parcel;
import android.os.Parcelable;

public class Params implements Parcelable {

    public static String KEY_TRADE_PAIR = "tradePair";  //ETHUSDT
    public static String KEY_PAIR_NAME = "pairName"; //ETH/USDT
    public static String KEY_GROUP_NAME = "groupName";  //USDT
    public static String KEY_SYMBOL = "symbol";
    public static String KEY_TYPE = "type";
    public static String KEY_PRICE_PRECISION = "pricePrecision";
    public static String KEY_CNT_PRECISION = "cntPrecision";
    public static String KEY_ASSET = "asset";
    public static String KEY_FROM = "from";
    public static String KEY_TO = "to";

    protected String tradePair;
    protected String pairName;
    protected String groupName;
    protected String symbol;
    protected String type;
    protected int pricePrecision;
    protected int cntPrecision;

    protected String asset;
    protected String from;
    protected String to;

    protected Params(Parcel in) {
        tradePair = in.readString();
        pairName = in.readString();
        groupName = in.readString();
        symbol = in.readString();
        type = in.readString();
        pricePrecision = in.readInt();
        cntPrecision = in.readInt();
        asset = in.readString();
        from = in.readString();
        to = in.readString();
    }

    public static final Creator<Params> CREATOR = new Creator<Params>() {
        @Override
        public Params createFromParcel(Parcel in) {
            return new Params(in);
        }

        @Override
        public Params[] newArray(int size) {
            return new Params[size];
        }
    };

    public String getTradePair() {
        return tradePair;
    }

    public void setTradePair(String tradePair) {
        this.tradePair = tradePair;
    }

    public String getPairName() {
        return pairName;
    }

    public void setPairName(String pairName) {
        this.pairName = pairName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPricePrecision() {
        return pricePrecision;
    }

    public void setPricePrecision(int pricePrecision) {
        this.pricePrecision = pricePrecision;
    }

    public int getCntPrecision() {
        return cntPrecision;
    }

    public void setCntPrecision(int cntPrecision) {
        this.cntPrecision = cntPrecision;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
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

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(tradePair);
        parcel.writeString(pairName);
        parcel.writeString(groupName);
        parcel.writeString(symbol);
        parcel.writeString(type);
        parcel.writeInt(pricePrecision);
        parcel.writeInt(cntPrecision);
        parcel.writeString(asset);
        parcel.writeString(from);
        parcel.writeString(to);
    }
}
