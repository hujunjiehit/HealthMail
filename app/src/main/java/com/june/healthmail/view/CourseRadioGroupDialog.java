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
import com.june.healthmail.model.Course;
import com.june.healthmail.untils.Tools;

import java.util.ArrayList;

/**
 * Created by june on 2017/7/15.
 */

public class CourseRadioGroupDialog {

  private Context mContext;
  private AlertDialog.Builder mAlertDialog;
  private RadioGroup mRadioGroup;
  private CourseRadioGroupDialog.CourseRadioGroupDialogInterface mInterface;
  private int selected;

  private ArrayList<Course> courseList;


  public CourseRadioGroupDialog(Context context) {
    mContext = context;
    mInterface = (CourseRadioGroupDialog.CourseRadioGroupDialogInterface)context;
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
            mInterface.onCourseRadiogroupPositive();
          }
        });
    mAlertDialog.setNegativeButton("取消",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            mInterface.onCourseRadiogroupNegative();
          }
        });
    mAlertDialog.setView(view);
  }

  //types is [0,1,2,3]
  public void showRadioGroupDialog(ArrayList<Course> courseList) {
    this.courseList = courseList;
    View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_radio_group_layout, null);
    mRadioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
    for(int i = 0; i < courseList.size(); i++){
      RadioButton radioButton = new RadioButton(mContext);
      radioButton.setText(courseList.get(i).getHm_gbc_title() + "--" + courseList.get(i).getHm_gbc_time());
      selected = 0;
      mRadioGroup.addView(radioButton);
    }
    mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        for(int i = 0; i < group.getChildCount(); i++) {
          RadioButton rb = (RadioButton) group.getChildAt(i);
          Log.e("test","checkedId = " + checkedId + "    status = " + rb.isChecked());
          if(rb.isChecked()) {
            selected = i;
          }
        }
      }
    });

    mAlertDialog = new AlertDialog.Builder(mContext);
    mAlertDialog.setTitle("选择需要获取评价详情的课程");
    initDialog(view);
    mAlertDialog.show();
  }



  public int getSelected(){
    return this.selected;
  }

  public interface CourseRadioGroupDialogInterface {
    public void onCourseRadiogroupPositive();
    public void onCourseRadiogroupNegative();
  }
}
