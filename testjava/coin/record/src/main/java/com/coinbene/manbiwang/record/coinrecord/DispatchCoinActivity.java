package com.coinbene.manbiwang.record.coinrecord;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.database.BalanceController;
import com.coinbene.common.database.BalanceInfoTable;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.manbiwang.record.R;
import com.coinbene.manbiwang.record.R2;

import butterknife.BindView;

public class DispatchCoinActivity extends CoinbeneBaseActivity {

    @BindView(R2.id.menu_back)
    RelativeLayout menuBack;
    @BindView(R2.id.menu_title_tv)
    TextView menuTitleTv;
    @BindView(R2.id.dispatcth_coin_logo)
    ImageView dispatcthCoinLogo;
    @BindView(R2.id.dispatch_coin_name)
    TextView dispatchCoinName;
    @BindView(R2.id.dispatch_coin_type)
    TextView dispatchCoinType;
    @BindView(R2.id.dispatch_num)
    TextView dispatchNum;
    @BindView(R2.id.dispatch_status)
    TextView dispatchStatus;
    @BindView(R2.id.dispatch_tip)
    TextView dispatchTip;
    @BindView(R2.id.dispatch_date)
    TextView dispatchDate;

    public static void startMe(Context context, Bundle bundle) {
        Intent intent = new Intent(context, DispatchCoinActivity.class);
        intent.putExtra("DispatchRes", bundle);
        context.startActivity(intent);
    }

    @Override
    public int initLayout() {
        return R.layout.record_activity_dispatch_coin;
    }

    @Override
    public void initView() {
        init();
    }

    @Override
    public void setListener() {
        listener();
    }

    @Override
    public void initData() {

    }

    @Override
    public boolean needLock() {
        return true;
    }

    /**
     * 初始View
     */
    public void init() {
        Bundle dispatchRes = getIntent().getBundleExtra("DispatchRes");

        if (dispatchRes != null) {
            String coinName = dispatchRes.getString("dispatchName");
            dispatchCoinName.setText(coinName);

            //备注不为空设置
            String remark = dispatchRes.getString("dispatchRemark");
            if (!TextUtils.isEmpty(remark)) {
                dispatchTip.setText(remark);
            }

            BalanceInfoTable balanceTable = BalanceController.getInstance().findByAsset(coinName);
            if (balanceTable != null) {

                if (LanguageHelper.isChinese(dispatchCoinType.getContext())) {
                    dispatchCoinType.setText(balanceTable.localAssetName);
                } else {
                    dispatchCoinType.setText(balanceTable.englishAssetName);
                }

            }

            dispatchDate.setText(dispatchRes.getString("dispatchDate"));
            dispatchNum.setText(dispatchRes.getString("dispatchNum"));
            Glide.with(this).load(dispatchRes.getString("dispatchIcon")).into(dispatcthCoinLogo);
        }

        menuTitleTv.setText(getString(R.string.recrod_dispatch_detail));

    }


    /**
     * 初始监听
     */
    public void listener() {
        menuBack.setOnClickListener(this);
    }


    /**
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        // 返回
        if (v.getId() == R.id.menu_back) {
            finish();
        }
    }
}
