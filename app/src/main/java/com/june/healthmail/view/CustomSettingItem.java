/**
 * @Description: TODO
 * @author dingguofeng
 * @version V1.0
 * @since 2014年12月12日
 */
package com.june.healthmail.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.june.healthmail.R;


/**
 * @author dingguofeng
 * @since 2014年12月12日
 */
public class CustomSettingItem extends LinearLayout {

  private TextView tvText;
  private ImageView ivIcon;
  private View lineTop;
  private View lineBottom;
  private TextView tvSubText;
  private ImageView ivIndicator;

  private Button mOperateButton;

  public CustomSettingItem(Context context) {
    super(context);
  }

  public CustomSettingItem(Context context, AttributeSet attrs) {
    super(context, attrs);
    TypedArray a = context.obtainStyledAttributes(attrs,
        R.styleable.CustomSetting);
    int textRes = a.getResourceId(R.styleable.CustomSetting_item_text, 0);
    int subTextRes = a.getResourceId(R.styleable.CustomSetting_sub_text, 0);
    int iconRes = a.getResourceId(R.styleable.CustomSetting_item_icon, 0);
    int textColor = a.getColor(R.styleable.CustomSetting_item_text_color, 0);
    boolean isTopLineVisiable = a.getBoolean(
        R.styleable.CustomSetting_top_line_visiable, false);
    boolean isBottomLineVisiable = a.getBoolean(
        R.styleable.CustomSetting_bottom_line_visiable, true);
    boolean isIndicatorLineVisiable = a.getBoolean(
        R.styleable.CustomSetting_indicator_visiable, true);
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    float textSize = a.getDimension(R.styleable.CustomSetting_text_size, 0);
    inflater.inflate(R.layout.layout_custom_setting_item, this);
    tvText = (TextView) findViewById(R.id.tv_text);
    ivIcon = (ImageView) findViewById(R.id.iv_icon);
    lineTop = findViewById(R.id.line_top);
    lineBottom = findViewById(R.id.line_bottom);
    tvSubText = (TextView) findViewById(R.id.tv_subtext);
    ivIndicator = (ImageView) findViewById(R.id.iv_indicator);
    mOperateButton = (Button) findViewById(R.id.btn_operate);

    if (textRes == 0) {
      tvText.setVisibility(View.GONE);
    } else {
      tvText.setText(textRes);
    }

    if (subTextRes == 0) {
      tvSubText.setVisibility(View.INVISIBLE);
    } else {
      tvSubText.setText(subTextRes);
    }

    if (textSize > 0) {
      tvText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    if (textColor != 0) {
      tvText.setTextColor(textColor);
    }

    if (iconRes == 0) {
      ivIcon.setVisibility(View.GONE);
    } else {
      ivIcon.setVisibility(View.VISIBLE);
      ivIcon.setImageResource(iconRes);
    }
    if (isTopLineVisiable) {
      lineTop.setVisibility(View.VISIBLE);
    } else {
      lineTop.setVisibility(View.GONE);
    }
    if (isBottomLineVisiable) {
      lineBottom.setVisibility(View.VISIBLE);
    } else {
      lineBottom.setVisibility(View.GONE);
    }
    if (isIndicatorLineVisiable) {
      ivIndicator.setVisibility(View.VISIBLE);
    } else {
      ivIndicator.setVisibility(View.GONE);
    }

  }

  public void setText(String text) {
    tvText.setText(text);
  }

  public String getText() {
    return tvText.getText().toString().trim();
  }

  public void setText(int textRes) {
    tvText.setText(textRes);
  }

  public void setIconRes(int iconRes) {
    ivIcon.setVisibility(View.VISIBLE);
    ivIcon.setImageResource(iconRes);
  }

  public void setSubText(String text) {
    tvSubText.setVisibility(View.VISIBLE);
    tvSubText.setText(text);
  }

  public String getSubText() {
    return tvSubText.getText().toString().trim();
  }

  public void setSubText(int textRes) {
    tvSubText.setVisibility(View.VISIBLE);
    tvSubText.setText(textRes);
  }

  public TextView getTvSubText() {
    return tvSubText;
  }

  public void setTvSubText(TextView tvSubText) {
    this.tvSubText = tvSubText;
  }

  public Button getOperateButton() {
    return mOperateButton == null ? (Button) findViewById(R.id.btn_operate)
        : mOperateButton;
  }

  public void setIvIndicatorVisibility(boolean isVisible) {

    if (isVisible) {
      ivIndicator.setVisibility(View.VISIBLE);
    } else {
      ivIndicator.setVisibility(View.GONE);
    }
  }

  public void setNotice(int num) {
//    if (ivTheRed != null) {
//      if (num > 0) {
//        ivTheRed.setVisibility(VISIBLE);
//        ivTheRed.addRedPoint(num);
//      } else if (num == 0) {
//        ivTheRed.setVisibility(VISIBLE);
//        ivTheRed.addRedPoint();
//      } else if (num < 0) {
//        ivTheRed.removeRedPoint();
//      }
//    }
  }

  public void setTopLineVisible(boolean isVisible) {
    if (isVisible) {
      lineTop.setVisibility(View.VISIBLE);
    } else {
      lineTop.setVisibility(View.GONE);
    }
  }

  public void setBottomLineVisible(boolean isVisible) {
    if (isVisible) {
      lineBottom.setVisibility(View.VISIBLE);
    } else {
      lineBottom.setVisibility(View.GONE);
    }
  }

}
