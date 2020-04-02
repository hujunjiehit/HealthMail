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
public class CurrencyPairSelectorDialog extends BottomSheetDialog {


	private View tvCancel;
	private RecyclerView mRecyclerView;
	private MyAdapter myAdapter;
	private CurrencyPairSelect listener;
	List<String> currencyPairs;
	private String pair;

	public CurrencyPairSelectorDialog(@NonNull Context context) {
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
		myAdapter = new CurrencyPairSelectorDialog.MyAdapter();
		mRecyclerView.setAdapter(myAdapter);
		myAdapter.setCurrencyPairs(currencyPairs);

		if (currencyPairs != null) {
			for (int i = 0; i < currencyPairs.size(); i++) {
				if (currencyPairs.get(i).equals(pair)) {
					myAdapter.setSelectPosition(i);
				}
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

	public void setSelectListener(CurrencyPairSelect listener) {
		this.listener = listener;
	}

	public void setCurrencyPairs(List<String> currencyPairs) {
		this.currencyPairs = currencyPairs;
		if (myAdapter != null) {
			myAdapter.setCurrencyPairs(currencyPairs);
		}
	}

	public void setDefaulPair(String pair) {
		this.pair = pair;
		if (myAdapter != null && currencyPairs != null) {
			for (int i = 0; i < currencyPairs.size(); i++) {
				if (currencyPairs.get(i).equals(pair)) {
					myAdapter.setSelectPosition(i);
				}
			}
		}
	}

	public class MyAdapter extends RecyclerView.Adapter<BaseViewHolder> {

		private int mPosition = 0;
		private CurrencyPairSelect listener;
		private List<String> currencyPairs;

		@NonNull
		@Override
		public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			return new BaseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_select, parent, false));
		}

		@Override
		public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {

			holder.setText(R.id.tv_account_name, currencyPairs.get(position));

			if (mPosition == position) {
				holder.setTextColor(R.id.tv_account_name, getContext().getResources().getColor(R.color.res_blue));
			} else {
				holder.setTextColor(R.id.tv_account_name, getContext().getResources().getColor(R.color.res_textColor_2));
			}

			holder.itemView.setOnClickListener(v -> {
				mPosition = position;
				if (listener != null) {
					listener.onSelected(this, currencyPairs.get(position), position);
				}
			});
		}


		@Override
		public int getItemCount() {
			return currencyPairs == null ? 0 : currencyPairs.size();
		}

		public void setSelectPosition(int position) {
			this.mPosition = position;
			notifyDataSetChanged();
		}

		public void setCurrencyPairs(List<String> currencyPairs) {
			this.currencyPairs = currencyPairs;
			notifyDataSetChanged();
		}

		public void setSelectListener(CurrencyPairSelect listener) {
			this.listener = listener;
		}

		public List<String> getData() {
			return currencyPairs;
		}
	}


	public interface CurrencyPairSelect {
		void onSelected(MyAdapter adapter, String currencyPair, int positon);
	}
}
