package com.coinbene.manbiwang.contract.newcontract.closeposition;

import android.content.Context;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.coinbene.manbiwang.contract.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import butterknife.ButterKnife;

/**
 * Created by tianpeng on 2017/9/22.
 */

public abstract class BaseBoard extends BottomSheetDialog {

  protected Context mContext;

  public BaseBoard(@NonNull Context context) {
    super(context, R.style.BottomSheetEdit);
    mContext = context;

    initView();
  }

  private void initView() {
    setContentView(initLayout());
    ButterKnife.bind(this);

    initDialog();
  }

  public abstract @LayoutRes int initLayout();

  /**
   *设置包裹自定义布局的控件背景为透明，相当于去除了BottomSheetDialog自己的动画。
   *设置包裹自定义布局的控件高度为全屏幕，使得自定义布局可以有全屏幕的动画。
   *适配自定义布局的高度为实际高度
   */

  public abstract void initDialog();
}
