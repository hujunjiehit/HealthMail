package com.june.healthmail.improve.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.june.healthmail.R;
import com.june.healthmail.activity.BaseActivity;
import com.june.healthmail.improve.service.BaseService;
import com.june.healthmail.improve.service.YuekeService;
import com.june.healthmail.untils.CommonUntils;
import com.june.healthmail.untils.PreferenceHelper;
import com.june.healthmail.untils.Tools;

/**
 * Created by june on 2017/3/4.
 */

public class NewYuekeActivity extends BaseActivity implements View.OnClickListener{

    private Button btn_start;
    private TextView tvShowResult;
    private TextView tvRemainTimes;

    private Boolean isRunning = false;

    private int offset;
    private int pageSize = 0;   //总页数

    //单私教最多约课数
    private int per_sijiao_max_courses;
    private TextView tvShowMaxCourses;
    private Button btnEditMaxCourses;
    private TextView tvDescMaxCourses;

    private CheckBox cbOnlyToday;
    private CheckBox cbSortCourse;
    private LinearLayout layoutConfig;
    private TextView tvShowConfig;

    private SeekBar mSeekBar;
    private TextView mBtnMinus;
    private TextView mBtnAdd;

    private YuekeService.YukeBinder mBinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (YuekeService.YukeBinder) service;
            mBinder.setHandler(mHandler);
            mBinder.setPageSize(per_sijiao_max_courses);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BaseService.SHOW_RESULT:
                    showTheResult((String) msg.obj);
                    break;
                case BaseService.UPDATE_TIMES:
                    tvRemainTimes.setText(msg.arg1 + "");
                    break;
                case BaseService.FINISH_YUEKE:
                    btn_start.setText("约课完成");
                    break;
                default:
                    Log.e("test","undefined message");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!CommonUntils.hasPermission()){
            Toast.makeText(this,"当前用户无授权，无法进入本页面",Toast.LENGTH_SHORT).show();
            finish();
        }
        setContentView(R.layout.activity_yueke);
        if(getIntent() != null){
            if(getIntent().getBooleanExtra("exception",false)){
                PreferenceHelper.getInstance().setRemainYuekeTimes(3000);
            }
        }
        initView();
        setListener();
        initData();

        //bindService
        Intent bindIntent = new Intent(this,YuekeService.class);
        bindService(bindIntent,connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        btn_start = (Button) findViewById(R.id.btn_start);
        tvShowResult = (TextView) findViewById(R.id.et_show_result);
        tvShowResult.setMovementMethod(ScrollingMovementMethod.getInstance());
        tvRemainTimes = (TextView) findViewById(R.id.tv_remmain_times);
        tvShowMaxCourses = (TextView) findViewById(R.id.tv_show_max_courses);
        btnEditMaxCourses = (Button) findViewById(R.id.btn_edit_max_courses);
        tvDescMaxCourses = (TextView) findViewById(R.id.tv_desc_max_courses);
        layoutConfig = (LinearLayout) findViewById(R.id.layout_config);
        cbOnlyToday = (CheckBox) findViewById(R.id.cb_only_today);
        tvShowConfig = (TextView)  findViewById(R.id.tv_show_config);
        if (PreferenceHelper.getInstance().getOnlyToday()) {
            cbOnlyToday.setChecked(true);
        } else {
            cbOnlyToday.setChecked(false);
        }

        cbSortCourse = (CheckBox) findViewById(R.id.cb_sort);
        if (PreferenceHelper.getInstance().getSortCourse()) {
            cbSortCourse.setChecked(true);
        } else {
            cbSortCourse.setChecked(false);
        }

        mSeekBar = (SeekBar) findViewById(R.id.seek_bar);
        mSeekBar.setMax(3000);
        mBtnMinus = (TextView) findViewById(R.id.btn_minus);
        mBtnAdd = (TextView) findViewById(R.id.btn_add);
    }

    private void setListener() {
        btn_start.setOnClickListener(this);
        findViewById(R.id.img_back).setOnClickListener(this);
        btnEditMaxCourses.setOnClickListener(this);

        cbOnlyToday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PreferenceHelper.getInstance().setOnlyToday(true);
                } else {
                    PreferenceHelper.getInstance().setOnlyToday(false);
                }
            }
        });

        cbSortCourse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PreferenceHelper.getInstance().setSortCourse(true);
                } else {
                    PreferenceHelper.getInstance().setSortCourse(false);
                }
            }
        });

        tvShowConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layoutConfig.getVisibility() == View.GONE) {
                    layoutConfig.setVisibility(View.VISIBLE);
                    tvShowConfig.setText("隐藏配置");
                }else {
                    layoutConfig.setVisibility(View.GONE);
                    tvShowConfig.setText("显示配置");
                }
            }
        });

        mSeekBar.setProgress(PreferenceHelper.getInstance().getMinYuekeTime());
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.e("test","progress = " + progress);
                Tools.updateCurrentYuekeTime(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mBtnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSeekBar.getProgress() == 0) {
                    toast("亲，无法更快了!");
                    return;
                }
                int result = mSeekBar.getProgress() - 100;
                if(result >= 0) {
                    Tools.updateCurrentYuekeTime(result);
                    mSeekBar.setProgress(result);
                }else {
                    result = 0;
                    Tools.updateCurrentYuekeTime(result);
                    mSeekBar.setProgress(result);
                }
            }
        });

        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSeekBar.getProgress() >= 3000) {
                    toast("亲，无法更慢了!");
                    return;
                }
                int result =mSeekBar.getProgress() + 100;
                if(result <= 3000) {
                    Tools.updateCurrentYuekeTime(result);
                    mSeekBar.setProgress(result);
                }else {
                    result = 3000;
                    Tools.updateCurrentYuekeTime(result);
                    mSeekBar.setProgress(result);
                }
            }
        });
    }

    private void initData() {
        tvRemainTimes.setText(PreferenceHelper.getInstance().getRemainYuekeTimes() + "");
        per_sijiao_max_courses = PreferenceHelper.getInstance().getMaxCourses();
        tvShowMaxCourses.setText(per_sijiao_max_courses + "");
        tvDescMaxCourses.setText(String.format("每个私教最多约%d节课",per_sijiao_max_courses));
        pageSize = (per_sijiao_max_courses - 1)/20 + 1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start:
                if("约课完成".equals(btn_start.getText().toString().trim())){
                    Toast.makeText(this,"约课已完成，如需继续约课请重新进入本页面",Toast.LENGTH_LONG).show();
                }else {
                    if(PreferenceHelper.getInstance().getRemainYuekeTimes() <= 0) {
                        toast("今日约课次数已用完，请充值");
                        return;
                    }
                    if (isRunning == false) {
                        isRunning = true;
                        if(mBinder != null) {
                            btn_start.setText("停止约课");
                            mBinder.startYueke();

                            layoutConfig.setVisibility(View.GONE);
                            tvShowConfig.setText("显示配置");
                        }
                    } else {
                        isRunning = false;
                        if(mBinder != null) {
                            btn_start.setText("开始约课");
                            mBinder.stopYuke();
                        }
                    }
                }
                break;
            case R.id.img_back:	//返回
                finish();
                break;
            case R.id.btn_edit_max_courses:	//修改每个私教最大课程数
                showEditCoursesDialog();
                break;
            default:
                break;
        }
    }

    private void showTheResult(String str){
        tvShowResult.append(str);
        offset = tvShowResult.getLineCount()* tvShowResult.getLineHeight();
        if(offset > tvShowResult.getHeight()){
            tvShowResult.scrollTo(0,offset- tvShowResult.getHeight());
        }
    }



    private void showEditCoursesDialog() {
        View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_max_courses,null);
        final EditText edit_text = (EditText) diaog_view.findViewById(R.id.edit_text);
        edit_text.setInputType(InputType.TYPE_CLASS_NUMBER);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改最大约课数");
        builder.setView(diaog_view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(TextUtils.isEmpty(edit_text.getText().toString().trim())){
                    return;
                }
                int value = Integer.valueOf(edit_text.getText().toString().trim());
                if(value > 100) {
                    toast("每个私教最多只能约100节课");
                }else if(value > 0 && value <= 100){
                    PreferenceHelper.getInstance().setMaxCourses(value);
                    per_sijiao_max_courses = PreferenceHelper.getInstance().getMaxCourses();
                    tvShowMaxCourses.setText(per_sijiao_max_courses + "");
                    tvDescMaxCourses.setText(String.format("每个私教最多约%d节课",per_sijiao_max_courses));
                    if(mBinder != null) {
                        mBinder.setPageSize(per_sijiao_max_courses);
                    }
                }else {
                    toast("数值必须大于0");
                }

            }
        });
        builder.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        mHandler.removeCallbacksAndMessages(null);
    }
}
