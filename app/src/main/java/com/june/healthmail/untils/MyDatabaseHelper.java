package com.june.healthmail.untils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.widget.Toast;

/**
 * Created by june on 2017/3/3.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String CREATE_ACOUNT_INFO = "create table account ("
            + "id integer primary key autoincrement, "
            + "phoneNumber text, "
            + "passWord text, "
            + "nickName text, "
            + "status integer)";

    private Context mContext;

    public MyDatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ACOUNT_INFO);//创建数据库表--account
        Toast.makeText(mContext,"db create sucess",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
