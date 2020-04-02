package com.coinbene.manbiwang.kline.bean;

import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by loro on 2017/3/2.
 */

public class MACDEntity {

    private List<Float> DEAs;
    private List<Float> DIFs;
    private List<Float> MACDs;

    /**
     * 得到MACD数据
     *
     * @param kLineBeen
     */
    public MACDEntity(List<KLineBean> kLineBeen) {
        DEAs = new CopyOnWriteArrayList<Float>();
        DIFs = new CopyOnWriteArrayList<Float>();
        MACDs = new CopyOnWriteArrayList<Float>();

        List<Float> dEAs = new CopyOnWriteArrayList<Float>();
        List<Float> dIFs = new CopyOnWriteArrayList<Float>();
        List<Float> mACDs = new CopyOnWriteArrayList<Float>();

        float eMA12 = 0.0f;
        float eMA26 = 0.0f;
        float close = 0f;
        float dIF = 0.0f;
        float dEA = 0.0f;
        float mACD = 0.0f;
        if (kLineBeen != null && kLineBeen.size() > 0) {
            for (int i = 0; i < kLineBeen.size(); i++) {
                close = kLineBeen.get(i).close;
                if (i == 0) {
                    eMA12 = close;
                    eMA26 = close;
                } else {
                    eMA12 = eMA12 * 11 / 13 + close * 2 / 13;
                    eMA26 = eMA26 * 25 / 27 + close * 2 / 27;
                }
                dIF = eMA12 - eMA26;
                dEA = dEA * 8 / 10 + dIF * 2 / 10;
                mACD = dIF - dEA;
                dEAs.add(dEA);
                dIFs.add(dIF);
                mACDs.add(mACD);
            }

            for (int i = 0; i < dEAs.size(); i++) {
                DEAs.add(dEAs.get(i));
                DIFs.add(dIFs.get(i));
                MACDs.add(mACDs.get(i));
            }

            Log.d("", "");
        }

    }

    public List<Float> getDEA() {
        return DEAs;
    }

    public List<Float> getDIF() {
        return DIFs;
    }

    public List<Float> getMACD() {
        return MACDs;
    }
}
