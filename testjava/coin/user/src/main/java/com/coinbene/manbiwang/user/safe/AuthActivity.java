package com.coinbene.manbiwang.user.safe;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import com.coinbene.common.Constants;
import com.coinbene.common.SelectCountryActivity;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.network.okgo.NewDialogCallback;
import com.coinbene.common.utils.CheckMatcherUtils;
import com.coinbene.common.utils.DensityUtil;
import com.coinbene.common.utils.FileUtils;
import com.coinbene.common.utils.GlideUtils;
import com.coinbene.common.utils.KeyboardUtils;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.utils.PhotoUtils;
import com.coinbene.common.utils.SiteController;
import com.coinbene.common.utils.TimeUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.widget.EditTextOneIcon;
import com.coinbene.common.widget.app.TakePhonePicDialog;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.manbiwang.model.http.KycInfoModel;
import com.coinbene.manbiwang.model.http.KycUploadModel;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class AuthActivity extends CoinbeneBaseActivity {
	@BindView(R2.id.menu_title_tv)
	TextView titleView;
	@BindView(R2.id.tv_AuthTip)
	TextView tvAuthTip;
	@BindView(R2.id.tv_auth_fail_cause)
	TextView tv_auth_fail_cause;
	@BindView(R2.id.et_id_card)
	EditTextOneIcon et_id_card;
	@BindView(R2.id.et_address)
	EditTextOneIcon etAddress;

	@BindView(R2.id.et_real_name)
	EditTextOneIcon et_real_name;

	@BindView(R2.id.menu_back)
	View backView;
	@BindView(R2.id.iv_id_card_front)
	ImageView iv_id_card_front;
	@BindView(R2.id.iv_id_card_of_hander)
	ImageView iv_id_card_of_hander;
	@BindView(R2.id.iv_id_card_back)
	ImageView iv_id_card_back;


	@BindView(R2.id.tv_country)
	TextView tv_country;
	@BindView(R2.id.tv_country_name)
	TextView tv_country_name;
	@BindView(R2.id.tv_card_type)
	TextView tv_card_type;
	@BindView(R2.id.tv_card_type_name)
	TextView tv_card_type_name;
	@BindView(R2.id.tv_id_card)
	TextView tv_id_card;
	@BindView(R2.id.tv_id_card_front)
	TextView tv_id_card_front;
	@BindView(R2.id.tv_id_card_back)
	TextView tv_id_card_back;
	@BindView(R2.id.tv_id_card_of_hander)
	TextView tv_id_card_of_hander;

	@BindView(R2.id.tv_click_front)
	TextView tv_click_front;
	@BindView(R2.id.tv_click_back)
	TextView tv_click_back;
	@BindView(R2.id.tv_click_hander)
	TextView tv_click_hander;
	@BindView(R2.id.ok_btn)
	Button ok_btn;
	@BindView(R2.id.rl_birthdayy)
	RelativeLayout rlBirthday;
	@BindView(R2.id.tv_birthday_value)
	TextView tvBirthdayValue;

	private TakePhonePicDialog dialog;
	private IdTypeDialog idTypeDialog;
	private int type = type_front;//1身份证正面  2身份证反面  3手持身份证
	private static final int type_front = 1;
	private static final int type_back = 2;
	private static final int type_hander = 3;
	private static final int CODE_GALLERY_REQUEST = 0xa0;
	private static final int CODE_CAMERA_REQUEST = 0xa1;
	private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;
	private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 0x04;

	private Uri imageUri;
	private String frontStr = "front.jpg";
	private String backStr = "back.jpg";
	private String handerStr = "hander.jpg";
	private Map<Integer, File> filePathMap = new HashMap<>();
	private File fileFrontPath;
	private File fileBackPath;
	private File fileHanderPath;
	private Bitmap cbitmap;
	private int cardType = 1;//1 二代身份证   2 身份证   3护照
	private int filePosition = 1;
	private String country_en = "China";
	private String curCountre_en = "China";
	private String country_hanyu = "중국";
	private String country_ISO = "CN";
	private String country_ch = "中国";
	private ProgressDialog progress;
	private int states = -1;
	private String etIdCardStr;
	private String country;
	//    private ArrayList<String> uploadFileList = new ArrayList<>();
	private HashMap<Integer, String> uploadFileMap = new HashMap<>();
	private TimePickerView pvTime;

	public static void startMe(Context context) {
		Intent intent = new Intent(context, AuthActivity.class);
		context.startActivity(intent);
	}

	@Override
	public int initLayout() {
		return R.layout.settings_activity_auth;
	}

	@Override
	public void initView() {
		setAuthTip();
		init();
	}

	private void setAuthTip() {
		String tip = "";
		if (LanguageHelper.isChinese(this)) {
			tip = "需要您本人一只<font color=\"#f99c03\">手持您的证件</font>，另一只手持一张有<font color=\"#f99c03\">您手写的CoinBene和当天日期的白纸</font>，确保证件和白纸在您的胸前，不遮挡您的脸部，并且证件和白纸上的信息清晰可见";
			tvAuthTip.setText(Html.fromHtml(tip));
		} else if (LanguageHelper.isEnglish(this)) {
			tip = "Write on the note: CoinBene and the day of the date (mm/dd/yyyy), then take a pictrue of <font color=\"#f99c03\">yourself holing the note</font> and <font color=\"#f99c03\">your passport / ID card.</font>";
			tvAuthTip.setText(Html.fromHtml(tip));
		}
	}

	private void init() {
		String site = SiteController.getInstance().getSiteName();

		if ("MAIN".equals(site)) {
			country = getString(R.string.ch_str);
			initDefeatCountry();
		} else if ("KO".equals(site)) {
			cardType = 3;
			country = getString(R.string.country_Korea_key);
			initSelectCountry();
		} else if ("VN".equals(site)) {
			cardType = 3;
			country = getString(R.string.country_Vietnam_key);
			initSelectCountry();
		} else {
			country = getString(R.string.ch_str);
			initDefeatCountry();
		}
		tv_country_name.setText(country);

		titleView.setText(R.string.real_name_auth);
		et_real_name.getInputText().setHint(R.string.input_name);
		etAddress.getInputText().setHint(R.string.please_input_family_address);
		backView.setOnClickListener(this);
		iv_id_card_front.setOnClickListener(this);
		iv_id_card_back.setOnClickListener(this);
		iv_id_card_of_hander.setOnClickListener(this);
		tv_country.setOnClickListener(this);
		tv_card_type.setOnClickListener(this);
		tv_click_back.setOnClickListener(this);
		tv_click_front.setOnClickListener(this);
		tv_click_hander.setOnClickListener(this);
		ok_btn.setOnClickListener(this);
		rlBirthday.setOnClickListener(this);

		initFile();
		initLoadingDialog();
		getKycInfo();
	}

	@Override
	public void setListener() {
	}

	@Override
	public void initData() {

	}

	@Override
	public boolean needLock() {
		return true;
	}


	private void initLoadingDialog() {
		progress = new ProgressDialog(this);
		progress.setMessage(this.getResources().getString(R.string.dialog_loading));
	}

	private void initFile() {
		fileFrontPath = PhotoUtils.newFile(frontStr, this);
		fileBackPath = PhotoUtils.newFile(backStr, this);
		fileHanderPath = PhotoUtils.newFile(handerStr, this);
	}

	//默认显示中国 /二代身份证 其他国家可选择证件类型
	private void initDefeatCountry() {
		et_id_card.getInputText().setHint(R.string.input_id_card_number);
		tv_country_name.setText(country);
		tv_card_type.setCompoundDrawables(null, null, null, null);
		tv_card_type.setEnabled(false);
		initIdCard(true);
	}

	private void initSelectCountry() {
		Drawable nav_up = getResources().getDrawable(R.drawable.res_down_arrow_big);
		nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
		tv_card_type.setCompoundDrawables(null, null, nav_up, null);
		tv_card_type.setEnabled(true);
		initPassport(true);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.menu_back) {
			finish();
		} else if (v.getId() == R.id.rl_birthdayy) {
			KeyboardUtils.hideKeyboard(v);
			showTimePicker();
		} else if (v.getId() == R.id.iv_id_card_front || v.getId() == R.id.tv_click_front) {
			type = type_front;
			initDialog();
		} else if (v.getId() == R.id.iv_id_card_back || v.getId() == R.id.tv_click_back) {
			type = type_back;
			initDialog();
		} else if (v.getId() == R.id.iv_id_card_of_hander || v.getId() == R.id.tv_click_hander) {
			type = type_hander;
			initDialog();
		} else if (v.getId() == R.id.tv_country) {
			SelectCountryActivity.startMe(this, SelectCountryActivity.REQUEST_CODE);
		} else if (v.getId() == R.id.tv_card_type) {//选择证件类型
			initIdTyeDialog();
		} else if (v.getId() == R.id.ok_btn) {
			if (TextUtils.isEmpty(tv_country_name.getText().toString())) {
				ToastUtil.show(R.string.please_select_country_or_region);
				return;
			}
			if (TextUtils.isEmpty(et_real_name.getInputText().getText().toString().trim())) {
				ToastUtil.show(R.string.input_name);
				return;
			}
			if (TextUtils.isEmpty(tvBirthdayValue.getText().toString())) {
				ToastUtil.show(R.string.please_choose_birthday);
				return;
			}
			if (TextUtils.isEmpty(etAddress.getInputText().getText().toString())) {
				ToastUtil.show(R.string.please_input_family_address);
				return;
			}

			if (TextUtils.isEmpty(et_id_card.getInputText().getText().toString().trim())) {
				if (cardType == 1) {
					ToastUtil.show(R.string.input_id_card_number);
				} else if (cardType == 2) {
					ToastUtil.show(R.string.input_id_card_number);
				} else if (cardType == 3) {
					ToastUtil.show(R.string.input_id_passport_number);
				}
				return;
			}

			etIdCardStr = et_id_card.getInputText().getText().toString().trim().replace(" ", "");
			if (cardType == 1) {
				if (!CheckMatcherUtils.isIdNoPattern(etIdCardStr)) {
					ToastUtil.show(R.string.input_suc_id_card_number);
					return;
				}
			}
			submit();
		}

	}

	private void showTimePicker() {
		//时间选择器
		if (pvTime == null) {
			pvTime = new TimePickerBuilder(this, (date, v) ->
					tvBirthdayValue.setText(TimeUtils.getStrFromDate(date)))
					.setCancelColor(getResources().getColor(R.color.res_blue))
					.setSubmitColor(getResources().getColor(R.color.res_blue))
					.setBgColor(getResources().getColor(R.color.res_background))
					.setDividerColor(getResources().getColor(R.color.res_divider_tile))
					.setTextColorCenter(getResources().getColor(R.color.res_textColor_1))
					.setTextColorOut(getResources().getColor(R.color.res_textColor_1))
					.setTitleBgColor(getResources().getColor(R.color.res_background))
					.build();
		}
		pvTime.show();
	}


	//选择护照 需要初始化一些控件显示   isChangeIdType点击选择证件类型的时候不需要清空名字
	private void initPassport(boolean isClear) {
		if (isClear) {
			et_real_name.setInputText("");
		}
		et_id_card.setInputText("");
		tv_card_type_name.setText(R.string.Passport);
		tv_id_card.setText(R.string.passport_no);
		et_id_card.getInputText().setHint(R.string.input_id_passport_number);

		tv_id_card_front.setText(R.string.passptort_cover);
		tv_id_card_back.setText(R.string.personal_information_page);
		tv_id_card_of_hander.setText(R.string.handheld_passport_personal_information_page);
		setCardFrontImgSize(false);
		iv_id_card_front.setImageDrawable(getResources().getDrawable((R.drawable.icon_passport_front)));
		iv_id_card_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_passport_back));
		iv_id_card_of_hander.setImageDrawable(getResources().getDrawable(R.drawable.icon_passport_hander));
		setImgTvClick();
		clearFileMap();
	}


	//选择身份证 需要初始化一些控件显示    isChangeIdType点击选择证件类型的时候不需要清空名字
	private void initIdCard(boolean isClear) {
		//因为身份证和二代身份证的好多title一样   所有写在同一个方法里
		if (cardType == 1) {//二代身份证（中国）
			tv_card_type_name.setText(R.string.id_card);
			iv_id_card_front.setImageDrawable(getResources().getDrawable((R.drawable.icon_id_card_front)));
			iv_id_card_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_id_card_back));
			iv_id_card_of_hander.setImageDrawable(getResources().getDrawable(R.drawable.icon_china_id_card_hander));
		} else {//身份证（非中国）
			tv_card_type_name.setText(R.string.id_card_no_china);
			iv_id_card_front.setImageDrawable(getResources().getDrawable((R.drawable.icon_id_card_no_china_front)));
			iv_id_card_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_id_card_no_china_back));
			iv_id_card_of_hander.setImageDrawable(getResources().getDrawable(R.drawable.icon_id_card_no_china_hander));
		}
		if (isClear) {
			et_real_name.setInputText("");
		}
		et_id_card.setInputText("");
		tv_id_card.setText(R.string.id_card_number);
		et_id_card.getInputText().setHint(R.string.input_id_card_number);
		tv_id_card_front.setText(R.string.id_card_front);
		tv_id_card_back.setText(R.string.id_card_back);
		tv_id_card_of_hander.setText(R.string.id_card_of_hander);
		setCardFrontImgSize(true);
		setImgTvClick();
		clearFileMap();
	}

	private void setImgTvClick() {
		tv_click_hander.setText(R.string.click_to_upload);
		tv_click_back.setText(R.string.click_to_upload);
		tv_click_front.setText(R.string.click_to_upload);
	}

	private void clearFileMap() {
		if (filePathMap != null && filePathMap.size() > 0) {
			filePathMap.clear();
		}
	}

	//身份类型选择
	private void initIdTyeDialog() {
		if (idTypeDialog == null)
			idTypeDialog = new IdTypeDialog(this);
		idTypeDialog.setOnItemClickListener(idTypeDialogClickListener);
		idTypeDialog.show();
	}

	IdTypeDialog.DialogClickListener idTypeDialogClickListener = itemIndex -> {
		if (itemIndex == -1) {
			return;
		}
		if (itemIndex == Constants.CODE_ID) {//身份证
			if (cardType == 2)
				return;

			cardType = 2;
			initIdCard(false);
		} else if (itemIndex == Constants.CODE_PASSPORT) {//护照
			if (cardType == 3)
				return;
			cardType = 3;
			initPassport(false);

		}
	};


	//拍照或选择照片dialog
	private void initDialog() {
		if (dialog == null)
			dialog = new TakePhonePicDialog(this);
		dialog.setOnItemClickListener(dialogClickListener);
		dialog.show();
	}

	TakePhonePicDialog.DialogClickListener dialogClickListener = itemIndex -> {
		if (itemIndex == -1) {
			return;
		}
		if (itemIndex == Constants.CODE_AUTH_TAKE_PHOTO) {//拍照
			autoObtainCameraPermission();
		} else if (itemIndex == Constants.CODE_AUTH_SELECT_PIC) {//选择照片
			autoObtainStoragePermission();
		}
	};

	//护照和身份证  imageview大小不一样  需要重新设置
	private void setCardFrontImgSize(boolean isIdCard) {
		ViewGroup.LayoutParams params = iv_id_card_front.getLayoutParams();
		if (isIdCard) {
			params.height = DensityUtil.dip2px(110);
			params.width = DensityUtil.dip2px(175);
		} else {
			params.height = DensityUtil.dip2px(110);
			params.width = DensityUtil.dip2px(78);
		}
		iv_id_card_front.setLayoutParams(params);
		iv_id_card_back.setLayoutParams(params);
	}


	/**
	 * 自动获取相机权限
	 */
	private void autoObtainCameraPermission() {

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
				|| ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_PERMISSIONS_REQUEST_CODE);
		} else {//有权限直接调用系统相机拍照
			parseUri();
		}
	}

	private void parseUri() {
		if (type == type_front) {
			imageUri = getFromFile(fileFrontPath);
		} else if (type == type_back) {
			imageUri = getFromFile(fileBackPath);
		} else if (type == type_hander) {
			imageUri = getFromFile(fileHanderPath);
		}
		PhotoUtils.takePicture(this, imageUri, CODE_CAMERA_REQUEST);
	}

	private Uri getFromFile(File filePath) {
		Uri imageUri = Uri.fromFile(filePath);
		//通过FileProvider创建一个content类型的Uri
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			imageUri = FileProvider.getUriForFile(this, getPackageName() + ".fileProvider", filePath);
		}
		return imageUri;
	}

	private void showImageview(File f) {
		if (f == null) {//从拍照过来的
			if (type == type_front) {
				f = fileFrontPath;
			} else if (type == type_back) {
				f = fileBackPath;
			} else if (type == type_hander) {
				f = fileHanderPath;
			}
		} else {

		}

		rotate(f);
	}


	private void showImageUri(Uri newUri) {
		String path = newUri.getPath();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            newUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileProvider", new File(path));
//        }
		showImageview(new File(path));

	}


	//解决三星拍照自己旋转90度的bug
	private void rotate(File f) {
		int degree = PhotoUtils.readPictureDegree(f.getAbsolutePath());
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
//        Bitmap bitmap = filePathMap.get(type);
//        if (bitmap != null) {
//            bitmap.recycle();
//            bitmap = null;
//        }
		BitmapFactory.Options opts = new BitmapFactory.Options();//获取缩略图显示到屏幕上
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		opts.inDither = true;
		cbitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), opts);

		if (cbitmap == null) {
			ToastUtil.show(R.string.select_picture_fail);
			return;
		}
		if (degree != 0) {
			cbitmap = PhotoUtils.rotaingImageView(degree, cbitmap);
		}

		File file = null;
//        Uri uri;
		if (type == type_front) {
			file = PhotoUtils.saveBitmapFile(this, cbitmap, frontStr);
			if (file == null) {
				ToastUtil.show(R.string.select_picture_fail);
				return;
			}
//            uri = Uri.fromFile(file);
//            DLog.d("压缩后文件大小", PhotoUtils.getFileSize(file) + "");
			iv_id_card_front.setImageBitmap(cbitmap);
			tv_click_front.setText(R.string.click_reload);
		} else if (type == type_back) {
			file = PhotoUtils.saveBitmapFile(this, cbitmap, backStr);
			if (file == null) {
				ToastUtil.show(R.string.select_picture_fail);
				return;
			}
//            uri = Uri.fromFile(file);
//            DLog.d("压缩后文件大小", PhotoUtils.getFileSize(file) + "");
			iv_id_card_back.setImageBitmap(cbitmap);
			tv_click_back.setText(R.string.click_reload);
		} else if (type == type_hander) {
			file = PhotoUtils.saveBitmapFile(this, cbitmap, handerStr);
			if (file == null) {
				ToastUtil.show(R.string.select_picture_fail);
				return;
			}
//            uri = Uri.fromFile(file);
//            DLog.d("压缩后文件大小", PhotoUtils.getFileSize(file) + "");
			iv_id_card_of_hander.setImageBitmap(cbitmap);
			tv_click_hander.setText(R.string.click_reload);
		}
		if (filePathMap.get(type) != null) {
			File file1 = filePathMap.get(type);
			{
				if (file1 != null && file1.exists()) {
					file1.delete();
				}
			}
			filePathMap.remove(type);
		}

		filePathMap.put(type, file);

//        PhotoUtils.saveBitmapFile(cbitmap,fileFrontPath.getPath());
	}

//    private void recycleBitmaps() {
//        if (filePathMap != null && filePathMap.size() > 0) {
//            for (int i = 0; i < filePathMap.size(); i++) {
//                Bitmap bitmap = filePathMap.get(i);
//                if (bitmap != null) {
//                    bitmap.recycle();
//                    bitmap = null;
//                }
//            }
//            filePathMap.clear();
//        }
//    }


	private void submit() {
		if (states == Constants.AUTH_FAILED) {//审核失败
			if (progress != null && !progress.isShowing()) {
				progress.show();
			}
			if (filePathMap == null || filePathMap.size() == 0) {
				addKyc();
			} else {
				uploadController();
			}
		} else {
			if (filePathMap != null) {
				if (cardType == 1 || cardType == 2) {//二代身份证  或者身份证
					if (filePathMap.size() == 0 || filePathMap.get(1) == null) {
						ToastUtil.show(R.string.please_upload_ID_card_front);
						return;
					} else if (filePathMap.get(2) == null) {
						ToastUtil.show(R.string.please_upload_ID_card_resverse);
						return;
					} else if (filePathMap.get(3) == null) {
						ToastUtil.show(R.string.please_upload_handheld_ID_card);
						return;
					}
				} else if (cardType == 3) {//护照
					if (filePathMap.size() == 0 || filePathMap.get(1) == null) {
						ToastUtil.show(R.string.please_upload_the_passport_cover);
						return;
					} else if (filePathMap.get(2) == null) {
						ToastUtil.show(R.string.please_upload_the_passport_information_page);
						return;
					} else if (filePathMap.get(3) == null) {
						ToastUtil.show(R.string.please_upload_the_handheld_passport_information_page);
						return;
					}
				}
				if (progress != null && !progress.isShowing()) {
					progress.show();
				}
				uploadController();
			}
		}
	}

	private void addKyc() {
		HttpParams params = new HttpParams();
//        if (uploadFileList != null && uploadFileList.size() > 2) {
//            if (!TextUtils.isEmpty(uploadFileList.get(0)))
//                params.put("idCardUp", uploadFileList.get(0));
//            if (!TextUtils.isEmpty(uploadFileList.get(1)))
//                params.put("idCardDown", uploadFileList.get(1));
//            if (!TextUtils.isEmpty(uploadFileList.get(2)))
//                params.put("idCardHold", uploadFileList.get(2));
//        }
		if (uploadFileMap != null && uploadFileMap.size() > 2) {
			if (!TextUtils.isEmpty(uploadFileMap.get(type_front)))
				params.put("idCardUp", uploadFileMap.get(type_front));
			if (!TextUtils.isEmpty(uploadFileMap.get(type_back)))
				params.put("idCardDown", uploadFileMap.get(type_back));
			if (!TextUtils.isEmpty(uploadFileMap.get(type_hander)))
				params.put("idCardHold", uploadFileMap.get(type_hander));
		}
		params.put("name", et_real_name.getInputStr().trim());
		params.put("type", cardType);
//        params.put("nationEn", curCountre_en);
//        params.put("nationZh", country_ch);
		params.put("country", country_ISO);
		params.put("idNumber", etIdCardStr);
		params.put("birthday", tvBirthdayValue.getText().toString());
		params.put("address", etAddress.getInputStr());


		OkGo.<BaseRes>post(Constants.KYC_ADD_USERINFO).params(params).tag(this).execute(new NewJsonSubCallBack<BaseRes>() {
			@Override
			public void onSuc(Response<BaseRes> response) {
				BaseRes baseResponse = response.body();
				if (baseResponse.isSuccess()) {
					if (progress != null && progress.isShowing())
						progress.dismiss();
					ToastUtil.show(R.string.submit_success);
					finish();
				}
			}


			@Override
			public void onE(Response<BaseRes> response) {
				if (progress != null && progress.isShowing())
					progress.dismiss();
				filePosition = 1;
				uploadFileMap.clear();
//                ToastUtil.show(R.string.submit_fail);
			}

		});
	}

	private void uploadController() {
		int i = 0;
		int typeParam = 0;
		File fileParm = null;
//        String filePath = null;
		boolean isFind = false;
		boolean isFail = false;
		for (int type : filePathMap.keySet()) {
			i++;
			if (filePosition == i) {
				typeParam = type;
				fileParm = filePathMap.get(typeParam);
				if (fileParm == null) {
					isFail = true;
				}
				isFind = true;
				break;
			}
		}
		if (isFail) {
			filePosition = 1;
			if (progress != null && progress.isShowing())
				progress.dismiss();
			ToastUtil.show(R.string.submit_fail);
			return;
		} else {
			if (isFind) {
				int finalTypeParam = typeParam;
				Luban.with(this)
						.load(fileParm)
						.ignoreBy(800)
						.setTargetDir(fileParm.getParentFile().getPath())
						.filter(path -> true)
						.setCompressListener(new OnCompressListener() {
							@Override
							public void onStart() {

							}

							@Override
							public void onSuccess(File file) {
								upload(file, finalTypeParam);
							}

							@Override
							public void onError(Throwable e) {
							}
						}).launch();

			} else {
				addKyc();
			}
		}
	}

	//上传图片
	private void upload(File file, int type) {
		OkGo.<KycUploadModel>post(Constants.KYC_UPLOAD)
				.tag(this)
				.isMultipart(true)
				.params("type", type)
				.params("file", file)
				.execute(new NewJsonSubCallBack<KycUploadModel>() {
					@Override
					public void uploadProgress(Progress progress) {
						super.uploadProgress(progress);

					}

					@Override
					public void onSuc(Response<KycUploadModel> response) {
						if (response.body() != null && response.body().isSuccess() && !TextUtils.isEmpty(response.body().getData().getPath())) {
							filePosition++;
//                            uploadFileList.add(response.body().getData().getPath());
							uploadFileMap.put(type, response.body().getData().getPath());
//                            submit();
							uploadController();
						} else {
							uploadFileMap.clear();
							filePosition = 1;
							if (progress != null && progress.isShowing())
								progress.dismiss();
						}
					}

					@Override
					public void onE(Response<KycUploadModel> response) {
						filePosition = 1;
						uploadFileMap.clear();
						if (progress != null && progress.isShowing())
							progress.dismiss();
					}

					@Override
					public void onFinish() {
						super.onFinish();
					}
				});
	}


	private void getKycInfo() {
		OkGo.<KycInfoModel>get(Constants.KYC_GET_INFO).tag(this).execute(new NewDialogCallback<KycInfoModel>(this) {
			@Override
			public void onSuc(Response<KycInfoModel> response) {
				KycInfoModel baseResponse = response.body();
				if (baseResponse.isSuccess()) {
					if (baseResponse.getData() != null) {
						getInfoToView(baseResponse.getData());
					}
				}
			}


			@Override
			public void onE(Response<KycInfoModel> response) {
			}

			@Override
			public void onFail(String msg) {
			}
		});
	}

	//初始化data  审核失败   和   待完善
	private void getInfoToView(KycInfoModel.DataBean data) {
//        List<String> er = new ArrayList<>();测试代码
//        data.setError(er);
//        er.add("test");
//        er.add("test1");
//        er.add("test");
//        er.add("test1");
//        er.add("test");
//        er.add("test1");
//未认证  不用处理
		if (tv_country_name == null) {
			return;
		}


		if (data.getStatus() == Constants.AUTH_UNVERIFIED)
			return;

		if (data.getStatus() == Constants.AUTH_FAILED || data.getStatus() == Constants.AUTH_UNFINISHED) {
			states = data.getStatus();
			country_ISO = data.getCountry();
			if (TextUtils.isEmpty(country_ISO)) {
				country_ISO = "CN";
			}
//            CountryTable countryTable = CountryController.getInstance().queryCountryByISO(country_ISO);
//            if (countryTable == null) {//如果查询不到，直接返回
//                return;
//            }
//            curCountre_en = countryTable.name_english;
//            country_ch = countryTable.name_zh;
//            country_hanyu = countryTable.name_hanyu;
//            if (CommonUtil.isLocalzh(this) && !TextUtils.isEmpty(country_ch))
//                tv_country_name.setText(country_ch);
//            else if (CommonUtil.isLocalHanyu(this) && !TextUtils.isEmpty(country_hanyu)) {
//                tv_country_name.setText(country_hanyu);
//            } else {
			if (!TextUtils.isEmpty(data.getCountryName())) {
				tv_country_name.setText(data.getCountryName());
				//tv_country.setText(data.getCountryName());
			}
			et_real_name.setInputText(TextUtils.isEmpty(data.getName()) ? "" : data.getName());
			et_id_card.setInputText(TextUtils.isEmpty(data.getIdNumber()) ? "" : data.getIdNumber());
			if (!TextUtils.isEmpty(data.getError())) {
//                String str = CommonUtil.listToString(data.getError());
				String str = data.getError();
				tv_auth_fail_cause.setVisibility(View.VISIBLE);
				tv_auth_fail_cause.setText(str);
			} else {
				tv_auth_fail_cause.setVisibility(View.GONE);
			}

			//修改返回的图片地址的url
//            if (!TextUtils.isEmpty(data.getIdCardUp())) {
//                data.setIdCardUp( + data.getIdCardUp());
//            }
//            if (!TextUtils.isEmpty(data.getIdCardDown())) {
//                data.setIdCardDown(Constants.BASE_URL + data.getIdCardDown());
//            }
//            if (!TextUtils.isEmpty(data.getIdCardHold())) {
//                data.setIdCardHold(Constants.BASE_URL + data.getIdCardHold());
//            }

			if (data.getType() == 1) {//type为1是国内身份证，2是国外身份证，3是护照
				cardType = 1;
				tv_card_type.setCompoundDrawables(null, null, null, null);
				tv_card_type.setEnabled(false);
				tv_card_type_name.setText(R.string.id_card);
				tv_id_card.setText(R.string.id_card_number);
				et_id_card.getInputText().setHint(R.string.input_id_card_number);
				tv_id_card_front.setText(R.string.id_card_front);
				tv_id_card_back.setText(R.string.id_card_back);
				tv_id_card_of_hander.setText(R.string.id_card_of_hander);
				setCardFrontImgSize(true);
				iv_id_card_front.setImageDrawable(getResources().getDrawable((R.drawable.icon_id_card_front)));
				iv_id_card_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_id_card_back));
				iv_id_card_of_hander.setImageDrawable(getResources().getDrawable(R.drawable.icon_china_id_card_hander));


				if (!TextUtils.isEmpty(data.getIdCardUp())) {
					tv_click_front.setText(R.string.click_reload);
					GlideUtils.loadImageViewLoad(this, Constants.BASE_URL, data.getIdCardUp(), iv_id_card_front, R.drawable.icon_id_loading_default, R.drawable.icon_id_card_front);
				}
				if (!TextUtils.isEmpty(data.getIdCardDown())) {
					tv_click_back.setText(R.string.click_reload);
					GlideUtils.loadImageViewLoad(this, Constants.BASE_URL, data.getIdCardDown(), iv_id_card_back, R.drawable.icon_id_loading_default, R.drawable.icon_id_card_back);
				}
				if (!TextUtils.isEmpty(data.getIdCardHold())) {
					tv_click_hander.setText(R.string.click_reload);
					GlideUtils.loadImageViewLoad(this, Constants.BASE_URL, data.getIdCardHold(), iv_id_card_of_hander, R.drawable.icon_id_loading_default, R.drawable.icon_china_id_card_hander);
				}
			} else if (data.getType() == 2) {
				cardType = 2;
				Drawable nav_up = getResources().getDrawable(R.drawable.res_down_arrow_big);
				nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
				tv_card_type.setCompoundDrawables(null, null, nav_up, null);
				tv_card_type.setEnabled(true);
				tv_card_type_name.setText(R.string.id_card_no_china);
				tv_id_card.setText(R.string.id_card_number);
				et_id_card.getInputText().setHint(R.string.input_id_card_number);
				tv_id_card_front.setText(R.string.id_card_front);
				tv_id_card_back.setText(R.string.id_card_back);
				tv_id_card_of_hander.setText(R.string.id_card_of_hander);
				setCardFrontImgSize(true);
				iv_id_card_front.setImageDrawable(getResources().getDrawable((R.drawable.icon_id_card_no_china_front)));
				iv_id_card_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_id_card_no_china_back));
				iv_id_card_of_hander.setImageDrawable(getResources().getDrawable(R.drawable.icon_id_card_no_china_hander));
				if (!TextUtils.isEmpty(data.getIdCardUp())) {
					tv_click_front.setText(R.string.click_reload);
					GlideUtils.loadImageViewLoad(this, Constants.BASE_URL, data.getIdCardUp(), iv_id_card_front, R.drawable.icon_id_loading_default, R.drawable.icon_id_card_no_china_front);
				}
				if (!TextUtils.isEmpty(data.getIdCardDown())) {
					tv_click_back.setText(R.string.click_reload);
					GlideUtils.loadImageViewLoad(this, Constants.BASE_URL, data.getIdCardDown(), iv_id_card_back, R.drawable.icon_id_loading_default, R.drawable.icon_id_card_no_china_back);
				}
				if (!TextUtils.isEmpty(data.getIdCardHold())) {
					tv_click_hander.setText(R.string.click_reload);
					GlideUtils.loadImageViewLoad(this, Constants.BASE_URL, data.getIdCardHold(), iv_id_card_of_hander, R.drawable.icon_id_loading_default, R.drawable.icon_id_card_no_china_hander);
				}
			} else if (data.getType() == 3) {
				cardType = 3;
				Drawable nav_up = getResources().getDrawable(R.drawable.res_down_arrow_big);
				nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
				tv_card_type.setCompoundDrawables(null, null, nav_up, null);
				tv_card_type.setEnabled(true);
				tv_card_type_name.setText(R.string.Passport);
				tv_id_card.setText(R.string.passport_no);
				et_id_card.getInputText().setHint(R.string.input_id_passport_number);
				setCardFrontImgSize(false);
				tv_id_card_front.setText(R.string.passptort_cover);
				tv_id_card_back.setText(R.string.personal_information_page);
				tv_id_card_of_hander.setText(R.string.handheld_passport_personal_information_page);
				iv_id_card_front.setImageDrawable(getResources().getDrawable((R.drawable.icon_passport_front)));
				iv_id_card_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_passport_back));
				iv_id_card_of_hander.setImageDrawable(getResources().getDrawable(R.drawable.icon_passport_hander));
				if (!TextUtils.isEmpty(data.getIdCardUp())) {
					tv_click_front.setText(R.string.click_reload);
					GlideUtils.loadImageViewLoad(this, Constants.BASE_URL, data.getIdCardUp(), iv_id_card_front, R.drawable.icon_id_loading_default, R.drawable.icon_passport_front);
				}
				if (!TextUtils.isEmpty(data.getIdCardDown())) {
					tv_click_back.setText(R.string.click_reload);
					GlideUtils.loadImageViewLoad(this, Constants.BASE_URL, data.getIdCardDown(), iv_id_card_back, R.drawable.icon_id_loading_default, R.drawable.icon_passport_back);
				}
				if (!TextUtils.isEmpty(data.getIdCardHold())) {
					tv_click_hander.setText(R.string.click_reload);
					GlideUtils.loadImageViewLoad(this, Constants.BASE_URL, data.getIdCardHold(), iv_id_card_of_hander, R.drawable.icon_id_loading_default, R.drawable.icon_passport_hander);
				}
			}
		}
	}


	private boolean checkPremissionResultArray(int[] grantResults) {
		if (grantResults == null || grantResults.length == 0) {
			return false;
		}
		for (int grantResult : grantResults) {
			if (grantResult != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (grantResults.length == 0) {
			return;
		}
		switch (requestCode) {
			//调用系统相机申请拍照权限回调
			case CAMERA_PERMISSIONS_REQUEST_CODE: {
				if (checkPremissionResultArray(grantResults)) {
					parseUri();
				}
				break;
			}
			//调用系统相册申请Sdcard权限回调
			case STORAGE_PERMISSIONS_REQUEST_CODE:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					PhotoUtils.openPic(this, CODE_GALLERY_REQUEST);
				}
				break;
			default:
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				//拍照完成回调
				case CODE_CAMERA_REQUEST:
					showImageview(null);
					break;
				//访问相册完成回调
				case CODE_GALLERY_REQUEST:
					if (data.getData() == null) {
						return;
					}
					String path = PhotoUtils.getPath(this, data.getData());
					if (TextUtils.isEmpty(path)) {
						return;
					}
					Uri newUri = Uri.parse(path);
					showImageUri(newUri);
					break;
				case SelectCountryActivity.REQUEST_CODE://选择国家回调
					country = data.getStringExtra("countryName");
					String s = data.getStringExtra("countryArea");
					country_ISO = data.getStringExtra("country_ISO");
					if (tv_country_name.getText().equals(country)) {
						return;
					}

					if (s.equals("86")) {
						cardType = 1;
						initDefeatCountry();
					} else {
						cardType = 3;
						initSelectCountry();
					}

					tv_country_name.setText(country);
					break;
				default:
			}
		}
	}


	/**
	 * 自动获取sdk权限
	 */

	private void autoObtainStoragePermission() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSIONS_REQUEST_CODE);
		} else {
			PhotoUtils.openPic(this, CODE_GALLERY_REQUEST);
		}

	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (cbitmap != null) {
			cbitmap.recycle();
			cbitmap = null;
		}
//        recycleBitmaps();
		clearFileMap();
		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			dialog = null;
		}
		if (progress != null && progress.isShowing()) {
			progress.dismiss();
		}
		progress = null;
		if (idTypeDialog != null && idTypeDialog.isShowing()) {
			idTypeDialog.dismiss();
			idTypeDialog = null;
		}
//        if (fileFrontPath != null && fileFrontPath.exists()) {
//            fileFrontPath.delete();
//        }
//        if (fileBackPath != null && fileBackPath.exists()) {
//            fileBackPath.delete();
//        }
//        if (fileHanderPath != null && fileHanderPath.exists()) {
//            fileHanderPath.delete();
//        }

		//删除目录下所有文件
		FileUtils.deleteFile(FileUtils.getSaveFile(CBRepository.getContext()));
	}
}
