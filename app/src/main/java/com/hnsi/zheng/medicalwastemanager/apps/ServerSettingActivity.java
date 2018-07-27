package com.hnsi.zheng.medicalwastemanager.apps;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
    }

    @Override
    public void initProgressDialog() {

    }
}
