package com.hnsi.zheng.medicalwastemanager.apps;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hnsi.zheng.medicalwastemanager.R;
import com.hnsi.zheng.medicalwastemanager.utils.LogUtil;
import com.hnsi.zheng.medicalwastemanager.utils.SharedPrefUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Zheng on 2018/7/24.
 */

public class SettingActivity2 extends BaseActivity implements View.OnClickListener {

    //电子秤（收集）
    @BindView(R.id.edittext1)
    EditText editText1;
    @BindView(R.id.button1)
    Button button1;

    //蓝牙打印机
    @BindView(R.id.edittext2)
    EditText editText2;
    @BindView(R.id.button2)
    Button button2;

    //电子秤（出库）
    @BindView(R.id.edittext3)
    EditText editText3;
    @BindView(R.id.button3)
    Button button3;

    private String mBluetoothPort_1;//蓝牙电子秤（收集）串口号
    private String mBluetoothPort_2;//蓝牙打印机串口号
    private String mBluetoothPort_3;//蓝牙电子秤（出库）串口号

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting2);
        ButterKnife.bind(this);

        Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!= null){
            getSupportActionBar().setTitle("蓝牙设置");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        View.OnFocusChangeListener listener= new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && v instanceof EditText){
                    ((EditText) v).setText("");
                }
            }
        };
        editText1.setOnFocusChangeListener(listener);
        editText2.setOnFocusChangeListener(listener);
        editText3.setOnFocusChangeListener(listener);
        mBluetoothPort_1= (String) SharedPrefUtils.get(getRealContext(), AppConstants.SharedPref_Bluetooth_Collect, "");
        LogUtil.e("串口号1", mBluetoothPort_1);
        if (mBluetoothPort_1== null || "".equals(mBluetoothPort_1)){
            editText1.setHint("未绑定");
        }else {
            editText1.setHint("已绑定：" + mBluetoothPort_1);
        }
        button1.setOnClickListener(this);

        mBluetoothPort_2= (String) SharedPrefUtils.get(getRealContext(), AppConstants.SharedPref_Print, "");
        LogUtil.e("串口号2", mBluetoothPort_2);
        if (mBluetoothPort_2== null || "".equals(mBluetoothPort_2)){
            editText2.setHint("未绑定");
        }else {
            editText2.setHint("已绑定：" + mBluetoothPort_2);
        }
        button2.setOnClickListener(this);

        mBluetoothPort_3= (String) SharedPrefUtils.get(getRealContext(), AppConstants.SharedPref_Bluetooth_Output, "");
        LogUtil.e("串口号3", mBluetoothPort_3);
        if (mBluetoothPort_3== null || "".equals(mBluetoothPort_3)){
            editText3.setHint("未绑定");
        }else {
            editText3.setHint("已绑定：" + mBluetoothPort_3);
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
        String portStr;
        switch (v.getId()){
            case R.id.button1:
                portStr= editText1.getText().toString();
                if (isValidPortNum(portStr)){
                    SharedPrefUtils.put(getRealContext(), AppConstants.SharedPref_Bluetooth_Collect, portStr);
                    editText1.setText("");
                    editText1.setHint("已绑定：" + portStr);
                }else {
                    showShortToast("请输入格式规范的串口号");
                }
                break;
            case R.id.button2:
                portStr= editText2.getText().toString();
                if (isValidPortNum(portStr)){
                    SharedPrefUtils.put(getRealContext(), AppConstants.SharedPref_Print, portStr);
                    editText2.setText("");
                    editText2.setHint("已绑定：" + portStr);
                }else {
                    showShortToast("请输入格式规范的串口号");
                }
                break;
            case R.id.button3:
                portStr= editText3.getText().toString();
                if (isValidPortNum(portStr)){
                    SharedPrefUtils.put(getRealContext(), AppConstants.SharedPref_Bluetooth_Output, portStr);
                    editText3.setText("");
                    editText3.setHint("已绑定：" + portStr);
                }else {
                    showShortToast("请输入格式规范的串口号");
                }
                break;
            default:
                break;
        }
    }

    /**
     * 简单的判断一下串口号格式
     * @param portNum 格式="AB:7D:37:57:34:02"
     * @return
     */
    private boolean isValidPortNum(String portNum){
        if (portNum.length()!= 17) return false;
        String[] strs= portNum.split(":");
        if (strs.length!= 6) return false;
        for (String str : strs){
            if (str.length()!=2) return false;
        }
        return true;
    }

}
