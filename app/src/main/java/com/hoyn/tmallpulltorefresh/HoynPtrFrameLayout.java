package com.hoyn.tmallpulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import in.srain.cube.views.ptr.indicator.PtrIndicator;


/**
 * Created by Hoyn on 2016/7/5.
 */
public class HoynPtrFrameLayout extends PtrFrameLayout {
    private static final String TAG = "PtrFrameLayout";
    private HoynRadioGroup myRadioGroup;
    private PtrIndicator mPtrIndicator;
    private View progressBar;
    //the maxHeight is the radioGroup * rate;
    private static final float OUTSIDE_RATE = 2f;

    public HoynRadioGroup getMyRadioGroup() {
        return myRadioGroup;
    }

    public void setMyRadioGroup(HoynRadioGroup myRadioGroup) {
        this.myRadioGroup = myRadioGroup;
    }

    public void setProgressBar(View progressBar) {
        this.progressBar = progressBar;
    }

    public HoynPtrFrameLayout(Context context) {
        super(context);
        init();
    }

    public HoynPtrFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HoynPtrFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mPtrIndicator = getPtrIndicator();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        boolean superEvent = super.dispatchTouchEvent(e);
        if (myRadioGroup != null) {
            //put touchEvent to customRadioGroup
            myRadioGroup.dispatchTouchEvent(e);
            float headerHeight = getPtrIndicator().getHeaderHeight();
            float off_y = getPtrIndicator().getCurrentPosY();
            if (off_y < headerHeight) {
                myRadioGroup.setIsHeaderShow(false);
                //In order to Alpha show fast , so off_y/3.
                myRadioGroup.setAlpha(off_y / 3 / headerHeight);
//                setKeepHeaderWhenRefresh(false);
            } else if (off_y > headerHeight * OUTSIDE_RATE) {
                myRadioGroup.setIsHeaderShow(false);
                myRadioGroup.setAlpha(1);
//                setKeepHeaderWhenRefresh(true);
            } else {
                myRadioGroup.setIsHeaderShow(true);
                myRadioGroup.setAlpha(1);
//                setKeepHeaderWhenRefresh(false);
            }
        }

        if(progressBar==null){
            Log.e("aa","null");
        }else{
            Log.e("aa","!null");
        }
        //show the progressBar
        if (e.getAction() == MotionEvent.ACTION_DOWN && progressBar != null) {
            progressBar.setVisibility(INVISIBLE);
        } else if (e.getAction() == MotionEvent.ACTION_UP && progressBar != null) {
            if (myRadioGroup.isHeaderShow()) {
                progressBar.setVisibility(VISIBLE);
            }
        }

        return superEvent;
    }


    @Override
    protected void onLayout(boolean flag, int i, int j, int k, int l) {
        super.onLayout(flag, i, j, k, l);
    }

}
