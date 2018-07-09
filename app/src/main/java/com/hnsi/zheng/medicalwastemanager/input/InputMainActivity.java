package com.hnsi.zheng.medicalwastemanager.input;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.EditText;
import android.widget.TextView;

import com.hnsi.zheng.medicalwastemanager.R;
import com.hnsi.zheng.medicalwastemanager.adapters.InputWasteRecyclerAdapter;
import com.hnsi.zheng.medicalwastemanager.apps.BaseActivity;
import com.hnsi.zheng.medicalwastemanager.collect.CollectInfoActivity;
import com.hnsi.zheng.medicalwastemanager.collect.CollectedListActivity;
import com.hnsi.zheng.medicalwastemanager.https.Network;
import com.hnsi.zheng.medicalwastemanager.https.ResponseTransformer;
import com.hnsi.zheng.medicalwastemanager.utils.LogUtil;
import com.hnsi.zheng.medicalwastemanager.widgets.InputWasteItemDecoration;
import com.hnsi.zheng.medicalwastemanager.widgets.progressDialog.ProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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

    //入库操作人员信息
    private String inputPersonInfo;
    private String[] inputPersonInfos;

    //入库垃圾桶
    private String wasteBucketInfo;
    private String[] wasteBucketInfos;

    //已扫描的垃圾带的id集合
    private ArrayList<String> mInputWasteGuids= new ArrayList<>();

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

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wasteBucketInfos== null || wasteBucketInfos.length< 1){
                    showShortToast("请扫描医废桶二维码");
                    return;
                }
                if (mInputWasteGuids== null || mInputWasteGuids.size()< 1){
                    showShortToast("请扫描医废袋二维码");
                    return;
                }
                JSONObject bucketObject= new JSONObject();
                JSONArray wasteArray= new JSONArray();
                for (String guid : mInputWasteGuids){
                    JSONObject wasteObj= new JSONObject();
                    try {
                        LogUtil.d("垃圾袋编号：", guid);
                        wasteObj.put("guid", guid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    wasteArray.put(wasteObj);
                }
                try {
                    //    {
                    //      guid:11,
                    //      orgId:1,
                    //      createBy:1,
                    //      createTime:2017-01-08 15:30:21
                    //      wasteList:[{guid:12},{guid:12}]
                    //    }
                    LogUtil.d("垃圾桶编号：", wasteBucketInfos[3]);
                    bucketObject.put("guid", wasteBucketInfos[3]);
                    bucketObject.put("orgId", wasteBucketInfos[0]);
                    bucketObject.put("createBy", inputPersonInfos[1]);
                    bucketObject.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    bucketObject.put("wasteList", wasteArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                inputWastes(bucketObject);
            }
        });
    }

    private void inputWastes(JSONObject wasteObj){
        addNetWork(Network.getInstance().inputMedicalWaste(wasteObj)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(ResponseTransformer.<String>handleResult())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        dismissDialog();
                        showInputCompleteDialog();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        dismissDialog();
                        showLongToast(throwable.toString());
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                }, new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showDialog();
                    }
                }));
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
        if (item.getItemId()== R.id.edit){
            Intent intent= new Intent(getRealContext(), InputedListActivity.class);
            intent.putExtra("input_person_info", inputPersonInfo);
            startActivity(intent);
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
        dialog.setLabel("请稍候..");
    }

    class InputReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String qrInfoStr= intent.getStringExtra(QR_DATA);
            LogUtil.d("红光扫码返回的数据：" , qrInfoStr);
            if (qrInfoStr!= null && qrInfoStr.length()> 0){
                String[] qrInfos= qrInfoStr.replace("\n" , "").split("_");
                int qrInfoSize= qrInfos.length;
                if (qrInfoSize== 4){//这是医废桶二维码
                    LogUtil.d("红光扫码医废桶编号：" , qrInfos[3] + "---" + qrInfos[3].length());
                    if (qrInfos[3].length()!= 19 ){
                        showShortToast("无效的二维码");
                        return;
                    }
                    wasteBucketInfo= qrInfoStr;
                    wasteBucketInfos= qrInfos;
                    mBucketNumTv.setText(qrInfos[3]);
                }else if (qrInfoSize== 10){//这是医废收集带二维码
                    if (qrInfos[9].length()!= 19 ){
                        showShortToast("无效的二维码");
                        return;
                    }
                    if (wasteBucketInfos== null || wasteBucketInfos.length< 1){
                        showShortToast("请先扫描医废桶二维码");
                        return;
                    }
                    if (mAdapter!= null){
                        String wasteGuid= qrInfos[9];
                        if (mInputWasteGuids.contains(wasteGuid)){
                            return;
                        }
                        mInputWasteGuids.add(wasteGuid);
                        mAdapter.addData(qrInfos);
                    }
                }else {
                    showShortToast("无效二维码");
                }
            }else {
                showShortToast("无效数据");
            }
        }
    }

    /**
     * 弹出入库成功弹窗
     */
    private void showInputCompleteDialog(){
        View view = LayoutInflater.from(getRealContext()).inflate(R.layout.layout_public_dialog, null);
        // 设置style 控制默认dialog带来的边距问题
        final Dialog dialog = new Dialog(getRealContext(), R.style.custom_dialog_no_titlebar);
        dialog.setContentView(view);
        dialog.show();

        TextView titleTv= view.findViewById(R.id.dialog_title);
        titleTv.setText("入库成功");
        TextView contentTv= view.findViewById(R.id.dialog_content);
        contentTv.setText("医废入库成功，是否继续医废入库工作？");
        TextView button1= view.findViewById(R.id.dialog_btn1);
        button1.setText("退出工作");
        TextView button2= view.findViewById(R.id.dialog_btn2);
        button2.setText("继续入库");
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mBucketNumTv.setText("");
                wasteBucketInfo= "";
                wasteBucketInfos= new String[4];
                mInputWasteGuids.clear();
                mAdapter.setNewData(new ArrayList<String[]>());
            }
        });

        // 设置相关位置，一定要在 show()之后
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

}
