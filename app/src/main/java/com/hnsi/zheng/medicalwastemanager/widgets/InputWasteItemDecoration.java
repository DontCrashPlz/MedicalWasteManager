package com.hnsi.zheng.medicalwastemanager.widgets;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Zheng on 2018/7/6.
 */

public class InputWasteItemDecoration extends RecyclerView.ItemDecoration {

    private Paint mPaint;

    public InputWasteItemDecoration() {
        mPaint= new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.rgb(161,161,161));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getChildAdapterPosition(view)!= 0)
            outRect.top= 1;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount= parent.getChildCount();
        for (int i= 0; i< childCount; i++){
            View view= parent.getChildAt(i);
            int position= parent.getChildAdapterPosition(view);
            if (position== 0) continue;
            c.drawLine(
                    parent.getPaddingLeft(),
                    view.getTop()-1,
                    parent.getWidth()-parent.getPaddingRight(),
                    view.getTop()-1,
                    mPaint);
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        c.drawLine(parent.getWidth()/7*3, parent.getTop()-parent.getPaddingTop(), parent.getWidth()/7*3, parent.getBottom(), mPaint);
        c.drawLine(parent.getWidth()/7*5, parent.getTop(), parent.getWidth()/7*5, parent.getBottom(), mPaint);
    }

}
