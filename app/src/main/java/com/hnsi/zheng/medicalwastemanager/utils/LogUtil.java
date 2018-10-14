package com.hnsi.zheng.medicalwastemanager.utils;

import android.util.Log;

/**
 * Created by Zheng on 2018/2/22.
 */

public class LogUtil {
    public static final int VERBOSE= 1;
    public static final int DEBUG= 2;
    public static final int INFO= 3;
    public static final int WARN= 4;
    public static final int ERROR= 5;
    public static final int NOTHING= 6;

    public static int level= VERBOSE;

    public static void v(String tag, String msg){
        if (level<= VERBOSE){
            if (msg== null || msg.length()== 0){
                Log.v(tag, "打印消息无效");
                return;
            }
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg){
        if (level<= DEBUG){
            if (msg== null || msg.length()== 0){
                Log.d(tag, "打印消息无效");
                return;
            }
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg){
        if (level<= INFO){
            if (msg== null || msg.length()== 0){
                Log.i(tag, "打印消息无效");
                return;
            }
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg){
        if (level<= WARN){
            if (msg== null || msg.length()== 0){
                Log.w(tag, "打印消息无效");
                return;
            }
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg){
        if (level<= ERROR){
            if (msg== null || msg.length()== 0){
                Log.e(tag, "打印消息无效");
                return;
            }
            Log.e(tag, msg);
        }
    }

}
