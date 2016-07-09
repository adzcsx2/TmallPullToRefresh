package com.hoyn.tmallpulltorefresh;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hoyn on 2016/7/5.
 */
public class HoynRadioGroup extends RadioGroup {

    private static final String TAG = "HoynRadioGroup";
    private float down_x;
    private int childCount;
    private int currentIndex;
    private Paint mPaint;
    private int mColor = 0xFFE61A5F;
    private Path path;

    private int off_left = 0, off_right = 0;
    private Circle circle;
    private List<Circle> circleList = new ArrayList<>();

    private boolean isAnimating = false; //判断动画是否正在执行
    private boolean isHeaderShow = false; //判断下拉控件是否完全显示出来且不超过1.5倍控件高度
    private boolean isShowCircle = true; //判断下拉过程中是否显示圆
    private boolean isChangeState = false;//圆动画和动画结束的切换
    private boolean isShowCircleAnimation = true; //下拉过程中是否应该显示圆的动画
    private boolean isCircleAnimating = false; //判断显示圆的动画是否在执行

    private static final int createCircleDuration = 150; //圆出现动画执行时间
    private static final int createCircleInterval = 10; //动画执行频率
    private static final int animatorDuration = 200;//圆左右移动动画执行时间
    private static final int animatorInterval = 20; //动画执行频率

    private float radius;//下拉过程中 圆动画的半径
    private float alpha;//下拉过程中 控件的透明度

//    private View tabView;
    private int tabViewHeight = 0;

    public HoynRadioGroup(Context context) {
        super(context);
        paintInit();
    }

    public HoynRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        paintInit();
    }

//    public View getTabView() {
//        return tabView;
//    }
//
//    public void setTabView(View tabView) {
//        this.tabView = tabView;
//    }

    @Override
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void setColor(int mColor) {
        this.mColor = mColor;
    }

    public boolean isHeaderShow() {
        return isHeaderShow;
    }

    public void setIsHeaderShow(boolean isHeaderShow) {
        this.isHeaderShow = isHeaderShow;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getTabViewHeight() {
        return tabViewHeight;
    }

    private void paintInit() {
        setWillNotDraw(false);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAlpha(100);
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(5);
        path = new Path();
    }

    private void animatInit() {
        isAnimating = false; //判断动画是否正在执行
        isHeaderShow = false; //判断下拉控件是否完全显示出来且不超过1.5倍控件高度
        isShowCircle = true; //判断下拉过程中是否显示圆
        isChangeState = false;//圆动画和动画结束的切换
        isShowCircleAnimation = true; //下拉过程中是否应该显示圆的动画
    }

    private boolean isShowTab = false;

    public boolean isShowTab() {
        return isShowTab;
    }

    public void setIsShowTab(boolean isShowTab) {
        this.isShowTab = isShowTab;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(isShowTab){
            return false;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //getCurrentIndex
                for (int i = 0; i < childCount; i++) {
                    if (getChildAt(i).getId() == getCheckedRadioButtonId())
                        currentIndex = i;
                }
                down_x = ev.getX();
                animatInit();
                radius = 0;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isAnimating) {
                    //
                    for (int i = 0; i < childCount; i++) {
                        getChildAt(i).setAlpha(alpha);
                    }
                    //showCircle animation
                    if (isChangeState != isHeaderShow && !isCircleAnimating) {
                        isShowCircleAnimation = true;
                        if (isHeaderShow) {
                            //open the circle when the header is show complete
                            circleAnimationStart(0, circle.getRadius(), createCircleDuration, true, ev);
                        } else {
                            //close the circle when the header is not show or the height more than the headerView's height * 2;
                            circleAnimationStart(circle.getRadius(), 0, createCircleDuration, false, ev);
                        }
                        return super.dispatchTouchEvent(ev);
                    }
                    if (!isShowCircleAnimation) {
                        ///pull left or right animation
                        int off_x = (int) (ev.getX() - down_x);
                        final View preChild = getChildAt(currentIndex);
                        if (preChild == null) {
                            return super.dispatchTouchEvent(ev);
                        }
                        int width = preChild.getWidth();
                        //Calculation of tensile strength and invalidate the view.
                        if (off_x < 0 && Math.abs(off_x) < width / 2 && currentIndex != 0) {
                            off_left = (int) Math.abs((off_x / 1.5));
                            off_right = 0;
                        } else if (off_x > 0 && Math.abs(off_x) < width / 2 && currentIndex < childCount - 1) {
                            off_right = (int) Math.abs((off_x / 1.5));
                            off_left = 0;
                        }
                        invalidate();
                        //judge the distance whether arrive at next position
                        //In order to be smooth to the touch , width need to /2.
                        if (Math.abs(off_x) > width / 2) {
                            down_x = ev.getX();
                            //pull to right
                            if (off_x > 0 && currentIndex < childCount - 1)
                                currentIndex++;
                                //pull to left
                            else if (off_x < 0 && currentIndex > 0)
                                currentIndex--;
                            final View currentChild = getChildAt(currentIndex);
                            if (currentChild != null && currentChild instanceof RadioButton) {
                                //setAnimation
                                moveAnimationStart(currentIndex, new AnimatorListener(currentChild, preChild));
                            }
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                off_left = 0;
                off_right = 0;
                invalidate();
                break;
        }

        return false;
    }


    /**
     * create circle animation
     *
     * @param from
     * @param to
     * @param duration
     * @param ShowCircle
     * @param ev       get the down_x when animation is end;
     */
    private void circleAnimationStart(final float from, final float to, final int duration, final boolean ShowCircle, final MotionEvent ev) {
        isCircleAnimating = true;
        if (ShowCircle) {
            //let the radiobutton is checked after the circle is showed;
            //if the animation is create circle
            if (from > to) {
                isShowCircle = ShowCircle;
                isAnimating = false;
                isChangeState = isHeaderShow;
                isShowCircleAnimation = false;
                isCircleAnimating = false;
//                setCurrentChecked(true);
//                RadioButton rb = (RadioButton) getChildAt(currentIndex);
//                rb.setChecked(true);
                if (ev != null)
                    down_x = ev.getX();
                return;
            }
            radius = from;
            invalidate();
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    float addInterval = to / (createCircleDuration / createCircleInterval);
                    circleAnimationStart(from + addInterval, to, duration - createCircleInterval, ShowCircle, ev);
                }
            }, createCircleInterval);
        } else {
            if (from < 0) {
                isShowCircle = ShowCircle;
                isAnimating = false;
                isChangeState = isHeaderShow;
                isShowCircleAnimation = false;
                isCircleAnimating = false;
//                setCurrentChecked(false);
//                RadioButton rb = (RadioButton) getChildAt(currentIndex);
//                rb.setChecked(false);
                if (ev != null)
                    down_x = ev.getX();
                return;
            }
            radius = from;
            invalidate(); //update this view
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    float addInterval = circle.getRadius() / (createCircleDuration / createCircleInterval);
                    circleAnimationStart(from - addInterval, to, duration - createCircleInterval, ShowCircle, ev);
                }
            }, createCircleInterval);
        }
    }

    public void setRadius(float radius) {
        this.radius = radius;
        invalidate();
    }



    /**
     * move animation.
     * control a circle slip to the new circle.
     *
     * @param index
     * @param onAnimatorListener
     */
    private void moveAnimationStart(final int index, OnAnimatorListener onAnimatorListener) {
        final Circle mCurrentCircle = circleList.get(index);
        int preX = circle.getX();
        int currentX = mCurrentCircle.getX();
        endX = preX;
        mOff_left = off_left;
        mOff_right = off_right;
        onAnimatorListener.onAnimatorStart();
        animationHelper(preX, currentX, animatorDuration, onAnimatorListener);
    }


    private int endX;
    private int mOff_left;
    private int mOff_right;

    /**
     * use recursion help animator to draw the view
     *
     * @param preX               previous circle x position
     * @param currentX           current circle x position
     * @param duration           use recursion to judge the duration whether is overtime
     * @param onAnimatorListener
     */
    private void animationHelper(final int preX, final int currentX, final int duration, final OnAnimatorListener onAnimatorListener) {
        if (duration < 0) {
            return;
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                int mPreX = preX;
                //move distance once
                int mOff_x = Math.abs(currentX - endX) / (animatorDuration / animatorInterval);
                //judge the pull is left or right
                int mPull_x = mOff_left > mOff_right ? mOff_left : mOff_right;
                //the recover time is half of the move time
                int mPull_interval = Math.abs(mPull_x) / (animatorDuration / animatorInterval) * 2;

                if (mPreX < currentX) {
                    Log.i(TAG, "toRight");
                    //update the new circle position
                    mPreX += mOff_x;
                    //gradually recover the left pull
                    if (off_right > 0) {
                        off_right -= mPull_interval;
                    } else {
                        off_right = 0;
                    }
                    if (mPreX > currentX) {
                        mPreX = currentX;
                    }
                } else {
                    Log.i(TAG, "toLeft");
                    mPreX -= mOff_x;
                    if (off_left > 0) {
                        off_left -= mPull_interval;
                    } else {
                        off_left = 0;
                    }
                    if (mPreX < currentX) {
                        mPreX = currentX;
                    }
                }
                //set circle X position to invalidate the view
                circle.setX(mPreX);
                invalidate();
                if (mPreX != currentX) {
                    animationHelper(mPreX, currentX, duration - animatorInterval, onAnimatorListener);
                } else {
                    onAnimatorListener.onAnimatorComplete();
                }
            }
        }, animatorInterval);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        //get the short edge
        int width = circle.getX() < circle.getY() ? circle.getX() : circle.getY();
        RectF rectLeft = null;
        RectF rectRight = null;
        if (isShowCircleAnimation) {
            //show the circle animation
            rectLeft = new RectF(width - radius, width - radius, width + radius, width + radius);
            rectRight = new RectF(width - radius, width - radius, width + radius, width + radius);
        } else {
            if (isShowCircle && isHeaderShow) {
                //show the circle and follow touch
                rectLeft = new RectF(width - circle.getRadius() - off_left, width - circle.getRadius(), width + circle.getRadius() + off_left, width + circle.getRadius());
                rectRight = new RectF(width - circle.getRadius() - off_right, width - circle.getRadius(), width + circle.getRadius() + off_right, width + circle.getRadius());
            }
        }
        if (rectLeft == null) {
            return;
        }
        rectLeft.offset(circle.getX() - circle.getRadius(), circle.getY() - circle.getRadius());
        rectRight.offset(circle.getX() - circle.getRadius(), circle.getY() - circle.getRadius());
        //draw the circle
        canvas.drawArc(rectLeft, 90, 180, true, mPaint);
        canvas.drawArc(rectRight, 270, 180, true, mPaint);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //get children
        childCount = getChildCount();
        circleList.clear();
        boolean hasChecked = false;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child instanceof RadioButton) {
                float start_x = getChildAt(i).getX();
                float start_y = getChildAt(i).getY();
                int width = getChildAt(i).getWidth();
                int height = getChildAt(i).getHeight();
                int center_x = (int) (start_x + width / 2);
                int center_y = (int) (start_y + height / 2);
                //add circles to List for simple to manager;
                circleList.add(new Circle(center_x, center_y, width < height ? width : height / 2));
                if (((RadioButton) child).isChecked()) {
                    hasChecked = true;
                    //calculate the current circle centerPoint and radius
                    circle = new Circle(center_x, center_y, width < height ? width : height / 2);
                    currentIndex = i;
                }
            }
        }
        if(!hasChecked){
            Log.e(TAG,"must select a radiobutton");
        }


    }

    /**
     * the animation callback listener
     */
    private class AnimatorListener implements OnAnimatorListener {

        View currentChild;
        View preChild;

        public AnimatorListener(View currentChild, View preChild) {
            this.currentChild = currentChild;
            this.preChild = preChild;
        }

        @Override
        public void onAnimatorStart() {
            isAnimating = true;
        }

        @Override
        public void onAnimatorComplete() {
            isAnimating = false;
            //First set current radiobutton be checked
            ((RadioButton) currentChild).setChecked(true);
            //Next set previous radiobuton be unchecked;
            if (preChild instanceof RadioButton && currentChild != preChild) {
                ((RadioButton) preChild).setChecked(false);
            }
        }
    }

    /**
     * the module of Circle
     */
    private class Circle {
        int x;
        int y;
        int radius;

        public Circle(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Circle(int x, int y, int radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
        }

        public int getRadius() {
            return radius;
        }

        public void setRadius(int radius) {
            this.radius = radius;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }

    /**
     * the listener of animation callback
     */
    private interface OnAnimatorListener {
        void onAnimatorStart();

        void onAnimatorComplete();
    }

}
