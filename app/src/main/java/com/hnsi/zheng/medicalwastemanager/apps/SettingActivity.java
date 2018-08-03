package com.hnsi.zheng.medicalwastemanager.apps;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.hnsi.zheng.medicalwastemanager.R;
import com.hnsi.zheng.medicalwastemanager.beans.OutputBucketEntity;
import com.hnsi.zheng.medicalwastemanager.utils.SharedPrefUtils;
import com.qs.helper.printer.Device;
import com.qs.helper.printer.PrintService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Zheng on 2018/7/24.
 */

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    //电子秤（收集）
    @BindView(R.id.textView1)
    TextView textView1;
    @BindView(R.id.button1)
    Button button1;

    //蓝牙打印机
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.button2)
    Button button2;

    //电子秤（出库）
    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.button3)
    Button button3;

    private String mBluetoothPort_1;//蓝牙电子秤（收集）串口号
    private String mBluetoothPort_2;//蓝牙打印机串口号
    private String mBluetoothPort_3;//蓝牙电子秤（出库）串口号

    private List<Device> deviceList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        mBluetoothPort_1= (String) SharedPrefUtils.get(getRealContext(), AppConstants.SharedPref_Bluetooth_Collect, "");
        if (mBluetoothPort_1== null || "".equals(mBluetoothPort_1)){
            textView1.setText("未绑定");
        }else {
            textView1.setText(mBluetoothPort_1);
        }
        button1.setOnClickListener(this);

        mBluetoothPort_2= (String) SharedPrefUtils.get(getRealContext(), AppConstants.SharedPref_Print, "");
        if (mBluetoothPort_2== null || "".equals(mBluetoothPort_2)){
            textView2.setText("未绑定");
        }else {
            textView2.setText(mBluetoothPort_2);
        }
        button2.setOnClickListener(this);

        mBluetoothPort_3= (String) SharedPrefUtils.get(getRealContext(), AppConstants.SharedPref_Bluetooth_Output, "");
        if (mBluetoothPort_3== null || "".equals(mBluetoothPort_3)){
            textView3.setText("未绑定");
        }else {
            textView3.setText(mBluetoothPort_3);
        }
        button3.setOnClickListener(this);

        deviceList=new ArrayList<>();

    }

    @Override
    public void initProgressDialog() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== android.R.id.home)
            finish();
        if (item.getItemId()== R.id.server_setting){
            startActivity(new Intent(getRealContext(), ServerSettingActivity.class));
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.server_setting, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        if(deviceList!= null) {
            deviceList.clear();
        }else {
            deviceList= new ArrayList<>();
        }
        if (!PrintService.pl.IsOpen()) {
            PrintService.pl.open(getRealContext());
        }
        PrintService.pl.scan();
        deviceList = PrintService.pl.getDeviceList();
        switch (v.getId()){
            case R.id.button1:
                break;
            case R.id.button2:
                break;
            case R.id.button3:
                break;
            default:
                break;
        }
    }

    /**
     * 弹出蓝牙设备弹窗
     * @param tag 1表示电子秤（收集）、2表示蓝牙打印机、3表示电子秤（出库）
     */
    private void showBluetoothListDialog(int tag){
        View view = LayoutInflater.from(getRealContext()).inflate(R.layout.layout_bluetooth_dialog, null);
        // 设置style 控制默认dialog带来的边距问题
        final Dialog dialog = new Dialog(getRealContext(), R.style.custom_dialog_no_titlebar);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        RecyclerView recyclerView= view.findViewById(R.id.bluetooth_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getRealContext()));


        // 设置相关位置，一定要在 show()之后
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

}
