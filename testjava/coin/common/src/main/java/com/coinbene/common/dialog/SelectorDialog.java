package com.coinbene.common.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.R;
import com.coinbene.common.base.BaseViewHolder;
import com.coinbene.manbiwang.model.http.BottomSelectModel;
import com.coinbene.common.widget.ItemDivider;
import com.coinbene.common.balance.Product;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class SelectorDialog<T> extends BottomSheetDialog {

	private RecyclerView mRecyclerView;
	private View tvCancel;
	private MyAdapter myAdapter;
	private SelectListener listener;

	private T selectedData;

	List<T> datas;

	public SelectorDialog(@NonNull Context context) {
		super(context, R.style.CoinBene_BottomSheet);
		setContentView(R.layout.common_dialog_selector);
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
//		mRecyclerView.setScrollIndicators();
		myAdapter = new MyAdapter<T>();
		mRecyclerView.setAdapter(myAdapter);
		myAdapter.setDatas(datas);

		// 查找默认位置
		for (int i = 0; i < datas.size(); i++) {
			if (datas.get(i).equals(selectedData)) {
				myAdapter.setSelectPosition(i);
				selectedData = datas.get(i);
			}
		}
	}

	private void listener() {
		tvCancel.setOnClickListener(v -> dismiss());

		myAdapter.setSelectListener((product, positon) -> {
			myAdapter.setSelectPosition(positon);
			if (listener != null) {
				listener.onItemSelected(product, positon);
			}
			dismiss();
		});
	}

	public void setSelectListener(SelectListener<T> listener) {
		this.listener = listener;
	}

	public void setDefaultData(T data) {
		this.selectedData = data;

		// 查找默认位置
		if (data instanceof Product) {
			if (myAdapter != null && datas != null) {
				for (int i = 0; i < datas.size(); i++) {
					if (((Product) datas.get(i)).getType() == ((Product) selectedData).getType()) {
						myAdapter.setSelectPosition(i);
					}
				}
			}
		} else if (data instanceof String) {
			if (myAdapter != null && datas != null) {
				for (int i = 0; i < datas.size(); i++) {
					if (datas.get(i).equals(data)) {
						myAdapter.setSelectPosition(i);
					}
				}
			}
		}
	}

	public void setDefaultPosition(int position) {
		if (datas == null || position > datas.size()) {
			return;
		}
		this.selectedData = datas.get(position);
		if (myAdapter != null && datas != null) {
			myAdapter.setSelectPosition(position);
		}
	}

	public void setDatas(List<T> datas) {
		this.datas = datas;

		if (myAdapter != null) {
			myAdapter.setDatas(datas);
		}
	}

	public class MyAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

		private int mPosition = 0;
		private SelectListener<T> listener;
		private List<T> datas;

		@NonNull
		@Override
		public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			return new BaseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.common_item_selector_dialog, parent, false));
		}

		@Override
		public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {

			T data = datas.get(position);

			if (data instanceof Product) {
				holder.setText(R.id.tv_selector_item, ((Product) data).getProductName());

				switch (((Product) data).getTransferStatus()) {
					case Product.STATUS_TRANSFER_OFF:
						holder.setText(R.id.tv_item_status, R.string.res_disable_transfer);
						holder.setVisibility(R.id.tv_item_status, View.VISIBLE);
						break;
					case Product.STATUS_TRANSFER_IN:
						//转入可用，暂停划出
						holder.setText(R.id.tv_item_status, R.string.res_disable_transfer_out);
						holder.setVisibility(R.id.tv_item_status, View.VISIBLE);
						break;
					case Product.STATUS_TRANSFER_OUT:
						//转出可用，暂停划入
						holder.setText(R.id.tv_item_status, R.string.res_disable_transfer_in);
						holder.setVisibility(R.id.tv_item_status, View.VISIBLE);
						break;
					case Product.STATUS_TRANSFER_ON:
						holder.setVisibility(R.id.tv_item_status, View.GONE);
						break;
					default:
						holder.setVisibility(R.id.tv_item_status, View.GONE);
						break;
				}
			}
			if (data instanceof BottomSelectModel) {
				holder.setText(R.id.tv_selector_item, ((BottomSelectModel) data).getTypeName());
			} else {
				holder.setText(R.id.tv_selector_item, data.toString());
			}

			if (mPosition == position) {
				holder.setTextColor(R.id.tv_selector_item, R.color.res_blue);
			} else {
				holder.setTextColor(R.id.tv_selector_item, R.color.res_textColor_2);
			}

			holder.itemView.setOnClickListener(v -> {
				mPosition = position;
				if (listener != null) {
					listener.onItemSelected(data, position);
				}
			});
		}


		@Override
		public int getItemCount() {
			return datas == null ? 0 : datas.size();
		}

		public void setSelectPosition(int position) {
			this.mPosition = position;
			notifyDataSetChanged();
		}

		public void setDatas(List<T> datas) {
			this.datas = datas;
			notifyDataSetChanged();
		}

		public void setSelectListener(SelectListener listener) {
			this.listener = listener;
		}

		public List<T> getData() {
			return datas;
		}
	}

	public interface SelectListener<T> {
		void onItemSelected(T data, int positon);
	}
}
