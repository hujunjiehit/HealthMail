package com.june.healthmail.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;


/**
 * Created by bjhujunjie on 2017/4/8.
 */

public class BaseActivity extends Activity{

    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    }

    protected void toast(String str){
        Toast.makeText(this,str,Toast.LENGTH_LONG).show();
    }

}
