package com.hnsi.zheng.medicalwastemanager.buckets;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hnsi.zheng.medicalwastemanager.R;
import com.hnsi.zheng.medicalwastemanager.apps.BaseActivity;
import com.hnsi.zheng.medicalwastemanager.collect.CollectInfoActivity;
import com.hnsi.zheng.medicalwastemanager.utils.LogUtil;
import com.hnsi.zheng.medicalwastemanager.widgets.progressDialog.ProgressDialog;
import com.qs.helper.printer.PrintService;
import com.qs.helper.printer.PrinterClass;
import com.qs.helper.printer.bt.BtService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Zheng on 2018/7/3.
 */

public class PrintBucketQRCodeActivity extends BaseActivity {

    @BindView(R.id.textView1)
    TextView mHospitalNameTv;
    @BindView(R.id.textView2)
    TextView mOperateNameTv;
    @BindView(R.id.button_print)
    Button mPrintBtn;

    private String operatePresonInfo;
    private String[] operateInfos;
    private String currentTime4Num;

    protected static final String TAG = "PrintQRCodeActivity";
    public static boolean checkState = true;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    Handler btStatushandler = null;//蓝牙打印机状态监听
    Handler handler = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_bucket_qrcode);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("打印医废桶二维码");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        operatePresonInfo= getIntent().getStringExtra("operate_person_info");
        operateInfos= operatePresonInfo.split("_");

        mHospitalNameTv.setText(operateInfos[4]);
        mOperateNameTv.setText(operateInfos[2]);

        mPrintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PrintService.pl == null){
                    showPrintServiceDialog();
                    return;
                }
                if (PrintService.pl.getState() != PrinterClass.STATE_CONNECTED){
                    showConnectPrintDialog();
                    return;
                }
                currentTime4Num= new SimpleDateFormat("yyMMddHHmmssFFF").format(new Date());
                String bucketNum= operateInfos[3] + currentTime4Num + 0;
                String str= operateInfos[3] + "_" + operateInfos[4] + "_" + 1 + "_" + bucketNum;
                LogUtil.d("医废桶二维码信息：" , str);
                PrintService.pl.printImage(createQRImage(str,300,300));
                PrintService.pl.write(new byte[] { 0x1d, 0x0c });
                showPrintCompleteDialog();
            }
        });

        initPrintService();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        showDialog();
        PrintService.pl.connect("57:4C:54:14:3A:68");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== android.R.id.home)
            finish();
        return true;
    }

    @Override
    public void initProgressDialog() {
        dialog= new ProgressDialog(getRealContext());
        dialog.setLabel("请稍候..");
    }

    @Override
    protected void onDestroy() {
        PrintService.pl.disconnect();
        super.onDestroy();
    }

    /**
     * 生成二维码 要转换的地址或字符串,可以是中文
     *
     * @param url
     * @param width
     * @param height
     * @return
     */
    public Bitmap createQRImage(String url, final int width, final int height) {
        try {
            // 判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1) {
                return null;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url,
                    BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000;
                    } else {
                        pixels[y * width + x] = 0xffffffff;
                    }
                }
            }
            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 初始化打印服务
     */
    private void initPrintService(){
        btStatushandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_READ://监听蓝牙打印机状态
                        byte[] readBuf = (byte[]) msg.obj;
                        String readMessage = new String(readBuf, 0, msg.arg1);
                        Log.i(TAG,"readMessage="+readMessage);
                        Log.i(TAG, "readBuf:" + readBuf[0]);
                        if (readBuf[0] == 0x13) {
                            PrintService.isFUll = true;
                            showShortToast(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_bufferfull));
                        } else if (readBuf[0] == 0x11) {
                            PrintService.isFUll = false;
                            showShortToast(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_buffernull));
                        } else if (readBuf[0] == 0x08) {
                            showShortToast(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_nopaper));
                        } else if (readBuf[0] == 0x01) {
                            //showShortToast(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_printing));
                        }  else if (readBuf[0] == 0x04) {
                            showShortToast(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_hightemperature));
                        } else if (readBuf[0] == 0x02) {
                            showShortToast(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_lowpower));
                        }else {
                            if (readMessage.contains("800"))// 80mm paper
                            {
                                PrintService.imageWidth = 72;
                                Toast.makeText(getApplicationContext(), "80mm",
                                        Toast.LENGTH_SHORT).show();
                            } else if (readMessage.contains("580"))// 58mm paper
                            {
                                PrintService.imageWidth = 48;
                                Toast.makeText(getApplicationContext(), "58mm",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    case MESSAGE_STATE_CHANGE://监听蓝牙打印机连接状态变化
                        switch (msg.arg1) {
                            case PrinterClass.STATE_CONNECTED:
                                break;
                            case PrinterClass.STATE_CONNECTING:
                                showDialog();
                                Toast.makeText(getApplicationContext(),
                                        "正在连接", Toast.LENGTH_SHORT).show();
                                break;
                            case PrinterClass.STATE_LISTEN:
                            case PrinterClass.STATE_NONE:
                                break;
                            case PrinterClass.SUCCESS_CONNECT:
                                ////PrintService.pl.write(new byte[] { 0x1b, 0x2b });
                                dismissDialog();
                                try {
                                    Thread.sleep(10);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                PrintService.pl.write(new byte[] { 0x1d, 0x67,0x33 });
                                Toast.makeText(getApplicationContext(),
                                        "连接成功", Toast.LENGTH_SHORT).show();
                                break;
                            case PrinterClass.FAILED_CONNECT:
                                dismissDialog();
                                Toast.makeText(getApplicationContext(),
                                        "连接失败", Toast.LENGTH_SHORT).show();

                                break;
                            case PrinterClass.LOSE_CONNECT:
                                Toast.makeText(getApplicationContext(), "打印机断开连接",
                                        Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case MESSAGE_WRITE:

                        break;
                }
                super.handleMessage(msg);
            }
        };

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                }
            }
        };

        PrintService.pl =new BtService(this, btStatushandler, handler);
    }

    /**
     * 弹出打印成功弹窗
     */
    private void showPrintCompleteDialog(){
        View view = LayoutInflater.from(getRealContext()).inflate(R.layout.layout_public_dialog, null);
        // 设置style 控制默认dialog带来的边距问题
        final Dialog dialog = new Dialog(getRealContext(), R.style.custom_dialog_no_titlebar);
        dialog.setContentView(view);
        dialog.show();

        TextView titleTv= view.findViewById(R.id.dialog_title);
        titleTv.setText("打印成功");
        TextView contentTv= view.findViewById(R.id.dialog_content);
        contentTv.setText("医废桶二维码标签已打印成功，是否继续打印标签？");
        TextView button1= view.findViewById(R.id.dialog_btn1);
        button1.setText("退出");
        TextView button2= view.findViewById(R.id.dialog_btn2);
        button2.setText("继续打印");
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
     * 弹出打印服务未启动弹窗
     */
    private void showPrintServiceDialog(){
        View view = LayoutInflater.from(getRealContext()).inflate(R.layout.layout_public_dialog, null);
        // 设置style 控制默认dialog带来的边距问题
        final Dialog dialog = new Dialog(getRealContext(), R.style.custom_dialog_no_titlebar);
        dialog.setContentView(view);
        dialog.show();

        TextView titleTv= view.findViewById(R.id.dialog_title);
        titleTv.setText("错误提示");
        TextView contentTv= view.findViewById(R.id.dialog_content);
        contentTv.setText("打印服务启动失败");
        TextView button1= view.findViewById(R.id.dialog_btn1);
        button1.setText("取消打印");
        TextView button2= view.findViewById(R.id.dialog_btn2);
        button2.setText("重试");
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPrintService();
                dialog.dismiss();
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
     * 弹出打印机未连接弹窗
     */
    private void showConnectPrintDialog(){
        View view = LayoutInflater.from(getRealContext()).inflate(R.layout.layout_public_dialog, null);
        // 设置style 控制默认dialog带来的边距问题
        final Dialog dialog = new Dialog(getRealContext(), R.style.custom_dialog_no_titlebar);
        dialog.setContentView(view);
        dialog.show();

        TextView titleTv= view.findViewById(R.id.dialog_title);
        titleTv.setText("错误提示");
        TextView contentTv= view.findViewById(R.id.dialog_content);
        contentTv.setText("打印机连接失败");
        TextView button1= view.findViewById(R.id.dialog_btn1);
        button1.setText("取消打印");
        TextView button2= view.findViewById(R.id.dialog_btn2);
        button2.setText("重试");
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
                PrintService.pl.connect("57:4C:54:14:3A:68");
                dialog.dismiss();
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
