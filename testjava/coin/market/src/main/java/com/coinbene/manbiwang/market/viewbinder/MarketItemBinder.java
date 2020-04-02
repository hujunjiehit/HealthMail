package com.coinbene.manbiwang.market.viewbinder;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.PostPointHandler;
import com.coinbene.common.database.TradePairGroupTable;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.StringUtils;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.manbiwang.market.R;
import com.coinbene.manbiwang.market.R2;
import com.coinbene.manbiwang.model.http.TradePairMarketRes;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;


/**
 * @author KG on 2017/6/14.
 */

public class MarketItemBinder extends ItemViewBinder<TradePairMarketRes.DataBean, MarketItemBinder.ViewHolder> {


	private Drawable redColor, greenColor;

	private boolean isRedRise;

	private String v24, number;

	private String mGroupName;
	private String forEver;

	public MarketItemBinder() {

	}

	@NonNull
	@Override
	protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
		View root = inflater.inflate(R.layout.market_item_layout, parent, false);

		redColor = root.getContext().getResources().getDrawable(R.drawable.market_item_percent_red_bg);
		greenColor = root.getContext().getResources().getDrawable(R.drawable.market_item_percent_green_bg);
		forEver = root.getContext().getResources().getString(R.string.forever_no_delivery);

//        volStr = root.getContext().getString(R.string.vol_market_item);
		v24 = root.getContext().getString(R.string.v24);
		number = root.getContext().getString(R.string.number);
		return new ViewHolder(root);
	}


	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull TradePairMarketRes.DataBean item) {
		isRedRise = SpUtil.isRedRise();

		if (mGroupName.equals(TradePairGroupTable.CONTRACT_GROUP)) {
			//设置合约数据
			setContractData(holder, item);
		} else {
			//设置现货数据
			setSpotData(holder, item);
		}
	}

	/**
	 * 自选和现货设置数据
	 *
	 * @param holder
	 * @param item
	 */
	private void setSpotData(ViewHolder holder, TradePairMarketRes.DataBean item) {

		holder.mIvOptionalMark.setVisibility(item.isOptional() ? View.VISIBLE : View.GONE);
//		holder.mIvHotMark.setVisibility(item.isHot() ? View.VISIBLE : View.GONE);
//		holder.mIvNewMark.setVisibility(item.isLatest() ? View.VISIBLE : View.GONE);

		String fenmu = "";
		if (!TextUtils.isEmpty(item.getName()) && item.getName().contains("/")) {
			holder.mTvLeftTop.setText(item.getName().substring(0, item.getName().indexOf("/")));
			holder.mTvLeftTopSub.setText(item.getName().substring(item.getName().indexOf("/")));
			fenmu = item.getName().substring(item.getName().indexOf("/") + 1);
		} else {
			holder.mTvLeftTop.setText(!TextUtils.isEmpty(item.getName()) ? item.getName() : "");
		}

		if (!TextUtils.isEmpty(item.getN())) {
			holder.mTvRightTop.setText(item.getN());
		} else {
			holder.mTvRightTop.setText("--");
		}

		String newCNYPrice = item.getNl();
		if (!TextUtils.isEmpty(newCNYPrice)) {
			holder.mTvRightBottom.setText(StringUtils.getCnyReplace(newCNYPrice));
		} else {
			holder.mTvRightBottom.setText("--");
		}

		holder.mBtnRight.setText(!TextUtils.isEmpty(item.getP()) ? item.getP() : "0.00%");
		if (BigDecimalUtils.lessThanToZero(item.getA())) {
			holder.mBtnRight.setBackground(isRedRise ? greenColor : redColor);
		} else {
			holder.mBtnRight.setBackground(isRedRise ? redColor : greenColor);
		}
		holder.mTvLeftBottom.setText(new StringBuilder().append(TextUtils.isEmpty(item.getAmt24()) ? v24 : v24 + " " +
				PrecisionUtils.getVolumeEn(String.valueOf(item.getAmt24()), Constants.PRECISION)).append(" ").append(fenmu).toString());

		holder.mLayoutRoot.setOnClickListener(v -> {
			//跳转到现货K线
			Bundle bundle = new Bundle();
			bundle.putString("pairName", item.getName());
			UIBusService.getInstance().openUri(v.getContext(), UrlUtil.getCoinbeneUrl("SpotKline"), bundle);
		});
	}

	/**
	 * 合约设置数据
	 *
	 * @param holder
	 * @param item
	 */
	private void setContractData(ViewHolder holder, TradePairMarketRes.DataBean item) {
		if (item.getName().contains("-")) {
			holder.mTvLeftTop.setText(!TextUtils.isEmpty(item.getBaseAsset()) ? String.format(forEver, item.getBaseAsset()) + "" : "--");
		} else {
			holder.mTvLeftTop.setText(!TextUtils.isEmpty(item.getName()) ? String.format(forEver, item.getName()) + "" : "--");

		}

		holder.mTvLeftTopSub.setVisibility(View.GONE);

		holder.mTvRightTop.setText(!TextUtils.isEmpty(item.getN()) ? item.getN() : "--");
		holder.mTvRightBottom.setText(!TextUtils.isEmpty(item.getNl()) ? StringUtils.getCnyReplace(item.getNl()) : "--");
		holder.mTvLeftBottom.setText(new StringBuilder().append(TextUtils.isEmpty(item.getV24()) ? v24 : v24 + " " +
				PrecisionUtils.getVolumeEn(String.valueOf(item.getV24()), Constants.PRECISION)).append(" ").append(number).toString());

		if (TextUtils.isEmpty(item.getA())) {
			item.setA("0");
		}

		if (BigDecimalUtils.compareZero(item.getA()) > 0) {
			holder.mBtnRight.setBackground(isRedRise ? redColor : greenColor);
			holder.mBtnRight.setText(!TextUtils.isEmpty(item.getP()) ? "+" + item.getP() : "0.00%");
		} else if (BigDecimalUtils.compareZero(item.getA()) == 0) {
			holder.mBtnRight.setBackground(isRedRise ? redColor : greenColor);
			holder.mBtnRight.setText(!TextUtils.isEmpty(item.getP()) ? item.getP() : "0.00%");
		} else {
			holder.mBtnRight.setBackground(isRedRise ? greenColor : redColor);
			holder.mBtnRight.setText(!TextUtils.isEmpty(item.getP()) ? item.getP() : "0.00%");
		}

		holder.mLayoutRoot.setOnClickListener(v -> {
			//跳转到合约K线
			UIBusService.getInstance().openUri(v.getContext(),
					"coinbene://ContractKline?symbol=" + item.getName(), null);

			PostPointHandler.postClickData(item.getName() + PostPointHandler.market_item);
		});
	}

	public void setGroupName(String groupName) {
		this.mGroupName = groupName;
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

		@BindView(R2.id.tv_left_top)
		TextView mTvLeftTop;
		@BindView(R2.id.tv_left_top_sub)
		TextView mTvLeftTopSub;
		@BindView(R2.id.tv_right_top)
		TextView mTvRightTop;
		@BindView(R2.id.tv_left_bottom)
		TextView mTvLeftBottom;
		@BindView(R2.id.tv_right_bottom)
		TextView mTvRightBottom;
		@BindView(R2.id.btn_right)
		QMUIRoundButton mBtnRight;
		@BindView(R2.id.iv_optional_mark)
		ImageView mIvOptionalMark;
		//		@BindView(R2.id.iv_new_mark)
//		ImageView mIvNewMark;
//		@BindView(R2.id.iv_hot_mark)
//		ImageView mIvHotMark;
		@BindView(R2.id.layout_root)
		ConstraintLayout mLayoutRoot;

		ViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
			mTvLeftTop.setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.roboto_medium));
			mTvRightTop.setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.roboto_medium));
		}
	}
}
