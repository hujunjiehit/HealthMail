package com.coinbene.manbiwang.market.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.database.TradePairInfoTable;
import com.coinbene.common.database.TradePairOptionalController;
import com.coinbene.common.utils.ListUtils;
import com.coinbene.manbiwang.market.R;
import com.coinbene.manbiwang.market.R2;
import com.coinbene.manbiwang.market.activity.adapter.SelfItemBinder;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.service.market.MarketService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * Created by june
 * on 2019-09-02
 */
public class EditOptionalActivity extends CoinbeneBaseActivity implements SelfItemBinder.OnItemClickLisener {


	@BindView(R2.id.empty_layout)
	LinearLayout empty_layout;
	@BindView(R2.id.rl_self_list)
	RecyclerView rl_self_list;
	@BindView(R2.id.rl_top)
	RelativeLayout rl_top;
	@BindView(R2.id.ll_root)
	LinearLayout ll_root;

	private MultiTypeAdapter mContentAdapter;
	private List<TradePairInfoTable> queryResult;
	private String beforeList;

	public static void startMe(Context context) {
		Intent intent = new Intent(context, EditOptionalActivity.class);
		context.startActivity(intent);
	}


	@Override
	public int initLayout() {
		return R.layout.market_edit_optional;
	}

	@Override
	public void initView() {
		mTopBar.setTitle(R.string.edit_self);

		setRightTextForTitleBar(getString(R.string.menu_name_done)).setOnClickListener(v -> editTradePairs());

		mContentAdapter = new MultiTypeAdapter();
		SelfItemBinder selfItemBinder = new SelfItemBinder();
		selfItemBinder.setOnItemClickLisener(this);
		mContentAdapter.register(TradePairInfoTable.class, selfItemBinder);

		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		rl_self_list.setLayoutManager(linearLayoutManager);
		rl_self_list.setAdapter(mContentAdapter);

		ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
			@Override
			public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
				final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
				final int swipeFlags = 0;
				return makeMovementFlags(dragFlags, swipeFlags);
			}

			@Override
			public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
				int fromPosition = viewHolder.getAdapterPosition();
				//拿到当前拖拽到的item的viewHolder
				int toPosition = target.getAdapterPosition();
				if (fromPosition < toPosition) {
					for (int i = fromPosition; i < toPosition; i++) {
						Collections.swap(mContentAdapter.getItems(), i, i + 1);
					}
				} else {
					for (int i = fromPosition; i > toPosition; i--) {
						Collections.swap(mContentAdapter.getItems(), i, i - 1);
					}
				}
				mContentAdapter.notifyItemMoved(fromPosition, toPosition);
				return true;
			}

			@Override
			public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

			}
		});
		mItemTouchHelper.attachToRecyclerView(rl_self_list);
	}

	@Override
	public void setListener() {

	}

	@Override
	public void initData() {
		queryResult = TradePairOptionalController.getInstance().queryTradePairOptional();
		if (queryResult == null || queryResult.size() == 0) {
			empty_layout.setVisibility(View.VISIBLE);
			rl_top.setVisibility(View.GONE);
			ll_root.setBackgroundColor(getResources().getColor(R.color.res_background));
			return;
		}
		beforeList = ListUtils.listToStringOption(queryResult);
		mContentAdapter.setItems(queryResult);
		mContentAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean needLock() {
		return false;
	}

	@Override
	protected void onBack() {
		String afterList = ListUtils.listToStringOption(queryResult);
		if (TextUtils.isEmpty(beforeList) || beforeList.equals(afterList)) {
			finish();
		} else {
			AlertDialog.Builder dialog = new AlertDialog.Builder(EditOptionalActivity.this);
			dialog.setTitle(R.string.is_save_change);
			dialog.setCancelable(false);
			dialog.setPositiveButton(R.string.btn_ok, (dialog12, which) -> editTradePairs());
			dialog.setNegativeButton(getString(R.string.btn_cancel), (dialog1, which) -> finish());
			dialog.show();
		}
	}

	//批量编辑自选
	public void editTradePairs() {
		if (ServiceRepo.getMarketService() != null) {
			List<String> tradePairList = new ArrayList<>();
			for(TradePairInfoTable item : queryResult) {
				tradePairList.add(item.tradePair);
			}
			ServiceRepo.getMarketService().editOptionalBatch(EditOptionalActivity.this, tradePairList, new MarketService.CallBack() {
				@Override
				public void onSuccess() {
					finish();
				}

				@Override
				public void onFailed() {

				}
			});
		}
	}

	@Override
	public void itemClick(int position) {

	}

	@Override
	public void delete(int position) {
		if (position < 0) {
			return;
		}
		mContentAdapter.notifyItemRemoved(position);
		queryResult.remove(position);
	}

	@Override
	public void toTop(int position) {
		mContentAdapter.notifyItemMoved(position, 0);
		replaceListPosition(position, 0);
		rl_self_list.scrollToPosition(0);
	}

	public void replaceListPosition(int fromPosition, int toPosition) {
		if (fromPosition > toPosition) {//上移
			queryResult.add(toPosition, queryResult.get(fromPosition));
			queryResult.remove(fromPosition + 1);
		} else if (fromPosition < toPosition) {//下移
			queryResult.add(toPosition, queryResult.get(fromPosition));
			queryResult.remove(fromPosition);
		}
	}

}
