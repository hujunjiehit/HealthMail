package com.coinbene.manbiwang.debug.networkcapture;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.manbiwang.R;
import com.coinbene.manbiwang.debug.networkcapture.adapter.CaptureDetailAdapter;
import com.coinbene.manbiwang.networkcapture.CaptureEntry;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by june
 * on 2019-12-20
 */
public class NetworkCaptureDetailActivity extends CoinbeneBaseActivity {

	@BindView(R.id.recycler_view)
	RecyclerView mRecyclerView;

	CaptureDetailAdapter mAdapter;

	public static void startMe(Context context, CaptureEntry item) {
		Intent intent = new Intent(context, NetworkCaptureDetailActivity.class);
		intent.putExtra("data", item);
		context.startActivity(intent);
	}

	@Override
	public int initLayout() {
		return R.layout.activity_network_capture_detail;
	}

	@Override
	public void initView() {
		mTopBar.setTitle("抓包详情");

		CaptureEntry captureEntry = (CaptureEntry) getIntent().getSerializableExtra("data");

		List<CaptureDetailAdapter.Entry> list = new ArrayList<>();

		list.add(new CaptureDetailAdapter.Entry("请求方式", captureEntry.getRequestMethod()));
		list.add(new CaptureDetailAdapter.Entry("请求URL", captureEntry.getRequestUrl()));
		if (!TextUtils.isEmpty(captureEntry.getRequestHeader())) {
			list.add(new CaptureDetailAdapter.Entry("请求Header", captureEntry.getRequestHeader()));
		}
		if(!TextUtils.isEmpty(captureEntry.getRequestBody())){
			list.add(new CaptureDetailAdapter.Entry("请求体", captureEntry.getRequestBody()));
		}
		list.add(new CaptureDetailAdapter.Entry("响应状态", captureEntry.getResponseStatus()));
		list.add(new CaptureDetailAdapter.Entry("响应Header", captureEntry.getResponseHeader()));
		list.add(new CaptureDetailAdapter.Entry("响应体", formatJson(captureEntry.getResponseBody())));

		mAdapter = new CaptureDetailAdapter(list);

		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

		mAdapter.bindToRecyclerView(mRecyclerView);
	}

	private String formatJson(String str){
		String json;
		try{
			if (str.startsWith("{")) {
				JSONObject jsonObject = new JSONObject(str);
				json = jsonObject.toString(4);
			} else if (str.startsWith("[")) {
				JSONArray jsonArray = new JSONArray(str);
				json = jsonArray.toString(4);
			} else {
				json = str;
			}

		}catch (Exception e){
			json = str;
		}
		return json;
	}

	@Override
	public void setListener() {

	}

	@Override
	public void initData() {

	}

	@Override
	public boolean needLock() {
		return false;
	}
}
