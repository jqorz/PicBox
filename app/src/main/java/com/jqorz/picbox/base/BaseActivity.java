package com.jqorz.picbox.base;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


/**
 * 基类Activity
 */
public abstract class BaseActivity extends AppCompatActivity {

    public Context mContext;

    @Override
    final public void onCreate(Bundle savedInstanceState) {
        mContext = BaseActivity.this;

        super.onCreate(savedInstanceState);
        //安卓8.0禁止非全屏应用更改屏幕方向
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//禁止横屏

        setContentView(getLayoutResId());

        init(savedInstanceState);


    }


    protected abstract void init(Bundle savedInstanceState);

    protected abstract int getLayoutResId();


}
