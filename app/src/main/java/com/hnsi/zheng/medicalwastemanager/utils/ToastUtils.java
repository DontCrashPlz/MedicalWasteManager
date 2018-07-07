package com.hnsi.zheng.medicalwastemanager.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Zheng on 2018/5/8.
 */

public class ToastUtils {

    private static Toast toast;

    public static void showShortToast(Context context, String content){
        if (toast== null){
            toast= Toast.makeText(context, content, Toast.LENGTH_SHORT);
        }else {
            toast.setText(content);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    public static void showLongToast(Context context, String content){
        if (toast== null){
            toast= Toast.makeText(context, content, Toast.LENGTH_LONG);
        }else {
            toast.setText(content);
            toast.setDuration(Toast.LENGTH_LONG);
        }
        toast.show();
    }

}
