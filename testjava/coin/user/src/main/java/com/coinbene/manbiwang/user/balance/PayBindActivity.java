package com.coinbene.manbiwang.user.balance;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.manbiwang.model.http.KycInfoModel;
import com.coinbene.manbiwang.model.http.OtcBindInfo;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.Unbinder;

public class PayBindActivity extends CoinbeneBaseActivity {
    @BindView(R2.id.menu_back)
    View backBtn;
    @BindView(R2.id.rl_bank)
    RelativeLayout rl_bank;
    @BindView(R2.id.rl_alipay)
    RelativeLayout rl_alipay;
    @BindView(R2.id.rl_wechat)
    RelativeLayout rl_wechat;
    private Unbinder mUnbinder;
    @BindView(R2.id.menu_title_tv)
    TextView menu_title_tv;

    @BindView(R2.id.tv_bank_type)
    TextView tv_bank_type;
    @BindView(R2.id.tv_now_bind)
    TextView tv_now_bind;

    @BindView(R2.id.tv_alipay_type)
    TextView tv_alipay_type;
    @BindView(R2.id.tv_alipay_band_now)
    TextView tv_alipay_band_now;

    @BindView(R2.id.tv_wechat_type)
    TextView tv_wechat_type;
    @BindView(R2.id.tv_wechat_band_now)
    TextView tv_wechat_band_now;
    public static int requestCode = 200;

    private OtcBindInfo otcBindInfo;
    private String realName = "";

    public static void startMe(Context context) {
        Intent intent = new Intent(context, PayBindActivity.class);
        context.startActivity(intent);
    }

    private void init() {
        menu_title_tv.setText(R.string.add_pay_type);
        rl_bank.setOnClickListener(this);
        rl_alipay.setOnClickListener(this);
        rl_wechat.setOnClickListener(this);
        backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.rl_bank) {
            BindBankActivity.startMe(this, realName, requestCode);
        } else if (id == R.id.rl_alipay) {
            BindAlipayOrWechatActivity.startMe(this, 2, realName, requestCode);
        } else if (id == R.id.rl_wechat) {
            BindAlipayOrWechatActivity.startMe(this, 3, realName, requestCode);
        } else if (id == R.id.menu_back) {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==200){
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public int initLayout() {
        return R.layout.activity_pay_bind;
    }

    @Override
    public void initView() {
        init();
        getKycInfo();
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



    private void getKycInfo() {
        OkGo.<KycInfoModel>get(Constants.KYC_GET_INFO).tag(this).execute(new DialogCallback<KycInfoModel>(this) {
            @Override
            public void onSuc(Response<KycInfoModel> response) {
                KycInfoModel baseResponse = response.body();
                if (baseResponse.isSuccess()) {
                    if (baseResponse.getData() != null) {
                        realName = baseResponse.getData().getName();
                    }
                }
            }

            @Override
            public void onE(Response<KycInfoModel> response) {
            }

            @Override
            public void onFail(String msg) {
            }
        });
    }
}
