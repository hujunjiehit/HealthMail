package com.coinbene.game.options;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.coinbene.common.Constants;
import com.coinbene.manbiwang.model.http.SettleModel;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.DensityUtil;
import com.coinbene.common.utils.FileUtils;
import com.coinbene.common.utils.PhotoUtils;
import com.coinbene.common.utils.ShareUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.game.R;
import com.coinbene.game.R2;
import com.google.zxing.client.result.ParsedResultType;
import com.mylhyl.zxing.scanner.encode.QREncode;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.HashMap;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import static com.coinbene.game.options.OptionsDelegate.BTC_CODE;
import static com.coinbene.game.options.OptionsDelegate.CONI_CODE;
import static com.coinbene.game.options.OptionsDelegate.ETH_CODE;
import static com.coinbene.game.options.OptionsDelegate.SIMULATE_CODE;
import static com.coinbene.game.options.OptionsDelegate.USDT_CODE;

/**
 * ding
 * 2019-09-10
 * com.coinbene.game.options
 */
public class OptionShareFragment extends DialogFragment implements PlatformActionListener {

	@BindView(R2.id.tv_Title)
	TextView tvTitle;
	@BindView(R2.id.tv_Rate)
	TextView tvRate;
	@BindView(R2.id.tv_ShareText)
	TextView tvShareText;
	@BindView(R2.id.opt_share_wechat)
	LinearLayout optShareWechat;
	@BindView(R2.id.opt_share_frend)
	LinearLayout optShareFrend;
	@BindView(R2.id.opt_share_sina)
	LinearLayout optShareSina;
	@BindView(R2.id.opt_share_qq)
	LinearLayout optShareQq;
	@BindView(R2.id.opt_share_facebook)
	LinearLayout optShareFacebook;
	@BindView(R2.id.opt_share_twitter)
	LinearLayout optShareTwitter;
	@BindView(R2.id.opt_save_image)
	LinearLayout optSaveImage;
	@BindView(R2.id.tv_TitleV)
	TextView tvTitleV;
	@BindView(R2.id.tv_RateV)
	TextView tvRateV;
	@BindView(R2.id.tv_ShareTextV)
	TextView tvShareTextV;
	@BindView(R2.id.option_share_vcode)
	ImageView optionShareVcode;
	@BindView(R2.id.layout_ShareImage)
	ConstraintLayout layoutShareImage;
	@BindView(R2.id.layout_BlankArea)
	ConstraintLayout layoutBlankArea;
	Unbinder unbinder;

	private String imagePath;
	private String cachePath;
	private SettleModel settleModel;
	private String asset;
	private Random random;
	private String[] stringArray;
	private Bitmap bitmap;

	public static OptionShareFragment init(SettleModel settleModel) {
		OptionShareFragment fragment = new OptionShareFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable("settleModel", settleModel);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NORMAL, R.style.Dialog_FullScreen);
	}

	@androidx.annotation.Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @androidx.annotation.Nullable ViewGroup container, @androidx.annotation.Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_option_share, container, false);
		unbinder = ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @androidx.annotation.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		cachePath = getContext().getExternalCacheDir().getPath() + "/";

		if (getArguments() == null && getArguments().getSerializable("settleModel") == null) {
			dismiss();
			return;
		}

		settleModel = (SettleModel) getArguments().getSerializable("settleModel");
		if (BigDecimalUtils.isGreaterThan(settleModel.getProfit(), "0") && BigDecimalUtils.isLessThan(settleModel.getProfit(), "0.6")) {
			//收益率0-60
			stringArray = getResources().getStringArray(R.array.options_share_copywriting_0_60);
		} else if (BigDecimalUtils.isGreaterThan(settleModel.getProfit(), "0.6") && BigDecimalUtils.isLessThan(settleModel.getProfit(), "0.8")) {
			//收益率60-80
			stringArray = getResources().getStringArray(R.array.options_share_copywriting_60_80);
		} else if (BigDecimalUtils.isGreaterThan(settleModel.getProfit(), "0.8") && BigDecimalUtils.isLessThan(settleModel.getProfit(), "1")) {
			//收益率80-100
			stringArray = getResources().getStringArray(R.array.options_share_copywriting_80_100);
		} else {
			//收益率100及以上
			stringArray = getResources().getStringArray(R.array.options_share_copywriting_100);
		}

		switch (settleModel.getAccountCurrency()) {
			case BTC_CODE:
				asset = "BTC";
				break;
			case ETH_CODE:
				asset = "ETH";
				break;
			case CONI_CODE:
				asset = "CONI";
				break;
			case SIMULATE_CODE:
				asset = "PRACTICE";
				break;
			case USDT_CODE:
				asset = "USDT";
				break;
		}

		random = new Random();
		String shareText = stringArray[random.nextInt(stringArray.length)];
		tvShareText.setText(shareText);
		tvShareTextV.setText(shareText);
		tvRate.setText(BigDecimalUtils.toPercentage(settleModel.getProfitRate(), "0.00%"));
		tvRateV.setText(BigDecimalUtils.toPercentage(settleModel.getProfitRate(), "0.00%"));
		tvTitle.setText(String.format(getResources().getString(R.string.option_share_topText), asset));
		tvTitleV.setText(String.format(getResources().getString(R.string.option_share_topText), asset));

		int size = DensityUtil.dip2px(optionShareVcode.getHeight());
		bitmap = new QREncode.Builder(getContext())
				.setMargin(1)
				.setParsedResultType(ParsedResultType.TEXT)
				.setContents(Constants.DOANLOAD_APK_QR_CODE_URL)
				.setSize(size)
				.build().encodeAsBitmap();

		optionShareVcode.setImageBitmap(bitmap);

		listener();
	}

	private void listener() {
		//点击其他空白区域消失
		layoutBlankArea.setOnClickListener(v -> dismiss());

		//微信分享
		optShareWechat.setOnClickListener(v -> {
			imagePath = FileUtils.saveBitmap(cachePath, "CoinBene_Option.JPG", ShareUtils.screenShot(layoutShareImage));
			if (imagePath == null) {
				return;
			}

			new ShareUtils.WechartBuilder(Wechat.NAME, Platform.SHARE_IMAGE)
					.setTitle("CoinBene")
					.setImagePath(imagePath)
					.setPlatformActionListener(this)
					.share();
		});

		//朋友圈分享
		optShareFrend.setOnClickListener(v -> {
			if (ShareUtils.screenShot(layoutShareImage) == null) {
				return;
			}
			new ShareUtils.WechartBuilder(WechatMoments.NAME, Platform.SHARE_IMAGE)
					.setTitle("CoinBene")
					.setBitmap(bitmap)
					.setPlatformActionListener(this)
					.share();
		});

		//qq分享
		optShareQq.setOnClickListener(v -> {
			imagePath = FileUtils.saveBitmap(cachePath, "CoinBene_Option.JPG", ShareUtils.screenShot(layoutShareImage));
			if (imagePath == null) {
				return;
			}
			new ShareUtils.QQBuilder()
					.setImagePath(imagePath)
					.setPlatformActionListener(this)
					.share();
		});

		//新浪微博分享
		optShareSina.setOnClickListener(v -> {
			if (ShareUtils.screenShot(layoutShareImage) == null) {
				return;
			}
			new ShareUtils.SinaBuilder(SinaWeibo.NAME)
					.setText("CoinBene")
					.setBitmap(bitmap)
					.setPlatformActionListener(this)
					.share();
		});

		//Facebook分享
		optShareFacebook.setOnClickListener(v -> {
			if (ShareUtils.screenShot(layoutShareImage) == null) {
				return;
			}
			new ShareUtils.FaceBookBuilder(Facebook.NAME)
					.setText("CoinBene")
					.setBitmap(bitmap)
					.setPlatformActionListener(this)
					.share();
		});

		//Twitter分享
		optShareTwitter.setOnClickListener(v -> {
			imagePath = FileUtils.saveBitmap(cachePath, "CoinBene_Option.JPG", ShareUtils.screenShot(layoutShareImage));
			if (imagePath == null) {
				return;
			}
			new ShareUtils.TwitterBuilder(Twitter.NAME)
					.setText("CoinBene")
					.setImagePath(imagePath)
					.setPlatformActionListener(this)
					.share();
		});

		//保存图片
		optSaveImage.setOnClickListener(v -> {
			AndPermission.with(getContext()).runtime().permission(Permission.Group.STORAGE)
					.onGranted(permissions -> {
						PhotoUtils.saveImageToGallery(getActivity(), ShareUtils.screenShot(layoutShareImage));
						ToastUtil.show(getResources().getString(R.string.save_suc));
					}).start();
		});
	}

	@Override
	public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
		ToastUtil.show(getResources().getString(R.string.share_succes));
	}

	@Override
	public void onError(Platform platform, int i, Throwable throwable) {
		ToastUtil.show(getResources().getString(R.string.share_failed));
	}

	@Override
	public void onCancel(Platform platform, int i) {
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}
	}
}
