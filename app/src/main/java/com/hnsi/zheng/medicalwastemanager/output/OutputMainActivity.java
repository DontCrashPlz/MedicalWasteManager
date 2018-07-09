package com.hnsi.zheng.medicalwastemanager.output;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.hnsi.zheng.medicalwastemanager.R;
import com.hnsi.zheng.medicalwastemanager.adapters.InputWasteRecyclerAdapter;
import com.hnsi.zheng.medicalwastemanager.apps.BaseActivity;
import com.hnsi.zheng.medicalwastemanager.input.InputedListActivity;
import com.hnsi.zheng.medicalwastemanager.utils.LogUtil;
import com.hnsi.zheng.medicalwastemanager.widgets.progressDialog.ProgressDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Zheng on 2018/6/29.
 */

public class OutputMainActivity extends BaseActivity {

    private static final String ACTION_QR_DATA_RECEIVED= "ACTION_QR_DATA_RECEIVED";
    private static final String QR_DATA= "qr_data";

    @BindView(R.id.textView1)
    TextView mOperatePersonTv;
    @BindView(R.id.textView2)
    TextView mBucketNumTv;
    @BindView(R.id.textView3)
    TextView mAllWeighTv;
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.button_output)
    Button mOutputButton;
    @BindView(R.id.button_photo)
    Button mPhotoButton;

    private OutputReceiver mOutputReceiver;
    private InputWasteRecyclerAdapter mAdapter;

    private String outputPersonInfo;
    private String[] outputPersonInfos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output_main);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("医废出库");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        outputPersonInfo= getIntent().getStringExtra("output_person_info");
        if (outputPersonInfo== null || outputPersonInfo.length()< 1){
            showShortToast("操作人员信息无效");
            return;
        }
        outputPersonInfos= outputPersonInfo.split("_");
        mOperatePersonTv.setText(outputPersonInfos[2]);

        mRecycler.setLayoutManager(new LinearLayoutManager(getRealContext()));
        mAdapter= new InputWasteRecyclerAdapter(R.layout.item_waste_input_recycler);
        mAdapter.bindToRecyclerView(mRecycler);
        //mRecycler.addItemDecoration(new InputWasteItemDecoration());
        mRecycler.setAdapter(mAdapter);

        //注册QRService的广播接收器
        mOutputReceiver= new OutputReceiver();
        IntentFilter intentFilter= new IntentFilter();
        intentFilter.addAction(ACTION_QR_DATA_RECEIVED);
        registerReceiver(mOutputReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        //注销此页面的广播接收器
        if (mOutputReceiver!= null) unregisterReceiver(mOutputReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== android.R.id.home)
            finish();
        if (item.getItemId()== R.id.edit){
            showShortToast("已出库医废桶列表");
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public void initProgressDialog() {
        dialog= new ProgressDialog(getRealContext());
        dialog.setLabel("请稍等..");
    }

    class OutputReceiver extends BroadcastReceiver{
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
