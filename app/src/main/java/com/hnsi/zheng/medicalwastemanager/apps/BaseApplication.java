package com.hnsi.zheng.medicalwastemanager.apps;

import android.app.Application;

/**
 * Created by Zheng on 2018/4/14.
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * 退出应用程序
     */
    public void AppExit() {
        ActivityManager.getInstance().removeAll();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

}