package com.june.healthmail.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.june.healthmail.Config.Constants;
import com.june.healthmail.R;
import com.june.healthmail.untils.ACache;
import com.june.healthmail.untils.PreferenceHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by june on 2017/9/12.
 */

public class AutopaySetupActivity extends BaseActivity implements View.OnClickListener {

  @BindView(R.id.container_1)
  View mContainer1;
  @BindView(R.id.container_2)
  View mContainer2;
  @BindView(R.id.container_3)
  View mContainer3;
  @BindView(R.id.container_4)
  View mContainer4;
  @BindView(R.id.radio_group)
  RadioGroup mRadioGroup;
  @BindView(R.id.radio_button_1)
  RadioButton mRadioButton1;
  @BindView(R.id.radio_button_2)
  RadioButton mRadioButton2;
  @BindView(R.id.radio_button_3)
  RadioButton mRadioButton3;
  @BindView(R.id.radio_button_4)
  RadioButton mRadioButton4;
  @BindView(R.id.cb_choose_pay_card)
  CheckBox cbChoosePayCard;
  @BindView(R.id.tv_bank_card)
  TextView tvBankCard;
  @BindView(R.id.tv_name)
  TextView tvName;
  @BindView(R.id.tv_id_card)
  TextView tvIdCard;
  @BindView(R.id.tv_phone_number)
  TextView tvPhoneNumber;
  @BindView(R.id.tv_pay_order_number)
  TextView tvPayOrderNumber;
  @BindView(R.id.tv_pay_bank_card)
  TextView tvPayBankCard;
  @BindView(R.id.tv_pay_password)
  TextView tvPayPassword;

  @BindView(R.id.tv_pay_credit_card)
  TextView tvPayCreditCard;
  @BindView(R.id.tv_credit_date)
  TextView tvCreditDate;
  @BindView(R.id.tv_credit_code)
  TextView tvCreditCode;

  @BindView(R.id.layout_bank_card)
  View layoutBankCard;
  @BindView(R.id.layout_bank_card_desc)
  View layoutBankCardDesc;
  @BindView(R.id.edit_bank_card)
  Button mEditBankCard;
  @BindView(R.id.edit_name)
  Button mEditName;
  @BindView(R.id.edit_id_card)
  Button mEditIdCard;
  @BindView(R.id.edit_phone_number)
  Button mEditPhoneNumber;
  @BindView(R.id.textView)
  TextView mTextView;
  @BindView(R.id.edit_pay_order_number)
  Button mEditPayOrderNumber;
  @BindView(R.id.edit_pay_bank_card)
  Button mEditPayBankCard;
  @BindView(R.id.edit_pay_password)
  Button mEditPayPassword;
  @BindView(R.id.tonglian_bank_card)
  TextView mTonglianBankCard;
  @BindView(R.id.edit_tonglian_bank_card)
  Button mEditTonglianBankCard;
  @BindView(R.id.tonglian_name)
  TextView mTonglianName;
  @BindView(R.id.edit_tonglian_name)
  Button mEditTonglianName;
  @BindView(R.id.tonglian_id_card)
  TextView mTonglianIdCard;
  @BindView(R.id.edit_tonglian_id_card)
  Button mEditTonglianIdCard;
  @BindView(R.id.tonglian_phone_number)
  TextView mTonglianPhoneNumber;
  @BindView(R.id.edit_tonglian_phone_number)
  Button mEditTonglianPhoneNumber;
  @BindView(R.id.edit_pay_credit_card)
  Button mEditPayCreditCard;
  @BindView(R.id.layout_credit_card)
  LinearLayout mLayoutCreditCard;
  @BindView(R.id.layout_creat_card_desc)
  TextView mLayoutCreatCardDesc;
  @BindView(R.id.edit_credit_date)
  Button mEditCreditDate;
  @BindView(R.id.layout_creat_date_desc)
  TextView mLayoutCreatDateDesc;
  @BindView(R.id.edit_credit_code)
  Button mEditCreditCode;
  @BindView(R.id.layout_creat_code_desc)
  TextView mLayoutCreatCodeDesc;

  private PreferenceHelper mPreferenceHelper;
  private ACache mCache;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_autopay_setup);
    ButterKnife.bind(this);

    mPreferenceHelper = PreferenceHelper.getInstance();
    mCache = ACache.get(this);

    initView();
    setListener();
  }

  private void initView() {
    tvBankCard.setText(mPreferenceHelper.getPayBankCard().trim());
    tvName.setText(mPreferenceHelper.getPayName().trim());
    tvIdCard.setText(mPreferenceHelper.getPayIdCard().trim());
    tvPhoneNumber.setText(mPreferenceHelper.getPayPhoneNumber().trim());
    tvPayOrderNumber.setText(mPreferenceHelper.getPayOrderNumber() + "");
    tvPayBankCard.setText(mPreferenceHelper.getPayBankCard().trim());
    tvPayPassword.setText(mPreferenceHelper.getPayPassword().trim());
    tvPayCreditCard.setText(mPreferenceHelper.getPayCreditCard().trim());
    tvCreditDate.setText(mPreferenceHelper.getCreditDate().trim());
    tvCreditCode.setText(mPreferenceHelper.getCreditCode().trim());

    if(mCache == null) {
      mCache = ACache.get(AutopaySetupActivity.this);
    }
    mTonglianBankCard.setText(mCache.getAsString(Constants.BANK_CARD));
    mTonglianName.setText(mCache.getAsString(Constants.USER_NAME));
    mTonglianIdCard.setText(mCache.getAsString(Constants.ID_CARD));
    mTonglianPhoneNumber.setText(mCache.getAsString(Constants.PHONE_NUMBER));

    if (PreferenceHelper.getInstance().getAutoPayMode() == 1) {
      mRadioButton1.setChecked(true);
      mContainer1.setVisibility(View.VISIBLE);
      mContainer2.setVisibility(View.GONE);
      mContainer3.setVisibility(View.GONE);
      mContainer4.setVisibility(View.GONE);
    } else if (PreferenceHelper.getInstance().getAutoPayMode() == 2) {
      mRadioButton2.setChecked(true);
      mContainer1.setVisibility(View.GONE);
      mContainer2.setVisibility(View.VISIBLE);
      mContainer3.setVisibility(View.GONE);
      mContainer4.setVisibility(View.GONE);
    } else if (PreferenceHelper.getInstance().getAutoPayMode() == 3) {
      mRadioButton3.setChecked(true);
      mContainer1.setVisibility(View.GONE);
      mContainer2.setVisibility(View.GONE);
      mContainer3.setVisibility(View.VISIBLE);
      mContainer4.setVisibility(View.GONE);
    } else if (PreferenceHelper.getInstance().getAutoPayMode() == 4) {
      mRadioButton4.setChecked(true);
      mContainer1.setVisibility(View.GONE);
      mContainer2.setVisibility(View.GONE);
      mContainer3.setVisibility(View.GONE);
      mContainer4.setVisibility(View.VISIBLE);
    }
    if (PreferenceHelper.getInstance().getChoosePayCard()) {
      cbChoosePayCard.setChecked(true);
      layoutBankCard.setVisibility(View.VISIBLE);
      layoutBankCardDesc.setVisibility(View.VISIBLE);
    } else {
      cbChoosePayCard.setChecked(false);
      layoutBankCard.setVisibility(View.GONE);
      layoutBankCardDesc.setVisibility(View.GONE);
    }
  }

  private void setListener() {
    findViewById(R.id.img_back).setOnClickListener(this);
    mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        int radioButtonId = group.getCheckedRadioButtonId();
        if (radioButtonId == R.id.radio_button_1) {
          PreferenceHelper.getInstance().setAutoPayMode(1);
          mContainer1.setVisibility(View.VISIBLE);
          mContainer2.setVisibility(View.GONE);
          mContainer3.setVisibility(View.GONE);
          mContainer4.setVisibility(View.GONE);
        } else if (radioButtonId == R.id.radio_button_2) {
          PreferenceHelper.getInstance().setAutoPayMode(2);
          mContainer1.setVisibility(View.GONE);
          mContainer2.setVisibility(View.VISIBLE);
          mContainer3.setVisibility(View.GONE);
          mContainer4.setVisibility(View.GONE);
        } else if (radioButtonId == R.id.radio_button_3) {
          PreferenceHelper.getInstance().setAutoPayMode(3);
          mContainer1.setVisibility(View.GONE);
          mContainer2.setVisibility(View.GONE);
          mContainer3.setVisibility(View.VISIBLE);
          mContainer4.setVisibility(View.GONE);
        } else if (radioButtonId == R.id.radio_button_4) {
          PreferenceHelper.getInstance().setAutoPayMode(4);
          mContainer1.setVisibility(View.GONE);
          mContainer2.setVisibility(View.GONE);
          mContainer3.setVisibility(View.GONE);
          mContainer4.setVisibility(View.VISIBLE);
        }
      }
    });
    tvPayPassword.setOnClickListener(this);
    cbChoosePayCard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          PreferenceHelper.getInstance().setChoosePayCard(true);
          layoutBankCard.setVisibility(View.VISIBLE);
          layoutBankCardDesc.setVisibility(View.VISIBLE);
        } else {
          PreferenceHelper.getInstance().setChoosePayCard(false);
          layoutBankCard.setVisibility(View.GONE);
          layoutBankCardDesc.setVisibility(View.GONE);
        }
      }
    });
  }

  @OnClick(R.id.edit_bank_card)
  public void editBankCard(View view) {
    showEditBankCardDialog();
  }

  @OnClick(R.id.edit_pay_bank_card)
  public void editPayBankCard(View view) {
    showEditBankCardDialog();
  }

  @OnClick(R.id.edit_name)
  public void editName(View view) {
    showEditNameDialog();
  }

  @OnClick(R.id.edit_id_card)
  public void editIdCard(View view) {
    showEditIdCardDialog();
  }

  @OnClick(R.id.edit_phone_number)
  public void editPhoneNumber(View view) {
    showEditPhoneDialog();
  }

  @OnClick(R.id.edit_pay_order_number)
  public void editPayOrderNumber(View view) {
    showEditPayOrderNumber();
  }

  @OnClick(R.id.edit_pay_password)
  public void editPayPassword(View view) {
    showEditPayPassword();
  }

  @OnClick(R.id.edit_pay_credit_card)
  public void editCreditCard(View view) {
    showEditCreditCardDialog();
  }

  @OnClick(R.id.edit_credit_date)
  public void editCreditDate(View view) {
    showEditCreditDateDialog();
  }

  @OnClick(R.id.edit_credit_code)
  public void editCreditCode(View view) {
    showEditCreditCodeDialog();
  }


  @OnClick(R.id.edit_tonglian_bank_card)
  public void editTonglianBankCard(View view) {
    showEditTonglianBankCardDialog();
  }

  @OnClick(R.id.edit_tonglian_name)
  public void editTonglianName(View view) {
    showEditTonglianNameDialog();
  }

  @OnClick(R.id.edit_tonglian_phone_number)
  public void editTonglianPhoneNumber(View view) {
    showEditTonglianPhoneNumberDialog();
  }

  @OnClick(R.id.edit_tonglian_id_card)
  public void editTonglianIdCard(View view) {
    showEditTonglianIdCardDialog();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.img_back:  //返回
        finish();
        break;
      case R.id.tv_pay_password:  //显示与隐藏密码
        if (tvPayPassword.getTransformationMethod() instanceof HideReturnsTransformationMethod) {
          tvPayPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
          tvPayPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        break;
      default:
        break;
    }
  }

  private void showEditBankCardDialog() {
    View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_max_courses, null);
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
        if (TextUtils.isEmpty(edit_text.getText().toString().trim())) {
          toast("请输入需正确的银行卡号");
          return;
        }
        mPreferenceHelper.setPayBankCard(edit_text.getText().toString().trim());
        tvBankCard.setText(edit_text.getText().toString().trim());
        tvPayBankCard.setText(edit_text.getText().toString().trim());
      }
    });
    builder.create().show();
  }

  private void showEditNameDialog() {
    View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_max_courses, null);
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
        if (TextUtils.isEmpty(edit_text.getText().toString().trim())) {
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
    View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_max_courses, null);
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
        if (TextUtils.isEmpty(edit_text.getText().toString().trim())) {
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
    View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_max_courses, null);
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
        if (TextUtils.isEmpty(edit_text.getText().toString().trim())) {
          toast("请输入需正确的手机号");
          return;
        }
        mPreferenceHelper.setPayPhoneNumber(edit_text.getText().toString().trim());
        tvPhoneNumber.setText(edit_text.getText().toString().trim());
      }
    });
    builder.create().show();
  }

  private void showEditPayOrderNumber() {
    View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_max_courses, null);
    final EditText edit_text = (EditText) diaog_view.findViewById(R.id.edit_text);
    final TextView tv_desc = (TextView) diaog_view.findViewById(R.id.tv_desc);
    tv_desc.setText("支付订单数：");
    edit_text.setInputType(InputType.TYPE_CLASS_NUMBER);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("修改支付订单数");
    builder.setView(diaog_view);
    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        if (TextUtils.isEmpty(edit_text.getText().toString().trim())) {
          toast("请输入需正确的手机号");
          return;
        }
        int value = Integer.valueOf(edit_text.getText().toString().trim());
        if (value > 0 && value <= 100) {
          mPreferenceHelper.setPayOrderNumber(value);
          tvPayOrderNumber.setText(value + "");
        } else {
          toast("数值必须大于0且小于等于100");
        }
      }
    });
    builder.create().show();
  }

  private void showEditPayPassword() {
    View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_max_courses, null);
    final EditText edit_text = (EditText) diaog_view.findViewById(R.id.edit_text);
    final TextView tv_desc = (TextView) diaog_view.findViewById(R.id.tv_desc);
    tv_desc.setText("支付密码：");
    edit_text.setInputType(InputType.TYPE_CLASS_TEXT);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("修改支付密码");
    builder.setView(diaog_view);
    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        if (TextUtils.isEmpty(edit_text.getText().toString().trim())) {
          toast("请输入需正确的支付密码");
          return;
        }
        mPreferenceHelper.setPayPassword(edit_text.getText().toString().trim());
        tvPayPassword.setText(edit_text.getText().toString().trim());
      }
    });
    builder.create().show();
  }

  private void showEditCreditCardDialog() {
    View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_max_courses, null);
    final EditText edit_text = (EditText) diaog_view.findViewById(R.id.edit_text);
    final TextView tv_desc = (TextView) diaog_view.findViewById(R.id.tv_desc);
    tv_desc.setText("信用卡卡号：");
    edit_text.setInputType(InputType.TYPE_CLASS_NUMBER);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("修改信用卡卡号");
    builder.setView(diaog_view);
    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        if (TextUtils.isEmpty(edit_text.getText().toString().trim())) {
          toast("请输入需正确的信用卡卡号");
          return;
        }
        mPreferenceHelper.setPayCreditCard(edit_text.getText().toString().trim());
        tvPayCreditCard.setText(edit_text.getText().toString().trim());
      }
    });
    builder.create().show();
  }

  private void showEditCreditDateDialog() {
    View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_max_courses, null);
    final EditText edit_text = (EditText) diaog_view.findViewById(R.id.edit_text);
    final TextView tv_desc = (TextView) diaog_view.findViewById(R.id.tv_desc);
    tv_desc.setText("信用卡有效期：");
    edit_text.setInputType(InputType.TYPE_CLASS_NUMBER);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("修改信用卡有效期");
    builder.setView(diaog_view);
    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        if (TextUtils.isEmpty(edit_text.getText().toString().trim())) {
          toast("请输入需正确的信用卡有效期");
          return;
        }
        if (edit_text.getText().toString().trim().length() != 4) {
          toast("信用卡有效期必须为四位数");
          return;
        }
        mPreferenceHelper.setCreditDate(edit_text.getText().toString().trim());
        tvCreditDate.setText(edit_text.getText().toString().trim());
      }
    });
    builder.create().show();
  }

  private void showEditCreditCodeDialog() {
    View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_max_courses, null);
    final EditText edit_text = (EditText) diaog_view.findViewById(R.id.edit_text);
    final TextView tv_desc = (TextView) diaog_view.findViewById(R.id.tv_desc);
    tv_desc.setText("信用卡安全码：");
    edit_text.setInputType(InputType.TYPE_CLASS_NUMBER);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("修改信用卡安全码");
    builder.setView(diaog_view);
    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        if (TextUtils.isEmpty(edit_text.getText().toString().trim())) {
          toast("请输入需正确的信用卡安全码");
          return;
        }
        if (edit_text.getText().toString().trim().length() != 3) {
          toast("安全码必须为三位数");
          return;
        }
        mPreferenceHelper.setCreditCode(edit_text.getText().toString().trim());
        tvCreditCode.setText(edit_text.getText().toString().trim());
      }
    });
    builder.create().show();
  }

  private void showEditTonglianBankCardDialog() {
    View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_max_courses, null);
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
        if (TextUtils.isEmpty(edit_text.getText().toString().trim())) {
          toast("请输入需正确的银行卡号");
          return;
        }
        //mPreferenceHelper.setPayBankCard(edit_text.getText().toString().trim());
        if(mCache == null) {
          mCache = ACache.get(AutopaySetupActivity.this);
        }
        mCache.put(Constants.BANK_CARD,edit_text.getText().toString().trim());
        mTonglianBankCard.setText(edit_text.getText().toString().trim());
      }
    });
    builder.create().show();
  }

  private void showEditTonglianNameDialog() {
    View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_max_courses, null);
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
        if (TextUtils.isEmpty(edit_text.getText().toString().trim())) {
          toast("请输入需正确的姓名");
          return;
        }
        //mPreferenceHelper.setPayName(edit_text.getText().toString().trim());
        if(mCache == null) {
          mCache = ACache.get(AutopaySetupActivity.this);
        }
        mCache.put(Constants.USER_NAME,edit_text.getText().toString().trim());
        mTonglianName.setText(edit_text.getText().toString().trim());
      }
    });
    builder.create().show();
  }

  private void showEditTonglianPhoneNumberDialog() {
    View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_max_courses, null);
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
        if (TextUtils.isEmpty(edit_text.getText().toString().trim())) {
          toast("请输入需正确的手机号");
          return;
        }
        //mPreferenceHelper.setPayPhoneNumber(edit_text.getText().toString().trim());
        if(mCache == null) {
          mCache = ACache.get(AutopaySetupActivity.this);
        }
        mCache.put(Constants.PHONE_NUMBER,edit_text.getText().toString().trim());
        mTonglianPhoneNumber.setText(edit_text.getText().toString().trim());
      }
    });
    builder.create().show();
  }

  private void showEditTonglianIdCardDialog() {
    View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_max_courses, null);
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
        if (TextUtils.isEmpty(edit_text.getText().toString().trim())) {
          toast("请输入需正确的姓名");
          return;
        }
        if(mCache == null) {
          mCache = ACache.get(AutopaySetupActivity.this);
        }
        //mPreferenceHelper.setPayIdCard(edit_text.getText().toString().trim());
        mCache.put(Constants.ID_CARD,edit_text.getText().toString().trim());
        mTonglianIdCard.setText(edit_text.getText().toString().trim());
      }
    });
    builder.create().show();
  }
}
