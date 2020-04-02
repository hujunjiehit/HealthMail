package com.coinbene.common.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.warkiz.widget.IndicatorSeekBar;

import java.lang.reflect.Field;

/**
 * Created by june
 * on 2019-12-26
 */
public class CustomSeekBar extends IndicatorSeekBar {

	public static int tick_text_top_margin = 10;

	public CustomSeekBar(Context context) {
		super(context);
	}

	public CustomSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		//通过反射修改tick_text文字位置
		try {
			Field fieldTickTextY = IndicatorSeekBar.class.getDeclaredField("mTickTextY");
			fieldTickTextY.setAccessible(true);
			float  mTickTextY = (float) fieldTickTextY.get(this);
			mTickTextY = mTickTextY + QMUIDisplayHelper.dp2px(getContext(), tick_text_top_margin);
			fieldTickTextY.set(this, mTickTextY);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
