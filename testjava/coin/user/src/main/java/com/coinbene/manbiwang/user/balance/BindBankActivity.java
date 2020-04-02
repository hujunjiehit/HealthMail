package com.coinbene.manbiwang.user.balance;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.common.network.newokgo.NewDialogJsonCallback;
import com.coinbene.common.utils.MD5Util;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.widget.EditTextOneIcon;
import com.coinbene.common.widget.EditTextTwoIcon;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.Unbinder;

public class BindBankActivity extends CoinbeneBaseActivity {

    @BindView(R2.id.et_name)
    EditTextOneIcon et_name;

    @BindView(R2.id.et_head_bank)
    EditTextOneIcon et_head_bank;
    @BindView(R2.id.et_bank)
    EditTextOneIcon et_bank;
    @BindView(R2.id.et_bank_code)
    EditTextOneIcon et_bank_code;
    @BindView(R2.id.et_pin)
    EditTextTwoIcon et_pin;

    private Unbinder mUnbinder;
    @BindView(R2.id.menu_title_tv)
    TextView menu_title_tv;
    @BindView(R2.id.submmit_btn)
    TextView submmit_btn;
    @BindView(R2.id.menu_back)
    View backBtn;


    public static void startMe(Activity context, String realName,int requestCode) {
        Intent intent = new Intent(context, BindBankActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("real_name", realName);
        intent.putExtras(bundle);
        context.startActivityForResult(intent,requestCode);
    }

    @Override
    public int initLayout() {
        return R.layout.activity_bind_bank;
    }

    @Override
    public void initView() {
        init();
    }

    private void init() {

        submmit_btn.setOnClickListener(this);
        menu_title_tv.setText(R.string.bind_bank);
        et_name.getInputText().setHint(R.string.input_name);
        et_head_bank.getInputText().setHint(R.string.input_bank_name);
        et_bank.getInputText().setHint(R.string.input_bank_address);
        et_bank_code.getInputText().setHint(R.string.input_bank_acount);
        et_pin.setSecondRightPwdEyeHint();
        et_bank_code.getInputText().setInputType(InputType.TYPE_CLASS_NUMBER);
        backBtn.setOnClickListener(this);
        String realName = getIntent().getStringExtra("real_name");
        if (!TextUtils.isEmpty(realName)) {
            et_name.setInputText(realName);
            if (UserInfoController.getInstance().getUserInfo() != null && UserInfoController.getInstance().getUserInfo().supplier) {
                et_name.getInputText().setEnabled(true);
                et_name.setCloseState(View.VISIBLE);
            } else {
                et_name.getInputText().setEnabled(false);
                et_name.setCloseState(View.GONE);
            }
        } else {
            et_name.getInputText().setEnabled(true);
        }

    }

    @Override
    public void setListener() {

    }

    @Override
    public void initData() {

    }

    @Override
    public boolean needLock() {
        return true;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.submmit_btn) {
            if (TextUtils.isEmpty(et_name.getInputText().getText().toString())) {
                ToastUtil.show(R.string.input_name);
                return;
            }
            if (TextUtils.isEmpty(et_head_bank.getInputText().getText().toString())) {
                ToastUtil.show(R.string.input_bank_name);
                return;
            }
            if (TextUtils.isEmpty(et_bank.getInputText().getText().toString())) {
                ToastUtil.show(R.string.input_bank_address);
                return;
            }
            if (TextUtils.isEmpty(et_bank_code.getInputText().getText().toString())) {
                ToastUtil.show(R.string.input_bank_acount);
                return;
            }
            if (TextUtils.isEmpty(et_pin.getInputText().getText().toString())) {
                ToastUtil.show(R.string.capital_pwd_is_empty);
                return;
            }
            submit();
        } else if (id == R.id.menu_back) {
            finish();
        }
    }

    private void submit() {
        UserInfoTable userInfoTable = UserInfoController.getInstance().getUserInfo();
        HttpParams params = new HttpParams();
        params.put("userName", et_name.getInputStr());
        params.put("bankName", et_head_bank.getInputStr());
        params.put("bankAddress", et_bank.getInputStr());
        params.put("bankAccount", et_bank_code.getInputStr());
        params.put("assetPassword", MD5Util.MD5(et_pin.getInputStr()));
        params.put("telphone", userInfoTable.phone);
        OkGo.<BaseRes>post(Constants.OTC_BIND_BANK).params(params).tag(this).execute(new NewDialogJsonCallback<BaseRes>(this) {
            @Override
            public void onSuc(Response<BaseRes> response) {
                ToastUtil.show(getString(R.string.bind_suc));
                setResult(200);
                finish();
            }

            @Override
            public void onE(Response<BaseRes> response) {

            }

        });

    }
}
