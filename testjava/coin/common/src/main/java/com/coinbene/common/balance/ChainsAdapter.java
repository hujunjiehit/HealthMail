package com.coinbene.common.balance;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.R;
import com.coinbene.common.database.BalanceChainTable;

import java.util.List;

/**
 *
 */
public class ChainsAdapter extends RecyclerView.Adapter<ChainsAdapter.MyHolder> {


	public static final int DEPOSIT_TYPE = 1;
	public static final int WITHDARW_TYPE = 2;
	public static final int ADD_WITHDARW_TYPE = 3;
	private int checkUsed = 1;//1充值   2 提现
	private List<BalanceChainTable> lists;
	private String select;
	private OnItemClickLisenter onItemClickLisenter;
	private int selectBack, defaultBack, cantCLickBack;
	private int selectColor, defaultColor, cantCLickColor;

	public void setLists(List<BalanceChainTable> lists, String select) {
		this.lists = lists;
		this.select = select;
		notifyDataSetChanged();

	}

	public ChainsAdapter(int checkUsed) {
		this.checkUsed = checkUsed;
	}

	public String getSelect() {
		return select;
	}

	public String getSelectChain() {
		return select;
	}

	public void setSelect(String select) {
		this.select = select;
		notifyDataSetChanged();
	}

	@NonNull
	@Override
	public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chain, parent, false);
		selectBack = R.drawable.chain_select;
		defaultBack = R.drawable.chain_default;
		cantCLickBack = R.drawable.chain_cant_click;
		selectColor = parent.getResources().getColor(R.color.res_blue);
		defaultColor = parent.getResources().getColor(R.color.res_textColor_1);
		cantCLickColor = parent.getResources().getColor(R.color.res_textColor_3);
		MyHolder holder = new MyHolder(itemView);
//        ViewGroup.LayoutParams params = holder.clRoot.getLayoutParams();
//        params.height = DensityUtil.dip2px(40);
//        params.width = (DensityUtil.getScreenWidth() - DensityUtil.dip2px(60)) / 3;
		return holder;
	}

	@Override
	public void onBindViewHolder(@NonNull MyHolder holder, int position) {
		holder.tvCoin.setText(lists.get(position).protocolName);
		boolean checkType;
		//如果是充值用 deposit ,提现用withdraw  添加提币地址不需要判断
		if (checkUsed == 1) {
			checkType = lists.get(position).deposit;
		} else if (checkUsed == 2) {
			checkType = lists.get(position).withdraw;
		} else {
			checkType = true;
		}
		if (lists.get(position) != null && checkType) {
			if (lists.get(position) != null && !TextUtils.isEmpty(select) && lists.get(position).protocolName.equals(select)) {
				holder.clRoot.setBackgroundResource(selectBack);
				holder.tvCoin.setTextColor(selectColor);
				holder.ivSelect.setVisibility(View.VISIBLE);
			} else {
				holder.clRoot.setBackgroundResource(defaultBack);
				holder.tvCoin.setTextColor(defaultColor);
				holder.ivSelect.setVisibility(View.GONE);
			}
			if (onItemClickLisenter != null) {
				holder.clRoot.setOnClickListener(view -> onItemClickLisenter.onItemClick(lists.get(position).protocolName, position));
			}
		} else {
			holder.clRoot.setBackgroundResource(cantCLickBack);
			holder.tvCoin.setTextColor(cantCLickColor);
			holder.ivSelect.setVisibility(View.GONE);
		}

	}

	@Override
	public int getItemCount() {
		return lists.size();
	}

	public void setOnItemClickLisenter(OnItemClickLisenter onItemClickLisenter) {
		this.onItemClickLisenter = onItemClickLisenter;
	}

	public class MyHolder extends RecyclerView.ViewHolder {
		private TextView tvCoin;
		private View clRoot;
		private ImageView ivSelect;

		MyHolder(View itemView) {
			super(itemView);
			tvCoin = itemView.findViewById(R.id.tv_coin);
			clRoot = itemView.findViewById(R.id.cl_root);
			ivSelect = itemView.findViewById(R.id.iv_select);
		}
	}

	public interface OnItemClickLisenter {
		void onItemClick(String selectStr, int position);
	}

}
