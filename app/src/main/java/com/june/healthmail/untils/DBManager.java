package com.june.healthmail.untils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by june on 2017/3/3.
 */

public class DBManager {

    private SQLiteDatabase db;
    private static DBManager instance;
    private Context mContext;

    private DBManager(Context context){
        this.mContext = context;
        db = new MyDatabaseHelper(mContext,"data.db",null,1).getWritableDatabase();
    }

    public static DBManager getInstance(Context context){
        if(instance == null) {
            synchronized (DBManager.class){
                if(instance == null) {
                    instance = new DBManager(context);
                }
            }
        }
        return instance;
    }

    public SQLiteDatabase getDb(){
        return db;
    }
}
