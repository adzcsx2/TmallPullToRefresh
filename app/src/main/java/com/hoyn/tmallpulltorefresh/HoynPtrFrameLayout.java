package com.hoyn.tmallpulltorefresh;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import in.srain.cube.views.ptr.indicator.PtrIndicator;


/**
 * Created by Hoyn on 2016/7/5.
 */
public class HoynPtrFrameLayout extends PtrFrameLayout {
    private static final String TAG = "PtrFrameLayout";
    //the effective max height rate,
    // if have tabview,the rate = 1 and the all height is tabview's height + group's height.
    // else the all height is group's height * NOT_HAS_TABVIEW_RATE;
    private float NOT_HAS_TABVIEW_RATE = 1.8f;
    private boolean hasTabView = true;
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
    private View tabView;
    private OnFiggerUpListener onFiggerUpListener;

    private boolean isShowTab = false;

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

    public View getTabView() {
        return tabView;
    }

    public void setTabView(View tabView) {
        this.tabView = tabView;
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
            float headerHeight = myRadioGroup.getHeight();
            float off_y = getPtrIndicator().getCurrentPosY();
            //control the header transparent alpha.
            if (off_y < myRadioGroup.getHeight()) {
                myRadioGroup.setIsHeaderShow(false);
                //In order to Alpha change fast , so off_y/3.
                myRadioGroup.setAlpha(-0.5f + off_y / headerHeight);
            } else if (off_y > getPtrIndicator().getHeaderHeight() * (hasTabView ? 1 : NOT_HAS_TABVIEW_RATE)) {
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
                if (getFixHeader()) {
                    setShowTab(false);
                    scrollToTop();
                    return superEvent;
                } else {
                    scrollTo(myRadioGroup.getHeight());
                    onFiggerUpListener.onFiggerUp(myRadioGroup.getCheckedRadioButtonId());
                }
            } else {
//                if (mPtrIndicator.getCurrentPosY() > getHeaderHeight() * OUTSIDE_RATE && hasTabView) {
                if (mPtrIndicator.getCurrentPosY() > getHeaderHeight() && hasTabView) {
                    setShowTab(true);
                    mScrollChecker.tryToScrollTo(mPtrIndicator.getHeaderHeight(), (int) getDurationToCloseHeader());
                    tabView.setAlpha(1);
                } else {
                    scrollToTop();
                }
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
        //set tabview alpha
        if (hasTabView) {
            if (!isShowTab) {
                float tabAlpha = (mPtrIndicator.getCurrentPosY() - myRadioGroup.getHeight()) / (float) tabView.getHeight();
                // in order to set the alpha show naturally,so tabalpha / 1.5
                tabAlpha = tabAlpha / 1.5f;
                tabView.setAlpha(tabAlpha);
            } else {
                tabView.setAlpha(1);
            }
        }


        invalidate();
    }


    public void showProgressBar() {
        progressLayout.setVisibility(VISIBLE);
    }

    public void hideProgressBar() {
        progressLayout.setVisibility(INVISIBLE);
    }

    public void scrollToTop() {
        mScrollChecker.tryToScrollTo(0, (int) getDurationToCloseHeader());
    }

    public void scrollTo(int to) {
        mScrollChecker.tryToScrollTo(to, (int) getDurationToCloseHeader());
    }

    public void completeRefresh() {
        scrollToTop();
    }

    private void setShowTab(boolean isShow) {
        if (hasTabView) {
            myRadioGroup.setIsShowTab(isShow);
            isShowTab = isShow;
            setFixHeader(isShow);
        }
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
        } else {
            RelativeLayout.LayoutParams groupParams = (RelativeLayout.LayoutParams) myRadioGroup.getLayoutParams();
            int groupHeight = myRadioGroup.getHeight()+groupParams.topMargin+groupParams.bottomMargin;

            ViewGroup.LayoutParams params = progressLayout.getLayoutParams();
            if (params.height != groupHeight) {
                params.height = groupHeight;
                progressLayout.setLayoutParams(params);
            }
        }
        //add TabView
        if (tabView == null) {
            //if not has tabView
            hasTabView = false;
            tabView = new LinearLayout(getContext());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.height = 1;
            headerView.addView(tabView, layoutParams);
        } else {
            if (headerView.findViewById(android.R.id.text1) == null) {
                headerView.removeAllViews();
                //let the footerView  translation is 0
                //set a casual id which can let groupView below the tabView
                tabView.setAlpha(0);
                tabView.setId(android.R.id.text1);
                tabView.setTag(TAG);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                headerView.addView(tabView, layoutParams);

                //RadioGroup
                RelativeLayout.LayoutParams groupParams = (RelativeLayout.LayoutParams) myRadioGroup.getLayoutParams();
                groupParams.addRule(RelativeLayout.BELOW, tabView.getId());
                myRadioGroup.setLayoutParams(groupParams);
                headerView.addView(myRadioGroup, groupParams);
                //progressbar
                int groupHeight = myRadioGroup.getHeight()+groupParams.topMargin+groupParams.bottomMargin;

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) progressLayout.getLayoutParams();
                params.height = groupHeight;
                params.addRule(RelativeLayout.BELOW, tabView.getId());
                progressLayout.setLayoutParams(params);
                headerView.addView(progressLayout, params);
            }
        }


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //draw the shadow and transparent alpha
        canvas.drawRect(0, mPtrIndicator.getCurrentPosY(), getWidth(), getHeight(), mPaint);
//        getChildAt(0).setAlpha(1 - alpha);
    }


    public interface OnFiggerUpListener {
        void onFiggerUp(int checkedId);
    }

}