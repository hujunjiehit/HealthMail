package com.coinbene.manbiwang.record.coinrecord;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.widget.wrapper.EndlessRecyclerOnScrollListener;
import com.coinbene.common.widget.wrapper.LoadMoreWrapper;
import com.coinbene.manbiwang.model.http.DepositListModel;
import com.coinbene.manbiwang.model.http.DispatchRecordBean;
import com.coinbene.manbiwang.model.http.TransferListModel;
import com.coinbene.manbiwang.record.R;
import com.coinbene.manbiwang.record.R2;
import com.coinbene.manbiwang.record.coinrecord.adapter.HistoryRechargeAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.List;

import butterknife.BindView;
import butterknife.Unbinder;



/**
 * Created by mengxiangdong on 2017/11/28.
 * 充值/提现记录
 */

public class WithDrawRechargeHisActivity extends CoinbeneBaseActivity {

    public static final String ORDER_TYPE_WITHDRAW = "2";//提现
    public static final String ORDER_TYPE_DEPOSIT = "1";//充值
    public static final String ORDER_TYPE_TRANSFER = "3";//转账
    public static final String ORDER_TYPE_DISPATCH = "4";//分发
    private Unbinder mUnbinder;

    @BindView(R2.id.menu_title_tv)
    TextView titleView;
    @BindView(R2.id.history_list)
    RecyclerView recyclerView;
    @BindView(R2.id.menu_right_tv)
    TextView menuRightTv;

    HistoryRechargeAdapter adapter;
    @BindView(R2.id.menu_back)
    View backView;
    @BindView(R2.id.menu_right_layout)
    LinearLayout menuRightLayout;

    @BindView(R2.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R2.id.empty_layout)
    LinearLayout emptyLayout;

    private LoadMoreWrapper loadMoreWrapper;

    public static void startMe(Context context, int type) {
        Intent intent = new Intent(context, WithDrawRechargeHisActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    public int initLayout() {
        return R.layout.record_draw_recharge_history;
    }

    @Override
    public void initView() {
        titleView.setText(getText(R.string.history_withdraw_recharge));
        backView.setOnClickListener(this);
        menuRightLayout.setVisibility(View.GONE);


        adapter = new HistoryRechargeAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadMoreWrapper = new LoadMoreWrapper(adapter);

        recyclerView.setAdapter(loadMoreWrapper);

        Intent intent = getIntent();

        dialog_Type = Constants.CODE_RECORD_ALL_TYPE;
        if (intent != null) {
            dialog_Type = intent.getIntExtra("type", Constants.CODE_RECORD_ALL_TYPE);
        }

        changedTypeAndTitle(dialog_Type);

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.res_blue));
        init();
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

    private void changedTypeAndTitle(int type) {
        if (type == Constants.CODE_RECORD_RECHARGE_TYPE) {
            titleView.setText(R.string.recharge_coin_new);
            post_param_type = type_desipose;
        } else if (type == Constants.CODE_RECORD_WITHDRAW_TYPE) {
            titleView.setText(R.string.reflect_coin_new);
            post_param_type = type_withdraw;
        } else if (type == Constants.CODE_RECORD_TRANSFER_TYPE) {
            titleView.setText(R.string.transfer);
            post_param_type = type_transfer;
        } else if (type == Constants.CODE_RECORD_DISPATCH_TYPE) {
            titleView.setText(R.string.recrod_dispatch_text);
            post_param_type = type_dispatch;
        }
    }

    private int dialog_Type = 0;

    private boolean isEnd = false;

    private void init() {

        refreshData();

        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                if (!isEnd) {
                    loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING);
                    loadMoreData();
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshData();
        });
    }

    private int current_page = 1;
    private int pageSize = 10, totalCount;
    //充值
    public String type_desipose = "1";
    //提现
    public String type_withdraw = "2";
    //转账
    public String type_transfer = "3";
    //分发
    public String type_dispatch = "4";

    public String type_all = "";

    private String post_param_type;

    private void refreshData() {
        current_page = 1;
        if (String.valueOf(dialog_Type).equals(ORDER_TYPE_DEPOSIT)) {
            getDepositList(current_page);
        } else if (String.valueOf(dialog_Type).equals(ORDER_TYPE_WITHDRAW)) {
            getWithDrawList(current_page);
        } else if (String.valueOf(dialog_Type).equals(ORDER_TYPE_TRANSFER)) {
            getTransferList(current_page);
        } else if (String.valueOf(dialog_Type).equals(ORDER_TYPE_DISPATCH)) {
            getDispatch(current_page);
        }
    }


    private void loadMoreData() {
        current_page++;
        if (String.valueOf(dialog_Type).equals(ORDER_TYPE_DEPOSIT)) {
            getDepositList(current_page);
        } else if (String.valueOf(dialog_Type).equals(ORDER_TYPE_WITHDRAW)) {
            getWithDrawList(current_page);
        } else if (String.valueOf(dialog_Type).equals(ORDER_TYPE_TRANSFER)) {
            getTransferList(current_page);
        } else if (String.valueOf(dialog_Type).equals(ORDER_TYPE_DISPATCH)) {
            getDispatch(current_page);
        }
    }


    /**
     * @param page 获取分发记录
     */
    private void getDispatch(int page) {

        UserInfoTable user = UserInfoController.getInstance().getUserInfo();

        HttpParams httpParams = new HttpParams();
        httpParams.put("pageNum", page);
        httpParams.put("pageSize", pageSize);

        if (user != null) {
            httpParams.put("userId", user.userId);
        }

        OkGo.<DispatchRecordBean>post(Constants.RECORD_DISPATCH_INTERFACE).tag(this).params(httpParams).execute(new NewJsonSubCallBack<DispatchRecordBean>() {


            @Override
            public void onSuc(Response<DispatchRecordBean> response) {
                if (WithDrawRechargeHisActivity.this.isDestroyed()) {
                    return;
                }
                if (response.body() != null && response.body().getData() != null && response.body().getData().getList() != null && response.body().getData().getList().size() > 0) {
                    emptyLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    List<DispatchRecordBean.DataBean.ListBean> list = response.body().getData().getList();
                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    if (list.size() < 10) {
                        isEnd = true;
                        loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_END);
                    } else {
                        isEnd = false;
                        loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_COMPLETE);
                    }
                    if (current_page > 1) {
                        adapter.appendItems(list, ORDER_TYPE_DISPATCH);
                    } else {
                        adapter.setItems(list, ORDER_TYPE_DISPATCH);
                    }
                    loadMoreWrapper.notifyDataSetChanged();
                } else {
                    isEnd = false;
                    loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_END);
                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    if (page == 1) {
                        adapter.setItems(null, null, ORDER_TYPE_DISPATCH);
                        loadMoreWrapper.notifyDataSetChanged();
                        emptyLayout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            }


            @Override
            public void onE(Response<DispatchRecordBean> response) {
                getDataFail();
            }

        });

    }


    //获取充值记录
    private void getDepositList(int page) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("accountType", 1);
        httpParams.put("asset", "");
        httpParams.put("pageNum", page);
        httpParams.put("pageSize", pageSize);
        OkGo.<DepositListModel>post(Constants.ACCOUNT_DEPOSIT_LIST).tag(this).params(httpParams).execute(new NewJsonSubCallBack<DepositListModel>() {

            @Override
            public void onSuc(Response<DepositListModel> response) {
                if (WithDrawRechargeHisActivity.this.isDestroyed()) {
                    return;
                }
                if (response.body().data != null && response.body().data.list != null && response.body().data.list.size() > 0) {
                    emptyLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    List<DepositListModel.DataBean.ListBean> list = response.body().getData().getList();
                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    if (list.size() < 10) {
                        isEnd = true;
                        loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_END);
                    } else {
                        isEnd = false;
                        loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_COMPLETE);
                    }
                    if (current_page > 1) {
                        adapter.appendItems(list, null, ORDER_TYPE_DEPOSIT);
                    } else {
                        adapter.setItems(list, null, ORDER_TYPE_DEPOSIT);
                    }
                    loadMoreWrapper.notifyDataSetChanged();
                    //                    }
                } else {
                    isEnd = false;
                    loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_END);
                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    if (page == 1) {
                        adapter.setItems(null, null, ORDER_TYPE_DEPOSIT);
                        loadMoreWrapper.notifyDataSetChanged();
                        emptyLayout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            }


            @Override
            public void onE(Response<DepositListModel> response) {
                getDataFail();
            }

            @Override
            public void onFail(String msg) {

            }
        });
    }


    //获取提现记录
    private void getWithDrawList(int page) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("accountType", 1);
        httpParams.put("asset", "");

        httpParams.put("pageNum", page);
        httpParams.put("pageSize", pageSize);
        OkGo.<DepositListModel>get(Constants.ACCOUNT_WITHDRAW_LIST).tag(this).params(httpParams).execute(new NewJsonSubCallBack<DepositListModel>() {

            @Override
            public void onSuc(Response<DepositListModel> response) {
                if (WithDrawRechargeHisActivity.this.isDestroyed()) {
                    return;
                }
                if (response.body().data != null && response.body().data.list != null && response.body().data.list.size() > 0) {

                    emptyLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    List<DepositListModel.DataBean.ListBean> list = response.body().getData().getList();

                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    if (list.size() < 10) {
                        isEnd = true;
                        loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_END);
                    } else {
                        isEnd = false;
                        loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_COMPLETE);
                    }
                    if (current_page > 1) {
                        adapter.appendItems(list, null, ORDER_TYPE_WITHDRAW);
                    } else {
                        adapter.setItems(list, null, ORDER_TYPE_WITHDRAW);
                    }
                    loadMoreWrapper.notifyDataSetChanged();
                } else {
                    isEnd = false;
                    loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_END);
                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    if (page == 1) {
                        adapter.setItems(null, null, ORDER_TYPE_WITHDRAW);
                        loadMoreWrapper.notifyDataSetChanged();
                        emptyLayout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            }


            @Override
            public void onE(Response<DepositListModel> response) {
                if (WithDrawRechargeHisActivity.this.isDestroyed()) {
                    return;
                }
                getDataFail();
            }

            @Override
            public void onFail(String msg) {
                if (WithDrawRechargeHisActivity.this.isDestroyed()) {
                    return;
                }
                getDataFail();
            }
        });
    }

    //获取转账记录
    private void getTransferList(int page) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("accountType", 1);
        httpParams.put("asset", "");

        httpParams.put("pageNum", page);
        httpParams.put("pageSize", pageSize);
        OkGo.<TransferListModel>get(Constants.ACCOUNT_TRANSFER_LIST).tag(this).params(httpParams).execute(new NewJsonSubCallBack<TransferListModel>() {

            @Override
            public void onSuc(Response<TransferListModel> response) {
                if (WithDrawRechargeHisActivity.this.isDestroyed()) {
                    return;
                }
                if (response.body().getData() != null && response.body().getData().getList() != null && response.body().getData().getList().size() > 0) {

                    emptyLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    List<TransferListModel.DataBean.ListBean> list = response.body().getData().getList();
                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    if (list.size() < 10) {
                        isEnd = true;
                        loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_END);
                    } else {
                        isEnd = false;
                        loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_COMPLETE);
                    }
                    if (current_page > 1) {
                        adapter.appendItems(null, list, ORDER_TYPE_TRANSFER);
                    } else {
                        adapter.setItems(null, list, ORDER_TYPE_TRANSFER);
                    }
                    loadMoreWrapper.notifyDataSetChanged();
                } else {
                    isEnd = false;
                    loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_END);
                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    if (page == 1) {
                        adapter.setItems(null, null, ORDER_TYPE_TRANSFER);
                        loadMoreWrapper.notifyDataSetChanged();
                        emptyLayout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            }


            @Override
            public void onE(Response<TransferListModel> response) {
                if (WithDrawRechargeHisActivity.this.isDestroyed()) {
                    return;
                }
                getDataFail();
            }

            @Override
            public void onFail(String msg) {
                if (WithDrawRechargeHisActivity.this.isDestroyed()) {
                    return;
                }
                getDataFail();
            }
        });
    }


    public void getDataFail() {
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        if (emptyLayout != null && recyclerView != null)
            if (current_page == 1) {
                emptyLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_COMPLETE);
                emptyLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.menu_back) {
            finish();
        }
    }

    public void changeTypeValue(int type) {
        changedTypeAndTitle(type);
        swipeRefreshLayout.post(() -> {
            swipeRefreshLayout.setRefreshing(true);
            refreshData();
        });
    }
}
