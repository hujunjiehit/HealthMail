package com.coinbene.manbiwang.kline.bean;

import java.util.List;

/**
 * Created by mxd on 2019/1/23.
 */

public class AveEntity {

    /**
     * 分时图的均价
     *
     * @param kLineBeen
     */
    public AveEntity(List<KLineBean> kLineBeen) {

        long amountVol = 0;
        KLineBean lastKlineBean = null;
        for (int i = 0; i < kLineBeen.size(); i++) {
            KLineBean lineBean = kLineBeen.get(i);
            amountVol += lineBean.vol;
            lineBean.amountVol = amountVol;

            if (i > 0) {
                float total = lineBean.vol * lineBean.close + kLineBeen.get(i - 1).total;
                lineBean.total = total;
                if (amountVol != 0) {
                    float avePrice = total / amountVol;
                    lineBean.ave = avePrice;
                }
            } else if (lastKlineBean != null) {
                float total = lineBean.vol * lineBean.close + lastKlineBean.total;
                lineBean.total = total;
                if (amountVol != 0) {
                    float avePrice = total / amountVol;
                    lineBean.ave = avePrice;
                }
            } else {
                lineBean.amountVol = lineBean.vol;
                lineBean.ave = lineBean.close;
                lineBean.total = lineBean.amountVol * lineBean.ave;
            }
        }

    }

}
