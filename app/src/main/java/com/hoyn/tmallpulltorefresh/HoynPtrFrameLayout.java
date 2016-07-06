package com.hoyn.tmallpulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;


/**
 * Created by Hoyn on 2016/7/5.
 */
public class HoynPtrFrameLayout extends PtrFrameLayout {
    private static final String TAG = "PtrFrameLayout";
    private HoynRadioGroup myRadioGroup;

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
        if (myRadioGroup != null) {
            myRadioGroup.dispatchTouchEvent(e);
            float headerHeight = getPtrIndicator().getHeaderHeight();
            float off_y = getPtrIndicator().getCurrentPosY();
            if(off_y<headerHeight||off_y>headerHeight*2){
                myRadioGroup.setIsHeaderShow(false);
            }else{
                myRadioGroup.setIsHeaderShow(true);
            }
        }
        return superEvent;
    }


    @Override
    protected void onLayout(boolean flag, int i, int j, int k, int l) {
        super.onLayout(flag, i, j, k, l);
    }

}
