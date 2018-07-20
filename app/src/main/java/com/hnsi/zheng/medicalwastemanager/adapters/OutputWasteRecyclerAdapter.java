package com.hnsi.zheng.medicalwastemanager.adapters;

import android.support.annotation.LayoutRes;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hnsi.zheng.medicalwastemanager.R;
import com.hnsi.zheng.medicalwastemanager.beans.OutputBucketEntity;
import com.hnsi.zheng.medicalwastemanager.utils.Tools;

/**
 * Created by Zheng on 2018/7/7.
 */

public class OutputWasteRecyclerAdapter extends BaseQuickAdapter<OutputBucketEntity, BaseViewHolder> {

    public OutputWasteRecyclerAdapter(@LayoutRes int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, OutputBucketEntity items) {
        helper.setText(R.id.textview_waste_num, items.getGuid())
                .setText(R.id.textview_waste_type, items.getInputWeigh() + "KG")
                .setText(R.id.textview_waste_weigh, items.getOutputWeigh() + "KG");
    }

}
