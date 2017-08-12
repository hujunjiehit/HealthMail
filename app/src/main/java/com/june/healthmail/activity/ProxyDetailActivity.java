package com.june.healthmail.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.june.healthmail.R;
import com.june.healthmail.adapter.ProxyUserAdapter;
import com.june.healthmail.model.UserInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by june on 2017/8/12.
 */

public class ProxyDetailActivity extends BaseActivity {

  private UserInfo userInfo;

  @BindView(R.id.ptrScrollView_home)
  PullToRefreshListView mPullToRefreshView;

  //分页加载
  private static final int STATE_REFRESH = 0;   // 下拉刷新
  private static final int STATE_MORE = 1;      // 加载更多
  private int count = 20;                       // 每页的数据是20条
  private int curPage = 0;                      // 当前页的编号，从0开始

  private List<UserInfo> mUsers = new ArrayList<UserInfo>();

  private String historyTime;               //记录最早的时间，用于获取旧的帖子(上拉加载更多)

  private ProxyUserAdapter mAdapter;
  ListView mListView;
  private TextView tvNoMoreData;
  private View noMoreDataView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_show_proxy_detail);
    ButterKnife.bind(this);
    userInfo = BmobUser.getCurrentUser(UserInfo.class);
    if(userInfo.getUserType() < 98) {
      Toast.makeText(this,"当前用户非管理员或者代理人，无法进入代理人页面",Toast.LENGTH_SHORT).show();
      finish();
    }

    noMoreDataView = LayoutInflater.from(mContext).inflate(R.layout.item_no_more_data, null);

    setOnListener();
    init();
  }

  @Override
  protected void onResume() {
    super.onResume();
    Log.e("test", "onResume");
    if(mUsers.size() != 0){
      //getNewData();
    }else{
      getDataByPage(0);
    }
  }


  private void setOnListener() {
    mPullToRefreshView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
    mPullToRefreshView.getLoadingLayoutProxy().setPullLabel("上拉加载更多");
    mPullToRefreshView.getLoadingLayoutProxy().setRefreshingLabel("正在加载");
    // 下拉刷新
    mPullToRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
      @Override
      public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        Log.e("test", "下拉刷新, mUsers.size = " + mUsers.size());
        //getPostByPage(0);
//        if(mPosts.size() != 0){
//          getNewPost();
//        }else{
//          getPostByPage(0);
//        }
      }

      @Override
      public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        Log.e("test", "上拉加载");
        getDataByPage(curPage);
      }
    });

  }

  private void init() {
    mListView = mPullToRefreshView.getRefreshableView();
    mAdapter = new ProxyUserAdapter(this, mUsers);
    mListView.setAdapter(mAdapter);
  }

  private void getDataByPage(int page) {
    BmobQuery<UserInfo> query = new BmobQuery<UserInfo>();
    query.addWhereEqualTo("proxyPerson", userInfo.getUsername().trim());
    //query.addWhereEqualTo("proxyPerson", "15387163766");
    // 按时间升序查询
    query.order("-createdAt");

    if (page == 0) { //第一次获取数据
      curPage = 0;
    }else{  //获取分页的数据
      Date date = null;
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      try {
        date = sdf.parse(historyTime);
      } catch (ParseException e) {
        e.printStackTrace();
      }
      // 只查询大于最后一个item时间的数据
      query.addWhereLessThan("createdAt", new BmobDate(date));
    }
    Log.e("test", "query = " + query);
    query.setLimit(count);
    query.findObjects(new FindListener<UserInfo>() {
      @Override
      public void done(List<UserInfo> list, BmobException e) {
        if (mPullToRefreshView.isRefreshing()) {
          mPullToRefreshView.onRefreshComplete();
        }
        if (e == null) {
          if(curPage == 0) {
            mUsers.clear();
          }
          if (list.size() > 0) {
              for (UserInfo model : list) {
                mUsers.add(model);
              }
            historyTime = list.get(list.size() - 1).getCreatedAt();
            mAdapter.notifyDataSetChanged();
            // 这里在每次加载完数据后，将当前页码+1，这样在上拉刷新的onPullUpToRefresh方法中就不需要操作curPage了
            curPage++;
            Log.e("test", "成功加载第" + curPage + "页数据");
            if(list.size() < count){
              Log.e("test", "没有数据了");
              mListView.addFooterView(noMoreDataView);
              mPullToRefreshView.setMode(PullToRefreshBase.Mode.DISABLED);
            }
          } else {
            Log.e("test", "没有数据了");
            mListView.addFooterView(noMoreDataView);
            mPullToRefreshView.setMode(PullToRefreshBase.Mode.DISABLED);
          }
        } else {
          Log.e("test", "请求数据失败，e" + e.toString());
        }
      }
    });
  }


  @OnClick(R.id.img_back)
  public void btnImageBack(View view){
    finish();
  }
}
