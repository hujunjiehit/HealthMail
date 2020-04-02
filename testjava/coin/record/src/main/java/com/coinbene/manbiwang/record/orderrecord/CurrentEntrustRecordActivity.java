package com.coinbene.manbiwang.record.orderrecord;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.manbiwang.record.R;
import com.coinbene.manbiwang.record.R2;
import com.coinbene.manbiwang.record.orderrecord.fragment.CurrentEntrustFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by mengxiangdong on 2017/12/4.
 * 当前委托页面
 */

public class CurrentEntrustRecordActivity extends CoinbeneBaseActivity {
    private Unbinder mUnbinder;
    @BindView(R2.id.menu_title_tv)
    TextView titleView;
    @BindView(R2.id.menu_back)
    View backView;
    @BindView(R2.id.menu_right_tv)
    TextView rightMenuTv;
    private Fragment currentFrag;
    private String accountType;

    public static void startMe(Context context) {
        Intent intent = new Intent(context, CurrentEntrustRecordActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public static void startMe(Context context, String accountType) {
        Intent intent = new Intent(context, CurrentEntrustRecordActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra("accountType",accountType);
        context.startActivity(intent);
    }

    @Override
    public int initLayout() {
        return R.layout.record_trade_history;
    }

    @Override
    public void initView() {
        titleView.setText(getText(R.string.cur_entrust_label));
        backView.setOnClickListener(this);
        rightMenuTv.setText(getText(R.string.cancel_all));
        rightMenuTv.setOnClickListener(this);
        rightMenuTv.setVisibility(View.VISIBLE);
        rightMenuTv.setTextColor(getResources().getColor(R.color.res_blue));
//        rightMenuTv.setVisibility(View.GONE);
        showCurFragment();
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

    public TextView getRightMenuTv() {
        return rightMenuTv;
    }

    private void showCurFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        this.accountType = getIntent().getStringExtra("accountType");
        bundle.putString("accountType",accountType);
        currentFrag = Fragment.instantiate(this, CurrentEntrustFragment.class.getName(), bundle);
        ft.add(R.id.context_layout, currentFrag, CurrentEntrustFragment.class.getName());
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.menu_back) {
            finish();
        }
//        else if (v.getId() == R.id.menu_right_tv) {
//            HistoryEntrustRecordActivity.startMe(v.getContext());
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentFrag != null) {
            getSupportFragmentManager().beginTransaction().remove(currentFrag).commitAllowingStateLoss();
        }
    }
}
