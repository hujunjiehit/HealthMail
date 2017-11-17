package com.june.healthmail.view;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.june.healthmail.R;
import com.june.healthmail.http.bean.Topic;
import com.june.healthmail.untils.Tools;

/**
 * Created by june on 2017/11/16.
 */

public class CommonTopicView extends FrameLayout {

  private Context mContext;

  private TextView mTitle;
  private TextView mContent;
  private TextView mUrlDesc;

  private Topic mTopic;

  public CommonTopicView(@NonNull Context context) {
    super(context);
    this.mContext = context;
    init();
  }

  public CommonTopicView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    this.mContext = context;
    init();
  }

  public CommonTopicView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.mContext = context;
    init();
  }

  private void init() {
    inflate(mContext, R.layout.layout_common_view, this);
    mTitle = (TextView) findViewById(R.id.activity_tittle);
    mContent = (TextView) findViewById(R.id.tv_activity_content);
    mUrlDesc = (TextView) findViewById(R.id.tv_url_desc);
  }

  private void initView(final Topic topic) {
    mTopic = topic;
    mTitle.setText(topic.getModuleName());
    mContent.setText(topic.getModuleContent());
    if(TextUtils.isEmpty(topic.getUrl())) {
      mUrlDesc.setVisibility(GONE);
    }else {
      mUrlDesc.setVisibility(VISIBLE);
      mUrlDesc.setText(mTopic.getUrlDesc());
      mUrlDesc.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          Tools.openTaobaoShopping(mContext, topic.getUrl());
        }
      });
    }
  }

  public void setData(Topic topic) {
    initView(topic);
  }
}
