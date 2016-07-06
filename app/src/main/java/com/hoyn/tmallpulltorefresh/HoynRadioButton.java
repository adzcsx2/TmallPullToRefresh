package com.hoyn.tmallpulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;

/**
 * Created by Hoyn on 2016/7/5.
 */
public class HoynRadioButton extends RadioButton {

    private float mRadius;
    private int mColor = 0xE61A5F;

    public HoynRadioButton(Context context) {
        super(context);
    }

    public HoynRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HoynRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mRadius = getWidth()/2;
    }
}
