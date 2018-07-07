package com.hnsi.zheng.medicalwastemanager.apps;

import android.content.Context;

/**
 * 所有页面的父接口
 * Created by Zheng on 2018/4/14.
 */

public interface IBaseView {
    void showLongToast(String msg);
    void showShortToast(String msg);
    Context getRealContext();
}
