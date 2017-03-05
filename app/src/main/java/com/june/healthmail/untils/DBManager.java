package com.june.healthmail.untils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.june.healthmail.model.AccountInfo;

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

    //清空账号数据库
    public void clearAccountInfo(){
        db.execSQL("delete from account");

        //自增主键归零
        //DELETE FROM sqlite_sequence WHERE name='TableName';
        db.execSQL("delete from sqlite_sequence where name = ?", new String[]{"account"});
    }

    /**
     * 添加小号
     * @param phone
     * @param pwd
     * @return true 添加成功   false 账号存在，添加失败
     */
    public boolean addAccount(String phone, String pwd){
        boolean result = true;
        Cursor cursor = db.rawQuery("select * from account where phoneNumber = ?",new String[]{phone});
        Log.d("test","count = " + cursor.getCount());
        if(cursor.getCount() > 0){
            //电话已存在
            result = false;
        }else {
            db.execSQL("insert into account (phoneNumber,passWord,nickName,status) values (?,?,?,?)",
                new String[]{phone,pwd,"", 1+""});
        }
        return  result;
    }

    public void setStatus(AccountInfo info, int status){
        db.execSQL("update account set status = ? where phoneNumber = ?",
                new String[]{status+"",info.getPhoneNumber()});
    }

    public void setStatus(int status){
        db.execSQL("update account set status = ?",
                new String[]{status+""});
    }

    public void updateNickName(String phoneNumber, String nickName){
        db.execSQL("update account set nickName = ? where phoneNumber = ?",
                new String[]{nickName,phoneNumber});
    }
}
