package com.hoyn.tmallpulltorefresh;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import in.srain.cube.views.ptr.indicator.PtrIndicator;


/**
 * Created by Hoyn on 2016/7/5.
 */
public class HoynPtrFrameLayout extends PtrFrameLayout {
    private static final String TAG = "PtrFrameLayout";
    //the effective max height rate, the effective height is between header height and  header height * OUTSIDE_RATE
    private static final float OUTSIDE_RATE = 2f;
    // the header view
    private HoynRadioGroup myRadioGroup;
    // the relevant of screen
    private PtrIndicator mPtrIndicator;
    // loading progressbar
    private View progressBar;
    // draw the shadow paint
    private Paint mPaint;
    // draw the shadow transparent alpha
    private float alpha;

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
        //let this view can draw
        setWillNotDraw(false);

        mPtrIndicator = getPtrIndicator();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setAlpha(0);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        boolean superEvent = super.dispatchTouchEvent(e);
        if (myRadioGroup != null) {
            //put touchEvent to customRadioGroup
            myRadioGroup.dispatchTouchEvent(e);
            float headerHeight = getPtrIndicator().getHeaderHeight();
            float off_y = getPtrIndicator().getCurrentPosY();
            //control the header transparent alpha.
            if (off_y < headerHeight) {
                myRadioGroup.setIsHeaderShow(false);
                //In order to Alpha change fast , so off_y/3.
                myRadioGroup.setAlpha(off_y / 3 / headerHeight);
            } else if (off_y > headerHeight * OUTSIDE_RATE) {
                myRadioGroup.setIsHeaderShow(false);
                myRadioGroup.setAlpha(1);
            } else {
                myRadioGroup.setIsHeaderShow(true);
                myRadioGroup.setAlpha(1);
            }
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
    protected void onPositionChange(boolean isInTouching, byte status, PtrIndicator mPtrIndicator) {
        super.onPositionChange(isInTouching, status, mPtrIndicator);
        alpha = (float) mPtrIndicator.getCurrentPosY() / mPtrIndicator.getHeaderHeight();
        if (alpha > 0.8) {
            alpha = 0.8f;
        }
        mPaint.setAlpha((int) (255 * alpha));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //draw the shadow and transparent alpha
        canvas.drawRect(0, mPtrIndicator.getCurrentPosY(), getWidth(), getHeight(), mPaint);
        getChildAt(0).setAlpha(1 - alpha);
    }


}
