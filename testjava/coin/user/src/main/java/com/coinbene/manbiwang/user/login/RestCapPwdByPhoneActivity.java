package com.coinbene.manbiwang.user.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.widget.EditTextTwoIcon;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by mengxiangdong on 2017/11/28.
 */

public class RestCapPwdByPhoneActivity extends CoinbeneBaseActivity {
    public static final int CODE_RESULT = 13;

    private Unbinder mUnbinder;

    @BindView(R2.id.ok_btn)
    TextView okBtn;
    @BindView(R2.id.menu_title_tv)
    TextView titleView;
    @BindView(R2.id.menu_back)
    View backView;

    @BindView(R2.id.msg_code_input)
    EditText phoneCodeInput;

    @BindView(R2.id.new_pwd_input)
    EditTextTwoIcon newCodeInput;

    public static void startMe(Context context) {
        Intent intent = new Intent(context, RestCapPwdByPhoneActivity.class);
        context.startActivity(intent);
    }

    public static void startMeForResult(Activity activity, int code) {
        Intent intent = new Intent(activity, RestCapPwdByPhoneActivity.class);
        activity.startActivityForResult(intent, code);
    }

    @Override
    public int initLayout() {
        return R.layout.resetcap_pwd_phone;
    }

    @Override
    public void initView() {
        okBtn.setOnClickListener(this);
        titleView.setText(getText(R.string.set_reset_cap_pwd_title));
        backView.setOnClickListener(this);

        phoneCodeInput.setHint(R.string.user_hint_msg_code);

        newCodeInput.setFirstRightIcon(R.drawable.icon_close);
        newCodeInput.setSecondRightEye_Num();//TODO:这里有bug，数字密码
        newCodeInput.getInputText().setHint(R.string.set_cap_num_hint);

    }

    @Override
    public void setListener() {

    }

    @Override
    public void initData() {

    }

    @Override
    public boolean needLock() {
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ok_btn) {
            setResult(RESULT_OK, new Intent());
            finish();
        } else if (v.getId() == R.id.menu_back) {
            finish();
        }
    }
}
