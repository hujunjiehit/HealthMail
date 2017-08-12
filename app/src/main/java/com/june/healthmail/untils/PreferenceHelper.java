package com.june.healthmail.untils;

import android.content.SharedPreferences;
import android.nfc.tech.NfcA;

import com.june.healthmail.Config.CommonConfig;
import com.june.healthmail.base.BasePerference;

/**
 * Created by june on 2017/3/4.
 */

public class PreferenceHelper extends BasePerference{

    public final static String PREFERENCE_KEY_PINGJIA_WORD = "pingjia_words";//评价语
    public final static String MAC_ADDRESS = "mac_address";//本机Mac地址

    public final static String KEY_MIN_PINGJIA_TIME = "min_pingjia_time";//最小评价时间
    public final static String KEY_MAX_PINGJIA_TIME = "max_pingjia_time";//最大评价时间
    public final static String KEY_MIN_YUEKE_TIME = "min_yeke_time";//最小约课时间
    public final static String KEY_MAX_YUEKE_TIME = "max_yueke_time";//最大约课时间
    public final static String KEY_MIN_CONFIG_TIME = "min_config_time";//云端配置的最小时间间隔
    public final static String KEY_MAX_SIJIAO = "max_sijiao";//最大约课私教数

    public final static String KEY_BUY_AUTH_URL = "bug_auth_url";//购买授权淘宝地址
    public final static String KEY_BUY_COINS_URL = "bug_coins_url";//购买金币淘宝地址
    public final static String KEY_UPDATE_LEVEL_URL = "update_level_url";//升级高级永久淘宝地址
    public final static String KEY_COINS_COST_FOR_POST = "coins_cost_for_post";//发帖金币消耗数量
    public final static String KEY_COINS_COST_FOR_SPECIAL_FUNCTION = "coins_cost_for_special_function";//特殊功能金币消耗数量

    public final static String KEY_FREE_TIMES_A_DAY = "free_times_a_day";//每天免费的评价、约课次数
    public final static String KEY_REMAIN_YUEKE_TIMES = "remain_yueke_times";//剩余约课次数
    public final static String KEY_REMAIN_PINGJIA_TIMES = "remain_pingjia_times";//剩余评价次数

    public final static String KEY_PAY_ALL_ORDERS = "pay_all_orders";//
    public final static String KEY_ONLY_TODAY = "yueke_only_today";//是否支付全部订单
    public final static String KEY_REMBER_PWD = "rember_pwd";//是否记住私教密码

    public final static String KEY_PAY_COST = "coins_pay_cost";//付款消耗的金币数量
    public final static String KEY_HAS_ACTIVITY = "has_activity";//是否有新的活动或者公告
    public final static String KEY_QQ_GROUP = "qq_group";//qq交流群
    public final static String KEY_SYSTEM_NOTIFICATION = "system_notification";//系统公告


    public final static String KEY_MAX_COURSES = "max_courses";//最多约多少节课
    public final static String KEY_AUTO_JUMP = "auto_jump";//系统公告

    public final static String KEY_TARGET_NUMBER = "target_number";//目标私教猫号
    public final static String KEY_IS_TARGET_EXIST = "is_target_exist";//目标私教是否存在

    public final static String KEY_UID = "uid";//存储的uid

    public final static String KEY_SIJIAO_UID = "sijiao_uid";//私教账号
    public final static String KEY_SIJIAO_SECRET = "sijiao_secret";//私教密码

    public final static String KEY_COURSE_NUMBER = "course_number";//发布多少节课
    public final static String KEY_COURSE_TITLE = "course_title";//课程标题前缀
    public final static String KEY_COURSE_PICTURE = "course_pic";//课程图片
    public final static String KEY_MAX_COURSE_PEOPLE = "max_course_people";//最大人数
    public final static String KEY_AVERGE_PRICE = "averge_price";//人均价格


    public final static String KEY_ACCESS_TOKEN = "accsee_token";//备份token

    private static PreferenceHelper instance;

    public static PreferenceHelper getInstance() {
        if (instance == null) {
            instance = new PreferenceHelper();
        }
        return instance;
    }

    /**
     * 存储uid
     */
    public void saveUid(String uid) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_UID, uid);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    /**
     * 获取uid
     */
    public String getUid() {
        String word = null;
        checkPrefs();
        if (prefs != null) {
            word = prefs.getString(KEY_UID,"nouid");
        }
        return word;
    }

    /**
     * 存储评价语
     */
    public void savePingjiaWord(String word) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PREFERENCE_KEY_PINGJIA_WORD, word);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }

    }

    /**
     * 获取评价语
     */
    public String getPingjiaWord() {
        String word = null;
        checkPrefs();
        if (prefs != null) {
            word = prefs.getString(PREFERENCE_KEY_PINGJIA_WORD,"很好非常好");
        }
        return word;
    }

    /**
     * 存储mac_address
     */
    public void saveMacAddress(String macaddress) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(MAC_ADDRESS, macaddress);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }

    }

    /**
     * 获取mac_address
     */
    public String getMacAddress() {
        String word = null;
        checkPrefs();
        if (prefs != null) {
            word = prefs.getString(MAC_ADDRESS,"");
        }
        return word;
    }

    public void setMinPingjiaTime(int time) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_MIN_PINGJIA_TIME, time);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }

    }

    public int getMinPingjiaTime() {
        int time = 600;
        checkPrefs();
        if (prefs != null) {
            time = prefs.getInt(KEY_MIN_PINGJIA_TIME,time);
        }
        if(getMinConfigTime() > time) {
            time = getMinConfigTime();
            setMinPingjiaTime(time);
        }
        return time;
    }

    public void setMaxPingjiaTime(int time) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_MAX_PINGJIA_TIME, time);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }

    }

    public int getMaxPingjiaTime() {
        int time = 1000;
        checkPrefs();
        if (prefs != null) {
            time = prefs.getInt(KEY_MAX_PINGJIA_TIME,time);
        }
        if(time < getMinPingjiaTime()) {
            time = getMinPingjiaTime();
            setMaxPingjiaTime(time);
        }
        return time;
    }

    public void setMinYuekeTime(int time) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_MIN_YUEKE_TIME, time);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }

    }

    public int getMinYuekeTime() {
        int time = 600;
        checkPrefs();
        if (prefs != null) {
            time = prefs.getInt(KEY_MIN_YUEKE_TIME,time);
        }
        if(getMinConfigTime() > time) {
            time = getMinConfigTime();
            setMinYuekeTime(time);
        }
        return time;
    }

    public void setMaxYuekeTime(int time) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_MAX_YUEKE_TIME, time);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }

    }

    public int getMaxYuekeTime() {
        int time = 1000;
        checkPrefs();
        if (prefs != null) {
            time = prefs.getInt(KEY_MAX_YUEKE_TIME,time);
        }
        if(time < getMinYuekeTime()) {
            time = getMinYuekeTime();
            setMaxYuekeTime(time);
        }
        return time;
    }

    public void setMinConfigTime(int time) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_MIN_CONFIG_TIME, time);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }

    }

    public int getMinConfigTime() {
        int time = 0;
        checkPrefs();
        if (prefs != null) {
            time = prefs.getInt(KEY_MIN_CONFIG_TIME,time);
        }
        return time;
    }

    public void setMaxSijiao(int time) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_MAX_SIJIAO,time);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }

    }

    public int getMaxSijiao() {
        int value = 20;
        checkPrefs();
        if (prefs != null) {
            value = prefs.getInt(KEY_MAX_SIJIAO,20);
        }
        return value;
    }

    public void setBuyAuthUrl(String url) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_BUY_AUTH_URL,url);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public String getBuyAuthUrl() {
        String url = "https://www.baidu.com/";
        checkPrefs();
        if (prefs != null) {
            url = prefs.getString(KEY_BUY_AUTH_URL,url);
        }
        return url;
    }

    public void setBuyConisUrl(String url) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_BUY_COINS_URL,url);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public String getBuyCoinsUrl() {
        String url = "https://www.baidu.com/";
        checkPrefs();
        if (prefs != null) {
            url = prefs.getString(KEY_BUY_COINS_URL,url);
        }
        return url;
    }

    public void setUpdateLevelUrl(String url) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_UPDATE_LEVEL_URL,url);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public String getUpdateLevelUrl() {
        String url = "https://www.baidu.com/";
        checkPrefs();
        if (prefs != null) {
            url = prefs.getString(KEY_UPDATE_LEVEL_URL,url);
        }
        return url;
    }

    public void setPayAllOrders(boolean getPayAllOrders) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_PAY_ALL_ORDERS,getPayAllOrders);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public boolean getPayAllOrders() {
        boolean value = false;
        checkPrefs();
        if (prefs != null) {
            value = prefs.getBoolean(KEY_PAY_ALL_ORDERS,value);
        }
        return value;
    }

    public void setOnlyToday(boolean getPayAllOrders) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_ONLY_TODAY,getPayAllOrders);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public boolean getOnlyToday() {
        boolean value = false;
        checkPrefs();
        if (prefs != null) {
            value = prefs.getBoolean(KEY_ONLY_TODAY,value);
        }
        return value;
    }

    public void setCoinsCostForPost(int cost) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_COINS_COST_FOR_POST,cost);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public int getCoinsCostForPost() {
        int value = 1;
        checkPrefs();
        if (prefs != null) {
            value = prefs.getInt(KEY_COINS_COST_FOR_POST,value);
        }
        return value;
    }

    public void setCoinsCostForSpecialFunction(int cost) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_COINS_COST_FOR_SPECIAL_FUNCTION,cost);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public int getCoinsCostForSpecialFunction() {
        int value = 2;
        checkPrefs();
        if (prefs != null) {
            value = prefs.getInt(KEY_COINS_COST_FOR_SPECIAL_FUNCTION,value);
        }
        return value;
    }

    public void setFreeTimesPerday(int cost) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_FREE_TIMES_A_DAY,cost);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public int getFreeTimesPerday() {
        int value = 500;
        checkPrefs();
        if (prefs != null) {
            value = prefs.getInt(KEY_FREE_TIMES_A_DAY,value);
        }
        return value;
    }

    public void setRemainYuekeTimes(int cost) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_REMAIN_YUEKE_TIMES,cost);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public int getRemainYuekeTimes() {
        int value = getFreeTimesPerday();
        checkPrefs();
        if (prefs != null) {
            value = prefs.getInt(KEY_REMAIN_YUEKE_TIMES,value);
        }
        return value;
    }

    public void setRemainPingjiaTimes(int cost) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_REMAIN_PINGJIA_TIMES,cost);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public int getRemainPingjiaTimes() {
        int value = getFreeTimesPerday();
        checkPrefs();
        if (prefs != null) {
            value = prefs.getInt(KEY_REMAIN_PINGJIA_TIMES,value);
        }
        return value;
    }

    public void setPayCost(int cost) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_PAY_COST,cost);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public int getPayCost() {
        int value = 1;
        checkPrefs();
        if (prefs != null) {
            value = prefs.getInt(KEY_PAY_COST,value);
        }
        return value;
    }

    public void setHasActivity(int cost) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_HAS_ACTIVITY,cost);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public int getHasActivity() {
        int value = 0;
        checkPrefs();
        if (prefs != null) {
            value = prefs.getInt(KEY_HAS_ACTIVITY,value);
        }
        return value;
    }

    public void setQQGroup(String desc) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_QQ_GROUP,desc);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public String getQQGroup() {
        String desc = "QQ交流群";
        checkPrefs();
        if (prefs != null) {
            desc = prefs.getString(KEY_QQ_GROUP,desc);
        }
        return desc;
    }

    public void setNotification(String desc) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_SYSTEM_NOTIFICATION,desc);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public String getNotification() {
        String desc = "系统公告";
        checkPrefs();
        if (prefs != null) {
            desc = prefs.getString(KEY_SYSTEM_NOTIFICATION,desc);
        }
        return desc;
    }

    public void setMaxCourses(int maxCourses) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_MAX_COURSES,maxCourses);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public int getMaxCourses() {
        int maxCourses = 20;
        checkPrefs();
        if (prefs != null) {
            maxCourses = prefs.getInt(KEY_MAX_COURSES,maxCourses);
        }
        return maxCourses;
    }

    public void setAutoJump(int autoJump) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_AUTO_JUMP,autoJump);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public int getAutoJump() {
        int autoJump = 0;
        checkPrefs();
        if (prefs != null) {
            autoJump = prefs.getInt(KEY_AUTO_JUMP,0);
        }
        return autoJump;
    }

    public void setTargetNumber(String number) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_TARGET_NUMBER,number);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public String getTargetNumber() {
        String number = "";
        checkPrefs();
        if (prefs != null) {
            number = prefs.getString(KEY_TARGET_NUMBER,number);
        }
        return number;
    }

    public void setIsTargetExist(boolean exist) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_IS_TARGET_EXIST,exist);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public boolean getIsTargetExist() {
        boolean exist = false;
        checkPrefs();
        if (prefs != null) {
            exist = prefs.getBoolean(KEY_IS_TARGET_EXIST,exist);
        }
        return exist;
    }

    public void setSijiaoUid(String uid) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_SIJIAO_UID,uid);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public String getSijiaoUid() {
        String uid = "";
        checkPrefs();
        if (prefs != null) {
            uid = prefs.getString(KEY_SIJIAO_UID,uid);
        }
        return uid;
    }

    public void setSijiaoPwd(String pwd) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_SIJIAO_SECRET,pwd);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public String getSijiaoPwd() {
        String pwd = "";
        checkPrefs();
        if (prefs != null) {
            pwd = prefs.getString(KEY_SIJIAO_SECRET,pwd);
        }
        return pwd;
    }

    public void setCourseNumber(int value) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_COURSE_NUMBER,value);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public int getCourseNumber() {
        int value = 10;
        checkPrefs();
        if (prefs != null) {
            value = prefs.getInt(KEY_COURSE_NUMBER,value);
        }
        return value;
    }

    public void setCourseTitle(String value) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_COURSE_TITLE,value);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public String getCourseTitle() {
        String value = "课程-";
        checkPrefs();
        if (prefs != null) {
            value = prefs.getString(KEY_COURSE_TITLE,value);
        }
        return value;
    }

    public void setMaxPeople(int value) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_MAX_COURSE_PEOPLE,value);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public int getMaxPeople() {
        int value = 200;
        checkPrefs();
        if (prefs != null) {
            value = prefs.getInt(KEY_MAX_COURSE_PEOPLE,value);
        }
        return value;
    }

    public void setAvergePrice(float value) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putFloat(KEY_AVERGE_PRICE,value);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public float getAvergePrice() {
        float value = 50;
        checkPrefs();
        if (prefs != null) {
            value = prefs.getFloat(KEY_AVERGE_PRICE,value);
        }
        return value;
    }

    public void setCoursePicture(String value) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_COURSE_PICTURE,value);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public String getCoursePicture() {
        String value = "";
        checkPrefs();
        if (prefs != null) {
            value = prefs.getString(KEY_COURSE_PICTURE,value);
        }
        return value;
    }


    public void setRemberPwd(boolean value) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_REMBER_PWD,value);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public boolean getRemberPwd() {
        boolean value = false;
        checkPrefs();
        if (prefs != null) {
            value = prefs.getBoolean(KEY_REMBER_PWD,value);
        }
        return value;
    }

    public void setAccessToken(String value) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_ACCESS_TOKEN,value);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public String getAccessToken() {
        String value = "";
        checkPrefs();
        if (prefs != null) {
            value = prefs.getString(KEY_ACCESS_TOKEN,"");
        }
        return value;
    }
}
