package com.jqorz.picbox.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import com.jqorz.picbox.R;
import com.jqorz.picbox.utils.ToolUtil;


/**
 * 自定义RecyclerView的ItemDecoration
 */

public abstract class TitleItemDecoration extends RecyclerView.ItemDecoration {
    private static int COLOR_TITLE_FONT;
    private Bitmap bitmap;
    private Paint mPaint;
    private Rect mBounds;//用于存放测量文字Rect
    private int mTitleHeight;//title的高
    private Context context;
    private int columnSize;

    protected TitleItemDecoration(Context context, int columnSize) {
        super();
        mPaint = new Paint();
        mBounds = new Rect();

        this.columnSize = columnSize;

        this.context = context;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.bg_flag);
        COLOR_TITLE_FONT = Color.WHITE;
        mTitleHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
        int mTitleFontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, context.getResources().getDisplayMetrics());
        mPaint.setTextSize(mTitleFontSize);
        mPaint.setAntiAlias(true);
    }


    /**
     * 计算当前位置是否应该有标题
     */
    public abstract boolean calculateShouldHaveHeader(int position);


    public abstract String getTag(int position);

    @Override//先调用 在绘制itemView之前绘制,此处为绘制每一个Title
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final int left = ToolUtil.dp2px(context, R.dimen.dp_m_38);
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            int position = parent.getChildLayoutPosition(child);
            //我记得Rv的item position在重置时可能为-1.保险点判断一下吧
            if (position > -1) {
                if (position == 0) {//等于0肯定要有title的
                    //等于0的时候，线的起始Y位置不能为0
                    drawTitleArea(c, left, right, child, params, position);
                } else {//其他的通过判断
                    if (calculateShouldHaveHeader(position)) {
                        drawTitleArea(c, left, right, child, params, position);
                    }
                }

            }
        }
    }


    /**
     * 绘制Title区域背景和文字的方法
     */
    private void drawTitleArea(Canvas c, int left, int right, View child, RecyclerView.LayoutParams params, int position) {//最先调用，绘制在最下层
        String tag = getTag(position);
//        mPaint.setColor(COLOR_TITLE_BG);
//        c.drawRect(left, child.getTop() - params.topMargin - mTitleHeight, right, child.getTop() - params.topMargin, mPaint);
        mPaint.setColor(COLOR_TITLE_FONT);
        mPaint.getTextBounds(tag, 0, String.valueOf(tag).length(), mBounds);
        //绘制标题背景
        long bitmapStartY = child.getTop() - params.topMargin - mTitleHeight / 2 - bitmap.getHeight() / 2;
        c.drawBitmap(bitmap, left + ToolUtil.dp2px(context, R.dimen.dp_10), bitmapStartY, mPaint);
        //绘制标题文字
        long textStartY = child.getTop() - params.topMargin - (mTitleHeight / 2 - mBounds.height() / 2);
        c.drawText(tag, left + ToolUtil.dp2px(context, R.dimen.dp_30), textStartY, mPaint);

    }

    @Override//设置指定itemView的paddingLeft，paddingTop， paddingRight， paddingBottom
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildLayoutPosition(view);
//        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        //我记得Rv的item position在重置时可能为-1.保险点判断一下吧
        if (position > -1) {


            if (calculateShouldHaveHeader(position)) {
                outRect.set(0, mTitleHeight, 0, 0);
            } else {
                outRect.set(0, 0, 0, 0);
            }
            if (position < columnSize) {//最上方的标题上方要空出20dp
                outRect.set(0, mTitleHeight + ToolUtil.dp2px(context, R.dimen.dp_20), 0, 0);
            }
        }
    }

}