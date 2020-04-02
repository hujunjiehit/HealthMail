package com.coinbene.manbiwang.kline.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.Float.NaN;

/**
 * Created by loro on 2017/3/7.
 */

public class BOLLEntity {

    private List<Float> UPs;
    private List<Float> MBs;
    private List<Float> DNs;

    /**
     * 得到BOLL指标
     *
     * @param kLineBeens
     * @param n
     */
    public BOLLEntity(List<KLineBean> kLineBeens, int n) {
        BOLLEntity2(kLineBeens, n, NaN);
//        calculateBOLL2(kLineBeens);
    }

    /**
     * 得到BOLL指标
     * @param kLineBeens
     * @param n
     * @param defult
     */
    public void BOLLEntity2(List<KLineBean> kLineBeens, int n, float defult) {
        UPs = new CopyOnWriteArrayList<>();
        MBs = new CopyOnWriteArrayList<>();
        DNs = new CopyOnWriteArrayList<>();

        float ma = 0.0f;
        float md = 0.0f;
        float mb = 0.0f;
        float up = 0.0f;
        float dn = 0.0f;

        if (kLineBeens != null && kLineBeens.size() > 0) {
            float closeSum = 0.0f;
            float sum = 0.0f;
            int index = 0;
            int index2 = n - 1;
            for (int i = 0; i < kLineBeens.size(); i++) {
                int k = i - n + 1;
                if (i >= n) {
                    index = n;
                } else {
                    index = i + 1;
                }
                if (i < index2) {
                    mb = defult;
                    up = defult;
                    dn = defult;
                } else {
                    closeSum = getSumClose(k, i, kLineBeens);
                    ma = closeSum / index;
                    sum = getSum2(k, i, ma, kLineBeens);
                    md = (float) Math.sqrt(sum / index);
                    mb = ((closeSum - (float) kLineBeens.get(i).close) / (index - 1));
                    up = mb + (2 * md);
                    dn = mb - (2 * md);
                }
                UPs.add(up);
                MBs.add(mb);
                DNs.add(dn);
            }
        }
    }

    public void calculateBOLL(List<KLineBean> dataList) {
        UPs = new ArrayList<>();
        MBs = new ArrayList<>();
        DNs = new ArrayList<>();
        float mb = 0.0f;
        float up = 0.0f;
        float dn = 0.0f;
        for (int i = 0; i < dataList.size(); i++) {
            KLineBean point = dataList.get(i);
            if (i == 0) {
                UPs.add(Float.NaN);
                MBs.add(Float.NaN);
                DNs.add(Float.NaN);
            } else {
                int n = 20;
                if (i < 20) {
                    n = i + 1;
                }
                float md = 0;
                for (int j = i - n + 1; j <= i; j++) {
                    float c = dataList.get(j).close;
                    float m = point.ma20;
                    float value = c - m;
                    md += value * value;
                }
                md = md / (n - 1);
                md = (float) Math.sqrt(md);
                mb = point.ma20;
                up = mb + 2f * md;
                dn = mb - 2f * md;

                UPs.add(up);
                MBs.add(mb);
                DNs.add(dn);
            }
        }
    }

    private void calculateBOLL2(List<KLineBean> dataList) {
        UPs = new ArrayList<>();
        MBs = new ArrayList<>();
        DNs = new ArrayList<>();
        float mb = 0.0f;
        float up = 0.0f;
        float dn = 0.0f;
        for (int i = 0; i < dataList.size(); i++) {
            KLineBean point = dataList.get(i);
            if (i < 19) {
                UPs.add(Float.NaN);
                MBs.add(Float.NaN);
                DNs.add(Float.NaN);
            } else {
                int n = 20;
                float md = 0;
                for (int j = i - n + 1; j <= i; j++) {
                    float c = dataList.get(j).close;//getClosePrice();
                    float m = point.ma20;//getres_bluePrice();
                    float value = c - m;
                    md += value * value;
                }
                md = md / (n - 1);
                md = (float) Math.sqrt(md);
                mb = point.ma20;//();
                up = mb + 2f * md;
                dn = mb - 2f * md;

                UPs.add(up);
                MBs.add(mb);
                DNs.add(dn);
            }
        }

    }

    private BigDecimal getSum(Integer a, Integer b, Float ma, ArrayList<KLineBean> kLineBeens) {
        if (a < 0)
            a = 0;
        KLineBean kLineBean;
//        float sum = 0.0f;
        BigDecimal sumBigDecimal = new BigDecimal(0.0f);
        for (int i = a; i <= b; i++) {
            kLineBean = kLineBeens.get(i);
//            sum += new BigDecimal(kLineBean.close - ma).multiplyDown(new BigDecimal(kLineBean.close - ma)).floatValue();
            sumBigDecimal = sumBigDecimal.add(new BigDecimal(kLineBean.close - ma).multiply(new BigDecimal(kLineBean.close - ma)));//.floatValue();
        }

        return sumBigDecimal;
    }

    private Float getSum2(Integer a, Integer b, Float ma, List<KLineBean> kLineBeens) {
        if (a < 0) {
            a = 0;
        }
        KLineBean kLineBean;
        float sum = 0.0f;
        for (int i = a; i <= b; i++) {
            kLineBean = kLineBeens.get(i);
            sum += ((kLineBean.close - ma) * (kLineBean.close - ma));
        }
        return sum;
    }


    private Float getSumClose(Integer a, Integer b, List<KLineBean> kLineBeens) {
        if (a < 0)
            a = 0;
        float close = 0.0f;
        for (int i = a; i <= b; i++) {
            close += kLineBeens.get(i).close;
        }

        return close;
    }


    public List<Float> getUPs() {
        return UPs;
    }

    public List<Float> getMBs() {
        return MBs;
    }

    public List<Float> getDNs() {
        return DNs;
    }
}
