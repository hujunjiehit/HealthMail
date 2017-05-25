package com.june.healthmail.view;

import android.accounts.Account;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.june.healthmail.R;
import com.june.healthmail.adapter.AcountListAdapter;
import com.june.healthmail.model.AccountInfo;
import com.june.healthmail.model.MessageDetails;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.DBManager;
import com.june.healthmail.untils.ShowProgress;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;

/**
 * Created by june on 2017/5/16.
 */

public class LeftTopbarOperatePop extends PopupWindow implements View.OnClickListener {

  private View mMenuView;
  private Context mContext;
  private TextView tvOperate1;
  private TextView tvOperate2;
  private TextView tvOperate3;

  private DBManager mDBManger;
  private List<BmobObject> accountList;
  private List<BmobObject> newList;
  private AcountListAdapter mAdapter;
  private ShowProgress showProgress;
  private UserInfo mUserInfo;


  public LeftTopbarOperatePop(Context context, List<BmobObject> list, AcountListAdapter adapter) {
    super(context);
    mContext = context;
    accountList = list;
    mAdapter = adapter;
    mDBManger = DBManager.getInstance(mContext);
    showProgress = new ShowProgress(mContext);
    mUserInfo = BmobUser.getCurrentUser(UserInfo.class);

    mMenuView = View.inflate(context, R.layout.left_top_operate_pop, null);
    tvOperate1 = (TextView) mMenuView.findViewById(R.id.tv_operate_1);
    tvOperate2 = (TextView) mMenuView.findViewById(R.id.tv_operate_2);
    tvOperate3 = (TextView) mMenuView.findViewById(R.id.tv_operate_3);


    setContentView(mMenuView);
    setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
    setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    setFocusable(true);

    tvOperate1.setOnClickListener(this);
    tvOperate2.setOnClickListener(this);
    tvOperate3.setOnClickListener(this);

    ColorDrawable cdw = new ColorDrawable(00000000);
    setBackgroundDrawable(cdw);
  }

  private void toast(String str){
    Toast.makeText(mContext,str,Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.tv_operate_1:
        //备份小号
        dismiss();
        uploadAccount();
        break;
      case R.id.tv_operate_2:
        //恢复小号
        dismiss();
        downloadAccount();
        break;
      case R.id.tv_operate_3:
        //清空云端小号
        dismiss();
        deleteAccount();
        break;
      default:
        break;
    }
  }


  private void downloadAccount() {
    if(showProgress != null && !showProgress.isShowing()){
      showProgress.setMessage("正在获取云端小号...");
      showProgress.show();
    }

    BmobQuery<AccountInfo> query = new BmobQuery<AccountInfo>();
    query.addWhereEqualTo("userName",mUserInfo.getUsername());
    query.setLimit(310);
    query.order("id");
    query.findObjects(new FindListener<AccountInfo>() {
      @Override
      public void done(final List<AccountInfo> object, BmobException e) {
        if(showProgress != null && showProgress.isShowing()){
          showProgress.dismiss();
        }
        if(e==null){
          Log.d("test","查询小号成功,云端：共"+object.size()+"条数据。");
          if(newList == null) {
            newList = new ArrayList<BmobObject>();
          }
          newList.clear();
          for(AccountInfo accountInfo:object){
            if(!isPhoneNumberExist(accountInfo.getPhoneNumber())){
              newList.add(accountInfo);
            }
          }
          Log.d("test","新增 " + newList.size() + "条数据");
          AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
          builder.setTitle("云端恢复提示");
          builder.setMessage("云端小号：" + object.size() + "个\n" + "本地小号：" + accountList.size() + "个\n\n"
              + "新增" + newList.size() + "个小号，是否确定恢复到本地？");
          builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dismiss();
                }
              });
          if(newList.size() > 0) {
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dismiss();
                if (object.size() >= 300) {
                  toast("最多只能备份恢复300个小号");
                  return;
                }
                for(BmobObject obj:newList) {
                  accountList.add(obj);
                  mDBManger.addAccount((AccountInfo)obj);
                }
                mAdapter.notifyDataSetChanged();
                toast("成功恢复" + newList.size() + "个小号到本地");
              }
            });
          }
          builder.create().show();
        }else{
          Log.i("test","查询失败："+e.getMessage()+","+e.getErrorCode());
        }
      }
    });
  }

  private boolean isPhoneNumberExist(String phoneNumber) {
    for(BmobObject obj:accountList) {
      if(((AccountInfo)obj).getPhoneNumber().equals(phoneNumber)) {
        return true;
      }
    }
    return false;
  }

  private void uploadAccount() {
    if(showProgress != null && !showProgress.isShowing()){
      showProgress.setMessage("正在上传小号到云端...");
      showProgress.show();
    }
    for (int i = 0 ; i < accountList.size(); i++) {
      ((AccountInfo)accountList.get(i)).setUserName(mUserInfo.getUsername());
    }

    if (accountList.size() >= 300) {
      toast("最多只能备份恢复300个小号");
      return;
    }

    int times = (accountList.size() - 1)/50 + 1;
    Log.e("test","需要上传次数：" + times);
    for(int i = 0; i < times; i++) {
      if(i == (times - 1)) {
        //最后一页 i * 50 ~ size() - 1
        new BmobBatch().insertBatch(accountList.subList(i*50,accountList.size())).doBatch(new QueryListListener<BatchResult>() {
          @Override
          public void done(List<BatchResult> list, BmobException e) {
            if(showProgress != null && showProgress.isShowing()){
              showProgress.dismiss();
            }
            if(e == null) {
              toast("备份成功");
              Log.e("test","最后页备份成功");
            } else {
              toast("备份失败，错误信息：" + e.getMessage());
            }
          }
        });
      } else {
        //完整一页 0~49 50~99 ... i*50~i*50+49
        new BmobBatch().insertBatch(accountList.subList(i*50,i*50+50)).doBatch(new QueryListListener<BatchResult>() {
          @Override
          public void done(List<BatchResult> list, BmobException e) {
            if(showProgress != null && showProgress.isShowing()){
              showProgress.setMessage("数据上传中，请稍后");
            }
            if(e == null) {
              Log.e("test","中间页备份成功");
            } else {
              toast("备份失败，错误信息：" + e.getMessage());
            }
          }
        });
      }
    }
  }

  private void deleteAccount() {
    if(showProgress != null && !showProgress.isShowing()){
      showProgress.setMessage("正在获取云端小号...");
      showProgress.show();
    }
    BmobQuery<AccountInfo> query = new BmobQuery<AccountInfo>();
    query.addWhereEqualTo("userName",mUserInfo.getUsername());
    query.setLimit(500);
    query.order("id");
    query.findObjects(new FindListener<AccountInfo>() {
      @Override
      public void done(final List<AccountInfo> list, final BmobException e) {
        if(showProgress != null && showProgress.isShowing()){
          showProgress.dismiss();
        }
        if(e==null){
          Log.d("test","查询小号成功,云端：共"+list.size()+"条数据。");
          AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
          builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dismiss();
            }
          });
          if(list.size() > 0) {
            builder.setTitle("重要提示！！！");
            builder.setMessage("云端一共备份了" + list.size() + "个小号，是否确定全部清空？\n(清空之后无法恢复到本地，需要重新备份)");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dismiss();
                if (showProgress != null && !showProgress.isShowing()) {
                  showProgress.setMessage("正在清空云端小号...");
                  showProgress.show();
                }
                if (newList == null) {
                  newList = new ArrayList<BmobObject>();
                }
                newList.clear();
                for (BmobObject obj : list) {
                  newList.add(obj);
                }

                int times = (newList.size() - 1)/50 + 1;
                Log.e("test","需要删除次数：" + times);
                for(int i = 0; i < times; i++) {
                  if (i == (times - 1)) {
                    new BmobBatch().deleteBatch(newList.subList(i*50,newList.size())).doBatch(new QueryListListener<BatchResult>() {
                      @Override
                      public void done(List<BatchResult> list, BmobException e) {
                        if (showProgress != null && showProgress.isShowing()) {
                          showProgress.dismiss();
                        }
                        if (e == null) {
                          toast("清空云端小号成功");
                          Log.e("test","最后页清空成功");
                        } else {
                          toast("清空云端小号成功失败，错误信息：" + e.getMessage());
                        }
                      }
                    });
                  } else {
                    new BmobBatch().deleteBatch(newList.subList(i*50,i*50+50)).doBatch(new QueryListListener<BatchResult>() {
                      @Override
                      public void done(List<BatchResult> list, BmobException e) {
                        if (showProgress != null && showProgress.isShowing()) {
                          showProgress.setMessage("数据清除中，请稍后");
                        }
                        if (e == null) {
                          Log.e("test","中间页清空成功");
                        } else {
                          toast("清空云端小号成功失败，错误信息：" + e.getMessage());
                        }
                      }
                    });
                  }
                }
              }
            });
          } else {
            builder.setTitle("提示");
            builder.setMessage("云端暂无备份小号");
          }
          builder.create().show();
        }else{
          Log.i("test","查询失败："+e.getMessage()+","+e.getErrorCode());
        }
      }
    });
  }
}
