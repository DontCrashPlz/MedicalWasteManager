package com.hnsi.zheng.medicalwastemanager.apps;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.hnsi.zheng.medicalwastemanager.utils.LogUtil;
import com.hnsi.zheng.medicalwastemanager.utils.ToastUtils;
import com.hnsi.zheng.medicalwastemanager.widgets.progressDialog.ProgressDialog;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Zheng on 2017/10/16.
 */

public abstract class BaseActivity extends AppCompatActivity implements IBaseView {

    private final String ACTIVITY_TAG= this.getClass().getSimpleName();

    public CompositeDisposable compositeDisposable;

    public ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //透明状态栏
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        LogUtil.e(ACTIVITY_TAG, ACTIVITY_TAG + " was Created.");
        ActivityManager.getInstance().addActivity(this);
        //ActivityManager.getInstance().logActivityStackInfo();

        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
        }

        initProgressDialog();
    }

    @Override
    protected void onDestroy() {
        ActivityManager.getInstance().removeActivity(this);
        //ActivityManager.getInstance().logActivityStackInfo();
        LogUtil.e(ACTIVITY_TAG, ACTIVITY_TAG + " was Destroyed.");
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
        super.onDestroy();
    }

    @Override
    public void showLongToast(String msg) {
        if (msg!= null)
            ToastUtils.showLongToast(getRealContext(), msg);
    }

    @Override
    public void showShortToast(String msg) {
        if (msg!= null)
            ToastUtils.showShortToast(getRealContext(), msg);
    }

    @Override
    public Context getRealContext() {
        return this;
    }

    public void addNetWork(Disposable disposable){
        if (compositeDisposable== null){
            compositeDisposable= new CompositeDisposable();
        }
        compositeDisposable.add(disposable);
    }

    public void clearNetWork(){
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }

    public abstract void initProgressDialog();

    public void showDialog(){
        if (dialog!= null && !dialog.isShowing()){
            dialog.show();
        }
    }

    public void dismissDialog(){
        if (dialog!= null && dialog.isShowing()){
            dialog.dismiss();
        }
    }
}
