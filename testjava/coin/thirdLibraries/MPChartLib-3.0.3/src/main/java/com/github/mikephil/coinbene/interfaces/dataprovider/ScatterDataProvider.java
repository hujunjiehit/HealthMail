package com.github.mikephil.coinbene.interfaces.dataprovider;

import com.github.mikephil.coinbene.data.ScatterData;

public interface ScatterDataProvider extends BarLineScatterCandleBubbleDataProvider {

    ScatterData getScatterData();
}
