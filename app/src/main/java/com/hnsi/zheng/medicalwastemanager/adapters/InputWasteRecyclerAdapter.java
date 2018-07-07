package com.hnsi.zheng.medicalwastemanager.adapters;

import android.support.annotation.LayoutRes;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hnsi.zheng.medicalwastemanager.R;
import com.hnsi.zheng.medicalwastemanager.utils.Tools;

/**
 * Created by Zheng on 2018/7/7.
 */

public class InputWasteRecyclerAdapter extends BaseQuickAdapter<String[], BaseViewHolder> {

    public InputWasteRecyclerAdapter(@LayoutRes int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, String[] items) {
        helper.setText(R.id.textview_waste_num, items[9])
                .setText(R.id.textview_waste_type, Tools.getWasteTypeNameById(Integer.parseInt(items[6])))
                .setText(R.id.textview_waste_weigh, items[7] + "kg");
    }

}
