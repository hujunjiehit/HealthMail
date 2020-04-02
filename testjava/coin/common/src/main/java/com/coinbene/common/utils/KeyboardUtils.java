package com.coinbene.common.utils;

import android.view.View;
import android.widget.EditText;

import com.qmuiteam.qmui.util.QMUIKeyboardHelper;

public class KeyboardUtils {

    /**
     * 关闭输入框
     * @param view  获得焦点的view
     */

    public static void hideKeyboard(View view) {
        QMUIKeyboardHelper.hideKeyboard(view);
    }

    /**
     * 弹出输入框
     * @param view
     */
    public static void showKeyboard(EditText view) {
        QMUIKeyboardHelper.showKeyboard(view, 200);
    }

}
