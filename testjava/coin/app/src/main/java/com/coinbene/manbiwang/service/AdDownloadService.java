package com.coinbene.manbiwang.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.coinbene.common.Constants;
import com.coinbene.common.context.CBRepository;
import com.coinbene.manbiwang.R;

import java.io.File;

public class AdDownloadService extends Service {

    public static final String CHANNEL_ID_STRING = "service_01";

    public static void startMe(Context context, String picUrl) {
        Intent intent1 = new Intent(context, AdDownloadService.class);
        intent1.putExtra("picUrl", picUrl);

        //解决启动service报错问题 参考链接 https://www.cnblogs.com/Sharley/p/10248384.html
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent1);
        } else {
            context.startService(intent1);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //适配8.0service
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID_STRING, getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(mChannel);
            Notification notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID_STRING).build();
            startForeground(1, notification);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        downloadPicOrFile(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void downloadPicOrFile(Intent intent) {
        if (intent == null) {
            return;
        }
        String picUrl = intent.getStringExtra("picUrl");
        if (TextUtils.isEmpty(picUrl)) {
            return;
        }
        RequestManager requestManager = Glide.with(this.getApplicationContext());
        RequestBuilder<File> requestBuilder = requestManager.downloadOnly();
        requestBuilder.listener(new RequestListener<File>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                stopSelf();//停止
                return false;
            }

            @Override
            public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                sendBroadcast();
                return false;
            }
        });
        requestBuilder.load(picUrl);
        requestBuilder.preload();
    }

    private void sendBroadcast() {
        Intent intent = new Intent(Constants.DOWNLOAD_IMAGE_SUCCESS);
        LocalBroadcastManager.getInstance(CBRepository.getContext()).sendBroadcast(intent);
        this.stopSelf();//停止
    }

}
