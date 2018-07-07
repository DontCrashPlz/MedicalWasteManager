package com.hnsi.zheng.medicalwastemanager.apps;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by Zheng on 2017/10/16.
 * 全局Activity管理器
 */

public class ActivityManager {

    private static ActivityManager mInstance= null;

    private static Stack<Activity> mActivityStack= new Stack<>();

    private ActivityManager(){}

    public static ActivityManager getInstance(){
        if (mInstance== null){
            synchronized (ActivityManager.class){
                if (mInstance== null){
                    mInstance= new ActivityManager();
                }
            }
        }
        return mInstance;
    }

    public void addActivity(Activity activity){
        if (mActivityStack== null){
            mActivityStack= new Stack<>();
        }
        mActivityStack.add(activity);
    }

    public void removeActivity(Activity activity){
        if (activity!= null){
            mActivityStack.remove(activity);
            activity.finish();
        }

    }

    public void removeAll(){
        for (int i=0; i<mActivityStack.size(); i++){
            if (mActivityStack.get(i)!= null)
                mActivityStack.get(i).finish();
        }
        mActivityStack.clear();
    }

}
