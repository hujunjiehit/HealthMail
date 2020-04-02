package com.coinbene.manbiwang.model.http;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mengxiangdong on 2018/3/23.
 */

public class PairInfo implements Parcelable {
    public String id;//交易对id
    public String name;

    public String basicAsset;//分子id
    public String evalAsset;//分母id
    public String chineseName;
    public String englishName;
    public String minAmount;
    public String minCnt;
    public String minPrice;
    public int pricePrecision;
    public String takerFee;
    public String priceChangeScale;
    public String groupId;
    public int volumePrecision;
    public int sellDisabled;//0,正常交易对，1,IEO交易对

    public PairInfo() {

    }

    public PairInfo(Parcel in) {
        id = in.readString();
        name = in.readString();
        chineseName = in.readString();
        englishName = in.readString();
        minAmount = in.readString();
        minCnt = in.readString();
        minPrice = in.readString();
        priceChangeScale = in.readString();
        groupId = in.readString();
        pricePrecision = in.readInt();
        takerFee = in.readString();
        volumePrecision = in.readInt();
        evalAsset = in.readString();
        basicAsset = in.readString();
        sellDisabled = in.readInt();
    }

    public static final Creator<PairInfo> CREATOR = new Creator<PairInfo>() {
        @Override
        public PairInfo createFromParcel(Parcel in) {
            return new PairInfo(in);
        }

        @Override
        public PairInfo[] newArray(int size) {
            return new PairInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(chineseName);
        dest.writeString(englishName);
        dest.writeString(minAmount);
        dest.writeString(minCnt);
        dest.writeString(minPrice);
        dest.writeString(priceChangeScale);
        dest.writeString(groupId);
        dest.writeInt(pricePrecision);
        dest.writeString(takerFee);
        dest.writeInt(volumePrecision);
        dest.writeString(evalAsset);
        dest.writeString(basicAsset);
        dest.writeInt(sellDisabled);
    }
}
