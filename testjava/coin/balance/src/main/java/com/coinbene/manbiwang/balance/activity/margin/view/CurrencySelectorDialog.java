package com.coinbene.manbiwang.balance.activity.margin.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.coinbene.common.widget.ItemDivider;
import com.coinbene.manbiwang.balance.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

/**
 * ding
 * 2019-08-16
 * com.coinbene.manbiwang.modules.tansfer.view
 */
public class CurrencySelectorDialog extends BottomSheetDialog {


	private View tvCancel;
	private RecyclerView mRecyclerView;
	private MyAdapter myAdapter;
	private CurrencySelect listener;
	private String currency;
	List<String> currencys;

	public CurrencySelectorDialog(@NonNull Context context) {
		super(context, R.style.CoinBene_BottomSheet);
		setContentView(R.layout.dialog_account_selector);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		listener();
	}

	private void init() {
		tvCancel = findViewById(R.id.tv_cancel);
		mRecyclerView = findViewById(R.id.select_RecyclerView);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mRecyclerView.addItemDecoration(new ItemDivider(getContext()));
		myAdapter = new CurrencySelectorDialog.MyAdapter();
		mRecyclerView.setAdapter(myAdapter);
		myAdapter.setCurrencys(currencys);
		for (int i = 0; i < currencys.size(); i++) {
			if (currencys.get(i).equals(currency)) {
				myAdapter.setSelectPosition(i);
			}
		}
	}

	private void listener() {
		tvCancel.setOnClickListener(v -> dismiss());
		myAdapter.setSelectListener((adapter, currency, positon) -> {
			if (listener != null) {
				listener.onSelected(adapter, currency, positon);
			}
			myAdapter.setSelectPosition(positon);
			dismiss();
		});
	}

	public void setSelectListener(CurrencySelect listener) {
		this.listener = listener;
	}

	public void setDefultCurrency(String currency) {
		this.currency = currency;

		//如果不为空就刷新
		if (myAdapter != null && currencys != null) {
			for (int i = 0; i < currencys.size(); i++) {
				if (currencys.get(i).equals(currency)) {
					myAdapter.setSelectPosition(i);
				}
			}
		}
	}

	public void setCurrencys(List<String> currencys) {
		this.currencys = currencys;
		if (myAdapter != null) {
			myAdapter.setCurrencys(currencys);
		}
	}

	public class MyAdapter extends RecyclerView.Adapter<BaseViewHolder> {

		private int mPosition = 0;
		private CurrencySelect listener;
		private List<String> currencys;

		@NonNull
		@Override
		public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			return new BaseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_select, parent, false));
		}

		@Override
		public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {

			holder.setText(R.id.tv_account_name, currencys.get(position));

			if (mPosition == position) {
				holder.setTextColor(R.id.tv_account_name, getContext().getResources().getColor(R.color.res_blue));
			} else {
				holder.setTextColor(R.id.tv_account_name, getContext().getResources().getColor(R.color.res_textColor_2));
			}

			holder.itemView.setOnClickListener(v -> {
				mPosition = position;
				if (listener != null) {
					listener.onSelected(this, currencys.get(position), position);
				}
			});
		}


		@Override
		public int getItemCount() {
			return currencys == null ? 0 : currencys.size();
		}

		public void setSelectPosition(int position) {
			this.mPosition = position;
			notifyDataSetChanged();
		}

		public void setCurrencys(List<String> currencys) {
			this.currencys = currencys;
			notifyDataSetChanged();
		}

		public void setSelectListener(CurrencySelect listener) {
			this.listener = listener;
		}

		public List<String> getData() {
			return currencys;
		}
	}


	public interface CurrencySelect {
		void onSelected(MyAdapter adapter, String currency, int positon);
	}
}
