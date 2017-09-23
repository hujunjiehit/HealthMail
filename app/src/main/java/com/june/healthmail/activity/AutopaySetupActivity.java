package com.june.healthmail.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.june.healthmail.R;
import com.june.healthmail.untils.PreferenceHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by june on 2017/9/12.
 */

public class AutopaySetupActivity  extends BaseActivity implements View.OnClickListener{

  @BindView(R.id.container_1)
  View mContainer1;
  @BindView(R.id.container_2)
  View mContainer2;
  @BindView(R.id.radio_group)
  RadioGroup mRadioGroup;
  @BindView(R.id.radio_button_1)
  RadioButton mRadioButton1;
  @BindView(R.id.radio_button_2)
  RadioButton mRadioButton2;
  @BindView(R.id.tv_bank_card)
  TextView tvBankCard;
  @BindView(R.id.tv_name)
  TextView tvName;
  @BindView(R.id.tv_id_card)
  TextView tvIdCard;
  @BindView(R.id.tv_phone_number)
  TextView tvPhoneNumber;

  private PreferenceHelper mPreferenceHelper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_autopay_setup);
    ButterKnife.bind(this);

    mPreferenceHelper = PreferenceHelper.getInstance();

    initView();
    setListener();
  }

  private void initView() {
    tvBankCard.setText(mPreferenceHelper.getPayBankCard().trim());
    tvName.setText(mPreferenceHelper.getPayName().trim());
    tvIdCard.setText(mPreferenceHelper.getPayIdCard().trim());
    tvPhoneNumber.setText(mPreferenceHelper.getPayPhoneNumber().trim());
    if(PreferenceHelper.getInstance().getAutoPayMode() == 1){
      mRadioButton1.setChecked(true);
      mContainer1.setVisibility(View.VISIBLE);
      mContainer2.setVisibility(View.GONE);
    }else {
      mRadioButton2.setChecked(true);
      mContainer1.setVisibility(View.GONE);
      mContainer2.setVisibility(View.VISIBLE);
    }
  }

  private void setListener() {
    findViewById(R.id.img_back).setOnClickListener(this);
    mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        int radioButtonId = group.getCheckedRadioButtonId();
        if(radioButtonId == R.id.radio_button_1) {
          PreferenceHelper.getInstance().setAutoPayMode(1);
          mContainer1.setVisibility(View.VISIBLE);
          mContainer2.setVisibility(View.GONE);
        }else if(radioButtonId == R.id.radio_button_2){
          PreferenceHelper.getInstance().setAutoPayMode(2);
          mContainer1.setVisibility(View.GONE);
          mContainer2.setVisibility(View.VISIBLE);
        }
      }
    });
  }

  @OnClick(R.id.edit_bank_card)
  public void editBankCard(View view){
    showEditBankCardDialog();
  }

  @OnClick(R.id.edit_name)
  public void editName(View view){
    showEditNameDialog();
  }

  @OnClick(R.id.edit_id_card)
  public void editIdCard(View view){
    showEditIdCardDialog();
  }

  @OnClick(R.id.edit_phone_number)
  public void editPhoneNumber(View view){
    showEditPhoneDialog();
  }




  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.img_back:	//返回
        finish();
        break;
      default:
        break;
    }
  }

  private void showEditBankCardDialog() {
    View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_max_courses,null);
    final EditText edit_text = (EditText) diaog_view.findViewById(R.id.edit_text);
    final TextView tv_desc = (TextView) diaog_view.findViewById(R.id.tv_desc);
    tv_desc.setText("银行卡号：");
    edit_text.setInputType(InputType.TYPE_CLASS_NUMBER);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("修改银行卡号");
    builder.setView(diaog_view);
    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        if(TextUtils.isEmpty(edit_text.getText().toString().trim())){
          toast("请输入需正确的银行卡号");
          return;
        }
        mPreferenceHelper.setPayBankCard(edit_text.getText().toString().trim());
        tvBankCard.setText(edit_text.getText().toString().trim());
      }
    });
    builder.create().show();
  }

  private void showEditNameDialog() {
    View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_max_courses,null);
    final EditText edit_text = (EditText) diaog_view.findViewById(R.id.edit_text);
    final TextView tv_desc = (TextView) diaog_view.findViewById(R.id.tv_desc);
    tv_desc.setText("姓名：");
    edit_text.setInputType(InputType.TYPE_CLASS_TEXT);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("修改姓名");
    builder.setView(diaog_view);
    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        if(TextUtils.isEmpty(edit_text.getText().toString().trim())){
          toast("请输入需正确的姓名");
          return;
        }
        mPreferenceHelper.setPayName(edit_text.getText().toString().trim());
        tvName.setText(edit_text.getText().toString().trim());
      }
    });
    builder.create().show();
  }

  private void showEditIdCardDialog() {
    View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_max_courses,null);
    final EditText edit_text = (EditText) diaog_view.findViewById(R.id.edit_text);
    final TextView tv_desc = (TextView) diaog_view.findViewById(R.id.tv_desc);
    tv_desc.setText("身份证：");
    edit_text.setInputType(InputType.TYPE_CLASS_TEXT);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("修改身份证号码");
    builder.setView(diaog_view);
    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        if(TextUtils.isEmpty(edit_text.getText().toString().trim())){
          toast("请输入需正确的姓名");
          return;
        }
        mPreferenceHelper.setPayIdCard(edit_text.getText().toString().trim());
        tvIdCard.setText(edit_text.getText().toString().trim());
      }
    });
    builder.create().show();
  }

  private void showEditPhoneDialog() {
    View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_max_courses,null);
    final EditText edit_text = (EditText) diaog_view.findViewById(R.id.edit_text);
    final TextView tv_desc = (TextView) diaog_view.findViewById(R.id.tv_desc);
    tv_desc.setText("手机号：");
    edit_text.setInputType(InputType.TYPE_CLASS_PHONE);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("修改手机号");
    builder.setView(diaog_view);
    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        if(TextUtils.isEmpty(edit_text.getText().toString().trim())){
          toast("请输入需正确的手机号");
          return;
        }
        mPreferenceHelper.setPayPhoneNumber(edit_text.getText().toString().trim());
        tvPhoneNumber.setText(edit_text.getText().toString().trim());
      }
    });
    builder.create().show();
  }
}
