package com.coinbene.common.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class BitmapUtils {


	/**
	 * Drawable转换成一个Bitmap
	 *
	 * @param drawable drawable对象
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable,int wirth,int heigth) {
		Bitmap bitmap = Bitmap.createBitmap(wirth, heigth,
				drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, wirth, heigth);
		drawable.draw(canvas);
		return bitmap;
	}
}
