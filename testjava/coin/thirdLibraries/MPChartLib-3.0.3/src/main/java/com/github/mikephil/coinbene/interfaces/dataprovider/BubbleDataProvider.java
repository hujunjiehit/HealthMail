package com.github.mikephil.coinbene.interfaces.dataprovider;

import com.github.mikephil.coinbene.data.BubbleData;

public interface BubbleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    BubbleData getBubbleData();
}
