package com.coinbene.manbiwang.record;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.rxjava.FlowControlStrategy;
import com.coinbene.common.utils.SiteController;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.widget.ItemDivider;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.service.record.RecordItem;
import com.coinbene.manbiwang.service.record.RecordType;

import butterknife.BindView;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * Created by june
 * on 2019-09-20
 */
@Route(path = RouteHub.Record.recordActivity)
public class RecordActivity extends CoinbeneBaseActivity {

	@BindView(R2.id.recycler_view)
	RecyclerView mRecyclerView;

	RecordGroupViewBinder recordGroupViewBinder;
	RecordItemViewBinder recordItemViewBinder;

	private MultiTypeAdapter mContentAdapter;

	private Items items;

	public static void startMe(Context context) {
		Intent intent = new Intent(context, RecordActivity.class);
		context.startActivity(intent);
	}

	@Override
	public int initLayout() {
		return R.layout.record_activity_record;
	}

	@Override
	public void initView() {
		mTopBar.setTitle(R.string.myself_record_label);

		mContentAdapter = new MultiTypeAdapter();
		recordGroupViewBinder = new RecordGroupViewBinder();
		recordItemViewBinder = new RecordItemViewBinder();

		mContentAdapter.register(String.class, recordGroupViewBinder);
		mContentAdapter.register(RecordItem.class, recordItemViewBinder);

		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.addItemDecoration(new ItemDivider(this));
		mRecyclerView.setAdapter(mContentAdapter);
	}

	@Override
	public void setListener() {
		mTopBar.setOnClickListener(v -> {
			long args = System.currentTimeMillis();
			doRequest(args);
		});
	}

	@AddFlowControl(strategy = FlowControlStrategy.throttleFirst, timeInterval = 3000)
	private void doRequest(long args) {
	}


	@Override
	public void initData() {
		notifyRecyclerView();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void notifyRecyclerView() {
		if (items == null) {
			items = new Items();
		}
		items.clear();

		//充币提币记录
		items.add(getString(R.string.rechargeAndreflect_histroy));

		items.add(new RecordItem(getString(R.string.recharge_coin), RecordType.RECHARGE));
		items.add(new RecordItem(getString(R.string.reflect_coin), RecordType.WITHDRAW));
		items.add(new RecordItem(getString(R.string.transfer_txt_new), RecordType.PLATFORM_TRANSFER));

		if (SwitchUtils.isOpenContract_Asset() || SwitchUtils.isOpenMarginAsset()) {
			items.add(new RecordItem(getString(R.string.transfer_record), RecordType.TRANSFER));
		}

		items.add(new RecordItem(getString(R.string.recrod_dispatch_text), RecordType.OTHER));

		//币币委托记录
		items.add(getString(R.string.entrust_history));

		items.add(new RecordItem(getString(R.string.cur_entrust_label), RecordType.SPOT_CURRENT_ORDER));
		items.add(new RecordItem(getString(R.string.history_entrust_label_new), RecordType.SPOT_HISTORY_ORDER));
		items.add(new RecordItem(getString(R.string.high_lever_order), RecordType.SPOT_HIGH_LEVER_ORDER));

		if (SiteController.getInstance().isMainSite()) {
			//杠杆委托记录
			items.add(getString(R.string.leverage_record));

			items.add(new RecordItem(getString(R.string.cur_entrust_label), RecordType.MARGIN_CURRENT_ORDER));
			items.add(new RecordItem(getString(R.string.history_entrust_label_new), RecordType.MARGIN_HISTORY_ORDER));

			//杠杆借币记录
			items.add(getString(R.string.leverage_loan_record));

			items.add(new RecordItem(getString(R.string.loan_current), RecordType.MARGIN_CURRENT_BORROW));
			items.add(new RecordItem(getString(R.string.loan_history), RecordType.MARGIN_HISTORY_BORROM));
		}
		if (SwitchUtils.isOpenContract_Asset()) {
			//合约委托记录
			items.add(getString(R.string.contract_commission_record));

			items.add(new RecordItem(getString(R.string.res_usdt_contract_record), RecordType.USDT_CONTRACT));
			items.add(new RecordItem(getString(R.string.res_btc_contract_record), RecordType.BTC_CONTRACT));
		}

		if (SwitchUtils.isOpenContract_Asset()) {
			//合约挖矿明细
			items.add(getString(R.string.res_contract_mining));

			items.add(new RecordItem(getString(R.string.res_contract_mining_detail), RecordType.MINING));
		}

//		if (SwitchUtils.isOpenOPT_Asset()) {
//			//猜涨跌交易记录
//			items.add(getString(R.string.option_record_title));
//
//			items.add(new RecordItem(getString(R.string.option_record), RecordType.OPTIONS));
//
//		}

		mContentAdapter.setItems(items);
		mContentAdapter.notifyDataSetChanged();
	}


	@Override
	public boolean needLock() {
		return true;
	}
}
