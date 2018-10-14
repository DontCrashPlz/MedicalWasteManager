package com.hnsi.zheng.medicalwastemanager.collect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.common.StringUtils;
import com.hnsi.zheng.medicalwastemanager.R;
import com.hnsi.zheng.medicalwastemanager.apps.AppConstants;
import com.hnsi.zheng.medicalwastemanager.apps.BaseActivity;
import com.hnsi.zheng.medicalwastemanager.apps.BaseNfcActivity;
import com.hnsi.zheng.medicalwastemanager.apps.MainActivity;
import com.hnsi.zheng.medicalwastemanager.beans.format.CardDataEntity;
import com.hnsi.zheng.medicalwastemanager.exception.InvalidCardDataException;
import com.hnsi.zheng.medicalwastemanager.utils.LogUtil;
import com.hnsi.zheng.medicalwastemanager.utils.SharedPrefUtils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zheng on 2018/6/29.
 */

public class CollectInfoActivity extends BaseNfcActivity implements CompoundButton.OnCheckedChangeListener {

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

    //二维码打印页面的请求码和返回码
    static final int QRCODE_REQUESTCODE= 0;
    static final int EXIT_RESULTCODE= 1;
    static final int CONTINUE_RESULTCODE= 2;

    @BindView(R.id.textView1)
    TextView mCollectPersonTv;
    @BindView(R.id.textView2)
    TextView mDepartmentTv;
    @BindView(R.id.textView3)
    TextView mDepartmentPersonTv;
    @BindView(R.id.textView4)
    TextView mWasteWeighTv;
    @BindView(R.id.textView5)
    TextView mWasteTypeTv;
    @BindView(R.id.button)
    Button mCreateTagBtn;

    @BindView(R.id.checkbox1)
    CheckBox checkBox1;
    @BindView(R.id.checkbox2)
    CheckBox checkBox2;
    @BindView(R.id.checkbox3)
    CheckBox checkBox3;
    @BindView(R.id.checkbox4)
    CheckBox checkBox4;
    @BindView(R.id.checkbox5)
    CheckBox checkBox5;

    //科室人员卡片信息
    private String mCardText;
    //当前重量暂存字段
    private String currentWeigh;
    //收集人员信息
    private CardDataEntity collectPerson;
    //科室人员信息
    private CardDataEntity departmentPerson;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_info);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("医废收集");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mDevicePort= (String) SharedPrefUtils.get(getRealContext(), AppConstants.SharedPref_Bluetooth_Collect, "");
        if (mDevicePort== null || mDevicePort.length()== 0){
            showLongToast("请在设置页面设置蓝牙秤");
            finish();
            return;
        }

        collectPerson = (CardDataEntity) getIntent().getSerializableExtra(MainActivity.COLLECT_PERSON);
        mCollectPersonTv.setText(collectPerson.getUserName());

        checkBox1.setOnCheckedChangeListener(this);
        checkBox2.setOnCheckedChangeListener(this);
        checkBox3.setOnCheckedChangeListener(this);
        checkBox4.setOnCheckedChangeListener(this);
        checkBox5.setOnCheckedChangeListener(this);

        mWasteWeighTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("点击此处重连蓝牙秤".equals(mWasteWeighTv.getText().toString().trim())){
                    mChatService.connect(device);
                }
            }
        });

        mCreateTagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String collect_person= mCollectPersonTv.getText().toString().trim();
                String department_name= mDepartmentTv.getText().toString().trim();
                String department_person= mDepartmentPersonTv.getText().toString().trim();
                String waste_weigh= mWasteWeighTv.getText().toString().trim();
                String waste_type= mWasteTypeTv.getText().toString().trim();
                if (collect_person== null || collect_person.length()< 1){
                    showShortToast("收集人员信息无效");
                    return;
                }
                if (department_name== null || department_name.length()< 1
                        || department_person== null || department_person.length()< 1){
                    showShortToast("科室人员信息无效");
                    return;
                }
                if (waste_weigh== null || waste_weigh.length()< 1){
                    showShortToast("请秤取医废重量");
                    return;
                }
                if (waste_type== null || waste_type.length()< 1){
                    showShortToast("请选择医废类型");
                    return;
                }
                Intent intent= new Intent(getRealContext(), PrintQRCodeActivity.class);
                intent.putExtra(MainActivity.COLLECT_PERSON, collectPerson);
                intent.putExtra(MainActivity.DEPARTMENT_PERSON, departmentPerson);
                intent.putExtra("waste_weigh", waste_weigh);
                intent.putExtra("waste_type", waste_type);
                startActivityForResult(intent, QRCODE_REQUESTCODE);
            }
        });

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Get the BLuetoothDevice object
        //device = mBluetoothAdapter.getRemoteDevice(BT04_A);
        device = mBluetoothAdapter.getRemoteDevice(mDevicePort);
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothSerialService(this, mHandler);
        // Attempt to connect to the device
        mChatService.connect(device);

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
                            mWasteWeighTv.setText(R.string.title_connected_to);
                            mWasteWeighTv.append(mConnectedDeviceName);
                            break;
                        case BluetoothSerialService.STATE_CONNECTING:
                            //mWasteWeighTv.setText(R.string.title_connecting);
                            mWasteWeighTv.setText("正在连接蓝牙秤..");
                            break;
                        case BluetoothSerialService.STATE_LISTEN:
                        case BluetoothSerialService.STATE_NONE:
                            //mWasteWeighTv.setText(R.string.title_not_connected);
                            mWasteWeighTv.setText("点击此处重连蓝牙秤");
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
                    mWasteWeighTv.setText(value);
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
            Intent intent= new Intent(getRealContext(), CollectedListActivity.class);
            intent.putExtra(MainActivity.COLLECT_PERSON, collectPerson);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode== QRCODE_REQUESTCODE){
            if (resultCode== EXIT_RESULTCODE){//打印标签后退出工作
                finish();
            }else if (resultCode== CONTINUE_RESULTCODE){//打印标签后继续工作
                mDepartmentTv.setText("");
                mDepartmentPersonTv.setText("");
                mWasteWeighTv.setText("");
                mWasteTypeTv.setText("");
                checkBox1.setChecked(false);
                checkBox2.setChecked(false);
                checkBox3.setChecked(false);
                checkBox4.setChecked(false);
                checkBox5.setChecked(false);

//                if (mBluetoothAdapter== null) mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//                if (device== null) device = mBluetoothAdapter.getRemoteDevice(BT04_A);
//                if (mChatService== null) mChatService = new BluetoothSerialService(this, mHandler);
//                mChatService.connect(device);

            }
        }
    }

    @Override
    public void initProgressDialog() {

    }


    @Override
    protected void onNewIntent(Intent intent) {
        readNfcTag(intent);
        try {
            departmentPerson = new CardDataEntity(mCardText);
            showShortToast("扫描成功：" + departmentPerson.toFormatStr());
        } catch (InvalidCardDataException e) {
            e.printStackTrace();
            showShortToast(e.getMessage());
            return;
        }
        mDepartmentTv.setText(departmentPerson.getDepartmentName());
        mDepartmentPersonTv.setText(departmentPerson.getUserName());
    }

    private void readNfcTag(Intent intent){
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())){
            Parcelable[] rawMsgs= intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage msgs[]= null;
            int contentSize= 0;
            if (rawMsgs!= null){
                msgs= new NdefMessage[rawMsgs.length];
                for (int i= 0; i< rawMsgs.length; i++){
                    msgs[i]= (NdefMessage) rawMsgs[i];
                    contentSize+= msgs[i].toByteArray().length;
                }
            }
            try {
                if (msgs!= null){
                    NdefRecord record= msgs[0].getRecords()[0];
                    String textRecord= parseTextRecord(record);
                    mCardText= textRecord;
                }
            }catch (Exception e){
                LogUtil.e("collect nfc read exception", e.toString());
            }
        }
    }

    /**
     * 解析NDEF文本数据，从第三个字节开始，后面的文本数据
     * @param ndefRecord
     * @return
     */
    public static String parseTextRecord(NdefRecord ndefRecord) {
        /**
         * 判断数据是否为NDEF格式
         */
        //判断TNF
        if (ndefRecord.getTnf() != NdefRecord.TNF_WELL_KNOWN) {
            return null;
        }
        //判断可变的长度的类型
        if (!Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
            return null;
        }
        try {
            //获得字节数组，然后进行分析
            byte[] payload = ndefRecord.getPayload();
            //下面开始NDEF文本数据第一个字节，状态字节
            //判断文本是基于UTF-8还是UTF-16的，取第一个字节"位与"上16进制的80，16进制的80也就是最高位是1，
            //其他位都是0，所以进行"位与"运算后就会保留最高位
            String textEncoding = ((payload[0] & 0x80) == 0) ? "UTF-8" : "UTF-16";
            //3f最高两位是0，第六位是1，所以进行"位与"运算后获得第六位
            int languageCodeLength = payload[0] & 0x3f;
            //下面开始NDEF文本数据第二个字节，语言编码
            //获得语言编码
            String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            //下面开始NDEF文本数据后面的字节，解析出文本
            String textRecord = new String(payload, languageCodeLength + 1,
                    payload.length - languageCodeLength - 1, textEncoding);
            return textRecord;
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.checkbox1:
                if (isChecked){
                    mWasteTypeTv.setText("感染性医废");
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(false);
                    checkBox4.setChecked(false);
                    checkBox5.setChecked(false);
                }
                break;
            case R.id.checkbox2:
                if (isChecked){
                    mWasteTypeTv.setText("损伤性医废");
                    checkBox1.setChecked(false);
                    checkBox3.setChecked(false);
                    checkBox4.setChecked(false);
                    checkBox5.setChecked(false);
                }
                break;
            case R.id.checkbox3:
                if (isChecked){
                    mWasteTypeTv.setText("药物性医废");
                    checkBox1.setChecked(false);
                    checkBox2.setChecked(false);
                    checkBox4.setChecked(false);
                    checkBox5.setChecked(false);
                }
                break;
            case R.id.checkbox4:
                if (isChecked){
                    mWasteTypeTv.setText("病理性医废");
                    checkBox1.setChecked(false);
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(false);
                    checkBox5.setChecked(false);
                }
                break;
            case R.id.checkbox5:
                if (isChecked){
                    mWasteTypeTv.setText("化学性医废");
                    checkBox1.setChecked(false);
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(false);
                    checkBox4.setChecked(false);
                }
                break;
        }
    }
}
