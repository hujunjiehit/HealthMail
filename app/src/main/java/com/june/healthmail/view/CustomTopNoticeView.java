package com.june.healthmail.view;

/**
 * @Description: TODO
 * @author dingguofeng
 * @version V1.0
 * @since 2015年6月17日
 * <p>
 * modify by lirui
 */

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.june.healthmail.R;
import com.june.healthmail.untils.Tools;


/**
 * 通用顶部公告栏控件
 *
 * @author dingguofeng
 * @since 2015年6月17日
 */
public class CustomTopNoticeView extends TextView {

  private static final int STATE_CLOSEED = 0;//关闭
  private static final int STATE_OPENING = 1;//正在下拉
  private static final int STATE_OPEN = 2;//展示中
  private static final int STATE_CLOSING = 3;//正在关闭


  private static final int AUTO_DISMISS_DURATION = 15000;// 自动消失的时间15秒
  private static final int MAX_HEIGHT = 30;
  private static final int ANIMATION_DURATION = 300;//300
  private Context mContext;
  private boolean isAutoDismiss = true;// 是否自动消失（15秒）
  private int currentState = STATE_CLOSEED;
  private ValueAnimator openAnimator;
  private ValueAnimator closeAnimator;

  private Paint paint = new Paint();
  private Path path = new Path();
  private int height;
  private int autoDissmissDuration = AUTO_DISMISS_DURATION;

  private Runnable autoDissmissRunnable = new Runnable() {
    @Override
    public void run() {
      close();
    }
  };

  public CustomTopNoticeView(Context context) {
    this(context, null);
  }

  public CustomTopNoticeView(Context context, AttributeSet attrs) {
    super(context, attrs);
    mContext = context;
    setTextColor(context.getResources().getColor(R.color.top_notice_text_color));
    setBackgroundColor(context.getResources().getColor(R.color.top_notice_bg_color));
    setPadding(16, 0, 16, 0);
    setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);

    setSingleLine();
    setEllipsize(TextUtils.TruncateAt.MARQUEE);
    setMarqueeRepeatLimit(-1);

    paint.setColor(context.getResources().getColor(R.color.div_line_color2));
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(2.0f);
  }

  public void showIndicator() {
    this.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.custom_top_notice_indicator_icon, 0);
    setCompoundDrawablePadding(6);
  }

  /**
   * 清除>符号
   */
  public void removeIndicator() {
    setCompoundDrawables(null, null, null, null);
  }

  /**
   * 展开
   */
  public void show(String desc) {

    switch (currentState) {
      case STATE_OPEN:
        setText(desc);
        if (isAutoDismiss) {
          removeCallbacks(autoDissmissRunnable);
          postDelayed(autoDissmissRunnable, autoDissmissDuration);
        }
        break;
      case STATE_OPENING:
        setText(desc);
        break;
      case STATE_CLOSING:
        dissmiss();
        show(desc);
        break;
      case STATE_CLOSEED:
        setText(desc);
        setSelected(true);
        open();
        break;
      default:
        break;
    }
  }

  public boolean isAutoDismiss() {
    return isAutoDismiss;
  }

  public void setAutoDismiss(boolean isAutoDismiss) {
    this.isAutoDismiss = isAutoDismiss;
  }

  public int getAutoDissmissDuration() {
    return autoDissmissDuration;
  }

  public void setAutoDissmissDuration(int autoDissmissDuration) {
    this.autoDissmissDuration = autoDissmissDuration;
  }

  /**
   * 展开动画
   */
  private void open() {
    currentState = STATE_OPENING;

    if (openAnimator == null) {
      openAnimator = ValueAnimator.ofInt(0, MAX_HEIGHT);
      openAnimator.setDuration(ANIMATION_DURATION);
      openAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
          height = (Integer) animation.getAnimatedValue();
          // 动态设置高度

          ViewGroup.LayoutParams lp = getLayoutParams();
          lp.height = Tools.getPixelByDip(mContext, height);
          setLayoutParams(lp);

          if (height == MAX_HEIGHT) {
            currentState = STATE_OPEN;
            if (isAutoDismiss){
              postDelayed(autoDissmissRunnable, autoDissmissDuration);
            }
          }
        }

      });
    }
    openAnimator.start();
  }

  //瞬间展开
  public void appear() {
    switch (currentState) {
      case STATE_CLOSEED:
        break;
      case STATE_OPENING:
        if (openAnimator != null){
          openAnimator.cancel();
        }
        removeCallbacks(autoDissmissRunnable);
        break;
      case STATE_OPEN:
        removeCallbacks(autoDissmissRunnable);
        break;
      case STATE_CLOSING:
        if (closeAnimator != null){
          closeAnimator.cancel();
        }
        break;
      default:
        break;
    }

    // 重新设置高度
    ViewGroup.LayoutParams lp = getLayoutParams();
    lp.height = Tools.getPixelByDip(mContext, MAX_HEIGHT);
    setLayoutParams(lp);

    if (isAutoDismiss){
      postDelayed(autoDissmissRunnable, autoDissmissDuration);
    }

    currentState = STATE_OPEN;
  }


  /**
   * 收起动画
   */
  private void close() {
    currentState = STATE_CLOSING;

    if (closeAnimator == null) {
      closeAnimator = ValueAnimator.ofInt(MAX_HEIGHT, 0);
      closeAnimator.setDuration(ANIMATION_DURATION);
      closeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
          height = (Integer) animation.getAnimatedValue();
          // 动态设置高度
          ViewGroup.LayoutParams lp = getLayoutParams();
          lp.height = Tools.getPixelByDip(mContext, height);
          setLayoutParams(lp);

          if (height == 0) {
            currentState = STATE_CLOSEED;
          }
        }
      });
    }
    closeAnimator.start();
  }

  /**
   * 底部分割线
   */
  @Override
  public void draw(Canvas canvas) {
    // TODO Auto-generated method stub
    super.draw(canvas);
    // 公告栏底部分割线
    path.reset();
    path.moveTo(getLeft(), Tools.getPixelByDip(mContext, height));
    path.lineTo(getRight(), Tools.getPixelByDip(mContext, height));
    canvas.drawPath(path, paint);
  }

  /**
   * 瞬间将小黄条设置成消失状态
   */
  public void dissmiss() {

    switch (currentState) {
      case STATE_CLOSEED:
        return;
      case STATE_OPENING:
        if (openAnimator != null){
          openAnimator.cancel();
        }
        removeCallbacks(autoDissmissRunnable);
        break;
      case STATE_OPEN:
        removeCallbacks(autoDissmissRunnable);
        break;
      case STATE_CLOSING:
        if (closeAnimator != null){
          closeAnimator.cancel();
        }
        break;
      default:
        break;
    }

    // 重新设置高度
    ViewGroup.LayoutParams lp = getLayoutParams();
    lp.height = Tools.getPixelByDip(mContext, 0);
    setLayoutParams(lp);
    currentState = STATE_CLOSEED;
  }

}
