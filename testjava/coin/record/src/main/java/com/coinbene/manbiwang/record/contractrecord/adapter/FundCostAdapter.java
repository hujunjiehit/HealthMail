package com.coinbene.manbiwang.record.contractrecord.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.coinbene.common.Constants;
import com.coinbene.common.base.BaseAdapter;
import com.coinbene.common.base.BaseViewHolder;
import com.coinbene.manbiwang.model.http.FundCostModel;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.manbiwang.record.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author ding
 * 资金费用
 */
public class FundCostAdapter extends BaseAdapter<FundCostModel.DataBean.ListBean> {

	private SimpleDateFormat format;
	private String symbleStr;
	private boolean isRedRise;
	private int greenDrawable, redDrawable;
	private int contractType;

	public FundCostAdapter() {
		super(R.layout.record_item_fund_cost);
		format = new SimpleDateFormat("MM-dd HH:mm:ss");
	}

	public void setContractType(int contractType) {
		this.contractType = contractType;
	}

	@Override
	protected void convert(BaseViewHolder holder, int position, FundCostModel.DataBean.ListBean item) {


		holder.setTypeface(R.id.fund_cost_contracts, R.font.roboto_medium);
		//合约类型
		holder.setText(R.id.fund_cost_contracts, String.format(symbleStr, item.getSymbol()));

		//时间
		holder.setText(R.id.fund_cost_time, format.format(new Date(Long.valueOf(item.getTime()))));

		//成交量
		holder.setText(R.id.fund_cost_vol, item.getPosition());

		//成交均价
		holder.setText(R.id.fund_cost_average, item.getMarkPrice());

		//价值
		holder.setText(R.id.fund_cost_value, item.getPositionValue());

		//费率
		holder.setText(R.id.fund_cost_rate, BigDecimalUtils.toPercentage(item.getFeeRate(), "0.0000%"));

		//已付费用
		holder.setText(R.id.fund_cost_feepaid, item.getFee());

		//资金费率文案
		holder.setText(R.id.textView25, getF8Desc(holder.itemView.getContext()));

		//金额文案
		holder.setText(R.id.textView26, contractType == Constants.CONTRACT_TYPE_BTC ? R.string.price_btc : R.string.price_usdt);

		//标签类型设置
		if (item.getSide().equals("long")) {//开多
			holder.setText(R.id.fund_cost_tag, R.string.long_text);
			holder.setBackGroundResource(R.id.fund_cost_tag, isRedRise ? redDrawable : greenDrawable);
		} else {
			holder.setText(R.id.fund_cost_tag, R.string.short_text);
			holder.setBackGroundResource(R.id.fund_cost_tag, isRedRise ? greenDrawable : redDrawable);
		}
	}

	//去掉资金费率后面的冒号
	private String getF8Desc(Context context) {
		String f8 = context.getResources().getString(R.string.f8);
		return f8.substring(0, f8.length() - 1);
	}

	@NonNull
	@Override
	public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		symbleStr = parent.getResources().getString(R.string.forever_no_delivery);
		greenDrawable = R.drawable.bg_green_sharp;
		redDrawable = R.drawable.bg_red_sharp;
		isRedRise = SwitchUtils.isRedRise();
		return super.onCreateViewHolder(parent, viewType);
	}


}
