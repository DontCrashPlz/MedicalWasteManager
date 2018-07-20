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

import java.nio.charset.Charset;
import java.util.Locale;

/**
 * Created by Zheng on 2018/6/27.
 */
@Deprecated
public class NfcTestActivity3 extends BaseNfcActivity {
    //private String mText = "123456789_2_张三_001_第一人民医院_011_后勤_2";//收集
    //private String mText = "123456780_3_李四_001_第一人民医院_001_门诊_1";//科室

    //private String mText = "123456781_5_王海_001_第一人民医院_003_口腔科_1";
    //private String mText = "123456782_6_李玉轩_001_第一人民医院_008_儿科_1";
    private String mText = "123456783_7_孙利平_001_第一人民医院_011_后勤科_2";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_test);
    }
    @Override
    public void onNewIntent(Intent intent) {
        if (mText == null)
            return;
        //获取Tag对象
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NdefMessage ndefMessage = new NdefMessage(
                new NdefRecord[] { createTextRecord(mText) });
        boolean result = writeTag(ndefMessage, detectedTag);
        if (result){
            showShortToast("写入成功");
        } else {
            showShortToast("写入失败");
        }
    }
    /**
     * 创建NDEF文本数据
     * @param text
     * @return
     */
    public static NdefRecord createTextRecord(String text) {
        byte[] langBytes = Locale.CHINA.getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = Charset.forName("UTF-8");
        //将文本转换为UTF-8格式
        byte[] textBytes = text.getBytes(utfEncoding);
        //设置状态字节编码最高位数为0
        int utfBit = 0;
        //定义状态字节
        char status = (char) (utfBit + langBytes.length);
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        //设置第一个状态字节，先将状态码转换成字节
        data[0] = (byte) status;
        //设置语言编码，使用数组拷贝方法，从0开始拷贝到data中，拷贝到data的1到langBytes.length的位置
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        //设置文本字节，使用数组拷贝方法，从0开始拷贝到data中，拷贝到data的1 + langBytes.length
        //到textBytes.length的位置
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        //通过字节传入NdefRecord对象
        //NdefRecord.RTD_TEXT：传入类型 读写
        NdefRecord ndefRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], data);
        return ndefRecord;
    }
    /**
     * 写数据
     * @param ndefMessage 创建好的NDEF文本数据
     * @param tag 标签
     * @return
     */
    public static boolean writeTag(NdefMessage ndefMessage, Tag tag) {
        try {
            Ndef ndef = Ndef.get(tag);
            ndef.connect();
            ndef.writeNdefMessage(ndefMessage);
            return true;
        } catch (Exception e) {
        }
        return false;
    }
}
