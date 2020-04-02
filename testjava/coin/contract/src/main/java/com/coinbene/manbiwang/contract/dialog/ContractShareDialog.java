package com.coinbene.manbiwang.contract.dialog;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.coinbene.common.Constants;
import com.coinbene.common.database.UserInvitationController;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.BitmapUtils;
import com.coinbene.common.utils.FileUtils;
import com.coinbene.common.utils.PhotoUtils;
import com.coinbene.common.utils.ShareUtils;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.model.http.AppConfigModel;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.service.app.AppService;
import com.google.zxing.client.result.ParsedResultType;
import com.mylhyl.zxing.scanner.encode.QREncode;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

import java.util.HashMap;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cn.sharesdk.whatsapp.WhatsApp;

/**
 * @author ding
 * 海报分享Dialog
 */
public class ContractShareDialog extends Dialog implements PlatformActionListener {

	static final String TAG = "ContractShareDialog";

	@BindView(R2.id.tv_Lever)
	TextView tvLever;
	@BindView(R2.id.tv_Rate)
	TextView tvRate;
	@BindView(R2.id.tv_ShareText)
	TextView tvShareText;
	@BindView(R2.id.tv_Symbol)
	TextView tvSymbol;
	@BindView(R2.id.tv_OpenAveragePrice)
	TextView tvOpenAveragePrice;
	@BindView(R2.id.tv_LatestPrice)
	TextView tvLatestPrice;
	@BindView(R2.id.img_QR)
	ImageView imgQR;
	@BindView(R2.id.tv_Slogan)
	TextView tvSlogan;
	@BindView(R2.id.layout_ShareImage)
	ConstraintLayout layoutShareImage;


	//	@BindView(R2.id.iv_profit)
//	ImageView ivProfit;
	@BindView(R2.id.tv_rate_of_return)
	TextView tvRateOfReturn;
	@BindView(R2.id.iv_rate_of_return)
	View ivRateOfReturn;
	@BindView(R2.id.ll_rate_of_return)
	LinearLayout llRateOfReturn;
	@BindView(R2.id.tv_amount_of_return)
	TextView tvAmountOfReturn;
	@BindView(R2.id.iv_amount_of_return)
	View ivAmountOfReturn;
	@BindView(R2.id.ll_amount_of_return)
	LinearLayout llAmountOfReturn;
	@BindView(R2.id.layout_Direction)
	LinearLayout layoutDirection;
	@BindView(R2.id.view_divider)
	View viewDivider;
	@BindView(R2.id.view_share_menu)
	View viewShareMenu;
	@BindView(R2.id.img_wechat)
	ImageView imgWechat;
	@BindView(R2.id.img_wechat_circle)
	ImageView imgWechatCircle;
	@BindView(R2.id.img_sina)
	ImageView imgSina;
	@BindView(R2.id.img_QQ)
	ImageView imgQQ;
	@BindView(R2.id.img_facebook)
	ImageView imgFacebook;
	@BindView(R2.id.img_save)
	ImageView imgSave;
	@BindView(R2.id.iv_rise_type)
	ImageView ivRiseType;
	@BindView(R2.id.img_whatsapp)
	ImageView imgWhatsapp;


	@BindView(R2.id.tv_cancel)
	TextView tvCancel;
	@BindView(R2.id.tv_1)
	TextView tv1;
	@BindView(R2.id.rl_data)
	ConstraintLayout rlData;
	@BindView(R2.id.tv_12)
	TextView tv12;
	@BindView(R2.id.tv_13)
	TextView tv13;
	//	@BindView(R2.id.tv14)
//	TextView tv14;
	@BindView(R2.id.tv2)
	TextView tv2;
	@BindView(R2.id.tv11)
	TextView tv11;
	@BindView(R2.id.tv4)
	TextView tv4;
	@BindView(R2.id.tv5)
	TextView tv5;
	@BindView(R2.id.tv6)
	TextView tv6;
	@BindView(R2.id.tv_order_direction)
	TextView tvOrderDirection;

	@BindView(R2.id.view20)
	View view20;

	@BindView(R2.id.middle_view)
	View mMiddleView;
	@BindView(R2.id.tv_logo)
	TextView tvLogo;
	@BindView(R2.id.iv_logo)
	ImageView ivLogo;
	@BindView(R2.id.bottom_middle_view)
	View mBottomMiddleView;

	private static int SHARE_RATE = 0;
	private static int SHARE_AMOUNT = 1;
	private int shareType = SHARE_RATE;
	private String cachePath;
	private Unbinder bind;
	private Bitmap bitmap;
	private String rate;
	private String[] stringArray;
	private String averagePrice;
	private String latestPrice;
	private String symbol;
	private String direction;
	private int leverage;
	private String unrealisedPnl;
	private String asset;
	String qrUrl;
	private boolean hasScaled = false;

	public ContractShareDialog(@NonNull Context context) {
		this(context, R.style.CoinBene_BottomSheet);
	}


	public ContractShareDialog(@NonNull Context context, int themeResId) {
		super(context, themeResId);
		init();
		cachePath = context.getExternalCacheDir().getPath() + "/";
	}

	private void init() {
		// 在构造方法里, 传入主题
		Window window = getWindow();
		window.getDecorView().setPadding(0, 0, 0, 0);
		window.setBackgroundDrawable(new BitmapDrawable());
		// 获取Window的LayoutParams
		WindowManager.LayoutParams attributes = window.getAttributes();
		attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
		attributes.height = WindowManager.LayoutParams.MATCH_PARENT;
		window.setGravity(Gravity.BOTTOM);
		// 一定要重新设置, 才能生效
		window.setAttributes(attributes);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = View.inflate(getContext(), R.layout.dialog_contract_share, null);
		setContentView(view);
		bind = ButterKnife.bind(this, view);
//		initView();
		listener();
	}

	private void autoStoragePermission() {
		if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(getOwnerActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
		} else {
			if (PhotoUtils.saveImageToGallery(getContext(), ShareUtils.screenShot(layoutShareImage))) {
				ToastUtil.show(R.string.save_suc);
			} else
				ToastUtil.show(R.string.save_err);
		}
	}


	private void listener() {
//		rlData.setBackground(ResourceProvider.getRectShape(QMUIDisplayHelper.dp2px(getContext(), 3), getContext().getResources().getColor(R.color.share_data_back)));
		tvCancel.setOnClickListener(v -> dismiss());

		imgSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				autoStoragePermission();
			}
		});


		//QQ分享
		imgQQ.setOnClickListener(v -> {
			String imagePath = FileUtils.saveBitmap(cachePath, "CoinBene.JPG", ShareUtils.screenShot(layoutShareImage));
			new ShareUtils.QQBuilder()
					.setImagePath(imagePath)
					.setPlatformActionListener(this)
					.share();
		});

		//微信好友分享
		imgWechat.setOnClickListener(v -> {
			String imagePath = FileUtils.saveBitmap(cachePath, "CoinBene.JPG", ShareUtils.screenShot(layoutShareImage));
			new ShareUtils.WechartBuilder(Wechat.NAME, Platform.SHARE_IMAGE)
					.setTitle("CoinBene")
					.setImagePath(imagePath)
					.setPlatformActionListener(this)
					.share();
		});

		//微信朋友圈分享
		imgWechatCircle.setOnClickListener(v -> {
			new ShareUtils.WechartBuilder(WechatMoments.NAME, Platform.SHARE_IMAGE)
					.setTitle("CoinBene")
					.setBitmap(ShareUtils.screenShot(layoutShareImage))
					.setPlatformActionListener(this)
					.share();
		});

		//Facebook分享
		imgFacebook.setOnClickListener(v -> {
			new ShareUtils.FaceBookBuilder(Facebook.NAME)
					.setText("CoinBene")
					.setBitmap(ShareUtils.screenShot(layoutShareImage))
					.setPlatformActionListener(this)
					.share();
		});

		imgWhatsapp.setOnClickListener(v -> {
			String imagePath = FileUtils.saveBitmap(cachePath, "CoinBene.JPG", ShareUtils.screenShot(layoutShareImage));
			new ShareUtils.WhatsAppBuilder(WhatsApp.NAME)
//					.setTitle("CoinBene")
					.setImagePath(imagePath)
//						.setBitmap(ShareUtils.screenShot(layoutShareImage))
					.setPlatformActionListener(this)
					.share();

		});

		// 新浪分享
		imgSina.setOnClickListener(v -> {
			new ShareUtils.SinaBuilder(SinaWeibo.NAME)
					.setText("CoinBene")
					.setBitmap(ShareUtils.screenShot(layoutShareImage))
					.setPlatformActionListener(this)
					.share();
		});

		//按收益率分享
		llRateOfReturn.setOnClickListener(v -> {
			tvRateOfReturn.setTextColor(getContext().getResources().getColor(R.color.res_blue));
			tvAmountOfReturn.setTextColor(getContext().getResources().getColor(R.color.res_textColor_1));
			ivAmountOfReturn.setVisibility(View.INVISIBLE);
			ivRateOfReturn.setVisibility(View.VISIBLE);
			tv1.setText(R.string.return_percent_share);
			String realRate = BigDecimalUtils.toPercentage(rate);
			setRateText(realRate);
			shareType = SHARE_RATE;
		});

		//按收益额分享
		llAmountOfReturn.setOnClickListener(v -> {
			tvRateOfReturn.setTextColor(getContext().getResources().getColor(R.color.res_textColor_1));
			tvAmountOfReturn.setTextColor(getContext().getResources().getColor(R.color.res_blue));
			ivAmountOfReturn.setVisibility(View.VISIBLE);
			ivRateOfReturn.setVisibility(View.INVISIBLE);
			tv1.setText(String.format(getContext().getString(R.string.res_profit_btc), asset));
			setRateText(unrealisedPnl);
			shareType = SHARE_AMOUNT;
		});
	}

	public void setQr() {

		String code = UserInvitationController.getInstance().findCodeByInvitation(Constants.USER_CUSTOMER);

		if (TextUtils.isEmpty(code)) {
			ServiceRepo.getAppService().getUserInInvitation(result -> {
				String codeByInvitation = UserInvitationController.getInstance().findCodeByInvitation(Constants.USER_CUSTOMER);
				if (TextUtils.isEmpty(codeByInvitation)) {
					qrUrl = Constants.DOANLOAD_APK_QR_CODE_URL;
					qrUrl = UrlUtil.replaceH5Url(qrUrl);
				} else {
					if (SpUtil.getAppConfig() == null || SpUtil.getAppConfig().getUrl_config() == null || TextUtils.isEmpty(SpUtil.getAppConfig().getUrl_config().getUser_invitation_url())) {
						qrUrl = String.format(Constants.USER_INVITATION_QRCODE, code);
					} else {
						qrUrl = SpUtil.getAppConfig().getUrl_config().getUser_invitation_url() + codeByInvitation;
					}
				}
				setImageQr();
			});
		} else {
			if (SpUtil.getAppConfig() == null || SpUtil.getAppConfig().getUrl_config() == null || TextUtils.isEmpty(SpUtil.getAppConfig().getUrl_config().getUser_invitation_url())) {
				qrUrl = String.format(Constants.USER_INVITATION_QRCODE, code);
				qrUrl = UrlUtil.replaceH5Url(qrUrl);
			} else {
				qrUrl = SpUtil.getAppConfig().getUrl_config().getUser_invitation_url() + code;
			}
			setImageQr();
		}


	}


	private void setImageQr() {
		int sizeQR = QMUIDisplayHelper.dp2px(getContext(), 42);
		bitmap = new QREncode.Builder(getContext())
				.setMargin(0)
				.setParsedResultType(ParsedResultType.URI)
				.setContents(qrUrl)
				.setLogoBitmap(BitmapUtils.drawableToBitmap(getContext().getDrawable(R.drawable.icon_logo_bitmap), QMUIDisplayHelper.dpToPx(10), QMUIDisplayHelper.dpToPx(10)))
				.setSize(sizeQR)
				.build().encodeAsBitmap();
		if (bitmap != null) {
			imgQR.setImageBitmap(bitmap);
		}
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (bitmap != null && !bitmap.isRecycled()) {
			imgQR.setImageBitmap(null);
			bitmap.recycle();
			bitmap = null;

		}
	}

	@Override
	public void show() {

		super.show();
		setQr();

		if (direction.equals("long")) {//多
			tvOrderDirection.setText(R.string.long_text_share);
			tvOrderDirection.setTextColor(getContext().getResources().getColor(R.color.share_green));
			ivRiseType.setBackground(getContext().getResources().getDrawable(R.drawable.res_green_up));
			tvLever.setText(leverage + "X");
			tvLever.setBackgroundResource(R.drawable.bg_green_share_sharp);
		} else {
			tvOrderDirection.setText(R.string.short_text_share);
			tvOrderDirection.setTextColor(getContext().getResources().getColor(R.color.res_red));
			ivRiseType.setBackground(getContext().getResources().getDrawable(R.drawable.res_red_down));
			tvLever.setText(String.format("%dX", leverage));
			tvLever.setBackgroundResource(R.drawable.bg_red_sharp);
		}

		if (BigDecimalUtils.lessThanToZero(rate)) {
			//回报率为负
			layoutShareImage.setBackgroundResource(R.drawable.res_icon_loss_bg);
			tvRate.setTextColor(getContext().getResources().getColor(R.color.res_red));
			stringArray = getContext().getResources().getStringArray(R.array.poster_string_negative);
		} else {
			//回报率为正
			layoutShareImage.setBackgroundResource(R.drawable.res_icon_profit_bg);
			tvRate.setTextColor((getContext().getResources().getColor(R.color.share_green)));
			stringArray = getContext().getResources().getStringArray(R.array.poster_string_positive);
		}

		if (shareType == SHARE_RATE) {
			String s = BigDecimalUtils.toPercentage(rate);
			setRateText(s);
			tv1.setText(R.string.return_percent_share);
		} else {
			tv1.setText(String.format(getContext().getString(R.string.res_profit_btc), asset));
			setRateText(unrealisedPnl);
		}


		Random random = new Random();
		tvShareText.setText(String.format("\"%s\"", stringArray[random.nextInt(stringArray.length)]));
		tvSymbol.setTypeface(ResourcesCompat.getFont(getContext(), R.font.roboto_regular));

		if (!TextUtils.isEmpty(symbol) && symbol.contains("-")) {
			tvSymbol.setText(String.format(getContext().getString(R.string.forever_no_delivery), TradeUtils.getUsdtContractBase(symbol)));
		} else {
			tvSymbol.setText(symbol);
		}
		tvOpenAveragePrice.setText(averagePrice);
		tvLatestPrice.setText(latestPrice);

		getWindow().getDecorView().post(() -> {
			if (!hasScaled) {
				//按照高度等比例缩放
				float scale = layoutShareImage.getHeight() * 1.0f / QMUIDisplayHelper.dp2px(getContext(), 354);

				handleTextView(tvShareText, scale);
				handleTextView(tv1, scale);
				handleTextView(tvRate, scale);

				handleTextView(tvSymbol, scale);
				handleTextView(tv12, scale);
				handleTextView(tv13, scale);

				handleTextView(tvOrderDirection, scale);
				handleTextView(tvLever, scale);
				handleTextView(tvOpenAveragePrice, scale);
				handleTextView(tvLatestPrice, scale);

				ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mMiddleView.getLayoutParams();
				layoutParams.leftMargin = (int) (layoutParams.leftMargin * scale);
				layoutParams.rightMargin = (int) (layoutParams.rightMargin * scale);
				layoutParams.height = (int) (layoutParams.height * scale);
				mMiddleView.setLayoutParams(layoutParams);
				mMiddleView.invalidate();

				handleImageView(ivRiseType, scale);


				layoutParams = (ConstraintLayout.LayoutParams) mBottomMiddleView.getLayoutParams();
				layoutParams.leftMargin = (int) (layoutParams.leftMargin * scale);
				layoutParams.rightMargin = (int) (layoutParams.rightMargin * scale);
				layoutParams.height = (int) (layoutParams.height * scale);
				mBottomMiddleView.setLayoutParams(layoutParams);
				mBottomMiddleView.invalidate();

				handleTextView(tvLogo, scale);
				handleTextView(tvSlogan, scale);
				handleImageView(imgQR, scale);
				handleImageView(ivLogo, scale);

				hasScaled = true;
			}

		});

	}

	private void handleImageView(ImageView imageView, float scale) {
		ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) imageView.getLayoutParams();
		layoutParams.width = (int) (layoutParams.width * scale);
		layoutParams.height = (int) (layoutParams.height * scale);
		layoutParams.rightMargin = (int) (layoutParams.rightMargin * scale);
		layoutParams.leftMargin = (int) (layoutParams.leftMargin * scale);
		imageView.setLayoutParams(layoutParams);
		imageView.invalidate();
	}

	private void handleTextView(TextView textView, float scale) {
		ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) textView.getLayoutParams();
		layoutParams.topMargin = (int) (layoutParams.topMargin * scale);
		layoutParams.leftMargin = (int) (layoutParams.leftMargin * scale);
		layoutParams.rightMargin = (int) (layoutParams.rightMargin * scale);

		float textSize = textView.getTextSize();
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, scale * textSize);

		textView.setLayoutParams(layoutParams);
		textView.invalidate();
	}

	private void setRateText(String rate) {
		if (TextUtils.isEmpty(rate)) {
			tvRate.setText("--");
		} else {
			String cache = rate;
			if (rate.contains("%")) {
				cache = rate.replace("%", "");
			}
			if (BigDecimalUtils.compareZero(cache) > 0) {
				rate = "+" + rate;
			}
			if (rate.contains(".")) {
				SpannableString spannableString = new SpannableString(rate);
				spannableString.setSpan(new RelativeSizeSpan(0.6f), rate.indexOf("."), rate.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				tvRate.setText(spannableString);
			} else
				tvRate.setText(rate);
		}
	}


	/**
	 * 设置开仓均价
	 */
	public void setOpenAveragePrice(String averagePrice) {
		this.averagePrice = averagePrice;
	}

	/**
	 * 设置最新价
	 */
	public void setLatestPrice(String latestPrice) {
		this.latestPrice = latestPrice;
	}

	/**
	 * 设置合约名称
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	/**
	 * 设置杠杆及多空
	 */
	public void setLever(String direction, int leverage) {
		this.direction = direction;
		this.leverage = leverage;
	}

	/**
	 * 设置回报率
	 */
	public void setReturnRate(String rate) {
		this.rate = rate;
	}

	/**
	 * 设置收益额
	 */
	public void setAmountOfReturn(String unrealisedPnl) {
		this.unrealisedPnl = unrealisedPnl;
	}

	public void setAsset(String asset) {
		this.asset = asset;
	}

	@Override
	public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
		ToastUtil.show(R.string.share_succes);
		dismiss();
	}

	@Override
	public void onError(Platform platform, int i, Throwable throwable) {
		ToastUtil.show(R.string.share_failed);
		dismiss();
	}

	@Override
	public void onCancel(Platform platform, int i) {
		dismiss();
	}

}
