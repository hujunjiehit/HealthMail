package com.coinbene.manbiwang.record.contractrecord;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.widget.wrapper.LoadMoreListener;
import com.coinbene.manbiwang.model.http.FundCostModel;
import com.coinbene.manbiwang.record.R;
import com.coinbene.manbiwang.record.R2;
import com.coinbene.manbiwang.record.contractrecord.adapter.FundCostAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.List;

import butterknife.BindView;
import butterknife.Unbinder;

/**
 * @author ding
 * 资金费用
 */
public class FundCostBtcActivity extends CoinbeneBaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R2.id.menu_back)
    RelativeLayout back;
    @BindView(R2.id.menu_title_tv)
    TextView title;
    @BindView(R2.id.fund_cost_RecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R2.id.fund_cost_Refresh)
    SwipeRefreshLayout refresh;
    private Unbinder bind;
    private int current = 1;
    private int size = 10;
    private FundCostAdapter adapter;

    public static void startMe(Context context) {
        Intent intent = new Intent(context, FundCostBtcActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public int initLayout() {
        return R.layout.record_activity_fund_cost;
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
        getFundCost();
    }

    @Override
    public boolean needLock() {
        return true;
    }


    public void init() {
        title.setText(R.string.record_fund_cost);
        refresh.setColorSchemeColors(getResources().getColor(R.color.res_blue));
        adapter = new FundCostAdapter();
        adapter.setContractType(Constants.CONTRACT_TYPE_BTC);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);

    }

    public void listener() {
        back.setOnClickListener(this);
        refresh.setOnRefreshListener(this);
        mRecyclerView.addOnScrollListener(new LoadMoreListener() {
            @Override
            public void loadMore() {
                current++;
                getFundCost();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.menu_back) {
            finish();
        }
    }


    /**
     * 刷新
     */
    @Override
    public void onRefresh() {
        current = 1;
        getFundCost();
    }


    /**
     * 获取资金费用记录
     */
    private void getFundCost() {

        HttpParams params = new HttpParams();
        params.put("pageNum", current);
        params.put("pageSize", size);

        OkGo.<FundCostModel>get(Constants.FUND_COST).tag(this).params(params).execute(new NewJsonSubCallBack<FundCostModel>() {
            @Override
            public void onSuc(Response<FundCostModel> response) {

                FundCostModel.DataBean data = response.body().getData();

                if (data == null) {
                    return;
                }

                List<FundCostModel.DataBean.ListBean> items = data.getList();

                if (current == 1) {
                    adapter.setItem(items);
                } else {
                    adapter.appendItem(items);
                }

                if (items.size() < size) {
                    adapter.setState(FundCostAdapter.LOADING_END);
                } else {
                    adapter.setState(FundCostAdapter.LOADING);
                }

            }

            @Override
            public void onE(Response<FundCostModel> response) {

                if (current > 1) {
                    current--;
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (refresh != null && refresh.isRefreshing()) {
                    refresh.setRefreshing(false);
                }
            }
        });

    }
}
