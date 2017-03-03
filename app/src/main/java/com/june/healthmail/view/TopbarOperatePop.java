package com.june.healthmail.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.june.healthmail.R;

/**
 * Created by bjhujunjie on 2017/3/3.
 */

public class TopbarOperatePop extends PopupWindow implements View.OnClickListener{

  private View mMenuView;
  private Context mContext;
  private TextView tvOperate1;
  private TextView tvOperate2;
  private TextView tvOperate3;

  public TopbarOperatePop(Context context) {
    super(context);
    mContext = context;

    mMenuView = View.inflate(context, R.layout.layout_top_operate_pop, null);
    tvOperate1 = (TextView) mMenuView.findViewById(R.id.tv_operate_1);
    tvOperate2 = (TextView) mMenuView.findViewById(R.id.tv_operate_2);
    tvOperate3 = (TextView) mMenuView.findViewById(R.id.tv_operate_3);

    setContentView(mMenuView);
    setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
    setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    setFocusable(true);

    tvOperate1.setOnClickListener(this);
    tvOperate2.setOnClickListener(this);
    tvOperate3.setOnClickListener(this);

    ColorDrawable cdw = new ColorDrawable(00000000);
    setBackgroundDrawable(cdw);
  }


  @Override
  public void onClick(View v) {
    Toast.makeText(mContext,"you click v.getid:" + v.getId(),Toast.LENGTH_SHORT).show();
    dismiss();
  }
}
