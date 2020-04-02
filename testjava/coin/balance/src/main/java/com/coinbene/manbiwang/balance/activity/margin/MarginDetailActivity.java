package com.coinbene.manbiwang.balance.activity.margin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.coinbene.common.Constants;
import com.coinbene.common.balance.Product;
import com.coinbene.common.balance.TransferParams;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.database.MarginSymbolController;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.router.UIRouter;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.common.widget.LoadMoreView;
import com.coinbene.common.widget.WrapperLinearLayoutManager;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;
import com.coinbene.manbiwang.balance.activity.margin.adapter.MarginDetailAdapter;
import com.coinbene.manbiwang.balance.activity.margin.adapter.MarginDetailItem;
import com.coinbene.manbiwang.model.http.MarginOpenOrderlistModel;
import com.coinbene.manbiwang.model.http.SingleAccountModel;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.service.record.RecordType;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.QMUIWindowInsetLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by june
 * on 2019-08-17
 * <p>
 * 杠杆账户详情页面
 */

@Route(path = RouteHub.Balance.marginDetail)
public class MarginDetailActivity extends CoinbeneBaseActivity {

	@BindView(R2.id.recycler_view)
	RecyclerView mRecyclerView;
	@BindView(R2.id.btn_borrow)
	QMUIRoundButton mBtnBorrow;
	@BindView(R2.id.btn_repay)
	QMUIRoundButton mBtnRepay;
	@BindView(R2.id.btn_transfer)
	QMUIRoundButton mBtnTransfer;
	@BindView(R2.id.top_bar)
	QMUITopBarLayout mTopBar;
	@BindView(R2.id.layout_top_bar)
	QMUIWindowInsetLayout mLayoutTopBar;
	@BindView(R2.id.view_bottom)
	View mViewBottom;
	@BindView(R2.id.swipe_refresh)
	SwipeRefreshLayout mRefreshView;

	private Unbinder mBind;

	private List<MarginDetailItem> list;
	private MarginDetailAdapter mMarginDetailAdapter;
	MarginDetailItem itemHeader;

	private int currentPage = 1;
	private final int PAZGE_SIZE = 20;

	@Autowired
	String symbol;


	public static void startMe(Context context, String symbol) {
		Intent intent = new Intent(context, MarginDetailActivity.class);
		intent.putExtra("symbol", symbol);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	private void init() {
		if (getIntent() == null || TextUtils.isEmpty(getIntent().getStringExtra("symbol"))) {
			finish();
			return;
		}
		symbol = getIntent().getStringExtra("symbol");
		mTopBar.setTitle(symbol);
		mTopBar.addLeftImageButton(R.drawable.res_icon_back, R.id.iv_left_image).setOnClickListener(v -> finish());
//		mTopBar.addRightTextButton(getString(R.string.res_history_borrow), R.id.tv_right_button).setOnClickListener(v -> ToastUtil.show("历史借币"));
		mTopBar.setBackgroundDividerEnabled(false);

		mRefreshView.setColorSchemeColors(getResources().getColor(R.color.res_blue));

		boolean isRedRise = SwitchUtils.isRedRise();
		if (isRedRise) {
			mBtnBorrow.setBackground(getDrawable(R.drawable.small_btn_red_bg));
			mBtnRepay.setBackground(getDrawable(R.drawable.small_btn_green_bg));
		} else {
			mBtnBorrow.setBackground(getDrawable(R.drawable.small_btn_green_bg));
			mBtnRepay.setBackground(getDrawable(R.drawable.small_btn_red_bg));
		}
		mBtnTransfer.setBackground(getDrawable(R.drawable.small_btn_blue_bg));

		list = new ArrayList<>();
		mMarginDetailAdapter = new MarginDetailAdapter(list);
		mRecyclerView.setLayoutManager(new WrapperLinearLayoutManager(this));
		mMarginDetailAdapter.setLoadMoreView(new LoadMoreView());
		mMarginDetailAdapter.bindToRecyclerView(mRecyclerView);
		mMarginDetailAdapter.disableLoadMoreIfNotFullPage();
		//允许加载更多
		mMarginDetailAdapter.setEnableLoadMore(true);

//		View view = LayoutInflater.from(this).inflate(R.layout.base_empty, null);
//		mMarginDetailAdapter.setEmptyView(view);
	}

	private void listener() {
		mBtnBorrow.setOnClickListener(v -> BorrowActivity.startMe(v.getContext(), symbol));
		mBtnRepay.setOnClickListener(v -> RepayActivity.startMe(v.getContext(), symbol));
		mBtnTransfer.setOnClickListener(v -> {
			if (symbol == null || MarginSymbolController.getInstance().querySymbolByName(symbol) == null) {
				return;
			}
			UIBusService.getInstance().openUri(this, UrlUtil.getCoinbeneUrl(UIRouter.HOST_TRANSFER),
					new TransferParams()
							.setAsset(MarginSymbolController.getInstance().querySymbolByName(symbol).base)
							.setFrom(Product.NAME_SPOT)
							.setTo(Product.NAME_MARGIN)
							.setToSymbol(symbol)
							.toBundle());
		});
		mMarginDetailAdapter.setOnItemChildClickListener((adapter, view, position) ->
				ServiceRepo.getRecordService().gotoRecord(this, RecordType.MARGIN_HISTORY_BORROM)
		);

		mMarginDetailAdapter.setOnLoadMoreListener(() -> {
			currentPage++;
			getOpenOrderList();
		}, mRecyclerView);

		mRefreshView.setOnRefreshListener(() -> doRefresh());
	}

	private void getData() {

	}

	@Override
	protected void onResume() {
		super.onResume();

		mRefreshView.post(() -> {
			mRefreshView.setRefreshing(true);
			doRefresh();
		});
	}

	@Override
	public int initLayout() {
		return R.layout.activity_margin_detail;
	}

	@Override
	public void initView() {
		ARouter.getInstance().inject(this);
		mBind = ButterKnife.bind(this);
		init();
	}

	@Override
	public void setListener() {
		listener();
	}

	@Override
	public void initData() {
		getData();
	}

	@Override
	public boolean needLock() {
		return true;
	}

	private void doRefresh() {
		currentPage = 1;
		getAccountSingle();
	}

	/**
	 * 获取指定交易对的账户列表
	 */
	private void getAccountSingle() {
		Map<String, String> parmas = new HashMap<>();
		parmas.put("symbol", symbol);
		OkGo.<SingleAccountModel>get(Constants.MARGIN_ACCOUNT_SINGLE).params(parmas).tag(this).execute(new NewJsonSubCallBack<SingleAccountModel>() {
			@Override
			public void onSuc(Response<SingleAccountModel> response) {
				if (response.body() != null) {
					if (itemHeader == null) {
						itemHeader = new MarginDetailItem(MarginDetailItem.TYPE_HEADER);
					}
					itemHeader.setSingleAccountModel(response.body());
					getOpenOrderList();
				}
			}

			@Override
			public void onE(Response<SingleAccountModel> response) {
				if (mRefreshView.isRefreshing()) {
					mRefreshView.setRefreshing(false);
				}
			}
		});
	}

	/**
	 * 获取未还清借币订单列表
	 */
	private void getOpenOrderList() {
		Map<String, String> parmas = new HashMap<>();
		parmas.put("symbol", symbol);
		parmas.put("pageNum", String.valueOf(currentPage));
		parmas.put("pageSize", String.valueOf(PAZGE_SIZE));
		OkGo.<MarginOpenOrderlistModel>get(Constants.MARGIN_OPEN_ORDERLIST).params(parmas).tag(this).execute(new NewJsonSubCallBack<MarginOpenOrderlistModel>() {
			@Override
			public void onSuc(Response<MarginOpenOrderlistModel> response) {
				if (response.body() != null && response.body().getData() != null) {

					List<MarginDetailItem> itemList = new ArrayList<>();
					if (response.body().getData().getList() == null || response.body().getData().getList().size() == 0) {
						if (currentPage == 1) {
							MarginDetailItem item = new MarginDetailItem(MarginDetailItem.TYPE_DETAIL_EMPTY);
							itemList.add(item);
						}
					} else {
						//遍历分页获取的数据
						for (MarginOpenOrderlistModel.DataBean.ListBean data : response.body().getData().getList()) {
							MarginDetailItem item = new MarginDetailItem(MarginDetailItem.TYPE_DETAIL);
							item.setOrderListItem(data);
							itemList.add(item);
						}
					}

					if (currentPage == 1) {
						itemList.add(0, itemHeader);
						mMarginDetailAdapter.setNewData(itemList);
					} else {
						mMarginDetailAdapter.addData(itemList);
					}

					if (response.body().getData().getList() == null || response.body().getData().getList().size() < PAZGE_SIZE) {
						//没有更多数据了
						if (currentPage == 1 && itemList.size() == 2) {
							//没有更多记录
							mMarginDetailAdapter.loadMoreEnd(itemList.get(1).getItemType() == MarginDetailItem.TYPE_DETAIL_EMPTY);
						} else {
							mMarginDetailAdapter.loadMoreEnd();
						}
					} else {
						mMarginDetailAdapter.loadMoreComplete();
					}
				} else if (response.body() != null && response.body().getData() != null && response.body().getData().getList() == null) {
					if (currentPage == 1) {
						//当前借币没有数据
						List<MarginDetailItem> itemList = new ArrayList<>();
						itemList.add(itemHeader);

						MarginDetailItem emptyItem = new MarginDetailItem(MarginDetailItem.TYPE_DETAIL);
						emptyItem.setOrderListItem(null);
						itemList.add(emptyItem);

						mMarginDetailAdapter.setNewData(itemList);
						mMarginDetailAdapter.loadMoreEnd(true);
					}
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
				if (mRefreshView.isRefreshing()) {
					mRefreshView.setRefreshing(false);
				}
			}

			@Override
			public void onE(Response<MarginOpenOrderlistModel> response) {

			}
		});
	}



}
