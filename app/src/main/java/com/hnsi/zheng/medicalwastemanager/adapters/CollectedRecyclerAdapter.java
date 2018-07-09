package com.hnsi.zheng.medicalwastemanager.adapters;

import android.support.annotation.LayoutRes;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hnsi.zheng.medicalwastemanager.R;
import com.hnsi.zheng.medicalwastemanager.beans.CollectedWasteEntity;

/**
 * Created by Zheng on 2018/7/8.
 */

public class CollectedRecyclerAdapter extends BaseQuickAdapter<CollectedWasteEntity, BaseViewHolder> {

    public CollectedRecyclerAdapter(@LayoutRes int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, CollectedWasteEntity item) {
        helper.setText(R.id.tv_wastetype, item.getWasteTypeDictName())
                .setText(R.id.tv_wasteweigh, item.getWeight() + "kg")
                .setText(R.id.tv_wastenum, item.getGuid())
                .setText(R.id.tv_collecttime, item.getCreateTime())
                .setText(R.id.tv_department, item.getDepartmentName() + "(" + item.getDepartmentUserName() + ")")
                .setText(R.id.tv_operate, item.getCreateByName());
    }
}
