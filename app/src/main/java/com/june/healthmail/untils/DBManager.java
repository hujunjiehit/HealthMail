package com.june.healthmail.untils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.june.healthmail.model.AccountInfo;
import com.june.healthmail.model.HmMemberUserVo;

/**
 * Created by june on 2017/3/3.
 */

public class DBManager {

    private SQLiteDatabase db;
    private static DBManager instance;
    private Context mContext;

    private DBManager(){
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public static DBManager getInstance(){
        if(instance == null) {
            synchronized (DBManager.class){
                if(instance == null) {
                    instance = new DBManager();
                }
            }
        }
        return instance;
    }

    public SQLiteDatabase getDb(){
        if(db == null) {
            db = new MyDatabaseHelper(mContext,"data.db",null,4).getWritableDatabase();
        }
        return db;
    }

    //清空账号数据库
    public void clearAccountInfo(){
        getDb().execSQL("delete from account");

        //自增主键归零
        //DELETE FROM sqlite_sequence WHERE name='TableName';
        getDb().execSQL("delete from sqlite_sequence where name = ?", new String[]{"account"});
    }

    /**
     * 添加小号
     * @param phone
     * @param pwd
     * @return true 添加成功   false 账号存在，添加失败
     */
    public boolean addAccount(String phone, String pwd){
        boolean result = true;
        Cursor cursor = getDb().rawQuery("select * from account where phoneNumber = ?",new String[]{phone});
        if(cursor.getCount() > 0){
            //电话已存在
            result = false;
        }else {
            getDb().execSQL("insert into account (phoneNumber,passWord,nickName,status) values (?,?,?,?)",
                new String[]{phone,pwd,"", 1+""});
        }
        return  result;
    }

    /**
     * 添加小号
     * @param accountInfo
     * @return true 添加成功   false 账号存在，添加失败
     */
    public boolean addAccount(AccountInfo accountInfo){
        boolean result = true;
        Cursor cursor = getDb().rawQuery("select * from account where phoneNumber = ?",new String[]{accountInfo.getPhoneNumber()});
        if(cursor.getCount() > 0){
            //电话已存在
            result = false;
        }else {
            getDb().execSQL("insert into account (phoneNumber,passWord,nickName,status) values (?,?,?,?)",
                new String[]{accountInfo.getPhoneNumber(),accountInfo.getPassWord(),accountInfo.getNickName(), accountInfo.getStatus()+""});
        }
        return  result;
    }

    public void setStatus(AccountInfo info, int status){
        getDb().execSQL("update account set status = ? where phoneNumber = ?",
                new String[]{status+"",info.getPhoneNumber()});
    }

    public void setStatus(int status){
        getDb().execSQL("update account set status = ?",
                new String[]{status+""});
    }

    public void setStatus(int status, int begin, int end){
        for(int i = begin; i <= end; i++) {
            getDb().execSQL("update account set status = ? where id = ?",
                    new String[]{status+"",i+""});
        }
    }

    public void updateUserInfo(String phoneNumber, HmMemberUserVo userInfo){
        getDb().execSQL("update account set nickName = ? , mallId = ? where phoneNumber = ?",
                new String[]{userInfo.getNickName(),userInfo.getMallId(),phoneNumber});
    }

    //设置账号密码不可用
    public void setPwdInvailed(String phoneNumber){
        getDb().execSQL("update account set status = ? where phoneNumber = ?",
            new String[]{"-1",phoneNumber});
    }

    //设置账号请求失效
    public void setRequestInvailed(String phoneNumber){
        getDb().execSQL("update account set status = ? where phoneNumber = ?",
            new String[]{"-2",phoneNumber});
    }

    public void updateAccountInfo(int id, String phonenumber, String pwd) {
        getDb().execSQL("update account set phoneNumber = ? , passWord = ?, status = ? where id = ?",
                new String[]{phonenumber,pwd,1+"",id+""});
    }

    public void deleteAccountInfo(String phonenumber) {
        //db.execSQL("update account set phoneNumber = ? , passWord = ?, status = ? where id = ?",
           // new String[]{phonenumber,pwd,1+"",id+""});
        getDb().delete("account","phonenumber = ?", new String[]{phonenumber});
    }

    //更新约课次数
    public void updateYukeTimes(AccountInfo accountInfo){
        getDb().execSQL("update account set yuekeTimes = ? , lastDay = ? where phoneNumber = ?",
            new String[]{accountInfo.getYuekeTimes()+"", accountInfo.getLastDay(), accountInfo.getPhoneNumber() });
    }

    //更新评价次数
    public void updatePingjiaTimes(AccountInfo accountInfo){
        getDb().execSQL("update account set pingjiaTimes = ? , lastDay = ? where phoneNumber = ?",
            new String[]{accountInfo.getPingjiaTimes()+"", accountInfo.getLastDay(), accountInfo.getPhoneNumber() });
    }

    //更新hasPayed
    public void updateHasPayed(AccountInfo accountInfo){
        getDb().execSQL("update account set hasPayed = ? , lastDay = ? where phoneNumber = ?",
            new String[]{accountInfo.getHasPayed()+"", accountInfo.getLastDay(), accountInfo.getPhoneNumber() });
    }

    //更新评价次数、清空约课私教
    public void resetPJYKTimes(AccountInfo accountInfo){
        getDb().execSQL("update account set pingjiaTimes = ? , yuekeTimes = ?, sijiaoName = ?, hasPayed = ? where phoneNumber = ?",
            new String[]{accountInfo.getPingjiaTimes()+"", accountInfo.getYuekeTimes()+"", accountInfo.getSijiaoName(), accountInfo.getHasPayed() + "", accountInfo.getPhoneNumber() });
    }

    //更新评价次数、清空约课私教
    public void updateYuekeSijiao(AccountInfo accountInfo){
        getDb().execSQL("update account set sijiaoName = ? where phoneNumber = ?",
          new String[]{accountInfo.getSijiaoName(), accountInfo.getPhoneNumber() });
    }

    //根据mallId获取用户手机号
    public QueryResult getPhoneByMallID(String mallId){
        QueryResult result = null;
        Cursor cursor = getDb().rawQuery("select * from account where mallId = ? ",new String[]{mallId});
        if(cursor.moveToFirst()){
            result = new QueryResult();
            result.setPhoneNumber(cursor.getString(cursor.getColumnIndex("phoneNumber")));
            result.setId(cursor.getString(cursor.getColumnIndex("id")));
        }
        return result;
    }

    public static class QueryResult {
        private String phoneNumber;
        private String id;

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
