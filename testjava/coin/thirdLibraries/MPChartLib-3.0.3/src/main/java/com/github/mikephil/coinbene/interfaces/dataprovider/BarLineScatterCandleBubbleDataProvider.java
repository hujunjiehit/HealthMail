package com.github.mikephil.coinbene.interfaces.dataprovider;

import com.github.mikephil.coinbene.data.BarLineScatterCandleBubbleData;
import com.github.mikephil.coinbene.utils.Transformer;
import com.github.mikephil.coinbene.components.YAxis;

public interface BarLineScatterCandleBubbleDataProvider extends ChartInterface {

    Transformer getTransformer(YAxis.AxisDependency axis);
    boolean isInverted(YAxis.AxisDependency axis);
    
    float getLowestVisibleX();
    float getHighestVisibleX();

    BarLineScatterCandleBubbleData getData();
}
