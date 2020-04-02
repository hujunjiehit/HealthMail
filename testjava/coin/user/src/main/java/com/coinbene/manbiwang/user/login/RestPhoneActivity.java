package com.coinbene.manbiwang.user.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;

import butterknife.BindView;
import butterknife.Unbinder;

/**
 * Created by mengxiangdong on 2017/11/28.
 */

public class RestPhoneActivity extends CoinbeneBaseActivity {
    public static final int CODE_RESULT = 10;
    @BindView(R2.id.ok_btn)
    TextView okBtn;
    @BindView(R2.id.menu_title_tv)
    TextView titleView;
    @BindView(R2.id.menu_back)
    View backView;

    public static void startMe(Context context) {
        Intent intent = new Intent(context, RestPhoneActivity.class);
        context.startActivity(intent);
    }

    public static void startMeForResult(Activity activity, int code) {
        Intent intent = new Intent(activity, RestPhoneActivity.class);
        activity.startActivityForResult(intent, code);
    }

    @Override
    public int initLayout() {
        return R.layout.setting_reset_phone;
    }

    @Override
    public void initView() {
        okBtn.setOnClickListener(this);
        titleView.setText(getText(R.string.reset_phone_title));
        backView.setOnClickListener(this);
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
            finish();
        }else if (v.getId() == R.id.menu_back) {
            finish();
        }
    }

}
