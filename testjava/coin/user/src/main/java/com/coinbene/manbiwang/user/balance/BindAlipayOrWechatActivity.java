package com.coinbene.manbiwang.user.balance;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.DensityUtil;
import com.coinbene.common.utils.FileUtils;
import com.coinbene.common.utils.MD5Util;
import com.coinbene.common.utils.PhotoUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.widget.EditTextOneIcon;
import com.coinbene.common.widget.EditTextTwoIcon;
import com.coinbene.common.widget.app.TakePhonePicDialog;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.manbiwang.model.http.UploadFileBindPayModel;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;

import java.io.File;

import butterknife.BindView;
import butterknife.Unbinder;


public class BindAlipayOrWechatActivity extends CoinbeneBaseActivity {

    @BindView(R2.id.et_name)
    EditTextOneIcon et_name;
    @BindView(R2.id.et_code)
    EditTextOneIcon et_code;
    @BindView(R2.id.et_pin)
    EditTextTwoIcon et_pin;
    @BindView(R2.id.tv_code)
    TextView tv_code;
    @BindView(R2.id.iv_pay_img)
    ImageView iv_pay_img;
    @BindView(R2.id.iv_pay_back)
    ImageView iv_pay_back;
    @BindView(R2.id.tv_click_upload)
    TextView tvClickUpload;

    private Unbinder mUnbinder;
    @BindView(R2.id.menu_title_tv)
    TextView menu_title_tv;

    @BindView(R2.id.submmit_btn)
    TextView submmit_btn;
    @BindView(R2.id.tv_upload_file_tip)
    TextView tv_upload_file_tip;
    @BindView(R2.id.menu_back)
    View backBtn;

    private int type = 2;//2支付宝  3微信
    private Bitmap cbitmap;
    private TakePhonePicDialog dialog;
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;
    private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 0x04;
    private Uri imageUri;
    private String fileStr = "pay.jpg";
    private File filetPath;
    private File uploadfile;
    private ProgressDialog progress;
    private String url;

    public static void startMe(Activity context, int type, String realName, int requestCode) {
        Intent intent = new Intent(context, BindAlipayOrWechatActivity.class);
        intent.putExtra("type", type);
        Bundle bundle = new Bundle();
        bundle.putString("realName", realName);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, requestCode);
    }


    @Override
    public int initLayout() {
        return R.layout.activity_bind_alipay_wechat;
    }

    @Override
    public void initView() {
        init();
    }

    private void init() {
        progress = new ProgressDialog(this);
        progress.setMessage(this.getResources().getString(R.string.dialog_loading));
        filetPath = PhotoUtils.newFile(fileStr, this);
        iv_pay_img.setOnClickListener(this);
        submmit_btn.setOnClickListener(this);
        iv_pay_back.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        tvClickUpload.setOnClickListener(this);
        type = getIntent().getIntExtra("type", 1);
        if (type == 2) {
            menu_title_tv.setText(R.string.bind_alipay);
            tv_code.setText(R.string.alipay_acount);
            et_code.getInputText().setHint(R.string.input_alipay_acount);
        } else {
            menu_title_tv.setText(R.string.bind_wechat);
            tv_code.setText(R.string.wechat_acount);
            et_code.getInputText().setHint(R.string.input_wechat_acount);
        }
        et_name.getInputText().setHint(R.string.input_name);
        et_pin.setSecondRightPwdEyeHint();

        String realName = getIntent().getStringExtra("realName");
        if (!TextUtils.isEmpty(realName)) {
            et_name.setInputText(realName);
            if (UserInfoController.getInstance().getUserInfo() != null && UserInfoController.getInstance().getUserInfo().supplier) {
                et_name.getInputText().setEnabled(true);
            } else {
                et_name.getInputText().setEnabled(false);
                et_name.setCloseState(View.GONE);
            }
        } else {
            et_name.getInputText().setEnabled(true);
        }

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

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.tv_click_upload) {
            initDialog();
        } else if (id == R.id.iv_pay_img) {
            initDialog();
        } else if (id == R.id.iv_pay_back) {
            initDialog();
        } else if (id == R.id.submmit_btn) {
            if (TextUtils.isEmpty(et_name.getInputStr())) {
                ToastUtil.show(R.string.input_name);
                return;
            }
            if (TextUtils.isEmpty(et_code.getInputStr())) {
                if (type == 2)
                    ToastUtil.show(R.string.input_alipay_acount);
                else {
                    ToastUtil.show(R.string.input_wechat_acount);
                }
                return;
            }
            if (TextUtils.isEmpty(et_pin.getInputStr())) {
                ToastUtil.show(R.string.capital_pwd_is_empty);
                return;
            }
            submit();
        } else if (id == R.id.menu_back) {
            finish();
        }
    }


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
        imageUri = getFromFile(filetPath);
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
            f = filetPath;
        } else {

        }
        rotate(f);
    }

    private void showImageUri(Uri newUri) {
        String path = newUri.getPath();
        showImageview(new File(path));
    }


    //解决三星拍照自己旋转90度的bug
    private void rotate(File f) {
        int degree = PhotoUtils.readPictureDegree(f.getAbsolutePath());
        double fileSize = 0;
        try {
            fileSize = PhotoUtils.getFileSize(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();//获取缩略图显示到屏幕上
        if (fileSize > 1 && fileSize <= 2)
            opts.inSampleSize = 2;
        else if (fileSize > 2 && fileSize <= 4) {
            opts.inSampleSize = 4;
        } else if (fileSize > 4) {
            opts.inSampleSize = 8;
        }
//        opts.inJustDecodeBounds = true;
//        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        cbitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), opts);

        if (cbitmap == null) {
            ToastUtil.show(R.string.select_picture_fail);
            return;
        }
        if (degree != 0) {
            cbitmap = PhotoUtils.rotaingImageView(degree, cbitmap);
        }


        uploadfile = PhotoUtils.saveBitmapFile(this, cbitmap, fileStr);
//        try {
//            PhotoUtils.getFileSize(uploadfile);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        if (uploadfile == null) {
            ToastUtil.show(R.string.select_picture_fail);
            return;
        }
        setImageViewWirth(cbitmap);
        iv_pay_img.setImageBitmap(cbitmap);
        tv_upload_file_tip.setVisibility(View.VISIBLE);
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

    private boolean checkPremissionResultArray(int[] grantResults) {
        if (grantResults == null || grantResults.length == 0) {
            return false;
        }
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults == null || grantResults.length == 0) {
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
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
                    tvClickUpload.setVisibility(View.GONE);
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
                    tvClickUpload.setVisibility(View.GONE);
                    break;
                default:
            }
        }
    }


    private void submit() {
        if (uploadfile == null) {
            if (TextUtils.isEmpty(url)) {
                ToastUtil.show(getString(R.string.upload_qr_code));
                return;
            } else {
                addBindInfo(url);
                if (progress != null && !progress.isShowing()) {
                    progress.show();
                }
            }
        } else {
            upload(uploadfile);
            if (progress != null && !progress.isShowing()) {
                progress.show();
            }
        }


    }

    //上传图片
    private void upload(File file) {
        OkGo.<UploadFileBindPayModel>post(Constants.OTC_UPLOAD_FILE)
                .tag(this)
                .isMultipart(true)
                .params("file", file)
                .execute(new NewJsonSubCallBack<UploadFileBindPayModel>() {
                    @Override
                    public void uploadProgress(Progress progress) {
                        super.uploadProgress(progress);

                    }

                    @Override
                    public void onSuc(Response<UploadFileBindPayModel> response) {
                        if (response.body() != null && response.body().isSuccess() && !TextUtils.isEmpty(response.body().getData())) {
                            addBindInfo(response.body().getData());
                        } else {
                            ToastUtil.show(getString(R.string.request_failed));
                            if (progress.isShowing())
                                progress.dismiss();
                        }
                    }

                    @Override
                    public void onE(Response<UploadFileBindPayModel> response) {
                        if (progress.isShowing())
                            progress.dismiss();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });
    }

    private void addBindInfo(String imgUrl) {
        HttpParams params = new HttpParams();
        params.put("flag", type);
        params.put("name", et_name.getInputStr());
        params.put("account", et_code.getInputStr());
        params.put("qrCode", imgUrl);
        params.put("assetPassword", MD5Util.MD5(et_pin.getInputStr()));

        OkGo.<BaseRes>post(Constants.OTC_BIND_OTHER).params(params).tag(this).execute(new NewJsonSubCallBack<BaseRes>() {
            @Override
            public void onSuc(Response<BaseRes> response) {
                BaseRes baseResponse = response.body();
                if (baseResponse.isSuccess()) {
                    ToastUtil.show(R.string.bind_suc);
                    setResult(200);
                    finish();
                }
            }

            @Override
            public void onE(Response<BaseRes> response) {

            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (progress != null && progress.isShowing())
                    progress.dismiss();
            }
        });
    }

    private void setImageViewWirth(Bitmap bitmap) {

        if (bitmap == null) {
            return;
        }
        int height = bitmap.getHeight();
        float ratio = BigDecimalUtils.divide(String.valueOf(DensityUtil.dip2px( 102)), String.valueOf(height), 2);
//        float ratio = (float) ( / height);
        int width = (int) (bitmap.getWidth() * ratio);
        ViewGroup.LayoutParams lp = iv_pay_img.getLayoutParams();
        lp.width = width;
        iv_pay_img.setLayoutParams(lp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cbitmap != null) {
            cbitmap.recycle();
            cbitmap = null;
        }
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = null;
        }
        //删除目录下所有文件
        FileUtils.deleteFile(FileUtils.getSaveFile(CBRepository.getContext()));
    }
}
