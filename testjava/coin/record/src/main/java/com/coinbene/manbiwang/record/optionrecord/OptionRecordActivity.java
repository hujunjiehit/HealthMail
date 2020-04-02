package com.coinbene.manbiwang.record.optionrecord;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.manbiwang.model.http.OptionsRecordMode;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.widget.wrapper.LoadMoreListener;
import com.coinbene.manbiwang.record.R;
import com.coinbene.manbiwang.record.R2;
import com.coinbene.manbiwang.record.optionrecord.adapter.OptionRecordAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;


/**
 * 猜涨跌交易记录
 */
public class OptionRecordActivity extends CoinbeneBaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R2.id.option_filter)
    CheckBox filter;
    @BindView(R2.id.option_record_RecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R2.id.option_Refresh)
    SwipeRefreshLayout mRefresh;
    @BindView(R2.id.option_record_back)
    ImageView mBack;
    @BindView(R2.id.option_record_title)
    TextView title;
    @BindView(R2.id.option_TitleBar)
    View titleBar;

    private OptionRecordAdapter adapter;
    private int currentPage = 1;
    private final int pageSize = 10;

    public static void startMe(Context context) {
        Intent intent = new Intent(context, OptionRecordActivity.class);
        context.startActivity(intent);
    }


    @Override
    public int initLayout() {
        return R.layout.record_activity_option_record;
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
        getOptionRecord();
    }

    @Override
    public boolean needLock() {
        return true;
    }


    public void init() {
        title.setText(R.string.option_record);
        mRefresh.setColorSchemeColors(getResources().getColor(R.color.res_blue));
        filter.setVisibility(View.GONE);
        setSwipeBackEnable(false);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        adapter = new OptionRecordAdapter();
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);

    }

    public void listener() {
        mBack.setOnClickListener(this);
        mRefresh.setOnRefreshListener(this);
        mRecyclerView.addOnScrollListener(new LoadMoreListener() {
            @Override
            public void loadMore() {
                currentPage++;
                getOptionRecord();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.option_record_back) {
            finish();
        }
    }



    @Override
    public void onRefresh() {
        currentPage = 1;
        getOptionRecord();
    }

    /**
     * 获取期权交易记录
     */
    private void getOptionRecord() {
        HttpParams params = new HttpParams();
        params.put("assetId", "");
        params.put("pageNum", currentPage);
        params.put("pageSize", pageSize);

        OkGo.<OptionsRecordMode>post(Constants.OPTIONS_RECORD).tag(this).params(params).execute(new NewJsonSubCallBack<OptionsRecordMode>() {

            @Override
            public void onSuc(Response<OptionsRecordMode> response) {

                OptionsRecordMode.DataBean data = response.body().getData();

                if (data != null &&  data.getList() != null) {
                    if (currentPage == 1) {
                        adapter.setItem( data.getList());
                    } else {
                        adapter.appendItem( data.getList());
                    }

                    if ( data.getList().size() < pageSize) {
                        adapter.setState(OptionRecordAdapter.LOADING_END);
                    } else {
                        adapter.setState(OptionRecordAdapter.LOADING);
                    }
                }else {
                    if (currentPage > 1)
                        adapter.setState(OptionRecordAdapter.LOADING_END);
                }
            }

            @Override
            public void onE(Response<OptionsRecordMode> response) {
                if (currentPage > 1) {
                    currentPage--;
                }
            }


            @Override
            public void onFinish() {
                super.onFinish();
                if (mRefresh != null && mRefresh.isRefreshing()) {
                    mRefresh.setRefreshing(false);
                }
            }
        });

    }
}
