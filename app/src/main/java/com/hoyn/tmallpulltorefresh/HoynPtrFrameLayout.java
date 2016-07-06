package com.hoyn.tmallpulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by Hoyn on 2016/7/5.
 */
public class HoynPtrFrameLayout extends PtrFrameLayout {
    private static final String TAG = "PtrFrameLayout";
    HoynRadioGroup myRadioGroup;

    public HoynRadioGroup getMyRadioGroup() {
        return myRadioGroup;
    }

    public void setMyRadioGroup(HoynRadioGroup myRadioGroup) {
        this.myRadioGroup = myRadioGroup;
    }

    public HoynPtrFrameLayout(Context context) {
        super(context);
    }

    public HoynPtrFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HoynPtrFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        boolean superEvent = super.dispatchTouchEvent(e);
        //put touchEvent to customRadioGroup
        if (myRadioGroup != null)
            myRadioGroup.dispatchTouchEvent(e);
        return superEvent;
    }

}
