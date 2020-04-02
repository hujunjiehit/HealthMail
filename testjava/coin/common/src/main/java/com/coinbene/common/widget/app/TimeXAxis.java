package com.coinbene.common.widget.app;

import android.util.SparseArray;

import com.github.mikephil.coinbene.components.XAxis;

/**
 * Created by loro on 2017/2/8.
 */
public class TimeXAxis extends XAxis {
    private SparseArray<String> labels;

    public SparseArray<String> getXLabels() {
        return labels;
    }

    public void setXLabels(SparseArray<String> labels) {
        this.labels = labels;
    }
}
