package com.june.healthmail.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.june.healthmail.R;
import com.june.healthmail.untils.Tools;

/**
 * Created by june on 2017/7/9.
 */

public class PicRadioGroupDialog {

  private Context mContext;
  private AlertDialog.Builder mAlertDialog;
  private RadioGroup mRadioGroup;
  private PicRadioGroupDialogInterface mInterface;
  private int selected;



  public PicRadioGroupDialog(Context context) {
    mContext = context;
    mInterface = (PicRadioGroupDialogInterface)context;
  }

  /**
   * 创建dialog
   *
   * @param view
   */
  private void initDialog(View view) {
    mAlertDialog.setPositiveButton("确定",
        new DialogInterface.OnClickListener() {

          @Override
          public void onClick(DialogInterface dialog, int which) {
            // TODO Auto-generated method stub
            dialog.dismiss();
            mInterface.onPicRadiogroupPositive();
          }
        });
    mAlertDialog.setNegativeButton("取消",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            mInterface.onPicRadiogroupNegative();
          }
        });
    mAlertDialog.setView(view);
  }

  //types is [0,1,2,3]
  public void showRadioGroupDialog(final String[] types) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_radio_group_layout, null);
    mRadioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
    for(int i = 0; i < types.length; i++){
      RadioButton radioButton = new RadioButton(mContext);
      radioButton.setText(types[i]);
      if(i == 0) {
        selected = Tools.parseInt(types[0]);
      }
      mRadioGroup.addView(radioButton);
    }
    mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        for(int i = 0; i < group.getChildCount(); i++) {
          RadioButton rb = (RadioButton) group.getChildAt(i);
          Log.e("test","checkedId = " + checkedId + "    status = " + rb.isChecked());
          if(rb.isChecked()) {
            selected = Tools.parseInt(types[i]);
          }
        }
      }
    });

    mAlertDialog = new AlertDialog.Builder(mContext);
    mAlertDialog.setTitle("选择课程图片\n(图片为私教小屋管理的图片)");
    initDialog(view);
    mAlertDialog.show();
  }

  public int getSelected(){
    return this.selected;
  }

  public interface PicRadioGroupDialogInterface {
    public void onPicRadiogroupPositive();
    public void onPicRadiogroupNegative();
  }
}
