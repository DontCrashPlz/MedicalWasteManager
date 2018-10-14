package com.hnsi.zheng.medicalwastemanager.output;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hnsi.zheng.medicalwastemanager.R;
import com.hnsi.zheng.medicalwastemanager.adapters.OutputWasteRecyclerAdapter;
import com.hnsi.zheng.medicalwastemanager.apps.AppConstants;
import com.hnsi.zheng.medicalwastemanager.apps.BaseActivity;
import com.hnsi.zheng.medicalwastemanager.beans.InputedBucketEntity;
import com.hnsi.zheng.medicalwastemanager.beans.OutputBucketEntity;
import com.hnsi.zheng.medicalwastemanager.beans.UploadPhotoEntity;
import com.hnsi.zheng.medicalwastemanager.collect.BluetoothSerialService;
import com.hnsi.zheng.medicalwastemanager.https.Network;
import com.hnsi.zheng.medicalwastemanager.https.ResponseTransformer;
import com.hnsi.zheng.medicalwastemanager.input.InputedListActivity;
import com.hnsi.zheng.medicalwastemanager.utils.LogUtil;
import com.hnsi.zheng.medicalwastemanager.utils.SharedPrefUtils;
import com.hnsi.zheng.medicalwastemanager.widgets.progressDialog.ProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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

public class OutputMainActivity extends BaseActivity {

    private static final String ACTION_QR_DATA_RECEIVED= "ACTION_QR_DATA_RECEIVED";
    private static final String QR_DATA= "qr_data";

    //蓝牙电子秤串口号
    //static final String BT04_A= "AB:7D:37:57:34:02";
    private String mDevicePort;
    // Debugging
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothSerialService mChatService = null;
    private BluetoothDevice device = null;

    //当前重量暂存字段
    private String currentWeigh;

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
    private OutputWasteRecyclerAdapter mAdapter;

    private String outputPersonInfo;
    private String[] outputPersonInfos;

    private int mBucketNum;
    private float mAllBucketWeigh;

    //已扫描的医废桶的id集合
    private ArrayList<String> mOutputedButcketGuids= new ArrayList<>();
    //附件上传返回的附件id，不为0表示已经上传附件
    private int fileId;

    public static final int REQUEST_CODE_TAKE_PICTURE = 1001;// 拍照请求码
    private String photoName;

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

        mDevicePort= (String) SharedPrefUtils.get(getRealContext(), AppConstants.SharedPref_Bluetooth_Output, "");
        LogUtil.d("蓝牙秤串口号", mDevicePort);
        if (mDevicePort== null || mDevicePort.length()!= 17){
            showLongToast("请在设置页面设置蓝牙秤");
            finish();
            return;
        }

        outputPersonInfo= getIntent().getStringExtra("output_person_info");
        if (outputPersonInfo== null || outputPersonInfo.length()< 1){
            showShortToast("操作人员信息无效");
            return;
        }
        outputPersonInfos= outputPersonInfo.split("_");
        mOperatePersonTv.setText(outputPersonInfos[2]);

        mRecycler.setLayoutManager(new LinearLayoutManager(getRealContext()));
        mAdapter= new OutputWasteRecyclerAdapter(R.layout.item_waste_input_recycler);
        mAdapter.bindToRecyclerView(mRecycler);
        //mRecycler.addItemDecoration(new InputWasteItemDecoration());
        mRecycler.setAdapter(mAdapter);

        //注册QRService的广播接收器
        mOutputReceiver= new OutputReceiver();
        IntentFilter intentFilter= new IntentFilter();
        intentFilter.addAction(ACTION_QR_DATA_RECEIVED);
        registerReceiver(mOutputReceiver, intentFilter);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Get the BLuetoothDevice object
        //device = mBluetoothAdapter.getRemoteDevice(BT04_A);
        device = mBluetoothAdapter.getRemoteDevice(mDevicePort);
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothSerialService(this, mHandler);
        // Attempt to connect to the device
        mChatService.connect(device);

        mOutputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<OutputBucketEntity> listDatas= mAdapter.getData();
                if (listDatas== null || listDatas.size()< 1){
                    showShortToast("请添加要出库的医废桶");
                    return;
                }
                if (fileId< 1){
                    showShortToast("请拍照留证");
                    return;
                }
                JSONObject bucketObject= new JSONObject();
                JSONArray wasteArray= new JSONArray();
                for (OutputBucketEntity entity : listDatas){
                    JSONObject wasteObj= new JSONObject();
                    try {
                        LogUtil.d("医废桶编号：", entity.getGuid());
                        wasteObj.put("guid", entity.getGuid());
                        wasteObj.put("outputTotalWeight:", String.valueOf(entity.getOutputWeigh()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    wasteArray.put(wasteObj);
                }
                try {
//                    {
//                        orgId:医院id
//                        userId:出库人
//                        fileIds:附件id(多文件,分割)
//                        outputTime:"2018-07-15 15:24:23"
//                        garbageBinList:[{
//                        guid:桶编码,
//                                outputTotalWeight:出库重量
//                    }]
//                    }
                    bucketObject.put("orgId", outputPersonInfos[3]);
                    bucketObject.put("userId", outputPersonInfos[1]);
                    bucketObject.put("fileIds", String.valueOf(fileId));
                    bucketObject.put("outputTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    bucketObject.put("garbageBinList", wasteArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                outputWasteBucket(bucketObject);
            }
        });

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoName= createJPEGTempFileName();
                File picture = new File(getExternalFilesDir(""), photoName);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picture));
                startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
            }
        });
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        LogUtil.d(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothSerialService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    @Override
    public void onDestroy() {

        //注销此页面的广播接收器
        if (mOutputReceiver!= null) unregisterReceiver(mOutputReceiver);

        super.onDestroy();

        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothSerialService.STATE_CONNECTED:
                            if (outputComfirmDialog!= null && outputComfirmDialog.isShowing()){
                                dialogOutputWeighTv.setText(R.string.title_connected_to);
                                dialogOutputWeighTv.append(mConnectedDeviceName);
                            }
                            break;
                        case BluetoothSerialService.STATE_CONNECTING:
                            //mWasteWeighTv.setText(R.string.title_connecting);
                            if (outputComfirmDialog!= null && outputComfirmDialog.isShowing()){
                                dialogOutputWeighTv.setText("正在连接蓝牙秤..");
                            }
                            break;
                        case BluetoothSerialService.STATE_LISTEN:
                        case BluetoothSerialService.STATE_NONE:
                            //mWasteWeighTv.setText(R.string.title_not_connected);
                            if (outputComfirmDialog!= null && outputComfirmDialog.isShowing()){
                                dialogOutputWeighTv.setText("点击此处重连蓝牙秤");
                            }
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String ss = new String(readBuf, 0,msg.arg1);
                    //LogUtil.d("message_read", ss);

                    currentWeigh += ss;
                    int m= currentWeigh.indexOf("+");
                    int n= currentWeigh.lastIndexOf("+");
                    if (m== -1 || n== -1){
                        return;
                    }
                    if (m== n){
                        return;
                    }
                    int j= currentWeigh.indexOf("+", m + 1);
                    String value= currentWeigh.substring(m + 1, j).trim();
                    //LogUtil.d("value", value);
                    if (outputComfirmDialog!= null && outputComfirmDialog.isShowing()){
                        dialogOutputWeighTv.setText(value);
                        String inputStr= dialogInputWeighTv.getText().toString().trim();
                        inputStr= inputStr.substring(0, inputStr.length() - 2);
                        float inputWeigh= Float.valueOf(inputStr);

                        String outputStr= value.trim();
                        outputStr= outputStr.substring(0, value.length() - 2);
                        float outputWeigh= Float.valueOf(outputStr);

                        if (inputWeigh> outputWeigh){
                            dialogErrorTv.setText(formatFloat((inputWeigh - outputWeigh)/inputWeigh * 100) + " %");
                        }else {
                            dialogErrorTv.setText(formatFloat((outputWeigh - inputWeigh)/inputWeigh * 100) + " %");
                        }

                    }
                    currentWeigh= "";
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== android.R.id.home)
            finish();
        if (item.getItemId()== R.id.edit){
            showShortToast("已出库列表");
//            Intent intent= new Intent(getRealContext(), OutputedListActivity.class);
//            intent.putExtra("output_person_info", outputPersonInfo);
//            startActivity(intent);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_TAKE_PICTURE) {//照相界面返回
            if (resultCode == Activity.RESULT_OK) {
                File picture = new File(getExternalFilesDir(""), photoName);
                if (picture.exists()){
                    uploadPhoto(picture);
                }
            }
        }
    }

    private long mLastReceiveTime;//上次接收时间
    private String mLastQrInfoStr;//上次接收内容
    class OutputReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String qrInfoStr= intent.getStringExtra(QR_DATA);
            if (qrInfoStr== null){
                showShortToast("无效数据");
                return;
            }
            qrInfoStr= qrInfoStr.replace("\n", "").trim();
            if (qrInfoStr.length()< 1){
                showShortToast("无效数据");
                return;
            }
            LogUtil.d("红光扫码返回的数据：" , qrInfoStr);

            long currentTime= System.currentTimeMillis();
            LogUtil.d("广播接收器接收时间：", "" + currentTime);
            if ((currentTime - mLastReceiveTime) < 300 && mLastQrInfoStr!= null){
                qrInfoStr= mLastQrInfoStr + qrInfoStr;
            }
            mLastReceiveTime= currentTime;
            mLastQrInfoStr= qrInfoStr;

            String[] qrInfos= qrInfoStr.split("_");
            if (isBucketQrCode(qrInfos)){
                showShortToast(qrInfos[3]);
                getInputWeigh(qrInfos[3]);
            }
        }
    }

    /**
     * 判断是否扫描的医废桶二维码
     * @param strs
     * @return
     */
    private boolean isBucketQrCode(String[] strs){
        if (strs== null) return false;
        int size= strs.length;
        if (size== 4 && strs[3].length()== 19){
            return true;
        }
        return false;
    }

    /**
     * 判断是否扫描的医废袋二维码
     * @param strs
     * @return
     */
    private boolean isWasteQrCode(String[] strs){
        if (strs== null) return false;
        int size= strs.length;
        if (size== 10 && strs[9].length()== 19){
            return true;
        }
        return false;
    }

    private Dialog outputComfirmDialog;
    private TextView dialogInputWeighTv;
    private TextView dialogOutputWeighTv;
    private TextView dialogErrorTv;
    /**
     * 弹出出库确认弹窗
     */
    private void showOutputComfirmDialog(final OutputBucketEntity entity){
        View view = LayoutInflater.from(getRealContext()).inflate(R.layout.layout_output_dialog, null);
        // 设置style 控制默认dialog带来的边距问题
        outputComfirmDialog = new Dialog(getRealContext(), R.style.custom_dialog_no_titlebar);
        outputComfirmDialog.setContentView(view);
        outputComfirmDialog.show();

        TextView guidTv= view.findViewById(R.id.tv_bucket_guid);
        guidTv.setText(entity.getGuid());
        dialogInputWeighTv= view.findViewById(R.id.tv_bucket_inputweigh);
        dialogInputWeighTv.setText(entity.getInputWeigh() + " kg");
        dialogOutputWeighTv= view.findViewById(R.id.tv_bucket_outputweigh);
        dialogOutputWeighTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("点击此处重连蓝牙秤".equals(dialogOutputWeighTv.getText().toString().trim())){
                    mChatService.connect(device);
                }
            }
        });
        dialogErrorTv= view.findViewById(R.id.tv_bucket_error);
        TextView button1= view.findViewById(R.id.dialog_btn1);
        TextView button2= view.findViewById(R.id.dialog_btn2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String outputStr= dialogOutputWeighTv.getText().toString().trim();
                outputStr= outputStr.substring(0, outputStr.length() - 2);
                float outputWeigh;
                try{
                    outputWeigh= Float.valueOf(outputStr);
                }catch (Exception e){
                    showShortToast("当前出库毛重无效");
                    return;
                }
                if (outputWeigh > 0){
                    if (mOutputedButcketGuids.contains(entity.getGuid())){
                        outputComfirmDialog.dismiss();
                        showShortToast("此医废桶已在出库列表中");
                        return;
                    }
                    entity.setOutputWeigh(outputWeigh);
                    mAdapter.addData(entity);
                    mOutputedButcketGuids.add(entity.getGuid());

                    mBucketNum += 1;
                    mBucketNumTv.setText("" + mBucketNum);

                    mAllBucketWeigh += outputWeigh;
                    mAllWeighTv.setText(mAllBucketWeigh + "KG");

                    outputComfirmDialog.dismiss();
                }else {
                    showShortToast("当前出库毛重无效");
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outputComfirmDialog.dismiss();
            }
        });

        if (mChatService.getState() != BluetoothSerialService.STATE_CONNECTED){
            dialogOutputWeighTv.setText("点击此处重连蓝牙秤");
        }

        // 设置相关位置，一定要在 show()之后
        Window window = outputComfirmDialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    /**
     * 弹出出库成功弹窗
     */
    private void showOutputCompleteDialog(){
        View view = LayoutInflater.from(getRealContext()).inflate(R.layout.layout_public_dialog, null);
        // 设置style 控制默认dialog带来的边距问题
        final Dialog dialog = new Dialog(getRealContext(), R.style.custom_dialog_no_titlebar);
        dialog.setContentView(view);
        dialog.show();

        TextView titleTv= view.findViewById(R.id.dialog_title);
        titleTv.setText("出库成功");
        TextView contentTv= view.findViewById(R.id.dialog_content);
        contentTv.setText(mBucketNum + "箱总重" + mAllBucketWeigh + "KG医废已出库成功，是否继续医废出库工作？");
        TextView button1= view.findViewById(R.id.dialog_btn1);
        button1.setText("退出工作");
        TextView button2= view.findViewById(R.id.dialog_btn2);
        button2.setText("继续出库");
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
                mBucketNum= 0;
                mAllBucketWeigh= 0;
                mOutputedButcketGuids.clear();
                fileId= 0;
                photoName= "";
                mBucketNumTv.setText("");
                mAllWeighTv.setText("");
                mAdapter.setNewData(new ArrayList<OutputBucketEntity>());
            }
        });

        // 设置相关位置，一定要在 show()之后
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    /**
     * 根据扫码获取的桶编号获取医废桶的入库重量
     * @param guid
     */
    private void getInputWeigh(String guid){
        addNetWork(Network.getInstance().getBucketDetail(guid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(ResponseTransformer.<InputedBucketEntity>handleResult())
                .subscribe(new Consumer<InputedBucketEntity>() {
                    @Override
                    public void accept(InputedBucketEntity inputedBucketEntity) throws Exception {
                        dismissDialog();
                        OutputBucketEntity entity= new OutputBucketEntity();
                        entity.setGuid(inputedBucketEntity.getGuid());
                        entity.setInputWeigh(inputedBucketEntity.getWasteWeight());
                        showOutputComfirmDialog(entity);
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

    /**
     * 批量出库
     * @param wasteObj
     */
    private void outputWasteBucket(JSONObject wasteObj){
        addNetWork(Network.getInstance().outputWasteBucket(wasteObj)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(ResponseTransformer.<String>handleResult())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        dismissDialog();
                        showOutputCompleteDialog();
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

    /**
     * 上传图片
     * @param file
     */
    private void uploadPhoto(File file){
//        showDialog();
//        if (!file.exists()){
//            showShortToast("请重新拍照");
//            return;
//        }
//        Bitmap photoBitmap= BitmapFactory.decodeFile(file.getPath());
//        compressImage(photoBitmap, file.getPath());
//        File picture2 = new File(getExternalFilesDir(""), photoName);
//        if (!picture2.exists()){
//            showShortToast("请重新拍照");
//            return;
//        }
        addNetWork(Network.getInstance().uploadPhoto(outputPersonInfos[1], file)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(ResponseTransformer.<UploadPhotoEntity>handleResult())
                .subscribe(new Consumer<UploadPhotoEntity>() {
                    @Override
                    public void accept(UploadPhotoEntity uploadPhotoEntity) throws Exception {
                        dismissDialog();
                        showShortToast("上传成功");
                        fileId= uploadPhotoEntity.getId();
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

    public String formatFloat(float f){
        BigDecimal b = new BigDecimal(f);
//        LogUtil.d("formatFloat before", "" + f );
        //  b.setScale(2,  BigDecimal.ROUND_HALF_UP)  表明四舍五入，保留两位小数
        f = b.setScale(2,  BigDecimal.ROUND_HALF_UP).floatValue();
//        LogUtil.d("formatFloat after", "" + f );
        return "" + f;
    }

    /**
     * 获取文件名称
     * @return
     */
    public String createJPEGTempFileName() {
        return String.valueOf(UUID.randomUUID()) + ".jpg";
    }

    // 质量压缩法：
    private Bitmap compressImage(Bitmap image, String filepath) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            while (baos.toByteArray().length / 1024 > 300) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
                baos.reset();//重置baos即清空baos
                options -= 10;//每次都减少10
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中

            }
            //压缩好后写入文件中
            FileOutputStream fos = new FileOutputStream(filepath);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}


