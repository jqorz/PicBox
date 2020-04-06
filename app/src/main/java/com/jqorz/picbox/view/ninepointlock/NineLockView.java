package com.jqorz.picbox.view.ninepointlock;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.jqorz.picbox.R;

import java.util.ArrayList;
import java.util.List;


public class NineLockView extends View {
    private static final String TAG = "ScreenLockView";

    // 错误格子的图片
    private Bitmap errorBitmap;
    // 正常格子的图片
    private Bitmap normalBitmap;
    // 手指按下时格子的图片
    private Bitmap pressedBitmap;
    // 错误时连线的图片
    private Bitmap lineErrorBitmap;
    // 手指按住时连线的图片
    private Bitmap linePressedBitmap;
    // 偏移量，使九宫格在屏幕中央
    private int offset;
    // 九宫格的九个格子是否已经初始化
    private boolean init;
    // 格子的半径
    private int radius;
    // 密码
    private String password = "123456";
    // 九个格子
    private NinePoint[][] mNinePoints = new NinePoint[3][3];
    private int width;
    private int height;
    private Matrix matrix = new Matrix();
    private float moveX = -1;
    private float moveY = -1;
    // 是否手指在移动
    private boolean isMove;
    // 是否可以触摸，当用户抬起手指，划出九宫格的密码不正确时为不可触摸
    private boolean canTouch = true;
    // 用来存储记录被按下的点
    private List<NinePoint> mPressedNinePoint = new ArrayList<>();
    // 屏幕解锁监听器
    private OnScreenLockListener listener;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            canTouch = true;
            reset();
            invalidate();
        }
    };
    private int margin;

    public NineLockView(Context context) {
        super(context);
        init();
    }

    public NineLockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NineLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private void init() {

        errorBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bitmap_error);
        normalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bitmap_normal);
        pressedBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bitmap_pressed);
        lineErrorBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.line_error);
        linePressedBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.line_pressed);
        radius = normalBitmap.getWidth() / 2;
        margin = Math.max(margin, radius);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthSize > heightSize) {
            offset = (widthSize - heightSize) / 2;
        } else {
            offset = (heightSize - widthSize) / 2;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!init) {
            width = getWidth();
            height = getHeight();
            initPoint();
            init = true;
        }
        drawPoint(canvas);
        if (moveX != -1 && moveY != -1) {
            drawLine(canvas);
        }
    }

    // 画直线
    private void drawLine(Canvas canvas) {

        // 将pressedPoint中的所有格子依次遍历，互相连线
        for (int i = 0; i < mPressedNinePoint.size() - 1; i++) {
            // 得到当前格子
            NinePoint ninePoint = mPressedNinePoint.get(i);
            // 得到下一个格子
            NinePoint nextNinePoint = mPressedNinePoint.get(i + 1);
            // 旋转画布
            canvas.rotate(RotateDegrees.getDegrees(ninePoint, nextNinePoint), ninePoint.getX(), ninePoint.getY());

            matrix.reset();
            // 根据距离设置拉伸的长度
            matrix.setScale(getDistance(ninePoint, nextNinePoint) / linePressedBitmap.getWidth(), 1f);
            // 进行平移
            matrix.postTranslate(ninePoint.getX(), ninePoint.getY() - linePressedBitmap.getWidth() / 2);


            if (ninePoint.getStatus() == NinePoint.STATUS_PRESSED) {
                canvas.drawBitmap(linePressedBitmap, matrix, null);
            } else {
                canvas.drawBitmap(lineErrorBitmap, matrix, null);
            }
            // 把画布旋转回来
            canvas.rotate(-RotateDegrees.getDegrees(ninePoint, nextNinePoint), ninePoint.getX(), ninePoint.getY());
        }

        // 如果是手指在移动的情况
        if (isMove) {
            NinePoint lastNinePoint = mPressedNinePoint.get(mPressedNinePoint.size() - 1);
            canvas.rotate(RotateDegrees.getDegrees(lastNinePoint, moveX, moveY), lastNinePoint.getX(), lastNinePoint.getY());

            matrix.reset();
            Log.i(TAG, "the distance : " + lastNinePoint.getDistance(moveX, moveY) / linePressedBitmap.getWidth());
            matrix.setScale((float) lastNinePoint.getDistance(moveX, moveY) / linePressedBitmap.getWidth(), 1f);
            matrix.postTranslate(lastNinePoint.getX(), lastNinePoint.getY() - linePressedBitmap.getWidth() / 2);
            canvas.drawBitmap(linePressedBitmap, matrix, null);

            canvas.rotate(-RotateDegrees.getDegrees(lastNinePoint, moveX, moveY), lastNinePoint.getX(), lastNinePoint.getY());
        }
    }


    // 根据两个point计算出之间的距离
    private float getDistance(NinePoint ninePoint, NinePoint nextNinePoint) {
        return (float) Math.sqrt(Math.pow(nextNinePoint.getX() - ninePoint.getX(), 2f) + Math.pow(nextNinePoint.getY() - ninePoint.getY(), 2f));
    }

    private void drawPoint(Canvas canvas) {
        for (int i = 0; i < mNinePoints.length; i++) {
            for (int j = 0; j < mNinePoints[i].length; j++) {
                int state = mNinePoints[i][j].getStatus();
                if (state == NinePoint.STATUS_DEFAULT) {
                    canvas.drawBitmap(normalBitmap, mNinePoints[i][j].getX() - radius, mNinePoints[i][j].getY() - radius, null);
                } else if (state == NinePoint.STATUS_PRESSED) {
                    canvas.drawBitmap(pressedBitmap, mNinePoints[i][j].getX() - radius, mNinePoints[i][j].getY() - radius, null);
                } else {
                    canvas.drawBitmap(errorBitmap, mNinePoints[i][j].getX() - radius, mNinePoints[i][j].getY() - radius, null);
                }
            }
        }
    }

    //初始化九宫格的点
    private void initPoint() {
        mNinePoints[0][0] = new NinePoint(width / 4f, offset + width / 4f, 1);
        mNinePoints[0][1] = new NinePoint(width / 2f, offset + width / 4f, 2);
        mNinePoints[0][2] = new NinePoint(width * 3f / 4, offset + width / 4f, 3);

        mNinePoints[1][0] = new NinePoint(width / 4f, offset + width / 2f, 4);
        mNinePoints[1][1] = new NinePoint(width / 2f, offset + width / 2f, 5);
        mNinePoints[1][2] = new NinePoint(width * 3f / 4, offset + width / 2f, 6);

        mNinePoints[2][0] = new NinePoint(width / 4f, offset + width * 3f / 4, 7);
        mNinePoints[2][1] = new NinePoint(width / 2f, offset + width * 3f / 4, 8);
        mNinePoints[2][2] = new NinePoint(width * 3f / 4, offset + width * 3f / 4, 9);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (canTouch) {
            float x = event.getX();
            float y = event.getY();
            NinePoint ninePoint;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 判断用户触摸的点是否在九宫格的任意一个格子之内
                    ninePoint = isPoint(x, y);
                    if (ninePoint != null) {
                        ninePoint.setStatus(NinePoint.STATUS_PRESSED);  // 切换为按下模式
                        mPressedNinePoint.add(ninePoint);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mPressedNinePoint.size() > 0) {
                        ninePoint = isPoint(x, y);
                        if (ninePoint != null) {
                            if (!crossPoint(ninePoint)) {
                                ninePoint.setStatus(NinePoint.STATUS_PRESSED);
                                mPressedNinePoint.add(ninePoint);
                            }
                        }
                        moveX = x;
                        moveY = y;
                        isMove = true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    isMove = false;
                    StringBuilder tempPwd = new StringBuilder();
                    for (NinePoint p : mPressedNinePoint) {
                        tempPwd.append(p.getPassword());
                    }
                    canTouch = false;
                    if (tempPwd.toString().equals(password)) {
                        if (listener != null) {
                            listener.lockResult(password, true);
                            this.postDelayed(runnable, 1000);
                        }

                    } else {
                        for (NinePoint p : mPressedNinePoint) {
                            p.setStatus(NinePoint.STATUS_ERROR);
                        }
                        this.postDelayed(runnable, 1000);
                        if (listener != null) {
                            listener.lockResult(password, false);
                        }
                    }
                    break;
            }
            invalidate();
        }
        return true;
    }

    private boolean crossPoint(NinePoint ninePoint) {
        return mPressedNinePoint.contains(ninePoint);
    }

    public void setOnScreenLockListener(OnScreenLockListener listener) {
        this.listener = listener;
    }

    private NinePoint isPoint(float x, float y) {
        NinePoint ninePoint;
        for (NinePoint[] point : mNinePoints) {
            for (NinePoint value : point) {
                ninePoint = value;
                if (isContain(ninePoint, x, y)) {
                    return ninePoint;
                }
            }
        }
        return null;
    }

    private boolean isContain(NinePoint ninePoint, float x, float y) {
        return Math.sqrt(Math.pow(x - ninePoint.getX(), 2f) + Math.pow(y - ninePoint.getY(), 2f)) <= radius;
    }

    // 重置格子
    private void reset() {
        for (NinePoint[] ninePoint : mNinePoints) {
            for (NinePoint point : ninePoint) {
                point.setStatus(NinePoint.STATUS_DEFAULT);
            }
        }
        mPressedNinePoint.clear();
    }

    public interface OnScreenLockListener {
        void lockResult(String password, boolean flag);
    }
}
