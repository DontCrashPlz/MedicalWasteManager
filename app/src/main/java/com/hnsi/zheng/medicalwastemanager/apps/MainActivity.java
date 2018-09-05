package com.hnsi.zheng.medicalwastemanager.apps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.hnsi.zheng.medicalwastemanager.R;
import com.hnsi.zheng.medicalwastemanager.buckets.BucketNfcReadActivity;
import com.hnsi.zheng.medicalwastemanager.collect.CollectNfcReadActivity;
import com.hnsi.zheng.medicalwastemanager.collect.NfcTestActivity;
import com.hnsi.zheng.medicalwastemanager.collect.NfcTestActivity3;
import com.hnsi.zheng.medicalwastemanager.input.InputNfcReadActivity;
import com.hnsi.zheng.medicalwastemanager.output.OutputNfcReadActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private TextView mButton1;
    private TextView mButton2;
    private TextView mButton3;
    private TextView mButton4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!= null){
            getSupportActionBar().setTitle("医废管理");
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mButton1= findViewById(R.id.home_btn1);
        mButton1.setOnClickListener(this);
        mButton2= findViewById(R.id.home_btn2);
        mButton2.setOnClickListener(this);
        mButton3= findViewById(R.id.home_btn3);
        mButton3.setOnClickListener(this);
        mButton4= findViewById(R.id.home_btn4);
        mButton4.setOnClickListener(this);
    }

    @Override
    public void initProgressDialog() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home_btn1:{
                //showShortToast("医废收集");
                startActivity(new Intent(getRealContext(), CollectNfcReadActivity.class));
                break;
            }
            case R.id.home_btn2:{
                //showShortToast("医废入库");
                startActivity(new Intent(getRealContext(), InputNfcReadActivity.class));
                break;
            }
            case R.id.home_btn3:{
                //showShortToast("医废出库");
                startActivity(new Intent(getRealContext(), OutputNfcReadActivity.class));
                break;
            }
            case R.id.home_btn4:{
                //showShortToast("医废核对");
                startActivity(new Intent(getRealContext(), BucketNfcReadActivity.class));
                break;
            }
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId()== android.R.id.home)
//            finish();
        if (item.getItemId()== R.id.setting){
            //showShortToast("设置");
            startActivity(new Intent(getRealContext(), SettingActivity.class));
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
