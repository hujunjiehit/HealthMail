package com.coinbene.manbiwang.market.viewbinder;

import android.graphics.Bitmap;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.coinbene.common.Constants;
import com.coinbene.common.PostPointHandler;
import com.coinbene.common.database.TradePairGroupTable;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.manbiwang.market.R;
import com.coinbene.manbiwang.market.R2;
import com.coinbene.manbiwang.market.util.MarketChangeType;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.security.MessageDigest;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;


/**
 * @author KG on 2017/6/14.
 */

public class NewMarketItemBinder extends ItemViewBinder<WsMarketData, NewMarketItemBinder.ViewHolder> {


	private Drawable redColor, greenColor;

	private boolean isRedRise;

	private String v24, number;

	private String mGroupName;
	private String forEver;

	private RequestOptions options;


	public NewMarketItemBinder() {

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

		int targetHeight = QMUIDisplayHelper.dp2px(root.getContext(), 16);
		options = new RequestOptions()
				.transform(new BitmapTransformation() {
					@Override
					protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
						//固定图片高度16dp，宽度等比例压缩
						int scaleWidth = (int) (targetHeight * ((float) toTransform.getWidth() / (float) toTransform.getHeight()));
						return Bitmap.createScaledBitmap(toTransform, scaleWidth, targetHeight, true);
					}

					@Override
					public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

					}
				});

		return new ViewHolder(root);
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull WsMarketData item, @NonNull List<Object> payloads) {
		if (payloads.isEmpty()) {
			onBindViewHolder(holder, item);
		} else {
			for (int i = 0; i < payloads.size(); i++) {
				List<MarketChangeType> list = (List<MarketChangeType>) payloads.get(i);
				for (MarketChangeType marketChangeType : list) {
					switch (marketChangeType) {
						case UPS_AND_DOWNS:
							setSpotUpsAndDowns(holder, item);
							break;
						case LAST_PRICE:
							setSpotLastPrice(holder, item);
							break;
						case VOLUME_24H:
							setSpotVolume24H(holder, item);
							break;
						case IS_OPTIONAL:
							setSpotIsOptional(holder, item);
							break;
						case TAGS:
							setSpotTags(holder, item);
							break;
					}
				}
			}
		}
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull WsMarketData item) {
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
	private void setSpotData(ViewHolder holder, WsMarketData item) {
		if (item.isMargin() && SwitchUtils.isOpenMargin()) {
			holder.mTvMargin.setVisibility(View.VISIBLE);
			holder.mTvMargin.setText(String.format("%sX", item.leverage));
		} else {
			holder.mTvMargin.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(item.getTradePairName()) && item.getTradePairName().contains("/")) {
			holder.mTvLeftTop.setText(item.getTradePairName().substring(0, item.getTradePairName().indexOf("/")));
			holder.mTvLeftTopSub.setText(item.getTradePairName().substring(item.getTradePairName().indexOf("/")));
		} else {
			holder.mTvLeftTop.setText(!TextUtils.isEmpty(item.getTradePairName()) ? item.getTradePairName() : "");
		}

		setSpotIsOptional(holder, item);
		setSpotTags(holder, item);
		setSpotLastPrice(holder, item);
		setSpotUpsAndDowns(holder, item);
		setSpotVolume24H(holder, item);

		holder.mLayoutRoot.setOnClickListener(v -> {
			//跳转到现货K线
			Bundle bundle = new Bundle();
			bundle.putString("pairName", item.getTradePairName());
			UIBusService.getInstance().openUri(v.getContext(), UrlUtil.getCoinbeneUrl("SpotKline"), bundle);
		});
	}

	private void setSpotIsOptional(ViewHolder holder, WsMarketData item) {
		holder.mIvOptionalMark.setVisibility(item.isOptional() ? View.VISIBLE : View.GONE);
	}

	private void setSpotVolume24H(ViewHolder holder, WsMarketData item) {
		holder.mTvLeftBottom.setText(new StringBuilder().append(TextUtils.isEmpty(item.getVolume24h()) ? v24 : v24 + " " +
				PrecisionUtils.getVolumeEn(String.valueOf(item.getVolume24h()), Constants.PRECISION)).append(" ").append(item.getQuoteAsset()).toString());
	}

	private void setSpotLastPrice(ViewHolder holder, WsMarketData item) {
		if (!TextUtils.isEmpty(item.getLastPrice())) {
			holder.mTvRightTop.setText(item.getLastPrice());
		} else {
			holder.mTvRightTop.setText("--");
		}

		if (!TextUtils.isEmpty(item.getLocalPrice())) {
			holder.mTvRightBottom.setText(item.getLocalPrice());
		} else {
			holder.mTvRightBottom.setText("--");
		}
	}

	private void setSpotUpsAndDowns(ViewHolder holder, WsMarketData item) {
		String upsAndDowns = TextUtils.isEmpty(item.getUpsAndDowns()) ? "0.00%" : item.getUpsAndDowns();
		if (upsAndDowns.equals("0.00%")) {
			holder.mBtnRight.setText(upsAndDowns);
			holder.mBtnRight.setBackground(isRedRise ? redColor : greenColor);
		} else if (upsAndDowns.contains("-")) {
			holder.mBtnRight.setText(upsAndDowns);
			holder.mBtnRight.setBackground(isRedRise ? greenColor : redColor);
		} else {
			holder.mBtnRight.setText("+" + upsAndDowns);
			holder.mBtnRight.setBackground(isRedRise ? redColor : greenColor);
		}
	}

	private void setSpotTags (ViewHolder holder, WsMarketData item) {
		holder.mIvTag1.setVisibility(item.getTags().size() > 0 ? View.VISIBLE : View.GONE);
		holder.mIvTag2.setVisibility(item.getTags().size() > 1 ? View.VISIBLE : View.GONE);
		holder.mIvTag3.setVisibility(item.getTags().size() > 2 ? View.VISIBLE : View.GONE);
		if (item.getTags().size() > 0) {
			if (holder.mIvTag1.getVisibility() == View.VISIBLE) {
				loadImage(item.getTags().get(0).iconUrl, holder.mIvTag1);
			}
			if (holder.mIvTag2.getVisibility() == View.VISIBLE) {
				loadImage(item.getTags().get(1).iconUrl, holder.mIvTag2);
			}
			if (holder.mIvTag3.getVisibility() == View.VISIBLE) {
				loadImage(item.getTags().get(2).iconUrl, holder.mIvTag3);
			}
		}
	}

	private void loadImage(String iconUrl, ImageView imageView) {
		Glide.with(imageView)
				.load(iconUrl)
				.skipMemoryCache(false)
				.apply(options)
				.dontAnimate()
				.into(imageView);
	}

	/**
	 * 合约设置数据
	 *
	 * @param holder
	 * @param item
	 */
	private void setContractData(ViewHolder holder, WsMarketData item) {
		if (item.getSymbol().contains("-")) {
			holder.mTvLeftTop.setText(!TextUtils.isEmpty(item.getBaseAsset()) ? String.format(forEver, item.getBaseAsset()) + "" : "--");
		} else {
			holder.mTvLeftTop.setText(!TextUtils.isEmpty(item.getSymbol()) ? String.format(forEver, item.getSymbol()) + "" : "--");
		}

		holder.mTvLeftTopSub.setVisibility(View.GONE);

		holder.mTvRightTop.setText(!TextUtils.isEmpty(item.getLastPrice()) ? item.getLastPrice() : "--");
		holder.mTvRightBottom.setText(!TextUtils.isEmpty(item.getLocalPrice()) ? item.getLocalPrice() : "--");
		holder.mTvLeftBottom.setText(new StringBuilder().append(TextUtils.isEmpty(item.getVolume24h()) ? v24 : v24 + " " +
				PrecisionUtils.getVolumeEn(String.valueOf(item.getVolume24h()), Constants.PRECISION)).append(" ").append(number).toString());

		String upsAndDowns = TextUtils.isEmpty(item.getUpsAndDowns()) ? "0.00%" : item.getUpsAndDowns();
		if (upsAndDowns.equals("0.00%")) {
			holder.mBtnRight.setText(upsAndDowns);
			holder.mBtnRight.setBackground(isRedRise ? redColor : greenColor);
		} else if (upsAndDowns.contains("-")) {
			holder.mBtnRight.setText(upsAndDowns);
			holder.mBtnRight.setBackground(isRedRise ? greenColor : redColor);
		} else {
			holder.mBtnRight.setText("+" + upsAndDowns);
			holder.mBtnRight.setBackground(isRedRise ? redColor : greenColor);
		}

		holder.mLayoutRoot.setOnClickListener(v -> {
			//跳转到合约K线
			UIBusService.getInstance().openUri(v.getContext(),
					"coinbene://ContractKline?symbol=" + item.getSymbol(), null);

			PostPointHandler.postClickData(item.getSymbol() + PostPointHandler.market_item);
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
		@BindView(R2.id.layout_root)
		ConstraintLayout mLayoutRoot;
		@BindView(R2.id.tv_margin)
		TextView mTvMargin;
		@BindView(R2.id.iv_tag1)
		ImageView mIvTag1;
		@BindView(R2.id.iv_tag2)
		ImageView mIvTag2;
		@BindView(R2.id.iv_tag3)
		ImageView mIvTag3;

		ViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
			mTvLeftTop.setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.roboto_medium));
			mTvRightTop.setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.roboto_medium));
		}
	}
}
