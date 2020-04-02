package com.github.fujianlian.klinechart.formatter;

import android.text.TextUtils;

import com.github.fujianlian.klinechart.base.IValueFormatter;

/**
 * Value格式化类
 * Created by tifezh on 2016/6/21.
 */

public class PrecisionValueFormatter implements IValueFormatter {

    private int precision;

    private String formatStr;

    public PrecisionValueFormatter(int precision) {
        this.precision = precision;
        formatStr = "";
    }

    public void setPrecision(int count) {
        this.precision = count;
        formatStr = "";
    }

    @Override
    public String format(float value) {
        if (TextUtils.isEmpty(formatStr)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("%.").append(precision).append("f");
            formatStr = stringBuilder.toString();
        }
        return String.format(formatStr, value);
    }
}
