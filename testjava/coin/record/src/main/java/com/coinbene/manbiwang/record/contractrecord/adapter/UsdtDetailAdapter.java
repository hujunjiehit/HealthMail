package com.coinbene.manbiwang.record.contractrecord.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.base.BaseViewHolder;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.manbiwang.model.http.ContractDetailMode;
import com.coinbene.manbiwang.record.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * ding
 * 2019-05-15
 * com.coinbene.manbiwang.modules.trade.contract.adapter
 */
public class UsdtDetailAdapter extends RecyclerView.Adapter<BaseViewHolder> {

	private SimpleDateFormat format;
	private Date date;
	private ContractUsdtInfoTable table;

	public UsdtDetailAdapter() {
		format = new SimpleDateFormat("MM-dd HH:mm:ss");

	}

	private Context context;
	private List<ContractDetailMode.DataBean.ListBean> list;

	@NonNull
	@Override
	public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		context = parent.getContext();
		return new BaseViewHolder(LayoutInflater.from(context).inflate(R.layout.record_item_contract_detail, parent, false));
	}

	@Override
	public void onBindViewHolder(BaseViewHolder holder, int position) {
		ContractDetailMode.DataBean.ListBean item = list.get(position);
		date = new Date(item.getTradeTime());
		holder.setText(R.id.time, format.format(date));
		holder.setText(R.id.transaction_Price, item.getOrderPrice());
		holder.setText(R.id.transaction_vol, String.format("%s%s", TradeUtils.getContractUsdtUnitValue(item.getQuantity(), table), TradeUtils.getContractUsdtUnit(table)));
	}

	@Override
	public int getItemCount() {
		return list == null ? 0 : list.size();
	}


	public void setItem(List<ContractDetailMode.DataBean.ListBean> list) {
		this.list = list;
		notifyDataSetChanged();
	}


	public void setTable(ContractUsdtInfoTable table) {
		this.table = table;
	}
}
