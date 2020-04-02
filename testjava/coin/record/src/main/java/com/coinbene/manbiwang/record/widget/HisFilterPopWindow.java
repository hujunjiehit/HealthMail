package com.coinbene.manbiwang.record.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.database.MarginSymbolController;
import com.coinbene.common.database.MarginSymbolTable;
import com.coinbene.common.database.TradePairGroupController;
import com.coinbene.common.database.TradePairGroupTable;
import com.coinbene.common.database.TradePairInfoController;
import com.coinbene.manbiwang.model.http.SelectTradepairModel;
import com.coinbene.common.utils.AllCapTransformationMethod;
import com.coinbene.common.utils.DensityUtil;
import com.coinbene.common.utils.TimeUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.widget.SupportPopupWindow;
import com.coinbene.manbiwang.record.R;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

import java.util.ArrayList;
import java.util.List;


public class HisFilterPopWindow implements View.OnClickListener {
	private static final String TAG_ALL = "ALL";
	private Context context;
	private View mAnchor;
	private SupportPopupWindow mPopupWindow;
	boolean mDismissed = false;
	private LinearLayout mPanel;
	private static final int BG_VIEW_ID = 10;
	private HistoryFilterListener mListener;

	private ImageView quoteImageView;
	private LinearLayout ll_checkbox, llSpotFiter;
	private ToggleButton checkBox_cb;
	private EditText quote_asset_edit;
	private String accountType;

	public HisFilterPopWindow(View anchor, String accountType) {
		mAnchor = anchor;
		context = mAnchor.getContext();
		int[] point = new int[2];
		mAnchor.getLocationInWindow(point);
		this.accountType = accountType;

		mPopupWindow = new SupportPopupWindow(anchor);
		mPopupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		mPopupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT - (point[1] + mAnchor.getHeight()));
		mPopupWindow.setFocusable(true);// true，才能popupWindow消失

		LinearLayout mContentView = new LinearLayout(mAnchor.getContext());
		mContentView.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		mContentView.setBackgroundColor(mAnchor.getContext().getResources().getColor(
				android.R.color.transparent));

		ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
		mPopupWindow.setBackgroundDrawable(dw);

		mPopupWindow.setContentView(mContentView);
		mPopupWindow.setOnDismissListener(() -> dismissQuickActionBar());
		initView();
	}

	private void initView() {
		InputMethodManager imm = (InputMethodManager) mAnchor.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive() && mAnchor != null) {
			imm.hideSoftInputFromWindow(mAnchor.getWindowToken(), 0);
		}
		FrameLayout parent = new FrameLayout(mAnchor.getContext());
		parent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));

		View mBg = new View(mAnchor.getContext());
		mBg.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		mBg.setBackgroundColor(Color.argb(105, 0, 0, 0));
		mBg.setId(BG_VIEW_ID);

		mPanel = (LinearLayout) LayoutInflater.from(mAnchor.getContext()).inflate(
				R.layout.record_history_filter_popview, null);
		View rootLayout = mPanel.findViewById(R.id.root_layout);
		rootLayout.setOnClickListener(v -> {
		});
		View bgLayout = mPanel.findViewById(R.id.bg_layout);
		bgLayout.setOnClickListener(v -> onDismiss());

		parent.addView(mBg);
		parent.addView(mPanel);

		((ViewGroup) mPopupWindow.getContentView()).addView(parent);
		mPopupWindow.setOnDismissListener(() -> {
			if (mListener != null) {
				mListener.onPopWindowDimiss();
			}
		});

		initListener();

	}

	private List<TradePairGroupTable> groupTableList;
	private List<MarginSymbolTable> groupMarginTableList;
	private RadioGroup rgTradePairList, timeGroup, directionGroup;
	private EditText baseAsset_edit;

	private RecyclerView mTradepairRecyclerview;
	private TradePairSelectAdapter mTradepairAdapter;
	private List<SelectTradepairModel> tradePairList;
	private String selectedSymbol = "";
	private String lastSelectedSymbol = "";

	private void initListener() {
		quoteImageView = mPanel.findViewById(R.id.coin_type_img);
		View img_layout = mPanel.findViewById(R.id.img_layout);
		img_layout.setOnClickListener(this);
		ll_checkbox = mPanel.findViewById(R.id.ll_checkbox);
		ll_checkbox.setOnClickListener(this);
		checkBox_cb = mPanel.findViewById(R.id.tb_hide_other_coin);

		baseAsset_edit = mPanel.findViewById(R.id.baseAsset_edit);
		//设置大写
		baseAsset_edit.setTransformationMethod(new AllCapTransformationMethod(true));

		quote_asset_edit = mPanel.findViewById(R.id.quote_asset_edit);

		directionGroup = mPanel.findViewById(R.id.type_radiogroup);
		RadioButton type_all_btn = directionGroup.findViewById(R.id.all_btn);
		type_all_btn.setChecked(true);

		timeGroup = mPanel.findViewById(R.id.time_radiogroup);
		RadioButton time_all_btn = timeGroup.findViewById(R.id.all_time_btn);
		time_all_btn.setChecked(true);

		rgTradePairList = mPanel.findViewById(R.id.rg_trade_pair_list);
		mTradepairRecyclerview = mPanel.findViewById(R.id.tradepair_recycler_view);
		rgTradePairList.setOnCheckedChangeListener((group, checkedId) -> {
			RadioButton radioButton = group.findViewById(checkedId);
			if (radioButton != null) {
				String textTitle = radioButton.getText().toString();
				quote_asset_edit.setText(textTitle);
			}
		});

		llSpotFiter = mPanel.findViewById(R.id.ll_spot_fiter);

		if (TextUtils.isEmpty(accountType)) {//现货
			llSpotFiter.setVisibility(View.VISIBLE);
			rgTradePairList.setVisibility(View.GONE);
			mTradepairRecyclerview.setVisibility(View.GONE);
			groupTableList = TradePairGroupController.getInstance().getTradePairGroupsFilterContrack();
			if (groupTableList == null || groupTableList.size() == 0) {
				return;
			}

			RadioButton denomiRadio = rgTradePairList.findViewById(R.id.rb_trade_pair);
			denomiRadio.setChecked(true);
			denomiRadio.setTag(TAG_ALL);


			int marginLeft = DensityUtil.dip2px(15);
			for (int i = 0; i < groupTableList.size(); i++) {
				TradePairGroupTable groupTable = groupTableList.get(i);
				if (groupTable.groupName.equals(TradePairGroupTable.SELF_GROUP)) {
					//去掉自选的名字
					continue;
				}
				RadioButton radioButton = new RadioButton(context);
				RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(
						0, RadioGroup.LayoutParams.MATCH_PARENT);
				lp.weight = 1;
				lp.setMargins(marginLeft, 0, 0, 0);
				radioButton.setLayoutParams(lp);

				radioButton.setBackgroundResource(R.drawable.btn_blue_selector);
				radioButton.setButtonDrawable(null);
				radioButton.setText(groupTable.groupName);
				radioButton.setTag(groupTable.groupName);
				radioButton.setGravity(Gravity.CENTER);
				ColorStateList stateList = context.getResources().getColorStateList(R.color.res_selector_text_color);
				radioButton.setTextColor(stateList);
				radioButton.setTextSize(13);
				rgTradePairList.addView(radioButton);
			}
		} else {//杠杆
			llSpotFiter.setVisibility(View.GONE);
			rgTradePairList.setVisibility(View.GONE);
			mTradepairRecyclerview.setVisibility(View.VISIBLE);

			groupMarginTableList = MarginSymbolController.getInstance().querySymbolList();
			if (groupMarginTableList == null || groupMarginTableList.size() == 0) {
				return;
			}

			mTradepairRecyclerview.setLayoutManager(new GridLayoutManager(context, 3));
			int totalWidth = QMUIDisplayHelper.getScreenWidth(context) - QMUIDisplayHelper.dp2px(context, 30);
			int itemWidth = QMUIDisplayHelper.dp2px(context, 100);
			mTradepairRecyclerview.addItemDecoration(new CustomSpaceItemDecoration(context, (totalWidth - itemWidth * 3)/6));
			mTradepairAdapter = new TradePairSelectAdapter();
			mTradepairAdapter.bindToRecyclerView(mTradepairRecyclerview);

			if (tradePairList == null) {
				tradePairList = new ArrayList<>();
			}
			tradePairList.clear();
			SelectTradepairModel all = new SelectTradepairModel();
			all.setChecked(true);
			all.setTradePairName(context.getResources().getString(R.string.all_coin_title));
			tradePairList.add(all);
			for (int i = 0; i < groupMarginTableList.size(); i++) {
				SelectTradepairModel model = new SelectTradepairModel();
				model.setChecked(false);
				model.setTradePairName(groupMarginTableList.get(i).symbol);
				tradePairList.add(model);
			}
			mTradepairAdapter.setNewData(tradePairList);

			mTradepairAdapter.setOnItemChildClickListener((adapter, view, position) -> {
				for(int i = 0; i < tradePairList.size(); i++) {
					if (i == position) {
						selectedSymbol = ((SelectTradepairModel)adapter.getItem(i)).getTradePairName();
						((SelectTradepairModel)adapter.getItem(i)).setChecked(true);
					} else {
						((SelectTradepairModel)adapter.getItem(i)).setChecked(false);
					}
				}
				adapter.notifyDataSetChanged();
			});
		}

		TextView resetBtn = mPanel.findViewById(R.id.reset_btn);
		resetBtn.setOnClickListener(this);
		TextView okBtn = mPanel.findViewById(R.id.ok_btn);
		okBtn.setOnClickListener(this);
	}

	public void showBelowAnchor() {
		mDismissed = false;
		int yoff = DensityUtil.dip2px(10);
		mPopupWindow.showAsDropDown(mAnchor, 0, yoff);
		reset();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.img_layout) {
			boolean isSelected = quoteImageView.isSelected();
			isSelected = !isSelected;
			quoteImageView.setSelected(isSelected);
			if (isSelected) {
				rgTradePairList.setVisibility(View.VISIBLE);
			} else {
				rgTradePairList.setVisibility(View.GONE);
			}
		} else if (v.getId() == R.id.ll_checkbox) {
			boolean isSelected = checkBox_cb.isChecked();
			isSelected = !isSelected;
			checkBox_cb.setChecked(isSelected);
		} else if (v.getId() == R.id.reset_btn) {
			resetButton();
		} else if (v.getId() == R.id.ok_btn) {
			doSubmmit();
		}
	}

	private void resetButton() {
		baseAsset_edit.setText("");
		quote_asset_edit.setText(R.string.history_filter_all_label);

		RadioButton denomiRBtn = rgTradePairList.findViewById(R.id.rb_trade_pair);
		denomiRBtn.setChecked(true);

		RadioButton type_all_btn = directionGroup.findViewById(R.id.all_btn);
		type_all_btn.setChecked(true);

		RadioButton time_all_btn = timeGroup.findViewById(R.id.all_time_btn);
		time_all_btn.setChecked(true);

		checkBox_cb.setChecked(false);

		if (tradePairList != null) {
			//点击重置，恢复默认设置
			for(int i = 0; i < tradePairList.size(); i++) {
				if (i == 0) {
					tradePairList.get(i).setChecked(true);
					selectedSymbol = "";
				} else {
					tradePairList.get(i).setChecked(false);
				}
			}
			mTradepairAdapter.setNewData(tradePairList);
		}
	}

	private void doSubmmit() {
		String inputBaseAsset = baseAsset_edit.getText().toString();

		int radioBtn_demoni = rgTradePairList.getCheckedRadioButtonId();
		RadioButton checkedBtn = rgTradePairList.findViewById(rgTradePairList.getCheckedRadioButtonId());
		String quoteAssetTag = "";
		if (checkedBtn != null) {
			quoteAssetTag = (String) checkedBtn.getTag();
			if (TAG_ALL.equals(checkedBtn.getTag())) {
				quoteAssetTag = "";
			}
		}

		//如果是杠杆，设置选择的交易对
		if ("margin".equals(accountType)) {
			if (TextUtils.isEmpty(selectedSymbol) || !selectedSymbol.contains("/")) {
				quoteAssetTag = "";
			} else {
				quoteAssetTag = selectedSymbol;
			}
			lastSelectedSymbol = selectedSymbol;
		}

		int timeSelectedId = timeGroup.getCheckedRadioButtonId();

		boolean ignoreCancelled = checkBox_cb.isChecked();
		String beginTime = "", endTime = "";

		int typeBtnId = directionGroup.getCheckedRadioButtonId();
		//买卖的方向
		int tradeDirection = 0;
		if (typeBtnId == R.id.buy_btn) {
			tradeDirection = 1;
		} else if (typeBtnId == R.id.sell_btn) {
			tradeDirection = 2;
		}
		//如果没有任何变化，直接关闭
		if (TextUtils.isEmpty(inputBaseAsset) && TextUtils.isEmpty(quoteAssetTag) && tradeDirection == 0 && timeSelectedId == R.id.all_time_btn && !ignoreCancelled) {
			if (mListener != null) {

				last_inputBaseAsset = inputBaseAsset;
				last_radioBtn_quoteAssetId = radioBtn_demoni;
				last_timeSelectedId = timeSelectedId;
				last_tradeDirection = typeBtnId;
				last_ignoreCancelled = ignoreCancelled;

				mListener.doHttpFilter(inputBaseAsset, quoteAssetTag, beginTime, endTime, tradeDirection, ignoreCancelled);
				onDismiss();
			}
			onDismiss();
			return;
		}

		//如果输入了分子(币种)
		if (!TextUtils.isEmpty(inputBaseAsset)) {
			//如果分母也没有选择，默认是全部
			boolean isExist;
			if (TextUtils.isEmpty(quoteAssetTag)) {
				isExist = TradePairInfoController.getInstance().isBaseAssetInDataBase(inputBaseAsset.trim());
			} else {
				isExist = TradePairInfoController.getInstance().isTradePairInDataBase(inputBaseAsset.trim() + quoteAssetTag);
			}
			if (!isExist) {
				ToastUtil.show(R.string.invalid_input_baseAsset);
				//暂无记录
				if (mListener != null) {
					last_inputBaseAsset = inputBaseAsset;
					last_radioBtn_quoteAssetId = radioBtn_demoni;
					last_timeSelectedId = timeSelectedId;
					last_tradeDirection = typeBtnId;
					last_ignoreCancelled = ignoreCancelled;
					mListener.invalidTradepair();
				}
				onDismiss();
				return;
			}
		}

		if (timeSelectedId == R.id.three_month_btn) {
			beginTime = "" + TimeUtils.getN_MonthBefore(3);
		} else if (timeSelectedId == R.id.one_month_btn) {
			beginTime = "" + TimeUtils.getN_MonthBefore(1);
		} else if (timeSelectedId == R.id.one_week_btn) {
			beginTime = "" + TimeUtils.getN_WeekBefore(1);
		} else if (timeSelectedId == R.id.one_day_btn) {
			beginTime = "" + TimeUtils.getNDayBefore_new(1);
		}
		endTime = "" + System.currentTimeMillis();
		//只有点击确定，所有的条件才生效；重置，空白处取消，都不会生效；
		if (mListener != null) {
			last_inputBaseAsset = inputBaseAsset;
			last_radioBtn_quoteAssetId = radioBtn_demoni;
			last_timeSelectedId = timeSelectedId;
			last_tradeDirection = typeBtnId;
			last_ignoreCancelled = ignoreCancelled;
			mListener.doHttpFilter(inputBaseAsset, quoteAssetTag, beginTime, endTime, tradeDirection, ignoreCancelled);
			onDismiss();
		}
	}

	private String last_inputBaseAsset;
	private int last_radioBtn_quoteAssetId;
	private int last_timeSelectedId, last_tradeDirection;
	private boolean last_ignoreCancelled;

	/**
	 * 点开后重新设置上一次的条件;
	 */
	private void reset() {
		if (!TextUtils.isEmpty(last_inputBaseAsset)) {
			baseAsset_edit.setText(last_inputBaseAsset);
		}
		if (last_radioBtn_quoteAssetId != 0) {
			rgTradePairList.check(last_radioBtn_quoteAssetId);
		}
		if (last_timeSelectedId != 0) {
			timeGroup.check(last_timeSelectedId);
		}
		if (last_tradeDirection != 0) {
			directionGroup.check(last_tradeDirection);
		}
		checkBox_cb.setChecked(last_ignoreCancelled);

		if ("margin".equals(accountType)) {
			if (!TextUtils.isEmpty(lastSelectedSymbol)) {
				//恢复上一次选中的杠杆交易对
				for(int i = 0; i < tradePairList.size(); i++) {
					if (tradePairList.get(i).getTradePairName().equals(lastSelectedSymbol)) {
						tradePairList.get(i).setChecked(true);
					} else {
						tradePairList.get(i).setChecked(false);
					}
				}
			} else {
				//默认选中全部
				for(int i = 0; i < tradePairList.size(); i++) {
					if (i == 0) {
						tradePairList.get(i).setChecked(true);
					} else {
						tradePairList.get(i).setChecked(false);
					}
				}

			}
			mTradepairAdapter.setNewData(tradePairList);
		}

	}

	public void setOnFilterListener(HistoryFilterListener historyFilterListener) {
		this.mListener = historyFilterListener;
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

	public boolean isShow() {
		return !mDismissed;
	}
}