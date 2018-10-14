package com.hnsi.zheng.medicalwastemanager.apps;

/**
 * Created by Zheng on 2018/4/20.
 */

public class MyApplication extends BaseApplication {

    public static final String TOKEN_TAG= "yifei";

    private static MyApplication mSingleInstance;

    public static MyApplication getInstance(){
        return mSingleInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSingleInstance= this;

        //init MyUncatchExceptionHandler
        MyUncatchExceptionHandler mUncatchExceptionHandler= MyUncatchExceptionHandler.getInstance();
        mUncatchExceptionHandler.init(this, getExternalFilesDir("").getPath());

    }

}