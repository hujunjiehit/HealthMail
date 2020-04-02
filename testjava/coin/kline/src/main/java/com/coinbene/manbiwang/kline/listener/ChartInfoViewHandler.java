package com.coinbene.manbiwang.kline.listener;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.github.mikephil.coinbene.charts.BarLineChartBase;
import com.github.mikephil.coinbene.highlight.Highlight;

/**
 * Created by dell on 2017/10/27.
 */

public class ChartInfoViewHandler implements View.OnTouchListener {

    private BarLineChartBase mChart;
    private final GestureDetector mDetector;

    private boolean mIsLongPress = false;

    public ChartInfoViewHandler(BarLineChartBase chart) {
        mChart = chart;
        mDetector = new GestureDetector(mChart.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                mIsLongPress = true;
                Highlight h = mChart.getHighlightByTouchPoint(e.getX(), e.getY());
                if (h == null) {
                    h = new Highlight(e.getX(), 0f, -1);
                    h.setDataIndex((int) e.getX());
                }
                try {
                    if (mChart.getData() != null) {
                        mChart.highlightValue(h, true);
                        mChart.disableScroll();
                    }
                } catch (Exception ex) {
                }
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                mChart.highlightValue(null, true);
                return false;
            }

        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mDetector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            mIsLongPress = false;
        }
        if (mIsLongPress && event.getAction() == MotionEvent.ACTION_MOVE) {
            Highlight h = mChart.getHighlightByTouchPoint(event.getX(), event.getY());
            if (h != null) {
                mChart.highlightValue(h, true);
                mChart.disableScroll();
            }
            return true;
        }
        return false;
    }
}
