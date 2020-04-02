package com.coinbene.manbiwang.model.http;

import android.os.Parcel;
import android.os.Parcelable;

public class TradePairParcel implements Parcelable {
    public String id;
    public String title_en;
    public String sort;

    public TradePairParcel() {
    }

    public TradePairParcel(Parcel in) {
        id = in.readString();
        title_en = in.readString();
//        title_zh = in.readString();
        sort = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title_en);
//        dest.writeString(title_zh);
        dest.writeString(sort);
    }

    public static final Creator<TradePairParcel> CREATOR = new Creator<TradePairParcel>() {
        @Override
        public TradePairParcel createFromParcel(Parcel in) {
            return new TradePairParcel(in);
        }

        @Override
        public TradePairParcel[] newArray(int size) {
            return new TradePairParcel[size];
        }
    };
}
