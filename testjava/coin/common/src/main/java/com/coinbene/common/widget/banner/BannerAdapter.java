package com.coinbene.common.widget.banner;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.coinbene.common.R;
import com.coinbene.manbiwang.model.http.BannerList;

import java.util.List;

import me.erwa.sourceset.view.banner.IBannerView;

/**
 * ding
 * 2019-10-21
 * com.coinbene.common.widget.banner
 */
public class BannerAdapter implements IBannerView {

	private List<BannerList.DataBean> banners;
	private BannerListener listener;

	@Override
	public View getDefaultView(Context context) {
		ImageView imageView = new ImageView(context);
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setImageResource(R.drawable.banner_placeholder_image);
		return imageView;
	}

	@Override
	public boolean isDefaultAutoScroll() {
		return true;
	}

	@Override
	public void onPageSelected(int position) {
		Log.d("BannerView", "onPageSelected interval >>> $position"+position);
	}

	@Override
	public int getCount() {
		return banners == null ? 0 : banners.size();
	}

	@Override
	public View getItemView(Context context) {
		return new ImageView(context);
	}

	@Override
	public void onBindView(View itemView, int position) {
		if (itemView instanceof ImageView) {
			((ImageView) itemView).setScaleType(ImageView.ScaleType.CENTER_CROP);
			Glide.with(itemView.getContext())
					.load(banners.get(position).getImg_url())
					.into((ImageView) itemView);

			itemView.setOnClickListener(v -> {
				if (banners == null || banners.size() == 0) { return; }
				Log.e("BannerAdapter", "URL:" + banners.get(position).getLink_url());
				if (listener != null) listener.onClickBanner(banners.get(position).getLink_url());
			});
		}
	}

	public void setData(List<BannerList.DataBean> banners) {
		this.banners = banners;
	}

	public void setBannerListener(BannerListener listener) {
		this.listener = listener;
	}

	public interface BannerListener {
		void onClickBanner(String url);
	}
}
