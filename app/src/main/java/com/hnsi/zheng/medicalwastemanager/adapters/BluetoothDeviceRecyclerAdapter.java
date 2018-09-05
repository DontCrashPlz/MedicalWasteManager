package com.hnsi.zheng.medicalwastemanager.adapters;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.LayoutRes;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hnsi.zheng.medicalwastemanager.R;
import com.qs.helper.printer.Device;

/**
 * Created by Zheng on 2018/7/8.
 */

public class BluetoothDeviceRecyclerAdapter extends BaseQuickAdapter<BluetoothDevice, BaseViewHolder> {

    public BluetoothDeviceRecyclerAdapter(@LayoutRes int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, BluetoothDevice item) {
        if (item.getName()== null
                || "".equals(item.getName().trim())){
            helper.setText(R.id.device_name, "未知设备");
        }else {
            helper.setText(R.id.device_name, item.getName());
        }

        if (item.getAddress()== null
                || "".equals(item.getAddress().trim())){
            helper.setText(R.id.device_port, "未知串口号");
        }else {
            helper.setText(R.id.device_port, item.getAddress());
        }
    }

}
