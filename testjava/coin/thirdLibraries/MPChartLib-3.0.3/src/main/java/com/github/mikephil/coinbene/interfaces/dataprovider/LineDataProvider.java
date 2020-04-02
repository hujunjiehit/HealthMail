package com.github.mikephil.coinbene.interfaces.dataprovider;

import com.github.mikephil.coinbene.components.YAxis;
import com.github.mikephil.coinbene.data.LineData;

public interface LineDataProvider extends BarLineScatterCandleBubbleDataProvider {

    LineData getLineData();

    YAxis getAxis(YAxis.AxisDependency dependency);
}
