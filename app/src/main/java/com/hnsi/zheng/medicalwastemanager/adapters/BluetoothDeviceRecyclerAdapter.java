package com.hnsi.zheng.medicalwastemanager.adapters;

import android.support.annotation.LayoutRes;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hnsi.zheng.medicalwastemanager.R;
import com.qs.helper.printer.Device;

/**
 * Created by Zheng on 2018/7/8.
 */

public class BluetoothDeviceRecyclerAdapter extends BaseQuickAdapter<Device, BaseViewHolder> {

    public BluetoothDeviceRecyclerAdapter(@LayoutRes int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, Device item) {
        helper.setText(R.id.device_name, item.deviceName)
                .setText(R.id.device_port, item.deviceAddress);
    }

}
