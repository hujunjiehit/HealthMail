package com.june.healthmail.view.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.june.healthmail.R;

/**
 * Created by june on 2017/10/28.
 */

public class CustomToolbar extends Toolbar{

  private LayoutInflater mInflater;
  private View mView;
  private TextView mTextTitle;
  private ImageButton mRightImageButton;

  public CustomToolbar(Context context) {
    this(context,null);
  }

  public CustomToolbar(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CustomToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    initView();
  }

  private void initView() {

    if(mView == null) {
      mInflater = LayoutInflater.from(getContext());
      mView = mInflater.inflate(R.layout.layout_toolbar,null);

      mTextTitle = (TextView) mView.findViewById(R.id.toolbar_title);
      mRightImageButton = (ImageButton) mView.findViewById(R.id.toolbar_rightButton);

      ViewGroup.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);
      addView(mView, lp);
    }
  }

  @Override
  public void setTitle(@StringRes int resId) {
    setTitle(getContext().getText(resId));
  }

  @Override
  public void setTitle(CharSequence title) {
    initView();
    if(mTextTitle != null) {
      mTextTitle.setText(title);
    }
  }
}
