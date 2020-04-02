package com.coinbene.common.widget;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.coinbene.common.Constants;
import com.coinbene.common.R;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.utils.CheckMatcherUtils;
import com.coinbene.common.utils.FileUtils;
import com.coinbene.common.utils.PhotoUtils;
import com.coinbene.common.utils.ShareUtils;
import com.coinbene.common.utils.SiteHelper;
import com.coinbene.common.utils.StringUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.utils.UrlUtil;
import com.google.zxing.client.result.ParsedResultType;
import com.mylhyl.zxing.scanner.encode.QREncode;

import java.util.HashMap;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cn.sharesdk.whatsapp.WhatsApp;

/**
 * ding
 * 2019-05-20
 * com.coinbene.manbiwang.widget.dialog
 */
public class InviteShareDialog extends Dialog implements PlatformActionListener {

    static final String TAG = "InviteShareDialog";

    private ImageView inviteQR;
    private Bitmap encode;
    private ImageView cardView;
    private View shareImg;
    private View cancel;
    private View wechat;
    private View qq;
    private View facebook;
    private View twitter;
    private View frend;
    private View sina;
    private View save;
    private View copy;
    private View whatsapp;


    private String imagePath;
    private String cachePath;
    private StringBuilder inviteUrl = new StringBuilder(Constants.BASE_URL_H5 + "/login/channel.html#/?hash=");

    private String inviteUrlString;

    public InviteShareDialog(@NonNull Context context) {
        this(context, 0);
    }

    public InviteShareDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        cachePath = CBRepository.getContext().getExternalCacheDir().getPath() + "/";
        init();
    }

    public InviteShareDialog(Context context, String inviteCode) {
        this(context);
        String site = SiteHelper.getCurrentSite();
        String account = CheckMatcherUtils.checkEmail(UserInfoController.getInstance().getUserInfo().loginId)
                ? StringUtils.settingEmail(UserInfoController.getInstance().getUserInfo().loginId)
                : StringUtils.settingPhone(UserInfoController.getInstance().getUserInfo().loginId);

        inviteUrl.append(inviteCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_dialog_invite_share);
        initView();
        listener();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (encode != null) {
            encode.recycle();
            encode = null;
        }
    }

    private void init() {
        // 在构造方法里, 传入主题
        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setBackgroundDrawable(new BitmapDrawable());
        // 获取Window的LayoutParams
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setGravity(Gravity.BOTTOM);
        // 一定要重新设置, 才能生效
        window.setAttributes(attributes);
    }

    public void initView() {
        inviteQR = findViewById(R.id.invite_QR);
        cardView = findViewById(R.id.invite_card_view);
        shareImg = findViewById(R.id.share_backGround);
        cancel = findViewById(R.id.cancel);
        wechat = findViewById(R.id.invite_Wechat);
        qq = findViewById(R.id.invite_QQ);
        facebook = findViewById(R.id.invite_Facebook);
        twitter = findViewById(R.id.invite_Twitter);
        frend = findViewById(R.id.invite_Frend);
        sina = findViewById(R.id.invite_Sina);
        save = findViewById(R.id.invite_Save);
        copy = findViewById(R.id.invite_Copy);
        whatsapp = findViewById(R.id.invite_whatsapp);

        inviteUrlString = UrlUtil.replaceH5Url(inviteUrl.toString());

        cardView.post(() -> {
            int margin = (int) (cardView.getWidth() / 3.28);
            int bottomMargin = (int) (cardView.getHeight() / 9.38);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) inviteQR.getLayoutParams();
            params.setMarginStart(margin);
            params.setMarginEnd(margin);
            params.bottomMargin = bottomMargin;
            inviteQR.setLayoutParams(params);
            encode = new QREncode.Builder(inviteQR.getContext())
                    .setMargin(0)
                    .setParsedResultType(ParsedResultType.TEXT)
                    .setContents(inviteUrlString)
                    .build().encodeAsBitmap();
            inviteQR.setImageBitmap(encode);
        });

    }

    public void listener() {
        qq.setOnClickListener(v -> shareQQ());
        cancel.setOnClickListener(v -> dismiss());
        wechat.setOnClickListener(v -> shareWechat());
        frend.setOnClickListener(v -> shareFrend());
        sina.setOnClickListener(v -> shareSina());
        save.setOnClickListener(v -> saveImage());
        copy.setOnClickListener(v -> copyLink());
        facebook.setOnClickListener(v -> shareFaceBook());
        twitter.setOnClickListener(v -> shareTwitter());
        whatsapp.setOnClickListener(v -> showWhatsApp());
    }

    private void showWhatsApp() {
        imagePath = FileUtils.saveBitmap(cachePath, "CoinBene_Invite.JPG", screenShot());
        new ShareUtils.WhatsAppBuilder(WhatsApp.NAME)
//                .setText("CoinBene")
                .setImagePath(imagePath)
//						.setPlatformActionListener(this)
                .share();
    }


    public void shareWechat() {
        if (screenShot() != null) {
            imagePath = FileUtils.saveBitmap(cachePath, "CoinBene_Invite.JPG", screenShot());
            new ShareUtils.WechartBuilder(Wechat.NAME, Platform.SHARE_IMAGE)
//                    .setTitle("CoinBene")
                    .setImagePath(imagePath)
                    .setPlatformActionListener(this)
                    .share();
        }
        dismiss();
    }

    public void shareFrend() {
        if (screenShot() != null) {
            new ShareUtils.WechartBuilder(WechatMoments.NAME, Platform.SHARE_IMAGE)
                    .setTitle("CoinBene")
                    .setBitmap(screenShot())
                    .setPlatformActionListener(this)
                    .share();
        }
        dismiss();
    }

    public void shareFaceBook() {
        if (screenShot() != null) {
            new ShareUtils.FaceBookBuilder(Facebook.NAME)
                    .setText("CoinBene")
                    .setBitmap(screenShot())
                    .setPlatformActionListener(this)
                    .share();
        }
        dismiss();
    }

    public void shareTwitter() {
        if (screenShot() != null) {
            imagePath = FileUtils.saveBitmap(cachePath, "CoinBene_Invite.JPG", screenShot());
            new ShareUtils.TwitterBuilder(Twitter.NAME)
                    .setText("CoinBene")
                    .setImagePath(imagePath)
                    .setPlatformActionListener(this)
                    .share();
        }
        dismiss();
    }

    public void shareSina() {
        if (screenShot() != null) {
            new ShareUtils.SinaBuilder(SinaWeibo.NAME)
                    .setText("CoinBene")
                    .setBitmap(screenShot())
                    .setPlatformActionListener(this)
                    .share();
        }
        dismiss();
    }

    public void shareQQ() {
        if (screenShot() != null) {
            imagePath = FileUtils.saveBitmap(cachePath, "CoinBene_Invite.JPG", screenShot());
            new ShareUtils.QQBuilder()
                    .setImagePath(imagePath)
                    .setPlatformActionListener(this)
                    .share();
        }
        dismiss();
    }

    public void saveImage() {
        //检查权限
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getOwnerActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 12580);
        } else {
            PhotoUtils.saveImageToGallery(getContext(), screenShot());
            ToastUtil.show(getContext().getString(R.string.save_suc));
        }
        dismiss();
    }

    public void copyLink() {
        ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("invite", inviteUrlString);
        cm.setPrimaryClip(mClipData);
        ToastUtil.show(R.string.qr_copy_success);
        dismiss();
    }

    /**
     * @return bitmap
     * 截图
     */
    private Bitmap screenShot() {
        Bitmap bitmap = Bitmap.createBitmap(shareImg.getWidth(), shareImg.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        shareImg.draw(canvas);

        return bitmap;
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        ToastUtil.show(getContext().getString(R.string.share_succes));
        this.dismiss();
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        ToastUtil.show(getContext().getString(R.string.share_failed));
        this.dismiss();
    }

    @Override
    public void onCancel(Platform platform, int i) {
        this.dismiss();
    }

}
