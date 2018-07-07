package com.hnsi.zheng.medicalwastemanager.widgets.progressDialog;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by 键盘 on 2016/1/26.
 *
 */
public class ProgressDialog extends BaseProgressDialog{
    private static final float ANIMATE_SPEED = 1;
    private SpinView mIndeterminateView;

    @Override
    protected View setContentView() {
        mIndeterminateView = new SpinView(getContext());
        mIndeterminateView.setAnimationSpeed(ANIMATE_SPEED);
        int widthMeasureSpec  = Helper.dpToPixel(40, getContext());
        int heightMeasureSpec = Helper.dpToPixel(40, getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(widthMeasureSpec, heightMeasureSpec);
        mIndeterminateView.setLayoutParams(lp);
        return mIndeterminateView;
    }

    public ProgressDialog(Context context){
        super(context);
    }

    public ProgressDialog(Context context,String requestTag){
        super(context, requestTag);
    }

    /**
     * 改变动画的速度
     * @param scale 默认 1, 2 速度提升2倍, 0.5 速度减少半倍..等
     */
    public void setAnimationSpeed(int scale) {
        mIndeterminateView.setAnimationSpeed(scale);
    }
}
