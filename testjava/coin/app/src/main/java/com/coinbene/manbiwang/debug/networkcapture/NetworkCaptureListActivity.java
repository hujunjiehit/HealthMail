package com.coinbene.manbiwang.debug.networkcapture;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.manbiwang.R;
import com.coinbene.manbiwang.debug.networkcapture.adapter.CaptureListAdapter;
import com.coinbene.manbiwang.networkcapture.CaptureEntry;
import com.coinbene.manbiwang.networkcapture.NetworkDataManager;

import butterknife.BindView;

public class NetworkCaptureListActivity extends CoinbeneBaseActivity {

	@BindView(R.id.recycler_view)
	RecyclerView mRecyclerView;

	CaptureListAdapter mAdapter;

	public static void startMe(Context context) {
		Intent intent = new Intent(context, NetworkCaptureListActivity.class);
		context.startActivity(intent);
	}

	@Override
	public int initLayout() {
		return R.layout.activity_network_capture_list;
	}

	@Override
	public void initView() {
		mTopBar.setTitle("抓包列表");

		mAdapter = new CaptureListAdapter(NetworkDataManager.INSTANCE.getData());

		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

		mAdapter.bindToRecyclerView(mRecyclerView);
	}

	@Override
	public void setListener() {
		mAdapter.setOnItemClickListener((adapter, view, position) -> {
			CaptureEntry item = mAdapter.getItem(position);
			NetworkCaptureDetailActivity.startMe(view.getContext(), item);
		});
	}

	@Override
	public void initData() {

	}

	@Override
	public boolean needLock() {
		return false;
	}
}
