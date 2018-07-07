package com.hnsi.zheng.medicalwastemanager.input;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hnsi.zheng.medicalwastemanager.R;
import com.hnsi.zheng.medicalwastemanager.adapters.InputWasteRecyclerAdapter;
import com.hnsi.zheng.medicalwastemanager.apps.BaseActivity;
import com.hnsi.zheng.medicalwastemanager.utils.LogUtil;
import com.hnsi.zheng.medicalwastemanager.widgets.InputWasteItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Zheng on 2018/6/29.
 */

public class InputMainActivity extends BaseActivity {

    private static final String ACTION_QR_DATA_RECEIVED= "ACTION_QR_DATA_RECEIVED";
    private static final String QR_DATA= "qr_data";

    @BindView(R.id.textView1)
    TextView mInputPersonTv;
    @BindView(R.id.textView2)
    TextView mBucketNumTv;
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.button)
    Button mButton;

    private InputReceiver mInputReceiver;
    private InputWasteRecyclerAdapter mAdapter;

    private String inputPersonInfo;
    private String[] inputPersonInfos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_main2);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("医废入库");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        inputPersonInfo= getIntent().getStringExtra("input_person_info");
        if (inputPersonInfo== null || inputPersonInfo.length()< 1){
            showShortToast("操作人员信息无效");
            return;
        }
        inputPersonInfos= inputPersonInfo.split("_");
        mInputPersonTv.setText(inputPersonInfos[2]);

        mRecycler.setLayoutManager(new LinearLayoutManager(getRealContext()));
        mAdapter= new InputWasteRecyclerAdapter(R.layout.item_waste_input_recycler);
        mAdapter.bindToRecyclerView(mRecycler);
        //mRecycler.addItemDecoration(new InputWasteItemDecoration());
        mRecycler.setAdapter(mAdapter);

        //注册QRService的广播接收器
        mInputReceiver= new InputReceiver();
        IntentFilter intentFilter= new IntentFilter();
        intentFilter.addAction(ACTION_QR_DATA_RECEIVED);
        registerReceiver(mInputReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        //注销此页面的广播接收器
        if (mInputReceiver!= null) unregisterReceiver(mInputReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== android.R.id.home)
            finish();
        return true;
    }

    @Override
    public void initProgressDialog() {

    }

    class InputReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String qrInfoStr= intent.getStringExtra(QR_DATA);
            LogUtil.d("红光扫码返回的数据：" , qrInfoStr);
            if (qrInfoStr!= null && qrInfoStr.length()> 0){
                String[] qrInfos= qrInfoStr.split("_");
                int qrInfoSize= qrInfos.length;
                if (qrInfoSize== 4){//这是医废桶二维码
                    mBucketNumTv.setText(qrInfos[3]);
                }else if (qrInfoSize== 10){//这是医废收集带二维码
                    if (mAdapter!= null){
                        mAdapter.addData(qrInfos);
                        //mAdapter.notifyDataSetChanged();
                    }
                }else {
                    showShortToast("数据格式错误");
                }
            }else {
                showShortToast("无效数据");
            }
        }
    }

}
