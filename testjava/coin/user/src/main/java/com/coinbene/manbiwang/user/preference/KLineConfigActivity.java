package com.coinbene.manbiwang.user.preference;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;

import butterknife.BindView;

public class KLineConfigActivity extends CoinbeneBaseActivity {


    @BindView(R2.id.menu_title_tv)
    TextView menuTitleTv;
    @BindView(R2.id.menu_right_tv)
    TextView menuRightTv;
    @BindView(R2.id.lineCofing_redUp)
    RelativeLayout redUp;
    @BindView(R2.id.lineCofing_greenUp)
    RelativeLayout greenUp;
    @BindView(R2.id.menu_back)
    RelativeLayout menuBack;
    @BindView(R2.id.redUp_img)
    ImageView redUpImg;
    @BindView(R2.id.greenUp_img)
    ImageView greenUpImg;


    private boolean isRedRiseGreenFall = true;


    public static void startMe(Context context) {
        Intent intent = new Intent(context, KLineConfigActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int initLayout() {
        return R.layout.settings_activity_kline_config;
    }

    @Override
    public void initView() {
//        初始View
        init();
    }

    @Override
    public void setListener() {
//        初始监听
        initOnclick();
    }

    @Override
    public void initData() {

    }

    @Override
    public boolean needLock() {
        return false;
    }

    private void initOnclick() {
//        返回按钮
        menuBack.setOnClickListener(this);

//        完成按钮
        menuRightTv.setOnClickListener(this);

//        红涨
        redUp.setOnClickListener(this);

//        绿涨
        greenUp.setOnClickListener(this);
    }


    private void init() {

        menuTitleTv.setText(R.string.kline_color_cofig);

        menuRightTv.setText(R.string.menu_name_done);

        menuRightTv.setTextColor(Color.parseColor("#5690ED"));

        isRedRiseGreenFall = SwitchUtils.isRedRise();
        ;

        if (isRedRiseGreenFall) {
            redUpImg.setVisibility(View.VISIBLE);
            greenUpImg.setVisibility(View.GONE);

        } else {

            greenUpImg.setVisibility(View.VISIBLE);
            redUpImg.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.menu_back) {
            finish();


        } else if (id == R.id.menu_right_tv) {
            if (!isRedRiseGreenFall == SwitchUtils.isRedRise()) {
                SpUtil.put(CBRepository.getContext(), SpUtil.IS_RED_RISE_GREEN_FALL, isRedRiseGreenFall);
            }
            finish();


        } else if (id == R.id.lineCofing_redUp) {
            redUpImg.setVisibility(View.VISIBLE);
            greenUpImg.setVisibility(View.GONE);
            isRedRiseGreenFall = true;


        } else if (id == R.id.lineCofing_greenUp) {
            greenUpImg.setVisibility(View.VISIBLE);
            redUpImg.setVisibility(View.GONE);
            isRedRiseGreenFall = false;
        }

    }
}
