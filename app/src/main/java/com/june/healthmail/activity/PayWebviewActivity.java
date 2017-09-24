package com.june.healthmail.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.june.healthmail.ContentResolver.SMSContentObserver;
import com.june.healthmail.R;
import com.june.healthmail.adapter.OrderListAdapter;
import com.june.healthmail.model.HmOrder;
import com.june.healthmail.service.MyAccessibilityService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by june on 2017/3/12.
 */

public class PayWebviewActivity extends Activity implements View.OnClickListener{

    private WebView webView;
    private String data;
    private ArrayList<HmOrder> ordersList;
    private TextView tvMoreInfo;
    private TextView tvClose;
    private TextView tvTitle;
    private String title;
    private LinearLayout mContainer;
    private SMSContentObserver smsContentObserver;

    private static final int MY_PERMISSION_REQUEST_CODE = 10000;

    private Handler smsHandler = new Handler() {
        //这里可以进行回调的操作
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                //tvCode.setText(msg.obj.toString());
                Log.e("test","handleMessage code:" + msg.obj.toString());
                Intent intent = new Intent(MyAccessibilityService.INTENT_ACTION_STATUS_CHANGE);
                intent.putExtra("code",msg.obj.toString());
                intent.putExtra("state",MyAccessibilityService.STATE_RECEIVE_SMS_CODE);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_webview_layout);
        if(getIntent() != null){
            title = getIntent().getStringExtra("title");
            data = getIntent().getStringExtra("data");
            ordersList = (ArrayList<HmOrder>) getIntent().getSerializableExtra("orders");
            Iterator<HmOrder> it = ordersList.iterator();
            while(it.hasNext()){
                HmOrder x = it.next();
                if(!x.isSelected()){
                    it.remove();
                }
            }
        }
        initViews();

        /**
         * 第 1 步: 检查是否有相应的权限
         */
        boolean isAllGranted = checkPermissionAllGranted(
            new String[] {
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS
            }
        );

        //如果两个权限都有，正常进行
        if (isAllGranted) {
            doTheWork();
        }else {
            /**
             * 第 2 步: 请求权限
             */
            // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
            ActivityCompat.requestPermissions(
                this,
                new String[] {
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_SMS
                },
                MY_PERMISSION_REQUEST_CODE
            );
        }
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    /**
     * 第 3 步: 申请权限结果返回处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;
            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted) {
                // 如果所有的权限都授予了, 则执行备份代码
                 doTheWork();
            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                openAppDetails();
            }
        }
    }

    private void doTheWork() {
        init();
        registObserver();
    }

    /**
     * 打开 APP 的详情设置
     */
    private void openAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("猫友圈辅助功能需要读取手机短信的权限，请到 “应用信息 -> 权限” 中授予！");
        builder.setPositiveButton("去手动授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void registObserver() {
        smsContentObserver = new SMSContentObserver(PayWebviewActivity.this, smsHandler);
        PayWebviewActivity.this.getContentResolver().registerContentObserver( Uri.parse("content://sms/"), true, smsContentObserver);
    }

    private void initViews() {
        webView = (WebView) findViewById(R.id.webview);
        tvMoreInfo = (TextView) findViewById(R.id.tv_more_info);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvClose = (TextView) findViewById(R.id.tv_close);
        tvMoreInfo.setOnClickListener(this);
        tvClose.setOnClickListener(this);
        tvTitle.setText(title);
    }

    @Override
    protected void onDestroy() {
       super.onDestroy();
        if(smsContentObserver != null) {
            PayWebviewActivity.this.getContentResolver().unregisterContentObserver(smsContentObserver);
        }
        try {
            if (webView != null) {
                ViewGroup parent = (ViewGroup) webView.getParent();
                if (parent != null) {
                    parent.removeView(webView);
                }
                webView.removeAllViews();
                webView.destroy();
                webView = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_close:
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.tv_more_info:
                //Toast.makeText(this,"size:"+ordersList.size(),Toast.LENGTH_SHORT).show();
                showAllOrdersDialog();
                break;
            default:
                break;
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        webView.pauseTimers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.resumeTimers();
    }

    private void init() {
        if (data == null) {
            data = "https://item.taobao.com/item.htm?spm=a230r.1.14.21.2l6ruV&id=540430775263";
        }

        //Log.e("test", "data=" + data);
        //启用支持javascript
        webView.getSettings().setJavaScriptEnabled(true);

        //webView.loadUrl(url);

        //webView.loadDataWithBaseURL(null,url, "text/html", "utf-8", null);
        //webView.loadDataWithBaseURL("", url, "text/html", "UTF-8", "");
        //webView.setWebChromeClient(new WebChromeClient());
        webView.loadData(data, "text/html; charset=utf-8", "UTF-8");
        //webView.loadUrl(getFilesDir() + "/index.html");


        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                if (url.startsWith("scheme:") || url.startsWith("scheme：")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    private void showAllOrdersDialog() {
        View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_show_all_orders_layout,null);
        diaog_view.findViewById(R.id.tv_status).setVisibility(View.GONE);

        ListView listView = (ListView) diaog_view.findViewById(R.id.list_view);
        OrderListAdapter adapter = new OrderListAdapter(this,ordersList);
        adapter.hideCheckbox();
        listView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("订单详情");
        builder.setView(diaog_view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
