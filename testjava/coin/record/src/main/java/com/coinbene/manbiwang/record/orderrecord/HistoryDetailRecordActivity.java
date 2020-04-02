package com.coinbene.manbiwang.record.orderrecord;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.network.newokgo.NewDialogJsonCallback;
import com.coinbene.manbiwang.model.http.HisOrderModel;
import com.coinbene.manbiwang.record.R;
import com.coinbene.manbiwang.record.R2;
import com.coinbene.manbiwang.record.orderrecord.adapter.HisDetailHeaderBinder;
import com.coinbene.manbiwang.record.orderrecord.adapter.HistoryDetailItemBinder;
import com.coinbene.manbiwang.service.RouteHub;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.List;

import butterknife.BindView;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * Created by mengxiangdong on 2018/7/30.
 * 历史委托的详情页面
 */

@Route(path = RouteHub.Record.spotHisOrderDetail)
public class HistoryDetailRecordActivity extends CoinbeneBaseActivity {
	@BindView(R2.id.menu_title_tv)
	TextView titleView;

	@BindView(R2.id.menu_back)
	View backView;

	private MultiTypeAdapter mContentAdapter;
	@BindView(R2.id.data_list)
	RecyclerView recyclerView;

	private Items items;

	@Autowired
	String orderId;

	public static void startMe(Context context,String OrderId) {
		Intent intent = new Intent(context, HistoryDetailRecordActivity.class);
		intent.putExtra("orderId", OrderId);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public int initLayout() {
		return R.layout.record_trade_history_detail;
	}

	@Override
	public void initView() {
		titleView.setText(getText(R.string.history_detail_title));
		backView.setOnClickListener(this);

		orderId = getIntent().getStringExtra("orderId");
		if (TextUtils.isEmpty(orderId)) {
			finish();
			return;
		}
		items = new Items();
		initAdapter();
		getDetailInfo();
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

	private void initAdapter() {
		mContentAdapter = new MultiTypeAdapter();

		HisDetailHeaderBinder headerBinder = new HisDetailHeaderBinder();
		mContentAdapter.register(HisOrderModel.DataBean.class, headerBinder);

		HistoryDetailItemBinder itemBinder = new HistoryDetailItemBinder();
		mContentAdapter.register(HisOrderModel.DataBean.TradeRecordVoBean.class, itemBinder);

		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(mContentAdapter);
	}

	private void getDetailInfo() {
		HttpParams httpParams = new HttpParams();
		httpParams.put("orderId", orderId);
		OkGo.<HisOrderModel>get(Constants.TRADE_ITEM_DETAIL_LIST).tag(this).params(httpParams).execute(new NewDialogJsonCallback<HisOrderModel>(HistoryDetailRecordActivity.this) {
			@Override
			public void onSuc(Response<HisOrderModel> response) {
				HisOrderModel t = response.body();
				if (t.isSuccess()) {
					if (t.getData() == null) {
						return;
					}
					List<HisOrderModel.DataBean.TradeRecordVoBean> dataBeans = t.getData().getTradeRecordVo();
					items.clear();
					if (dataBeans != null && dataBeans.size() > 0) {
						items.add(t.getData());
						items.addAll(dataBeans);
					}
					mContentAdapter.setItems(items);
					mContentAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onE(Response<HisOrderModel> response) {
			}

		});
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.menu_back) {
			finish();
		}
	}
}
