package com.june.healthmail.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.june.healthmail.R;
import com.june.healthmail.adapter.AcountListAdapter;
import com.june.healthmail.model.AccountInfo;
import com.june.healthmail.untils.DBManager;

import java.util.List;

/**
 * Created by bjhujunjie on 2017/3/3.
 */

public class TopbarOperatePop extends PopupWindow implements View.OnClickListener{

  private View mMenuView;
  private Context mContext;
  private TextView tvOperate1;
  private TextView tvOperate2;
  private TextView tvOperate3;
  private TextView tvOperate4;
  private TextView tvOperate5;
  private DBManager mDBManger;

  private List<AccountInfo> accountList;
  private AcountListAdapter mAdapter;

  public TopbarOperatePop(Context context, List<AccountInfo> list, AcountListAdapter adapter) {
    super(context);
    mContext = context;
    accountList = list;
    mAdapter = adapter;
    mDBManger = DBManager.getInstance(mContext);

    mMenuView = View.inflate(context, R.layout.layout_top_operate_pop, null);
    tvOperate1 = (TextView) mMenuView.findViewById(R.id.tv_operate_1);
    tvOperate2 = (TextView) mMenuView.findViewById(R.id.tv_operate_2);
    tvOperate3 = (TextView) mMenuView.findViewById(R.id.tv_operate_3);
    tvOperate4 = (TextView) mMenuView.findViewById(R.id.tv_operate_4);
    tvOperate5 = (TextView) mMenuView.findViewById(R.id.tv_operate_5);

    setContentView(mMenuView);
    setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
    setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    setFocusable(true);

    tvOperate1.setOnClickListener(this);
    tvOperate2.setOnClickListener(this);
    tvOperate3.setOnClickListener(this);
    tvOperate4.setOnClickListener(this);
    tvOperate5.setOnClickListener(this);

    ColorDrawable cdw = new ColorDrawable(00000000);
    setBackgroundDrawable(cdw);
  }


  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.tv_operate_1:
        //添加小号
        dismiss();
        showAddAcountDialog();
        break;
      case R.id.tv_operate_2:
        //从文件导入小号

        break;
      case R.id.tv_operate_3:
        //清空小号
        dismiss();
        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle("重要警告！！！")
                .setMessage("此操作将清空本地小号且不可恢复，是否继续？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                  }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                    mDBManger.clearAccountInfo();
                    accountList.clear();
                    mAdapter.notifyDataSetChanged();
                  }
                })
                .create();
        dialog.show();
        break;
      case R.id.tv_operate_4:
        //全部选择小号
        dismiss();
        mDBManger.setStatus(1);
        for(int i = 0; i < accountList.size(); i++){
          accountList.get(i).setStatus(1);
          mAdapter.getSelected().put(i,i);
        }
        mAdapter.notifyDataSetChanged();
        break;
      case R.id.tv_operate_5:
        //全部反选小号
        dismiss();
        mDBManger.setStatus(0);
        for(AccountInfo info:accountList){
          info.setStatus(0);
        }
        mAdapter.getSelected().clear();
        mAdapter.notifyDataSetChanged();
        break;
      default:
        break;
    }
  }

  private void showAddAcountDialog() {

    View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_add_account_layout,null);
    final EditText editText = (EditText) dialogView.findViewById(R.id.edit_text);

    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
    builder.setTitle(" 按照格式\"账号|密码\"输入小号，每行一个小号");
    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        //Toast.makeText(mContext, editText.getText().toString(),Toast.LENGTH_SHORT).show();
        String[] lines = editText.getText().toString().trim().split("\\r?\\n");
        for(int i = 0; i < lines.length; i++){
          if(lines[i].contains("|")){
            String [] result = lines[i].split("\\|");
            if(mDBManger.addAccount(result[0],result[1])){
              Log.d("test", result[0] + "--" + result[1] + " 添加成功");
              AccountInfo info = new AccountInfo();
              info.setId(accountList.size()+1);
              info.setNickName("");
              info.setPhoneNumber(result[0]);
              info.setPassWord(result[1]);
              info.setStatus(1);
              accountList.add(info);
              mAdapter.notifyDataSetChanged();
            }else{
              Log.d("test", result[0] + "--" + result[1] + " 已存在，添加失败");
            }
          }else if(lines[i].contains(",")) {
            String[] result = lines[i].split(",");
            if (mDBManger.addAccount(result[0], result[1])) {
              Log.d("test", result[0] + "--" + result[1] + " 添加成功");
              AccountInfo info = new AccountInfo();
              info.setId(accountList.size() + 1);
              info.setNickName("");
              info.setPhoneNumber(result[0]);
              info.setPassWord(result[1]);
              info.setStatus(1);
              accountList.add(info);
              mAdapter.notifyDataSetChanged();
            }
          }
        }
      }
    });
    builder.setView(dialogView);
    builder.create().show();
  }
}
