package com.june.healthmail.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import com.june.healthmail.R;
import com.june.healthmail.adapter.OrderListAdapter;
import com.june.healthmail.model.HmOrder;

import java.util.ArrayList;
import java.util.Iterator;

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
        init();
    }

    private void initViews() {
        //webView = (WebView) findViewById(R.id.webview);
        mContainer = (LinearLayout) findViewById(R.id.webview_container);
        webView = new WebView(getApplicationContext());
        mContainer.addView(webView);
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
        try {
            if (webView != null) {
                webView.getSettings().setJavaScriptEnabled(false);

                webView.clearHistory();
                webView.clearCache(true);
                webView.freeMemory();
                webView.pauseTimers();

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
