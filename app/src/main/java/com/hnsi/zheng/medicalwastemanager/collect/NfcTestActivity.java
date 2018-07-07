package com.hnsi.zheng.medicalwastemanager.collect;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.hnsi.zheng.medicalwastemanager.R;
import com.hnsi.zheng.medicalwastemanager.apps.BaseNfcActivity;

/**
 * Created by Zheng on 2018/6/27.
 */
@Deprecated
public class NfcTestActivity extends BaseNfcActivity {
    private String mPackageName= "com.android.mms";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_test);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (mPackageName== null) return;
        Tag detectedTag= intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        writeNfcTag(detectedTag);
    }

    public void writeNfcTag(Tag tag){
        if (tag== null) return;
        NdefMessage ndefMessage= new NdefMessage(new NdefRecord[]{NdefRecord.createApplicationRecord(mPackageName)});
        int size= ndefMessage.toByteArray().length;
        try {
            Ndef ndef= Ndef.get(tag);
            if (ndef!= null){
                ndef.connect();
                if (!ndef.isWritable()){
                    return;
                }
                if (ndef.getMaxSize()< size){
                    return;
                }
                ndef.writeNdefMessage(ndefMessage);
                showShortToast("写入成功");
            }else {
                NdefFormatable formatable= NdefFormatable.get(tag);
                if (formatable!= null){
                    formatable.connect();
                    formatable.format(ndefMessage);
                    showShortToast("写入成功");
                }else {
                    showShortToast("写入失败");
                }
            }
        }catch (Exception e){

        }
    }

}
