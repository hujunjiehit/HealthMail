package com.coinbene.manbiwang.contract.contractbtc.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.database.ContractConfigController;
import com.coinbene.common.database.ContractConfigTable;
import com.coinbene.common.database.ContractInfoController;
import com.coinbene.common.database.ContractInfoTable;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.contract.contractbtc.activity.fragment.CalculateBtcFCFragment;
import com.coinbene.manbiwang.contract.contractbtc.activity.fragment.CalculateBtcPLFragment;
import com.coinbene.manbiwang.contract.contractbtc.activity.fragment.CalculateBtcTPFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.Unbinder;

/**
 * 合约计算器
 */
public class CalculatorBtcActivity extends CoinbeneBaseActivity implements TabLayout.OnTabSelectedListener {

    @BindView(R2.id.calculator_Back)
    ImageView back;
    @BindView(R2.id.calculator_TabLayout)
    TabLayout tabLayout;
    @BindView(R2.id.layout_Indicator)
    LinearLayout indicator;
    private Unbinder bind;

    private FragmentTransaction transaction;
    private CalculateBtcFCFragment fragmentFC;
    private CalculateBtcPLFragment fragmentPL;
    private CalculateBtcTPFragment fragmentTP;
    private Fragment curFragment = new Fragment();

    /**
     * 合约种类数据
     */
    private String[] symbols;

    private ContractConfigTable contractConfig;
    private String leverDefault;
    private String symbolDefault;

    public static void startMe(Context context, String symbol, String lever) {
        Intent intent = new Intent(context, CalculatorBtcActivity.class);
        intent.putExtra("symbol", symbol);
        intent.putExtra("lever", lever);
        context.startActivity(intent);
    }

    @Override
    public int initLayout() {
        return R.layout.activity_calculator;
    }

    @Override
    public void initView() {
        if (getIntent() != null) {
            symbolDefault = getIntent().getStringExtra("symbol");
            leverDefault = getIntent().getStringExtra("lever");
        }
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
        return false;
    }


    private void init() {

        symbolhandle();

        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.pl_calculate_text)), true);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tp_calculate_text)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.fc_calculate_text)));
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSelectedTabIndicatorHeight(0);
        setIndicator(0);

        //通过这里将数据传过去
        fragmentFC = CalculateBtcFCFragment.newInstance(symbolDefault, leverDefault, symbols);
        fragmentPL = CalculateBtcPLFragment.newInstance(symbolDefault, leverDefault, symbols);
        fragmentTP = CalculateBtcTPFragment.newInstance(symbolDefault, leverDefault, symbols);

        setFragment(fragmentPL);

    }

    /**
     * @param fragment 设置当前显示fragment
     */
    private void setFragment(Fragment fragment) {

        transaction = getSupportFragmentManager().beginTransaction();

        if (curFragment != fragment) {

            if (!fragment.isAdded()) {
                transaction.hide(curFragment).add(R.id.content_fragment, fragment, fragment.getClass().getName());
            } else {
                transaction.hide(curFragment).show(fragment);
            }

            transaction.commit();
            curFragment = fragment;

        }

    }

    private void listener() {
        tabLayout.addOnTabSelectedListener(this);
        back.setOnClickListener(this);
    }

    /**
     * @param index 设置当前指示器位置
     */
    private void setIndicator(int index) {

        int count = indicator.getChildCount();

        for (int i = 0; i < count; i++) {
            indicator.getChildAt(i).setVisibility(View.INVISIBLE);
        }

        indicator.getChildAt(index).setVisibility(View.VISIBLE);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.calculator_Back) {
            finish();
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        setIndicator(tab.getPosition());

        switch (tab.getPosition()) {
            case 0:
                setFragment(fragmentPL);
                break;
            case 1:
                setFragment(fragmentTP);
                break;
            case 2:
                setFragment(fragmentFC);
                break;

            default:

        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    /**
     * 处理合约类型数据
     */
    private void symbolhandle() {
        List<ContractInfoTable> list = ContractInfoController.getInstance().queryContrackList();
        if (list != null && list.size() > 0) {
            symbols = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                symbols[i] = list.get(i).name;
            }
        }
    }

}
