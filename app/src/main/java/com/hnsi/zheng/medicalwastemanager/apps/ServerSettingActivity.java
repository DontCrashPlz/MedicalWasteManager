package com.hnsi.zheng.medicalwastemanager.apps;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.hnsi.zheng.medicalwastemanager.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Zheng on 2018/7/24.
 */

public class ServerSettingActivity extends BaseActivity {

    @BindView(R.id.edittext1)
    EditText mIpAddressEt;
    @BindView(R.id.edittext2)
    EditText mPortEt;
    @BindView(R.id.button_test)
    Button mConnectTestBtn;
    @BindView(R.id.button_cancel)
    Button mCancelBtn;
    @BindView(R.id.button_confirm)
    Button mConfirmBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_setting);
        ButterKnife.bind(this);

        Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!= null){
            getSupportActionBar().setTitle("服务器设置");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public void initProgressDialog() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== android.R.id.home)
            finish();
        return true;
    }
}
