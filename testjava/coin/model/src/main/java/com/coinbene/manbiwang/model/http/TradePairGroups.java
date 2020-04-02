package com.coinbene.manbiwang.model.http;

import android.os.Parcel;
import android.os.Parcelable;

public class TradePairGroups implements Parcelable {
    public String id;
    public String title_en;
    public String title_zh;
    public String sort;

    public TradePairGroups(Parcel in) {
        id = in.readString();
        title_en = in.readString();
        title_zh = in.readString();
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
        dest.writeString(title_zh);
        dest.writeString(sort);
    }

    public final Creator<TradePairGroups> CREATOR = new Creator<TradePairGroups>() {
        @Override
        public TradePairGroups createFromParcel(Parcel in) {
            return new TradePairGroups(in);
        }

        @Override
        public TradePairGroups[] newArray(int size) {
            return new TradePairGroups[size];
        }
    };
}
