package com.june.healthmail.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatTextView;

import com.june.healthmail.R;
import com.june.healthmail.untils.Tools;

/**
 * Created by lirui on 15/11/5.
 */
public class RedPointNumTextView extends AppCompatTextView {

  private Context mContext;
  private Paint mRedPaint;
  private Paint mWhitePaint;
  private boolean isRed;
  private int mXoffset = 50; // 红点中心相对于最右边的偏移百分比
  private int mYoffset = 50; // 红点中心相对于最上面的偏移百分比
  private int mFinalX;
  private int mFinalY;
  private int mRadius = 8; // 红点半径
  private int mRectWidth = 12;//长方形宽
  private int mNum; //红点上显示的数字
  private boolean isAlignRight;//是否右对齐

  public RedPointNumTextView(Context context) {
    super(context);
    init(context);
  }

  public RedPointNumTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);

  }

  public RedPointNumTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);

  }

  private void init(Context contex) {
    this.mContext = contex;
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right,
                          int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    calculate();
  }

  private void calculate() {
    int w = getWidth();
    int h = getHeight();
    mFinalX = w * (100 - mXoffset) / 100;
    mFinalY = h * mYoffset / 100;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (isRed) {
      if (mRedPaint == null) {
        mRedPaint = new Paint();
        mRedPaint.setAntiAlias(true);
        mRedPaint.setDither(true);
        mRedPaint.setColor(mContext.getResources().getColor(R.color.text_color_red));
      }

      if (mNum > 0) {
        mRadius = 8;
        if (mWhitePaint == null) {
          mWhitePaint = new Paint();
          mWhitePaint.setAntiAlias(true);
          mWhitePaint.setDither(true);
          mWhitePaint.setColor(mContext.getResources().getColor(R.color.text_color_white));
          mWhitePaint.setTextSize(Tools.dip2px(mContext, 11));
        }

        String textNum = "";
        int textX = 0;

        if (mNum < 10) {

          if (isAlignRight) {
            mFinalX = getWidth() - Tools.dip2px(mContext, mRadius);
          }
          //圆点
          canvas.drawCircle(mFinalX, mFinalY, Tools.dip2px(mContext, mRadius),
              mRedPaint);

          textNum = mNum + "";
          textX = mFinalX - Tools.dip2px(mContext, 3);


          canvas.drawText(textNum, textX, mFinalY + Tools.dip2px(mContext, 3) + 1, mWhitePaint);
        } else if (mNum >= 10 && mNum <= 99) {
          //椭圆点
          mRectWidth = 8;

          if (isAlignRight) {
            mFinalX = getWidth() - Tools.dip2px(mContext, mRadius + mRectWidth / 2);
          }

          int orlX = mFinalX - Tools.dip2px(mContext, mRectWidth / 2);

          canvas.drawCircle(orlX, mFinalY, Tools.dip2px(mContext, mRadius),
              mRedPaint);

          canvas.drawCircle(orlX + Tools.dip2px(mContext, mRectWidth), mFinalY, Tools.dip2px(mContext,
              mRadius),
              mRedPaint);
          canvas.drawRect(orlX, mFinalY - Tools.dip2px(mContext, mRadius), orlX + Tools.dip2px(mContext, mRectWidth), mFinalY + Tools.dip2px(mContext, mRadius), mRedPaint);

          textNum = mNum + "";

          textX = mFinalX - Tools.dip2px(mContext, 3);
          textX -= Tools.dip2px(mContext, 4);

          canvas.drawText(textNum, textX, mFinalY + Tools.dip2px(mContext, 4), mWhitePaint);

        } else {
          //椭圆点
          mRectWidth = 12;

          if (isAlignRight) {
            mFinalX = getWidth() - Tools.dip2px(mContext, mRadius + mRectWidth / 2);
          }

          //椭圆点
          int orlX = mFinalX - Tools.dip2px(mContext, mRectWidth / 2);

          canvas.drawCircle(orlX, mFinalY, Tools.dip2px(mContext, mRadius),
              mRedPaint);

          canvas.drawCircle(orlX + Tools.dip2px(mContext, mRectWidth), mFinalY, Tools.dip2px(mContext,
              mRadius),
              mRedPaint);
          canvas.drawRect(orlX, mFinalY - Tools.dip2px(mContext, mRadius), orlX + Tools.dip2px(mContext, mRectWidth), mFinalY + Tools.dip2px(mContext, mRadius), mRedPaint);

          textNum = mNum + "";

          textX = mFinalX - Tools.dip2px(mContext, 3);
          textX -= Tools.dip2px(mContext, 6);
          textNum = "99+";

          canvas.drawText(textNum, textX, mFinalY + Tools.dip2px(mContext, 4), mWhitePaint);

        }

      } else {
        mRadius = 4;
        canvas.drawCircle(mFinalX, mFinalY, Tools.dip2px(mContext, mRadius),
            mRedPaint);
      }
    }
  }

  public void addRedPoint(int num) {
    mNum = num;
    isRed = true;
    invalidate();
  }

  public void addRedPoint() {
    mNum = 0;
    isRed = true;
    invalidate();
  }

  public void removeRedPoint() {
    mNum = 0;
    isRed = false;
    invalidate();
  }

  public void setXoffset(int value) {
    mXoffset = value;
  }

  public void setYoffset(int value) {
    mYoffset = value;
  }

  public boolean isAlignRight() {
    return isAlignRight;
  }

  public void setIsAlignRight(boolean isAlignRight) {
    this.isAlignRight = isAlignRight;
  }
}
