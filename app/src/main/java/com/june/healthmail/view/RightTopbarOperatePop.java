package com.june.healthmail.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.june.healthmail.R;
import com.june.healthmail.adapter.AcountListAdapter;
import com.june.healthmail.model.AccountInfo;
import com.june.healthmail.untils.DBManager;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by bjhujunjie on 2017/3/3.
 */

public class RightTopbarOperatePop extends PopupWindow implements View.OnClickListener{

  private View mMenuView;
  private Context mContext;
  private TextView tvOperate1;
  private TextView tvOperate2;
  private TextView tvOperate3;
  private TextView tvOperate4;
  private TextView tvOperate5;
  private TextView tvOperate6;
  private DBManager mDBManger;

  private List<BmobObject> accountList;
  private AcountListAdapter mAdapter;

  public RightTopbarOperatePop(Context context, List<BmobObject> list, AcountListAdapter adapter) {
    super(context);
    mContext = context;
    accountList = list;
    mAdapter = adapter;
    mDBManger = DBManager.getInstance();

    mMenuView = View.inflate(context, R.layout.layout_top_operate_pop, null);
    tvOperate1 = (TextView) mMenuView.findViewById(R.id.tv_operate_1);
    tvOperate2 = (TextView) mMenuView.findViewById(R.id.tv_operate_2);
    tvOperate3 = (TextView) mMenuView.findViewById(R.id.tv_operate_3);
    tvOperate4 = (TextView) mMenuView.findViewById(R.id.tv_operate_4);
    tvOperate5 = (TextView) mMenuView.findViewById(R.id.tv_operate_5);
    tvOperate6 = (TextView) mMenuView.findViewById(R.id.tv_operate_6);

    setContentView(mMenuView);
    setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
    setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    setFocusable(true);

    tvOperate1.setOnClickListener(this);
    tvOperate2.setOnClickListener(this);
    tvOperate3.setOnClickListener(this);
    tvOperate4.setOnClickListener(this);
    tvOperate5.setOnClickListener(this);
    tvOperate6.setOnClickListener(this);

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
          ((AccountInfo)accountList.get(i)).setStatus(1);
          mAdapter.getSelected().put(i,i);
        }
        mAdapter.notifyDataSetChanged();
        break;
      case R.id.tv_operate_5:
        //全部反选小号
        dismiss();
        mDBManger.setStatus(0);
        for(BmobObject info:accountList){
          ((AccountInfo)info).setStatus(0);
        }
        mAdapter.getSelected().clear();
        mAdapter.notifyDataSetChanged();
        break;
      case R.id.tv_operate_6:
        //按编号启用小号
        dismiss();
        showInputDialog();
//        mDBManger.setStatus(0);
//        for(AccountInfo info:accountList){
//          info.setStatus(0);
//        }
//        mAdapter.getSelected().clear();
//        mAdapter.notifyDataSetChanged();
        break;
      default:
        break;
    }
  }

  private void showInputDialog() {
    View diaog_view = LayoutInflater.from(mContext).inflate(R.layout.dialog_inout_number,null);
    final EditText edit_text_begin = (EditText) diaog_view.findViewById(R.id.et_begin_number);
    final EditText edit_text_end = (EditText) diaog_view.findViewById(R.id.et_end_number);
    edit_text_begin.setInputType(InputType.TYPE_CLASS_NUMBER);
    edit_text_end.setInputType(InputType.TYPE_CLASS_NUMBER);

    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
    builder.setTitle("输入开始和结束编号");
    builder.setView(diaog_view);
    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        if(TextUtils.isEmpty(edit_text_begin.getText().toString().trim())
                || TextUtils.isEmpty(edit_text_end.getText().toString().trim())){
          toast("编号不能为空！");
          return;
        }
        int begin,end;
        begin = Integer.valueOf(edit_text_begin.getText().toString().trim());
        end = Integer.valueOf(edit_text_end.getText().toString().trim());
        if(begin <= 0) {
          toast("开始编号必须大于0");
          return;
        }
        if(begin > end) {
          toast("开始编号不能大于结束编号");
          return;
        }
        if(end > accountList.size()) {
          toast("结束编号不能大于最大编号");
          return;
        }
        for (int i = begin; i <= end; i++) {
          mDBManger.setStatus((AccountInfo)accountList.get(i-1),1);
        }
        //mDBManger.setStatus(1,begin,end);
        Log.e("test", "begin = " + begin + "   end = " + end);
        for(int i = begin; i <= end; i++){
          Log.e("test", "i = " + i);
          ((AccountInfo)accountList.get(i-1)).setStatus(1);
          mAdapter.getSelected().put(i-1,i-1);
        }
        mAdapter.notifyDataSetChanged();
      }
    });
    builder.create().show();
  }

  private void toast(String str){
    Toast.makeText(mContext,str,Toast.LENGTH_SHORT).show();
  }

  private void showAddAcountDialog() {

    View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_add_account_layout,null);
    final EditText editText = (EditText) dialogView.findViewById(R.id.edit_text);

    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
    builder.setTitle("按照\"账号,密码\"或者\"帐号|密码\"的格式输入小号，每行一个小号,支持多行");
    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        //Toast.makeText(mContext, editText.getText().toString(),Toast.LENGTH_SHORT).show();
        String[] lines = editText.getText().toString().trim().split("\\r?\\n");
        for(int i = 0; i < lines.length; i++){
          if(lines[i].contains("|")){
            String [] result = lines[i].split("\\|");
            if(result.length < 2){
              toast("账号格式不对，账号密码在同一行，用|分开");
              return;
            }
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
              toast(result[0]+"账号已存在，添加失败");
              Log.d("test", result[0] + "--" + result[1] + " 已存在，添加失败");
            }
          }else if(lines[i].contains(",")) {
            String[] result = lines[i].split(",");
            if(result.length < 2){
              toast("账号格式不对，账号密码在同一行，用\",\"分开");
              return;
            }
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
            }else{
              toast(result[0]+"账号已存在，添加失败");
              Log.d("test", result[0] + "--" + result[1] + " 已存在，添加失败");
            }
          }else if(lines[i].contains("，")) {
            String[] result = lines[i].split("，");
            Log.e("test","length = " + result.length);
            if(result.length < 2){
              toast("账号格式不对，账号密码在同一行，用\"，\"分开");
              return;
            }
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
            }else{
              toast(result[0]+"账号已存在，添加失败");
              Log.d("test", result[0] + "--" + result[1] + " 已存在，添加失败");
            }
          }
        }
      }
    });
    builder.setView(dialogView);
    builder.create().show();
  }
}
