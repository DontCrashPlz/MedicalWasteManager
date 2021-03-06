package com.hnsi.zheng.medicalwastemanager.apps;

import android.app.Dialog;

import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hnsi.zheng.medicalwastemanager.R;
import com.hnsi.zheng.medicalwastemanager.adapters.BluetoothDeviceRecyclerAdapter;

import com.hnsi.zheng.medicalwastemanager.utils.BluetoothUtils;
import com.hnsi.zheng.medicalwastemanager.utils.LogUtil;
import com.hnsi.zheng.medicalwastemanager.utils.SharedPrefUtils;
import com.hnsi.zheng.medicalwastemanager.widgets.CollectedWasteItemDecoration;

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

    //蓝牙工具类
    private BluetoothUtils blueUtils;
    //蓝牙的Adapter
    private BluetoothDeviceRecyclerAdapter blueAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!= null){
            getSupportActionBar().setTitle("蓝牙设置");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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
        blueUtils = BluetoothUtils.getBlueUtils();
        //初始化工具类
        blueUtils.getInitialization(this);
        //判断是否支持蓝牙
        if (!blueUtils.isSupportBlue()) {
            showShortToast("蓝牙设备准备就绪");
            blueUtils.getmBluetoothAdapter().enable();
        }else {
            showShortToast("设备不支持蓝牙4.0");
        }

        switch (v.getId()){
            case R.id.button1:
                showBluetoothListDialog(1);
                break;
            case R.id.button2:
                showBluetoothListDialog(2);
                break;
            case R.id.button3:
                showBluetoothListDialog(3);
                break;
            default:
                break;
        }
    }

    /**
     * 弹出蓝牙设备弹窗
     * @param tag 1表示电子秤（收集）、2表示蓝牙打印机、3表示电子秤（出库）
     */
    private void showBluetoothListDialog(final int tag){
        View view = LayoutInflater.from(getRealContext()).inflate(R.layout.layout_bluetooth_dialog, null);
        // 设置style 控制默认dialog带来的边距问题
        final Dialog dialog = new Dialog(getRealContext(), R.style.custom_dialog_no_titlebar);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        RecyclerView recyclerView= view.findViewById(R.id.bluetooth_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getRealContext()));
        blueAdapter= new BluetoothDeviceRecyclerAdapter(R.layout.item_bluetooth_device_recycler);
        blueAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                TextView nameTv= view.findViewById(R.id.device_name);
                TextView portTv= view.findViewById(R.id.device_port);
                String deviceStr= nameTv.getText().toString() + "(" + portTv.getText().toString() + ")";
                LogUtil.d("device info", deviceStr);
                if (tag== 1){
                    SharedPrefUtils.put(getRealContext(), AppConstants.SharedPref_Bluetooth_Collect, deviceStr);
                    textView1.setText(deviceStr);
                }else if (tag== 2){
                    SharedPrefUtils.put(getRealContext(), AppConstants.SharedPref_Print, deviceStr);
                    textView2.setText(deviceStr);
                }else if (tag==3){
                    SharedPrefUtils.put(getRealContext(), AppConstants.SharedPref_Bluetooth_Output, deviceStr);
                    textView3.setText(deviceStr);
                }
                showShortToast("设备已保存");
                dialog.dismiss();
            }
        });
        recyclerView.setAdapter(blueAdapter);
        recyclerView.addItemDecoration(new CollectedWasteItemDecoration());
        blueUtils.setCallback(new BluetoothUtils.Callbacks() {
            @Override
            public void CallbackList(List<BluetoothDevice> mBlueLis) {
                blueAdapter.setNewData(mBlueLis);
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                blueUtils.stopBlue();
            }
        });

        blueUtils.startBlue();

        // 设置相关位置，一定要在 show()之后
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

}
