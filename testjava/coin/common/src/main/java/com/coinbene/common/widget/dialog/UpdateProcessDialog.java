package com.coinbene.common.widget.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.coinbene.common.R;
import com.coinbene.common.utils.FileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;


public class UpdateProcessDialog extends Dialog {

    private String downUrl;
    private boolean isForceUpdate;
    private static final int CODE_DOWNLOAD_FAIL = 1;
    private static final int CODE_DOWNLOAD_START = 100;
    private static final int CODE_DOWNLOAD_DONE = 2;
    private static final int CODE_DOWNLOAD_DOING = 3;

    private static final String DEFAUL_METHOD = "GET";
    private static final int DEFAUL_TIMEOUTMILLIS = 10000;
    private static final int DEFAUL_READTIMEOUT = 10000;
    private Context mContext;

    private static final String fileName = "manbiwang.apk";
    private File apkFile;
    private boolean isGoing = false; // 正在下载
    private TextView precent_tv;
    private ProgressBar progress_bar;
    private boolean isCancel = false;
    private TextView retryBtn, tips_precent_tv, total_length_tv,cancel_tv;
    private DecimalFormat fileDf;

    public    UpdateProcessDialog(Context context) {
        this(context, R.style.CoinBene_BottomSheet);
        this.mContext = context;
    }

    public UpdateProcessDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setContentView(R.layout.common_dialog_process_update);

        cancel_tv  = findViewById(R.id.btn_cancel);
        cancel_tv.setOnClickListener(v -> {
            isCancel = true;
            dismiss();
            if (handler != null)
                handler.removeCallbacksAndMessages(null);
        });

        retryBtn = findViewById(R.id.btn_retry);
        retryBtn.setOnClickListener(v -> {
            isCancel = false;
            startDownload();
        });
        retryBtn.setVisibility(View.GONE);
        total_length_tv = (TextView) findViewById(R.id.total_length_tv);
        tips_precent_tv = (TextView) findViewById(R.id.tips_precent_tv);
        precent_tv = (TextView) findViewById(R.id.precent_tv);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        progress_bar.setMax(100);

        fileDf = new DecimalFormat("0.00");
        setCancelable(false);
        setCanceledOnTouchOutside(false);

    }

    public void setData(String downUrl, boolean isForceUpdate) {
        this.downUrl = downUrl;
        this.isForceUpdate = isForceUpdate;
    }

    @Override
    public void show() {
        super.show();
        startDownload();
    }

    private void startDownload() {
        downUrl = downUrl.trim();
        if (TextUtils.isEmpty(downUrl)) {
            return;
        }
        if (isForceUpdate) {
            cancel_tv.setVisibility(View.GONE);
        }
        if (!isGoing) {
            isGoing = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    download();
                    isGoing = false;
                }
            }).start();
        }
    }

    /**
     * 下载文件
     */

    private void download() {
        HttpURLConnection conn = null;
        RandomAccessFile raf = null;
        BufferedInputStream inputStream = null;
        try {
            URL url = new URL(downUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(DEFAUL_TIMEOUTMILLIS);
            conn.setRequestMethod(DEFAUL_METHOD);
            conn.setReadTimeout(DEFAUL_READTIMEOUT); // 读取超时
            apkFile = new File(FileUtils.getApkDownloadFile(getContext()), fileName);
//            Log.e("mxd", "apkFile=" + apkFile.getAbsolutePath());
            apkFile.delete();
            int done = 0;
            inputStream = new BufferedInputStream(conn.getInputStream());
            int fileLength = conn.getContentLength();
            raf = new RandomAccessFile(apkFile, "rwd");
            raf.seek(done);
            byte[] buffer = new byte[1024 * 1024];
            int rc;
            int part = 0; // 用来表示通知handler刷新的长度
            Message msg = new Message();
            msg.what = CODE_DOWNLOAD_START;
            msg.arg2 = fileLength;
            handler.sendMessage(msg);

            double divPart = fileLength * 0.02;
//            Log.e("mxd", "download 1 divPart=" + divPart);
            //没有被取消
            while ((rc = inputStream.read(buffer)) > 0 && !isCancel) {
                raf.write(buffer, 0, rc);
                done += rc;
                part += rc;

                if (part > divPart) {
//                    Log.e("mxd", "download 1 do send done=" + done);
                    // 刷新进度条
                    part = 0;
                    msg = new Message();
                    msg.what = CODE_DOWNLOAD_DOING;
                    msg.arg1 = done;
                    handler.sendMessage(msg);
                }
            }
//            Log.e("mxd", "download 2 isCancel=" + isCancel);
            if (isCancel) {
                msg = new Message();
                msg.what = CODE_DOWNLOAD_FAIL;
                handler.sendMessage(msg);
            } else {
                msg = new Message();
                msg.what = CODE_DOWNLOAD_DONE;
                handler.sendMessage(msg);
            }
        } catch (Exception e) {
//            Log.e("mxd", "download 3");
            handler.sendEmptyMessageDelayed(CODE_DOWNLOAD_FAIL, 500);
            e.printStackTrace();
        } finally {
//            Log.e("mxd", "download 4");
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        int fileLength = 0;

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_DOWNLOAD_START:
                    progress_bar.setProgress(0);
                    precent_tv.setText("0%");
                    precent_tv.setTextColor(getContext().getResources().getColor(R.color.res_blue));
                    tips_precent_tv.setText("0MB");
                    retryBtn.setVisibility(View.GONE);

                    fileLength = msg.arg2;
                    String totalSize = fileDf.format((double) fileLength / 1048576) + "MB";
                    total_length_tv.setText("/" + totalSize);
                    break;
                case CODE_DOWNLOAD_FAIL:
                    retryBtn.setVisibility(View.VISIBLE);
                    precent_tv.setText(mContext.getResources().getString(R.string.update_apk_fail));
                    precent_tv.setTextColor(getContext().getResources().getColor(R.color.res_textColor_3));
                    break;
                case CODE_DOWNLOAD_DONE:
                    progress_bar.setProgress(100);
                    precent_tv.setText(100 + "%");
                    //下载完成，点击安装
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        Uri contentUri = FileProvider.getUriForFile(mContext, getContext().getPackageName() + ".fileProvider", apkFile);
                        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                    } else {
                        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    mContext.startActivity(intent);
                    dismiss();
                    break;
                case CODE_DOWNLOAD_DOING:
                    float doneLength = (float) msg.arg1;
                    if (fileLength == 0) {
                        return;
                    }
                    int progressNum = (int) ((doneLength / fileLength) * 100);
                    // 下载中，更新进度
                    if (progressNum < 0) {
                        return;
                    }
                    progress_bar.setProgress(progressNum);
                    precent_tv.setText(progressNum + "%");
                    String doneStr = fileDf.format((double) doneLength / 1048576) + "MB";
                    tips_precent_tv.setText(doneStr);
                    break;
            }
        }
    };

    /**
     * 文件存放路径
     *
     * @return
     */
//    private String getDownloadpath() {
//        File file = FileUtils.getApkDownloadFile(mContext);
//        return file.getAbsolutePath();
//    }

    public interface UpdateDialogClickListener {
        void onCancel();

        void goToUpdate(String downUrl, boolean isForceUpdate);
    }
}
