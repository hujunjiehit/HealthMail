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
import com.june.healthmail.adapter.WordListAdapter;
import com.june.healthmail.model.WordInfo;
import com.june.healthmail.untils.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by june on 2018/1/7.
 */

public class ManagePingjiaWordsActivity extends BaseActivity implements View.OnClickListener, WordListAdapter.Callback {

  @BindView(R.id.img_back)
  ImageView mImgBack;
  @BindView(R.id.list_view)
  ListView mListView;
  @BindView(R.id.layout_add_words)
  LinearLayout mLayoutAddWords;

  private WordListAdapter mAdapter;
  private List<WordInfo> mWords;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_manage_pingjia_words);
    ButterKnife.bind(this);
    setListener();
    init();
  }

  private void setListener() {
    mLayoutAddWords.setOnClickListener(this);
    mImgBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
  }

  private void init() {
    mWords = PreferenceHelper.getInstance().getWordList();
    if (mWords == null) {
      mWords = new ArrayList<>();
      WordInfo wordInfo = new WordInfo();
      wordInfo.setWords("很好非常好");
      wordInfo.setSelected(true);
      mWords.add(wordInfo);
    }

    mAdapter = new WordListAdapter(this, mWords);
    mAdapter.setCallback(this);
    mListView.setAdapter(mAdapter);
  }


  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.layout_add_words:
        if (mWords.size() >= 20) {
          toast("最多只能添加20个评价语哦(长按删除无用的评价语)");
        } else {
          showAddWordsDialog();
        }
        break;
      default:
        break;
    }
  }

  @Override
  public void onItemClick(int position) {
    if (position < mWords.size()) {
      if (mWords.get(position).isSelected()) {
        mWords.get(position).setSelected(false);
      } else {
        mWords.get(position).setSelected(true);
      }
      PreferenceHelper.getInstance().setWordList(mWords);
      mAdapter.notifyDataSetChanged();
    }
  }

  @Override
  public void onItemLongClick(final int position) {
    if (position < mWords.size()) {
      AlertDialog dialog = new AlertDialog.Builder(this)
        .setTitle("功能选择")
        .setMessage("请选择需要的操作？")
        .setNegativeButton("删除评价", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            mWords.remove(position);
            PreferenceHelper.getInstance().setWordList(mWords);
            mAdapter.notifyDataSetChanged();
            toast("删除成功");
          }
        })
        .setPositiveButton("修改评价", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            showEditWordsDialog(position);
          }
        })
        .create();
      dialog.show();
    }
  }

  private void showEditWordsDialog(final int position) {
    View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_pingjia_word_layout,null);
    final EditText edit_text = (EditText) diaog_view.findViewById(R.id.edit_text);
    edit_text.setText(mWords.get(position).getWords());
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("修改评价语");
    builder.setView(diaog_view);
    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        String new_pingjia_word = edit_text.getText().toString().trim();
        if(new_pingjia_word.length() >= 5){
          mWords.get(position).setWords(new_pingjia_word);
          PreferenceHelper.getInstance().setWordList(mWords);
          mAdapter.notifyDataSetChanged();
          toast("修改成功");
          dialog.dismiss();
        }else {
          Toast.makeText(ManagePingjiaWordsActivity.this,"评价语需要大于五个字，请重新输入",Toast.LENGTH_LONG).show();
        }
      }
    });
    builder.create().show();
  }

  private void showAddWordsDialog() {
    View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_pingjia_word_layout,null);
    final EditText edit_text = (EditText) diaog_view.findViewById(R.id.edit_text);
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("新增评价语");
    builder.setView(diaog_view);
    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        String new_pingjia_word = edit_text.getText().toString().trim();
        if(new_pingjia_word.length() >= 5){
          WordInfo wordInfo = new WordInfo();
          wordInfo.setWords(new_pingjia_word);
          wordInfo.setSelected(true);
          mWords.add(wordInfo);
          PreferenceHelper.getInstance().setWordList(mWords);
          mAdapter.notifyDataSetChanged();
          dialog.dismiss();
        }else {
          Toast.makeText(ManagePingjiaWordsActivity.this,"评价语需要大于五个字，请重新输入",Toast.LENGTH_LONG).show();
        }
      }
    });
    builder.create().show();
  }
}
