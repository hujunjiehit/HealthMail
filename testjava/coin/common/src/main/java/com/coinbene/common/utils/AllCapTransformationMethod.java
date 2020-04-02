package com.coinbene.common.utils;


import android.graphics.Rect;
import android.text.method.ReplacementTransformationMethod;
import android.view.View;

/**
 * Created by mengxiangdong on 2018/3/12.
 */

public class AllCapTransformationMethod extends ReplacementTransformationMethod {

    private boolean allUpper = false;
    private char[] lower = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private char[] upper = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    public AllCapTransformationMethod() {
        super();
    }

    public AllCapTransformationMethod(boolean needUpper) {
        this.allUpper = needUpper;
    }

    @Override
    public CharSequence getTransformation(CharSequence source, View v) {
        return super.getTransformation(source, v);
    }

    @Override
    public void onFocusChanged(View view, CharSequence sourceText, boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(view, sourceText, focused, direction, previouslyFocusedRect);
    }

    @Override
    protected char[] getOriginal() {
        if (allUpper) {
            return lower;
        } else {
            return upper;
        }
    }

    @Override
    protected char[] getReplacement() {
        if (allUpper) {
            return upper;
        } else {
            return lower;
        }
    }
}
