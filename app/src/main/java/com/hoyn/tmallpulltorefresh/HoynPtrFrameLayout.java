package com.hoyn.tmallpulltorefresh;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

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
    // the scroll util
    private ScrollChecker mScrollChecker;
    // draw the shadow paint
    private Paint mPaint;
    // draw the shadow transparent alpha
    private float alpha;
    // control progressBar show or hide
    private LinearLayout progressLayout;
    //custom progressBar,if null,use the default progressbar
    private ProgressBar progressBar;
    private OnFiggerUpListener onFiggerUpListener;

    public HoynRadioGroup getMyRadioGroup() {
        return myRadioGroup;
    }

    public void setMyRadioGroup(HoynRadioGroup myRadioGroup) {
        this.myRadioGroup = myRadioGroup;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public void setOnFiggerUpListener(OnFiggerUpListener onFiggerUpListener) {
        this.onFiggerUpListener = onFiggerUpListener;
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
        mScrollChecker = getScrollChecker();
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
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            hideProgressBar();
            onPositionChange(true, PTR_STATUS_LOADING, mPtrIndicator);
        } else if (e.getAction() == MotionEvent.ACTION_UP && onFiggerUpListener != null) {
            if (myRadioGroup.isHeaderShow()) {
                onFiggerUpListener.onFiggerUp(myRadioGroup.getCheckedRadioButtonId());
            } else {
                refreshComplete();
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


    public void showProgressBar() {
        progressLayout.setVisibility(VISIBLE);
    }

    public void hideProgressBar() {
        progressLayout.setVisibility(INVISIBLE);
    }

    public void scrollToTop() {
        tryScrollBackToTop();
    }

    /**
     * add the progressBar
     */
    @Override
    protected void onLayout(boolean flag, int i, int j, int k, int l) {
        super.onLayout(flag, i, j, k, l);
        RelativeLayout headerView = (RelativeLayout) getHeaderView();
        if (progressLayout == null) {
            progressLayout = new LinearLayout(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.height = getHeaderView().getHeight();
            progressLayout.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= 16) {
                progressLayout.setBackground(getHeaderView().getBackground());
            } else {
                progressLayout.setBackgroundColor(Color.WHITE);
            }
            if (progressBar == null) {
                progressBar = new ProgressBar(getContext());
            }
            progressLayout.addView(progressBar);
            headerView.addView(progressLayout, params);
            progressLayout.setVisibility(INVISIBLE);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //draw the shadow and transparent alpha
        canvas.drawRect(0, mPtrIndicator.getCurrentPosY(), getWidth(), getHeight(), mPaint);
        getChildAt(0).setAlpha(1 - alpha);
    }


    public interface OnFiggerUpListener {
        void onFiggerUp(int checkedId);
    }

}
