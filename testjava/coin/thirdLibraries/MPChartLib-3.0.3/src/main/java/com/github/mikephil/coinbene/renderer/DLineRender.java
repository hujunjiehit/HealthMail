package com.github.mikephil.coinbene.renderer;

import android.graphics.Canvas;
import android.util.Log;

import com.github.mikephil.coinbene.animation.ChartAnimator;
import com.github.mikephil.coinbene.charts.CombinedChart;
import com.github.mikephil.coinbene.data.CandleData;
import com.github.mikephil.coinbene.data.CandleDataSet;
import com.github.mikephil.coinbene.data.CandleEntry;
import com.github.mikephil.coinbene.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.coinbene.utils.ViewPortHandler;

import java.util.List;

public class DLineRender extends CombinedChartRenderer {
    public DLineRender(CombinedChart chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    @Override
    public void drawData(Canvas c) {
        super.drawData(c);
        drawLine(c);
    }

    private void drawLine(Canvas canvas) {
        CombinedChart chart = (CombinedChart) mChart.get();
        if (chart == null) {
            return;
        }

        CandleData candleData = chart.getCandleData();
        if (candleData == null) {
            return;
        }
        int count = candleData.getDataSetCount();
        List<ICandleDataSet> dataSets = candleData.getDataSets();
        CandleDataSet candleDataSet = null;
        for (int i = 0; i < dataSets.size(); i++) {
            if (dataSets.get(i) instanceof CandleDataSet) {
                candleDataSet = (CandleDataSet) dataSets.get(i);
                break;
            }
        }
        if (candleDataSet == null || candleDataSet.getEntryCount() == 0) {
            return;
        }
        int entryCount = candleDataSet.getEntryCount();
        CandleEntry candleEntry = candleDataSet.getEntryForIndex(entryCount - 1);

        float startx = 0;
        float endx = mViewPortHandler.contentRight();
        float starty = 0;
        float endy = 0;

        int width = (int) mViewPortHandler.getChartWidth();
        int height = (int) mViewPortHandler.getChartHeight();


//        canvas.drawLine(startx,starty,endx,endy,);
    }
}
