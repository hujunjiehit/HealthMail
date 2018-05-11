package com.june.healthmail.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.june.healthmail.R;
import com.june.healthmail.adapter.SijiaoListAdapter;
import com.june.healthmail.model.SijiaoInfo;
import com.june.healthmail.untils.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by june on 2018/5/10.
 */

public class ManageSijiaoActivity extends BaseActivity implements View.OnClickListener, SijiaoListAdapter.Callback {

  @BindView(R.id.img_back)
  ImageView mImgBack;
  @BindView(R.id.list_view)
  ListView mListView;
  @BindView(R.id.layout_add_sijiao)
  LinearLayout mLayoutAddSijiao;

  private SijiaoListAdapter mAdapter;
  private List<SijiaoInfo> mSijiaoInfos;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_manage_sijiao);
    ButterKnife.bind(this);
    setListener();
    init();
  }

  private void setListener() {
    mLayoutAddSijiao.setOnClickListener(this);
    mImgBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
  }

  private void init() {
    mSijiaoInfos = PreferenceHelper.getInstance().getSijiaoList();
    if (mSijiaoInfos == null) {
      mSijiaoInfos = new ArrayList<>();
    }

    mAdapter = new SijiaoListAdapter(this, mSijiaoInfos);
    mAdapter.setCallback(this);
    mListView.setAdapter(mAdapter);
  }


  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.layout_add_sijiao:
        if (mSijiaoInfos.size() >= 20) {
          toast("最多只能添加20个私教哦(长按删除不需要的私教)");
        } else {
          showAddSijiaoDialog();
        }
        break;
      default:
        break;
    }
  }

  @Override
  public void onItemClick(int position) {
    if (position < mSijiaoInfos.size()) {
      if (mSijiaoInfos.get(position).isSelected()) {
        mSijiaoInfos.get(position).setSelected(false);
      } else {
        mSijiaoInfos.get(position).setSelected(true);
      }
      PreferenceHelper.getInstance().setSijiaoList(mSijiaoInfos);
      mAdapter.notifyDataSetChanged();
    }
  }

  @Override
  public void onItemLongClick(final int position) {
    if (position < mSijiaoInfos.size()) {
      AlertDialog dialog = new AlertDialog.Builder(this)
        .setTitle("功能选择")
        .setMessage("请选择需要的操作？")
        .setNegativeButton("删除私教", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            mSijiaoInfos.remove(position);
            PreferenceHelper.getInstance().setSijiaoList(mSijiaoInfos);
            mAdapter.notifyDataSetChanged();
            toast("删除成功");
          }
        })
        .setPositiveButton("修改私教", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            showEditSijiaoDialog(position);
          }
        })
        .create();
      dialog.show();
    }
  }

  private void showAddSijiaoDialog() {
    View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_add_sijiao_layout,null);
    final EditText edit_text = (EditText) diaog_view.findViewById(R.id.edit_text);
    final EditText edit_name = (EditText) diaog_view.findViewById(R.id.edit_name);
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("新增私教");
    builder.setView(diaog_view);
    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        String mallId = edit_text.getText().toString().trim();
        String name =  edit_name.getText().toString().trim();
        if(mallId.length() > 0){
          SijiaoInfo sijiaoInfo = new SijiaoInfo();
          sijiaoInfo.setMallId(mallId);
          sijiaoInfo.setName(name);
          sijiaoInfo.setSelected(true);
          mSijiaoInfos.add(sijiaoInfo);
          PreferenceHelper.getInstance().setSijiaoList(mSijiaoInfos);
          mAdapter.notifyDataSetChanged();
          dialog.dismiss();
        }else {
          Toast.makeText(ManageSijiaoActivity.this,"请输入私教猫号",Toast.LENGTH_LONG).show();
        }
      }
    });
    builder.create().show();
  }

  private void showEditSijiaoDialog(final int position) {
    final SijiaoInfo sijiaoInfo = mSijiaoInfos.get(position);
    View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_add_sijiao_layout,null);
    final EditText edit_text = (EditText) diaog_view.findViewById(R.id.edit_text);
    edit_text.setText(sijiaoInfo.getMallId());
    edit_text.setEnabled(false);

    final EditText edit_name = (EditText) diaog_view.findViewById(R.id.edit_name);
    edit_name.setText(sijiaoInfo.getName());

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("修改私教");
    builder.setView(diaog_view);
    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        String mallId = edit_text.getText().toString().trim();
        String name =  edit_name.getText().toString().trim();
        if(mallId.length() > 0){
          mSijiaoInfos.get(position).setName(name);
          PreferenceHelper.getInstance().setSijiaoList(mSijiaoInfos);
          mAdapter.notifyDataSetChanged();
          dialog.dismiss();
        }
      }
    });
    builder.create().show();
  }
}
