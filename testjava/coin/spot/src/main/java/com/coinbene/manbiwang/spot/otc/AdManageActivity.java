package com.coinbene.manbiwang.spot.otc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.manbiwang.model.http.SellerAdModel;
import com.coinbene.common.network.newokgo.NewJsonCallback;
import com.coinbene.common.widget.wrapper.LoadMoreListener;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.R2;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author ding
 * 广告管理
 */

public class AdManageActivity extends CoinbeneBaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R2.id.menu_back)
    RelativeLayout imgBack;

    @BindView(R2.id.menu_title_tv)
    TextView tvTitle;

    @BindView(R2.id.ad_RecyclerView)
    RecyclerView mRecyclerView;

    @BindView(R2.id.ad_refresh)
    SwipeRefreshLayout mRefresh;

    @BindView(R2.id.menu_right_tv)
    TextView tvRight;

    /**
     * 当前分页
     */
    private int curPage = 1;

    /**
     * 分页请求数量
     */
    private int pageSize = 10;

    private SellerOtcAdapter adapter;

    public static void startMe(Context context) {
        Intent intent = new Intent(context, AdManageActivity.class);
        context.startActivity(intent);
    }


    @Override
    public int initLayout() {
        return R.layout.spot_activity_ad_manage;
    }

    @Override
    public void initView() {
        init();
    }

    @Override
    public void setListener() {
        listenner();
    }

    @Override
    public void initData() {

    }

    @Override
    public boolean needLock() {
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        //下架成功返回刷新
//        if (resultCode == AdEditorActivity.NOTIFY_REFRESH
//                && requestCode == AdEditorActivity.REQUEST_CODE) {
//            onRefresh();
//        }

    }

    /**
     * 初始化放这里
     */
    public void init() {
        tvTitle.setText(R.string.ad_manage_title);
        tvRight.setText(R.string.new_ad_text);
        tvRight.setTextColor(Color.parseColor("#3b7bfd"));
        mRefresh.setColorSchemeColors(getResources().getColor(R.color.res_blue));

        adapter = new SellerOtcAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);

    }


    public void listenner() {
        imgBack.setOnClickListener(this);
        tvRight.setOnClickListener(this);
        mRefresh.setOnRefreshListener(this);
        mRecyclerView.addOnScrollListener(new LoadMoreListener() {
            @Override
            public void loadMore() {
                curPage++;
                getData();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        onRefresh();
    }

    /**
     * 获得数据
     */
    public void getData() {

        HttpParams httpParams = new HttpParams();
        httpParams.put("curPage", curPage);
        httpParams.put("pageSize", pageSize);

        OkGo.<SellerAdModel>post(Constants.SELLER_ADVERTISEMENT).params(httpParams).tag(this).execute(new NewJsonCallback<SellerAdModel>() {
            @Override
            public void onSuccess(Response<SellerAdModel> response) {


                SellerAdModel.DataBean data = response.body().getData();

                if (data == null) {
                    return;
                }

                List<SellerAdModel.DataBean.ListBean> list = data.getList();

//                if (list == null || list.size() == 0) {
//                    return;
//                }

                if (curPage == 1) {
                    adapter.setItem(data.getList());
                } else {
                    adapter.appendItem(data.getList());
                }


                if (list.size() < pageSize) {
                    adapter.setState(SellerOtcAdapter.LOADING_END);
                } else {
                    adapter.setState(SellerOtcAdapter.LOADING);
                }


            }

            @Override
            public void onError(Response<SellerAdModel> response) {
                super.onError(response);
                if (curPage > 0) {
                    curPage--;
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


    /**
     * @param v 点击事件在这里
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();//返回
        if (id == R.id.menu_back) {
            finish();
        } else if (id == R.id.menu_right_tv) {
            AddOrderActivity.startMe(this);
        }
    }

    /**
     * 刷新功能
     */
    @Override
    public void onRefresh() {
        curPage = 1;
        getData();
    }
}
