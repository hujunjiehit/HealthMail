package com.coinbene.manbiwang.record.contractrecord;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.manbiwang.model.http.ContractDetailMode;
import com.coinbene.manbiwang.model.http.HistoryDelegationModel;
import com.coinbene.common.network.newokgo.NewDialogJsonCallback;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.manbiwang.record.R;
import com.coinbene.manbiwang.record.R2;
import com.coinbene.manbiwang.record.contractrecord.adapter.UsdtDetailAdapter;
import com.coinbene.manbiwang.service.RouteHub;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.List;

import butterknife.BindView;

@Route(path = RouteHub.Record.contractUsdtOrderDetail)
public class OrderDetailUsdtActivity extends CoinbeneBaseActivity {

	@BindView(R2.id.back)
	ImageView back;
	@BindView(R2.id.part_cancel)
	TextView partCancel;
	@BindView(R2.id.detail_vol)
	TextView vol;
	@BindView(R2.id.detail_top_price)
	TextView price;
	@BindView(R2.id.detail_worth)
	TextView worth;
	@BindView(R2.id.detail_free)
	TextView free;

	@BindView(R2.id.detail_RecyclerView)
	RecyclerView mRecyclerView;
	@BindView(R2.id.direction_Symbol)
	TextView direction;
	@BindView(R2.id.contract_Symbol)
	TextView contract;

	static final String TAG = "OtcOrderDetailActivity";

	private HistoryDelegationModel.DataBean.ListBean bean;
	private UsdtDetailAdapter adapter;

	public static void startMe(Context context, HistoryDelegationModel.DataBean.ListBean bean) {
		Intent intent = new Intent(context, OrderDetailUsdtActivity.class);
		intent.putExtra("bean", bean);
		context.startActivity(intent);
	}

	@Override
	public int initLayout() {
		return R.layout.record_activity_order_detail_contract;
	}

	@Override
	public void initView() {
		if (getIntent() != null) {
			bean = (HistoryDelegationModel.DataBean.ListBean) getIntent().getSerializableExtra("bean");
			if (bean != null) {
				init();
				getDetail(bean.getOrderId(), bean.getDirection());
			}
		}

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


	private void init() {
		ContractUsdtInfoTable table = ContractUsdtInfoController.getInstance().queryContrackByName(bean.getSymbol());

		price.setText(String.format("%s/%s", bean.getAveragePrice(),
				Constants.TYPE_PLAN_MARKET.equals(bean.getOrderType()) ? "--" : bean.getOrderPrice()));
		free.setText(bean.getFee());
		vol.setText(String.format("%s/%s%s", TradeUtils.getContractUsdtUnitValue(bean.getFilledQuantity(), table),
				TradeUtils.getContractUsdtUnitValue(bean.getQuantity(), table), TradeUtils.getContractUsdtUnit(table)));
		worth.setText(bean.getOrderValue());
		setTagBackGround(direction, bean.getDirection());

		contract.setText(String.format(getResources().getString(R.string.forever_no_delivery), table.baseAsset));

		String s = "";
		String r = "";

		if ("partiallyFilled".equals(bean.getStatus()) || "partiallyCanceled".equals(bean.getStatus())) {
			s = getString(R.string.part_cancel);
			r = BigDecimalUtils.divideToPercentage(bean.getFilledQuantity(), bean.getQuantity());
		} else if ("canceled".equals(bean.getStatus())) {
			s = getString(R.string.cancel_all);
			r = BigDecimalUtils.divideToPercentage(bean.getFilledQuantity(), bean.getQuantity());
		} else if ("filled".equals(bean.getStatus())) {
			s = getString(R.string.all_deal);
			r = "100%";
		}

		partCancel.setText(String.format("%s %s", s, r));

		adapter = new UsdtDetailAdapter();
		adapter.setTable(table);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.setAdapter(adapter);

		back.setOnClickListener(v -> finish());
	}

	/**
	 * @param direction 设置方向背景
	 */
	private void setTagBackGround(TextView view, String direction) {

		boolean isRed = SwitchUtils.isRedRise();
		int lever = bean.getLeverage();
		//标签类型设置
		if (direction.equals("openLong")) {
			//开多
			view.setText(lever == 0 ? getResources().getString(R.string.open_long_v3) : String.format(getString(R.string.open_long_v2), lever));
			view.setBackgroundResource(isRed ? R.drawable.bg_red_sharp : R.drawable.bg_green_sharp);
		} else if (direction.equals("openShort")) {
			//开空
			view.setText(lever == 0 ? getResources().getString(R.string.open_short_v3) : String.format(getString(R.string.open_short_v2), lever));
			view.setBackgroundResource(isRed ? R.drawable.bg_green_sharp : R.drawable.bg_red_sharp);
		} else if (direction.equals("closeLong")) {
			//平多
			view.setText(getResources().getString(R.string.side_close_long));
			view.setBackgroundResource(isRed ? R.drawable.bg_green_sharp : R.drawable.bg_red_sharp);
		} else if (direction.equals("closeShort")) {
			//平空
			view.setText(getResources().getString(R.string.side_close_short));
			view.setBackgroundResource(isRed ? R.drawable.bg_red_sharp : R.drawable.bg_green_sharp);
		}

	}


	/**
	 * @param id
	 * @param direction 获取详情
	 */
	private void getDetail(String id, String direction) {

		if (TextUtils.isEmpty(id) || TextUtils.isEmpty(direction)) {
			return;
		}

		HttpParams params = new HttpParams();

		params.put("orderId", id);
		params.put("direction", direction);

		OkGo.<ContractDetailMode>get(Constants.CONTRACT_TRANSACTION_DETAIL_USDT).params(params).tag(this).execute(new NewDialogJsonCallback<ContractDetailMode>(this) {
			@Override
			public void onSuc(Response<ContractDetailMode> response) {

				ContractDetailMode.DataBean data = response.body().getData();

				if (data != null) {
					List<ContractDetailMode.DataBean.ListBean> list = data.getList();
					adapter.setItem(list);
				}

			}

			@Override
			public void onE(Response<ContractDetailMode> response) {

			}
		});

	}

}
