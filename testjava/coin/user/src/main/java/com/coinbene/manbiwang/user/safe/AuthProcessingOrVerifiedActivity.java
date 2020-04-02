package com.coinbene.manbiwang.user.safe;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.manbiwang.model.http.KycInfoModel;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import butterknife.BindView;

public class AuthProcessingOrVerifiedActivity extends CoinbeneBaseActivity {

    private int authStutas = 0;
    @BindView(R2.id.iv_auth_state)
    ImageView iv_auth_state;
    @BindView(R2.id.tv_auth_stutas_src)
    TextView tv_auth_stutas_src;
    @BindView(R2.id.tv_country_name)
    TextView tv_country_name;
    @BindView(R2.id.tv_real_name_value)
    TextView tv_real_name_value;
    @BindView(R2.id.tv_id_card_value)
    TextView tv_id_card_value;
    @BindView(R2.id.tv_id_card)
    TextView tv_id_card;
    @BindView(R2.id.menu_title_tv)
    TextView titleView;
    @BindView(R2.id.menu_back)
    View backView;

    public static void startMe(Context context, int authStutas) {
        Intent intent = new Intent(context, AuthProcessingOrVerifiedActivity.class);
        intent.putExtra("authStutas", authStutas);
        context.startActivity(intent);
    }

    @Override
    public int initLayout() {
        return R.layout.settings_activity_auth_processing_or_verified;
    }

    @Override
    public void initView() {
        init();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.menu_back) {
            finish();
        }
    }

    private void init() {
        backView.setOnClickListener(this);
        titleView.setText(R.string.real_nameauthentication);
        authStutas = getIntent().getIntExtra("authStutas", 0);
        if (authStutas == Constants.AUTH_PROCESSING) {//审核中
            iv_auth_state.setImageDrawable(getResources().getDrawable((R.drawable.icon_auth_processing)));
            tv_auth_stutas_src.setText(getString(R.string.identification_processing));
        } else if (authStutas == Constants.AUTH_VERIFIED) {//已通过
            iv_auth_state.setImageDrawable(getResources().getDrawable((R.drawable.icon_auth_verified)));
            tv_auth_stutas_src.setText(getString(R.string.preliminary_approval_has_passed));
        }
    }

    @Override
    public void setListener() {

    }

    @Override
    public void initData() {
        getKycInfo();
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
                        setSycInfo(baseResponse.getData());
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

    private void setSycInfo(KycInfoModel.DataBean data) {
//        String country = data.getCountry();
//        if (TextUtils.isEmpty(country)) {
//            return;
//        }
//        CountryTable countryTable = CountryController.getInstance().queryCountryByISO(country);
//        String country_english = "";
//        String country_cn = "";
//        if (countryTable != null) {
//            country_english = countryTable.name_english;
//            country_cn = countryTable.name_zh;
//        }
//        if (CommonUtil.isLocalzh(this)) {//中文
//            tv_country_name.setText(country_cn);
//        } else {//英文
        if (!TextUtils.isEmpty(data.getCountryName()))
            tv_country_name.setText(data.getCountryName());
//        }

        if (!TextUtils.isEmpty(data.getName()))
            tv_real_name_value.setText(data.getName());
        if (!TextUtils.isEmpty(data.getIdNumber()))
            tv_id_card_value.setText(data.getIdNumber());
        if (data.getType() == 1 || data.getType() == 2)//type为1是国内身份证，2是国外身份证
            tv_id_card.setText(getString(R.string.id_card_number));
        else if (data.getType() == 3)
            tv_id_card.setText(getString(R.string.passport_no));
    }
}
