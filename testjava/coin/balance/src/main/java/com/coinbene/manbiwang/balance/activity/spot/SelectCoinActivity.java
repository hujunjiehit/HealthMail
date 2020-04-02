package com.coinbene.manbiwang.balance.activity.spot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.database.BalanceController;
import com.coinbene.common.database.BalanceInfoTable;
import com.coinbene.common.utils.KeyboardUtils;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;
import com.coinbene.manbiwang.balance.activity.spot.adapter.SelectCoinBinder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * Created by mengxiangdong on 2017/12/3.
 * 选择币种页面
 */

public class SelectCoinActivity extends CoinbeneBaseActivity {
    //充值
    public static final int FROM_DEPOSIT = 1;
    //提现
    public static final int FROM_WITHDRAW = 2;
    public static final int RESULT_SELECTED_CODE = 102;
    public static final String KEY_RETURN = "key_return";
    //第一进入时获取的数据
    private List<BalanceInfoTable> assetDataBase;

    public static void startMe(Context context, Bundle bundle) {
        Intent intent = new Intent(context, SelectCoinActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void startMeForResult(Activity activity) {
        Intent intent = new Intent(activity, SelectCoinActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_RETURN, true);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, RESULT_SELECTED_CODE);
    }

    @BindView(R2.id.list_coin_category)
    RecyclerView recyclerView;
    MultiTypeAdapter mContentAdapter;
    @BindView(R2.id.menu_title_tv)
    TextView titleView;
    private int fromType;
    @BindView(R2.id.menu_back)
    View backView;
    private boolean needReturn;

    @BindView(R2.id.search_input)
    EditText searchInput;
    @BindView(R2.id.close_view)
    ImageView closeView;
    @BindView(R2.id.empty_layout)
    View emptyLayout;

    @Override
    public int initLayout() {
        return R.layout.selecte_coin_list;
    }

    @Override
    public void initView() {
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            fromType = intent.getExtras().getInt("from");
        }
        needReturn = intent.getBooleanExtra(KEY_RETURN, false);

        mContentAdapter = new MultiTypeAdapter();
        SelectCoinBinder coinBinder = new SelectCoinBinder(fromType);
        coinBinder.setItemClickListener(onItemClickListener);

        mContentAdapter.register(BalanceInfoTable.class, coinBinder);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mContentAdapter);
        titleView.setText(com.coinbene.common.R.string.selecte_coin_title_label);
        backView.setOnClickListener(this);
        closeView.setOnClickListener(this);
        searchInput.setHint(com.coinbene.common.R.string.search_coin_input_hint);

        getData();

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                changeInputStr(s.toString());
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    KeyboardUtils.hideKeyboard(recyclerView);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
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


    public void getData() {
        assetDataBase = BalanceController.getInstance().getAllAssets();
        if (assetDataBase == null || assetDataBase.size() == 0)
            return;
        mContentAdapter.setItems(assetDataBase);
        mContentAdapter.notifyDataSetChanged();
    }

    private String lastInputStr;

    private void changeInputStr(String inputStr) {
        if (TextUtils.isEmpty(inputStr)) {
            lastInputStr = "";
            closeView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            mContentAdapter.setItems(assetDataBase);
            mContentAdapter.notifyDataSetChanged();
            return;
        }
        closeView.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(lastInputStr) && lastInputStr.equalsIgnoreCase(inputStr)) {
            return;
        }
        lastInputStr = inputStr;

        List<BalanceInfoTable> queryResult = BalanceController.getInstance().queryDataLikeStr(LanguageHelper.isChinese(this), inputStr);
        if (queryResult == null) {
            queryResult = new ArrayList<>();
        }
        if (queryResult.size() == 0) {
            emptyLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        mContentAdapter.setItems(queryResult);
        mContentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == com.coinbene.common.R.id.menu_back) {
            KeyboardUtils.hideKeyboard(searchInput);
            finish();
        } else if (v.getId() == com.coinbene.common.R.id.close_view) {
            searchInput.setText("");
            KeyboardUtils.hideKeyboard(recyclerView);
        }
    }

    private SelectCoinBinder.OnItemClickListener onItemClickListener = this::onItemClickLocal;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
    }

    private void onItemClickLocal(BalanceInfoTable item) {
        if (fromType == SelectCoinActivity.FROM_DEPOSIT && !item.deposit) {
            String str;
            if (TextUtils.isEmpty(item.banDepositReason)) {
                str = this.getResources().getString(com.coinbene.common.R.string.cannot_deposit_tips);
                str = String.format(str, item.asset);
            } else {
                str = item.banDepositReason + getString(com.coinbene.common.R.string.cannot_deposit_tips_new);
            }
            ToastUtil.show(str);
            return;
        }

//        if (fromType == SelectCoinActivity.FROM_WITHDRAW && !item.withdraw) {
//            String str;
//            if (TextUtils.isEmpty(item.banWithdrawReason)) {
//                str = this.getResources().getString(com.coinbene.common.R.string.cannot_withdraw_tips);
//                str = String.format(str, item.asset);
//            } else {
//                str = item.banWithdrawReason + getString(com.coinbene.common.R.string.cannot_withdraw_tips_new);
//            }
//        }

        if (needReturn) {
            Intent intent = new Intent();
            intent.putExtra("item", item.asset);
            setResult(RESULT_OK, intent);
            finish();
            return;
        }

        if (fromType == SelectCoinActivity.FROM_DEPOSIT) {
            DepositActivity.startMe(SelectCoinActivity.this, item.asset);
        } else {
            WithDrawActivity.startMe(SelectCoinActivity.this, item.asset);
        }
    }
}
