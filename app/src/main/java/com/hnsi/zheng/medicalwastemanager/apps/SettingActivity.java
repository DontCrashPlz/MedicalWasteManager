package com.hnsi.zheng.medicalwastemanager.apps;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.TextView;

import com.hnsi.zheng.medicalwastemanager.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Zheng on 2018/7/24.
 */

public class SettingActivity extends BaseActivity {

    @BindView(R.id.textView1)
    TextView textView1;
    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.button2)
    Button button2;
    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.button3)
    Button button3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
    }

    @Override
    public void initProgressDialog() {

    }
}
