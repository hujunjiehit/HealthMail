package com.coinbene.manbiwang.balance.products.fortune.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.balance.AssetManager;
import com.coinbene.common.balance.TransferParams;
import com.coinbene.manbiwang.model.http.FortuneTotalInfoModel;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.router.UIRouter;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;
import com.coinbene.common.balance.Product;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * Created by june
 * on 2019-10-15
 */
public class FortuneItemViewBinder extends ItemViewBinder<FortuneTotalInfoModel.DataBean.FinancialAccountListBean, FortuneItemViewBinder.ViewHolder> {

	@NonNull
	@Override
	protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
		View root = inflater.inflate(R.layout.item_fortune_item, parent, false);
		return new ViewHolder(root);
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull FortuneTotalInfoModel.DataBean.FinancialAccountListBean item) {
		holder.setIsRecyclable(false);
		boolean hideValue =	AssetManager.getInstance().isHideValue();
		if (hideValue) {
			holder.mTvAvailableAsset.setText("*****");
		} else {
			holder.mTvAvailableAsset.setText(item.getAvailableBalance());
		}

		holder.mTvAsset.setText(item.getAsset());
		holder.mTvTransferIn.setOnClickListener(v ->
				UIBusService.getInstance().openUri(v.getContext(), UrlUtil.getCoinbeneUrl(UIRouter.HOST_TRANSFER),
				new TransferParams()
						.setAsset(item.getAsset())
						.setFrom(Product.NAME_SPOT)
						.setTo(Product.NAME_FORTUNE)
						.toBundle())
		);

		holder.mTvTransferOut.setOnClickListener(v ->
				UIBusService.getInstance().openUri(v.getContext(), UrlUtil.getCoinbeneUrl(UIRouter.HOST_TRANSFER),
				new TransferParams()
						.setAsset(item.getAsset())
						.setFrom(Product.NAME_FORTUNE)
						.setTo(Product.NAME_SPOT)
						.toBundle()));

		//隐藏最后一条的底部分割线
		if (holder.getAdapterPosition() == getAdapter().getItemCount() - 2){
			holder.mDividerLine.setVisibility(View.GONE);
		} else {
			holder.mDividerLine.setVisibility(View.VISIBLE);
		}
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

		@BindView(R2.id.tv_asset)
		TextView mTvAsset;
		@BindView(R2.id.tv_available_asset)
		TextView mTvAvailableAsset;
		@BindView(R2.id.tv_transfer_in)
		TextView mTvTransferIn;
		@BindView(R2.id.tv_transfer_out)
		TextView mTvTransferOut;
		@BindView(R2.id.view_item_divider)
		View mDividerLine;

		ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
