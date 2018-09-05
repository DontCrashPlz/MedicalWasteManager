package com.hnsi.zheng.medicalwastemanager.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.BLUETOOTH_SERVICE;

public class BluetoothUtils {

    //蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;
    //搜索状态的标示
    private boolean mScanning = true;
    //蓝牙适配器List
    private List<BluetoothDevice> mBlueList;
    //上下文
    private Context context;
    //单例模式
    private static BluetoothUtils blueUtils;
    //蓝牙的回调地址
    //private BluetoothAdapter.LeScanCallback mLesanCall;
    //扫描执行回调
    private BluetoothUtils.Callbacks callback;

    private BluetoothLeScanner scanner;
    private ScanCallback scanCallback;

    //单例模式
    public static BluetoothUtils getBlueUtils(){
        if(blueUtils == null){
            blueUtils = new BluetoothUtils();
        }
        return blueUtils;
    }

    /***
     * 初始化蓝牙的一些信息
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void getInitialization(Context context){
        this.context = context;

        //初始化蓝牙适配器
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(BLUETOOTH_SERVICE);
        //初始化蓝牙
        mBluetoothAdapter = bluetoothManager.getAdapter();
        scanner= mBluetoothAdapter.getBluetoothLeScanner();
        //初始化List
        mBlueList = new ArrayList<>();
        //实例化蓝牙回调
//        mLesanCall = new BluetoothAdapter.LeScanCallback() {
//            @Override
//            public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
//                //返回三个对象 分类别是 蓝牙对象 蓝牙信号强度 以及蓝牙的广播包
//                if(!mBlueList.contains(bluetoothDevice)){//重复的则不添加
//                    mBlueList.add(bluetoothDevice);
//                    //接口回调
//                    callback.CallbackList(mBlueList);
//                }
//            }
//        };
        scanCallback= new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                BluetoothDevice device = result.getDevice();
                if(!mBlueList.contains(device)){//重复的则不添加
                    mBlueList.add(device);
                    //接口回调
                    callback.CallbackList(mBlueList);
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                LogUtil.e("蓝牙搜索","搜索失败");
            }
        };
    }

    /**
     * 开启蓝牙
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startBlue(){
        if(mScanning){
            mScanning = false;
            //开始扫描并设置回调
            //mBluetoothAdapter.startLeScan(mLesanCall);
            scanner.startScan(scanCallback);
        }
    }

    /**
     * 停止蓝牙扫描
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void stopBlue(){
        if(!mScanning){
            mScanning = true;
            //结束蓝牙扫描
            //mBluetoothAdapter.stopLeScan(mLesanCall);
            scanner.stopScan(scanCallback);
        }
    }

    /**
     * 接口回调
     */
    public interface Callbacks{
        void CallbackList(List<BluetoothDevice> mBlueLis);
    }

    /**
     * 设置接口回调
     * @param callback 自身
     */
    public void setCallback(Callbacks callback) {
        this.callback = callback;
    }

    /**
     * 判断是否支持蓝牙
     * @return
     */
    public boolean isSupportBlue(){
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * 返回蓝牙对象
     * @return
     */
    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }

}
