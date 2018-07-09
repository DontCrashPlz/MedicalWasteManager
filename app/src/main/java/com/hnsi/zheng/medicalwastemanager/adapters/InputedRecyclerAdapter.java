package com.hnsi.zheng.medicalwastemanager.adapters;

import android.support.annotation.LayoutRes;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hnsi.zheng.medicalwastemanager.R;
import com.hnsi.zheng.medicalwastemanager.beans.InputedBucketEntity;

/**
 * Created by Zheng on 2018/7/8.
 */

public class InputedRecyclerAdapter extends BaseQuickAdapter<InputedBucketEntity, BaseViewHolder> {

    public InputedRecyclerAdapter(@LayoutRes int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, InputedBucketEntity item) {
        helper.setText(R.id.tv_bucketnum, item.getGuid())
                .setText(R.id.tv_wastenum, item.getWasteAmount() + "(袋)")
                .setText(R.id.tv_inputtime, item.getCreateTime())
                .setText(R.id.tv_weigh, item.getWasteWeight() + "KG")
                .addOnClickListener(R.id.tv_detail);
    }
}
