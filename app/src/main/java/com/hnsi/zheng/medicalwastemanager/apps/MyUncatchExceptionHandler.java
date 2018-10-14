package com.hnsi.zheng.medicalwastemanager.apps;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.hnsi.zheng.medicalwastemanager.utils.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * This class is used for handle uncatchException.
 * to be specific is to collect decive info and write in a file.
 * Created by Zheng on 2017/6/8.
 */

public class MyUncatchExceptionHandler<T extends BaseApplication> implements Thread.UncaughtExceptionHandler {

    /** 程序的Context对象 */
    private T mApplication;
    /**用于格式化日期,作为日志文件名的一部分*/
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
    /**默认的UncaughtExceptionHandler*/
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private static MyUncatchExceptionHandler INSTANCE;

    /**用来存储设备信息和异常信息*/
    private Map<String, String> infos = new HashMap<>();

    private String mSavePath;

    private MyUncatchExceptionHandler(){}

    /**
     * 通过此方法获取MyUncatchExceptionHandler全局单例
     * @return
     */
    public static MyUncatchExceptionHandler getInstance(){
        if(INSTANCE==null){
            synchronized (MyUncatchExceptionHandler.class){
                if(INSTANCE==null){
                    INSTANCE=new MyUncatchExceptionHandler();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 初始化MyUncatchExceptionHandler，应在Application初始化时进行此操作
     * @param application Application
     * @param savePath
     */
    public void init(T application , String savePath){
        mApplication= application;
        mSavePath= savePath;
        mDefaultHandler=Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        if (!handleException(throwable) && mDefaultHandler!=null){//自定义异常处理失败 && 可以默认异常处理
            mDefaultHandler.uncaughtException(thread,throwable);
        }else{
            mApplication.AppExit();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**\
     * 自定义异常处理方法
     * @param throwable
     * @return
     */
    private boolean handleException(Throwable throwable){
        if (throwable==null){
            return false;
        }

        collectDeciveInfo(mApplication);

        saveExceptionInfo(throwable);

        return true;
    }

    /**
     * 收集设备信息
     * @param ctx
     */
    private void collectDeciveInfo(Context ctx){

        infos.put("phoneModel", Build.MODEL);//手机型号
        infos.put("phoneBrand", Build.BRAND);//手机品牌
        infos.put("androidVersion", Build.VERSION.RELEASE);//安卓版本

        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.d("MyUncatchExceptionHandler", "an error occured when collect package info" + e.toString());
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                LogUtil.d("MyUncatchExceptionHandler", field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                LogUtil.d("MyUncatchExceptionHandler", "an error occured when collect crash info" + e.toString());
            }
        }

    }

    /**
     * @param throwable
     * @return where throwable info save
     */
    private String saveExceptionInfo(Throwable throwable){
        StringBuffer sb = new StringBuffer();

        if (infos!=null&&infos.size()>0){
            for (Map.Entry<String, String> entry : infos.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                sb.append(key + "=" + value + "\n");
            }
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        Throwable cause = throwable.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.flush();
        printWriter.close();
        String result = writer.toString();
        sb.append(result);

        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".log";
            File dir = new File(mSavePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(mSavePath + fileName);
            fos.write(sb.toString().getBytes());
            fos.close();
            return fileName;
        } catch (Exception e) {
            LogUtil.d("MyUncatchExceptionHandler", "an error occured while writing file..." + e.toString());
        }
        return null;
    }

}