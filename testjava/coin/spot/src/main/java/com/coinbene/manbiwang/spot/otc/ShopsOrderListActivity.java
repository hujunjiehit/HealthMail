package com.coinbene.manbiwang.spot.otc;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.coinbene.common.aspect.annotation.NeedLogin;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.R2;

import butterknife.BindView;
import butterknife.Unbinder;

/**
 * 商家订单列表页
 */
public class ShopsOrderListActivity extends CoinbeneBaseActivity {
    @BindView(R2.id.menu_back)
    RelativeLayout menuBack;
    @BindView(R2.id.menu_title_tv)
    TextView menuTitleTv;
    @BindView(R2.id.rg_shops)
    RadioGroup rgShops;
    @BindView(R2.id.fl_content)
    FrameLayout flContent;
    @BindView(R2.id.rb_sell_order)
    RadioButton rbSellOrder;
    @BindView(R2.id.rb_buy_order)
    RadioButton rbBuyOrder;
    @BindView(R2.id.menu_right_tv)
    TextView menuRightTv;
    @BindView(R2.id.view_line1)
    View viewLine1;
    private Unbinder mUnbinder;
    private FragmentManager fragmentManager;
    private ShopsOrderFragment fragments1;
    private ShopsOrderFragment fragments2;
    private ShopsOrderFragment fragments3;


    @NeedLogin(jump = true)
    public static void startMe(Context context) {
        Intent intent = new Intent(context, ShopsOrderListActivity.class);
        context.startActivity(intent);
    }



    @Override
    public int initLayout() {
        return R.layout.spot_activity_shops_order_list;
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

    private void listener() {
        menuBack.setOnClickListener(v -> finish());
        menuTitleTv.setText(getString(R.string.order_manager));
        rgShops.setOnCheckedChangeListener((group, checkedId) -> {
            ShowFragment(checkedId);
        });

    }

    private void init() {
        menuRightTv.setVisibility(View.VISIBLE);
        if (UserInfoController.getInstance().getUserInfo() != null && UserInfoController.getInstance().getUserInfo().supplier) {
            menuRightTv.setVisibility(View.VISIBLE);
        } else {
            menuRightTv.setVisibility(View.GONE);
        }
        menuRightTv.setTextColor(getResources().getColor(R.color.res_blue));
        menuRightTv.setText(R.string.ad_text);
        menuRightTv.setOnClickListener(v -> AdManageActivity.startMe(ShopsOrderListActivity.this));
        if (fragments1 == null) {
            fragments1 = ShopsOrderFragment.newInstance(1);

        }
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fl_content, fragments1).commitAllowingStateLoss();
        rbSellOrder.setChecked(true);
    }

    private void ShowFragment(int resouceId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (resouceId == R.id.rb_sell_order) {
            viewLine1.setVisibility(View.GONE);
            if (fragments2 != null) {
                transaction.hide(fragments2);
            }
            if (fragments1 == null) {
                fragments1 = ShopsOrderFragment.newInstance(1);
                transaction.add(R.id.fl_content, fragments1);
            } else {
                transaction.show(fragments1);
            }
        } else if (resouceId == R.id.rb_buy_order) {
            viewLine1.setVisibility(View.GONE);
            if (fragments1 != null) {
                transaction.hide(fragments1);
            }
            if (fragments2 == null) {
                fragments2 = ShopsOrderFragment.newInstance(0);
                transaction.add(R.id.fl_content, fragments2);
            } else {
                transaction.show(fragments2);
            }
        }
        transaction.commitAllowingStateLoss();
    }
}
