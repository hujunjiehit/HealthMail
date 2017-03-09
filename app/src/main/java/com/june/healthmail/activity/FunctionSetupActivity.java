package com.june.healthmail.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.june.healthmail.R;
import com.june.healthmail.untils.PreferenceHelper;

/**
 * Created by june on 2017/3/7.
 */

public class FunctionSetupActivity extends Activity implements View.OnClickListener{

    private TextView tvMinPingjiaTime;
    private TextView tvMaxPingjiaTime;
    private TextView tvMinYuekeTime;
    private TextView tvMaxYuekeTime;

    private Button editMinPingjiaTime;
    private Button editMaxPingjiaTime;
    private Button editMinYuekeTime;
    private Button editMaxYuekeTime;

    private TextView tvMaxSijiao;
    private Button editMaxSijiao;

    private PreferenceHelper mPreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_layout);
        initView();
        setListener();
        initData();
    }

    private void initView() {
        tvMinPingjiaTime = (TextView) findViewById(R.id.tv_min_pingjia_time);
        tvMaxPingjiaTime = (TextView) findViewById(R.id.tv_max_pingjia_time);
        tvMinYuekeTime = (TextView) findViewById(R.id.tv_min_yueke_time);
        tvMaxYuekeTime = (TextView) findViewById(R.id.tv_max_yueke_time);

        editMinPingjiaTime = (Button) findViewById(R.id.edit_min_pingjia_time);
        editMaxPingjiaTime = (Button) findViewById(R.id.edit_max_pingjia_time);
        editMinYuekeTime = (Button) findViewById(R.id.edit_min_yueke_time);
        editMaxYuekeTime = (Button) findViewById(R.id.edit_max_yueke_time);

        tvMaxSijiao = (TextView) findViewById(R.id.tv_max_sijiao);
        editMaxSijiao = (Button) findViewById(R.id.edit_max_sijiao);
    }

    private void setListener() {
        editMinPingjiaTime.setOnClickListener(this);
        editMaxPingjiaTime.setOnClickListener(this);
        editMinYuekeTime.setOnClickListener(this);
        editMaxYuekeTime.setOnClickListener(this);
        editMaxSijiao.setOnClickListener(this);
        findViewById(R.id.img_back).setOnClickListener(this);
    }

    private void initData() {
        mPreferenceHelper = PreferenceHelper.getInstance();
        tvMinPingjiaTime.setText(mPreferenceHelper.getMinPingjiaTime()+"ms(毫秒)");
        tvMaxPingjiaTime.setText(mPreferenceHelper.getMaxPingjiaTime()+"ms(毫秒)");
        tvMinYuekeTime.setText(mPreferenceHelper.getMinYuekeTime()+"ms(毫秒)");
        tvMaxYuekeTime.setText(mPreferenceHelper.getMaxYuekeTime()+"ms(毫秒)");
        tvMaxSijiao.setText(mPreferenceHelper.getMaxSijiao()+"个");
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.edit_min_pingjia_time:
                showEditMinPingjiaDialog();
                break;
            case R.id.edit_max_pingjia_time:
                showEditMaxPingjiaDialog();
                break;
            case R.id.edit_min_yueke_time:
                showEditMinYuekeDialog();
                break;
            case R.id.edit_max_yueke_time:
                showEditMaxYuekeDialog();
                break;
            case R.id.edit_max_sijiao:
                showEditMaxSijiao();
                break;
            case R.id.img_back:	//返回
                finish();
                break;
            default:
                break;
        }

    }


    private void toast(String str){
        Toast.makeText(FunctionSetupActivity.this,str,Toast.LENGTH_LONG).show();

    }

    private void showEditMinPingjiaDialog() {
        View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_pingjia_word_layout,null);
        final EditText edit_text = (EditText) diaog_view.findViewById(R.id.edit_text);
        final TextView tv_text = (TextView) diaog_view.findViewById(R.id.tv_desc);
        tv_text.setText("最小延迟：");
        edit_text.setHint("输入新的延迟时间");
        edit_text.setInputType(InputType.TYPE_CLASS_NUMBER);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改评价最小延迟");
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
                if(TextUtils.isEmpty(edit_text.getText().toString().trim())){
                    return;
                }
                int value = Integer.valueOf(edit_text.getText().toString().trim());
                if(value > 10000 || value <= 0){
                    toast("时间设置必须大于0，并且不能超过10000ms（10秒钟)");
                }else if(value > mPreferenceHelper.getMaxPingjiaTime()){
                    toast("评价最小延迟时间不能超过最大延迟时间");
                }else {
                    mPreferenceHelper.setMinPingjiaTime(value);
                    toast("设置成功");
                    tvMinPingjiaTime.setText(value+"ms(毫秒)");
                    dialog.dismiss();
                }
            }
        });
        builder.create().show();
    }

    private void showEditMaxPingjiaDialog() {
        View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_pingjia_word_layout,null);
        final EditText edit_text = (EditText) diaog_view.findViewById(R.id.edit_text);
        final TextView tv_text = (TextView) diaog_view.findViewById(R.id.tv_desc);
        tv_text.setText("最大延迟：");
        edit_text.setHint("输入新的延迟时间");
        edit_text.setInputType(InputType.TYPE_CLASS_NUMBER);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改评价最大延迟");
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
                if(TextUtils.isEmpty(edit_text.getText().toString().trim())){
                    return;
                }
                int value = Integer.valueOf(edit_text.getText().toString().trim());
                if(value > 10000 || value <= 0){
                    toast("时间设置必须大于0，并且不能超过10000ms（10秒钟)");
                }else if(value < mPreferenceHelper.getMinPingjiaTime()){
                    toast("评价最大延迟时间不能小于最小延迟时间");
                }else {
                    mPreferenceHelper.setMaxPingjiaTime(value);
                    toast("设置成功");
                    tvMaxPingjiaTime.setText(value+"ms(毫秒)");
                    dialog.dismiss();
                }
            }
        });
        builder.create().show();
    }

    private void showEditMinYuekeDialog() {
        View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_pingjia_word_layout,null);
        final EditText edit_text = (EditText) diaog_view.findViewById(R.id.edit_text);
        final TextView tv_text = (TextView) diaog_view.findViewById(R.id.tv_desc);
        tv_text.setText("最小延迟：");
        edit_text.setHint("输入新的延迟时间");
        edit_text.setInputType(InputType.TYPE_CLASS_NUMBER);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改约课最小延迟");
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
                if(TextUtils.isEmpty(edit_text.getText().toString().trim())){
                    return;
                }
                int value = Integer.valueOf(edit_text.getText().toString().trim());
                if(value > 10000 || value <= 0){
                    toast("时间设置必须大于0，并且不能超过10000ms（10秒钟)");
                }else if(value > mPreferenceHelper.getMaxYuekeTime()){
                    toast("约课最小延迟时间不能超过最大延迟时间");
                }else {
                    mPreferenceHelper.setMinYuekeTime(value);
                    toast("设置成功");
                    tvMinYuekeTime.setText(value+"ms(毫秒)");
                    dialog.dismiss();
                }
            }
        });
        builder.create().show();
    }

    private void showEditMaxYuekeDialog() {
        View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_pingjia_word_layout,null);
        final EditText edit_text = (EditText) diaog_view.findViewById(R.id.edit_text);
        final TextView tv_text = (TextView) diaog_view.findViewById(R.id.tv_desc);
        tv_text.setText("最大延迟：");
        edit_text.setHint("输入新的延迟时间");
        edit_text.setInputType(InputType.TYPE_CLASS_NUMBER);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改约课最大延迟");
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
                if(TextUtils.isEmpty(edit_text.getText().toString().trim())){
                    return;
                }
                int value = Integer.valueOf(edit_text.getText().toString().trim());
                if(value > 10000 || value <= 0){
                    toast("时间设置必须大于0，并且不能超过10000ms（10秒钟)");
                }else if(value < mPreferenceHelper.getMinYuekeTime()){
                    toast("约课最大延迟时间不能小于最小延迟时间");
                }else {
                    mPreferenceHelper.setMaxYuekeTime(value);
                    toast("设置成功");
                    tvMaxYuekeTime.setText(value+"ms(毫秒)");
                    dialog.dismiss();
                }
            }
        });
        builder.create().show();
    }

    private void showEditMaxSijiao() {
        View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_pingjia_word_layout,null);
        final EditText edit_text = (EditText) diaog_view.findViewById(R.id.edit_text);
        final TextView tv_text = (TextView) diaog_view.findViewById(R.id.tv_desc);
        tv_text.setText("私教数量：");
        edit_text.setHint("输入最大私教数");
        edit_text.setInputType(InputType.TYPE_CLASS_NUMBER);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改最多约课的私教数量");
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
                if(TextUtils.isEmpty(edit_text.getText().toString().trim())){
                    return;
                }
                int value = Integer.valueOf(edit_text.getText().toString().trim());
                if(value > 20){
                    toast("最大约课私教数量不能超过20");
                }else if(value <= 0){
                    toast("最大约课私教数量必须大于0");
                }else {
                    mPreferenceHelper.setMaxSijiao(value);
                    toast("设置成功");
                    tvMaxSijiao.setText(value+"个");
                    dialog.dismiss();
                }
            }
        });
        builder.create().show();
    }

}
