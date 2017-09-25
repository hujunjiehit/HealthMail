package com.june.healthmail.untils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;
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
            + "status integer,"
            + "mallId text, "
            + "lastDay text, "
            + "pingjiaTimes integer,"
            + "yuekeTimes integer,"
            + "hasPayed integer"
            + ")";

    private Context mContext;

    public MyDatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("test","onCreate");
        db.execSQL(CREATE_ACOUNT_INFO);//创建数据库表--account
        Toast.makeText(mContext,"db create sucess",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("test","onUpgrade  oldVersion =  " + oldVersion + "   newVersion = " + newVersion );
        if(oldVersion == 1) {
            db.execSQL("ALTER TABLE account ADD  mallId text;");
            db.execSQL("ALTER TABLE account ADD  lastDay text;");
            db.execSQL("ALTER TABLE account ADD  pingjiaTimes integer;");
            db.execSQL("ALTER TABLE account ADD  yuekeTimes integer;");

            db.execSQL("ALTER TABLE account ADD  hasPayed integer;");
        }else if(oldVersion == 2) {
            db.execSQL("ALTER TABLE account ADD  hasPayed integer;");
        }
        Toast.makeText(mContext,"db update sucess",Toast.LENGTH_SHORT).show();
    }
}
