package com.github.mikephil.coinbene.interfaces.dataprovider;

import com.github.mikephil.coinbene.data.CandleData;

public interface CandleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    CandleData getCandleData();
}
