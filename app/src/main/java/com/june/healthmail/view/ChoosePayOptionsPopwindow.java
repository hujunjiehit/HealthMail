package com.june.healthmail.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.june.healthmail.R;
import com.june.healthmail.activity.FukuanActivity;

/**
 * Created by bjhujunjie on 2017/3/9.
 */

public class ChoosePayOptionsPopwindow extends PopupWindow{
    private Context mContext;
    private View view;

    private Button[] btns = new Button[5];

    public ChoosePayOptionsPopwindow(Context context, int[] data,View.OnClickListener listener){
      this.mContext = context;
      this.view = LayoutInflater.from(context).inflate(R.layout.layout_choose_pay_options, null);

      btns[0] = (Button) view.findViewById(R.id.btn_fukuan_kuaijie);
      btns[1] = (Button) view.findViewById(R.id.btn_fukuan_kuaiqian);
      btns[2] = (Button) view.findViewById(R.id.btn_fukuan_tonglian);
      btns[3] = (Button) view.findViewById(R.id.btn_fukuan_jingdong);
      btns[4] = (Button) view.findViewById(R.id.btn_fukuan_yilian);

      for (int i = 0; i < 5; i++){
        btns[i].setOnClickListener(listener);
        if(data[i] == 1){
          btns[i].setVisibility(View.VISIBLE);
        }else{
          btns[i].setVisibility(View.GONE);
        }
      }

      //this.setOutsideTouchable(true);
      this.setContentView(this.view);

      // 设置弹出窗体的宽和高
      this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
      this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);

      this.setFocusable(true);

      // 实例化一个ColorDrawable颜色为半透明
      ColorDrawable dw = new ColorDrawable(0xb0000000);
      // 设置弹出窗体的背景
      this.setBackgroundDrawable(dw);

      // 设置弹出窗体显示时的动画，从底部向上弹出
      this.setAnimationStyle(R.style.popup_window_anim);
    }
}
