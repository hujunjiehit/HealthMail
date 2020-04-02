package com.coinbene.manbiwang.spot.margin.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.database.MarginSymbolController;
import com.coinbene.common.database.MarginSymbolTable;
import com.coinbene.common.widget.SupportPopupWindow;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.margin.adapter.SymbolListBinder;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;


/**
 * @author huyong
 */
public class SymbolChangeWindow implements View.OnClickListener {
	private View mAnchor;
	private SupportPopupWindow mPopupWindow;
	boolean mDismissed = false;
	private View mPanel, llRoot;
	private RecyclerView recyclerView;
	private MultiTypeAdapter adapter;
	private List<String> datas;
	private SymbolListBinder symbolListBinder;

	/**
	 * @param anchor 显示在这个view下面
	 */
	public SymbolChangeWindow(View anchor) {
		mAnchor = anchor;

		int[] point = new int[2];
		mAnchor.getLocationInWindow(point);

		mPopupWindow = new SupportPopupWindow(anchor);
		mPopupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		mPopupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT - (point[1] + mAnchor.getHeight()));
		mPopupWindow.setFocusable(true);
		mPanel = LayoutInflater.from(mAnchor.getContext()).inflate(
				R.layout.spot_margin_change_pop_window, null);
		mPopupWindow.setContentView(mPanel);
		initView();
	}

	public void setOnItemClickContrckListener(SymbolListBinder.OnItemClickContrackListener onItemClickContrckListener) {
		symbolListBinder.setOnItemClickContrackListener(onItemClickContrckListener);
	}

	private void initView() {
		mPopupWindow.setOnDismissListener(this::dismissQuickActionBar);
		recyclerView = mPanel.findViewById(R.id.rlv_symbol);
		llRoot = mPanel.findViewById(R.id.ll_root);
		llRoot.setOnClickListener(v -> onDismiss());


		adapter = new MultiTypeAdapter();
		symbolListBinder = new SymbolListBinder();
		adapter.register(String.class, symbolListBinder);
		recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
		recyclerView.setAdapter(adapter);

		List<MarginSymbolTable> tables = MarginSymbolController.getInstance().querySymbolList();
		datas = new ArrayList<>();
		if (tables != null && tables.size() > 0) {
			for (int i = 0; i < tables.size(); i++) {
				MarginSymbolTable table = tables.get(i);
				if (table == null) {
					continue;
				}
				datas.add(table.symbol);
			}
			setData(datas);
		}
	}


	public void setData(List<String> datas) {
		adapter.setItems(datas);
		adapter.notifyDataSetChanged();
	}


	public void showBelowAnchor() {
		mDismissed = false;
		mPopupWindow.showAsDropDown(mAnchor);
	}

	@Override
	public void onClick(View v) {
		onDismiss();
	}


	public void onDismiss() {
		dismissQuickActionBar();

	}

	private void dismissQuickActionBar() {
		if (mDismissed) {
			return;
		}
		mDismissed = true;
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
		}
	}

	public SupportPopupWindow getPopupWindow() {
		return mPopupWindow;
	}


	public void setSelectSymbol(String symbol) {
		symbolListBinder.setSelect(symbol);
		adapter.notifyDataSetChanged();
	}
}