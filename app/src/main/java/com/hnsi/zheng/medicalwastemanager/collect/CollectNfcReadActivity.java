package com.hnsi.zheng.medicalwastemanager.collect;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.hnsi.zheng.medicalwastemanager.R;
import com.hnsi.zheng.medicalwastemanager.apps.BaseActivity;
import com.hnsi.zheng.medicalwastemanager.apps.BaseNfcActivity;
import com.hnsi.zheng.medicalwastemanager.utils.LogUtil;

import java.util.Arrays;

/**
 * Created by Zheng on 2018/6/27.
 */

public class CollectNfcReadActivity extends BaseNfcActivity {

    private String mCardText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_nfc_read);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("医废收集");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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

    @Override
    protected void onNewIntent(Intent intent) {
//        Tag detectedTag= intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//        Ndef ndef= Ndef.get(detectedTag);
        readNfcTag(intent);
        if (mCardText== null || mCardText.length()< 1) return;
        showShortToast("扫描成功：" + mCardText);
        Intent infoIntent= new Intent(getRealContext(), CollectInfoActivity.class);
        infoIntent.putExtra("collect_person_info", mCardText);
        startActivity(infoIntent);
        finish();
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

}
