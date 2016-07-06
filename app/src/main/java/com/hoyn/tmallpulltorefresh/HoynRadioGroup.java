package com.hoyn.tmallpulltorefresh;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
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


    private static final int animatorDuration = 200;//动画执行时间
    private static final int animatorInterval = 20; //动画执行频率

    private float radius;

    public HoynRadioGroup(Context context) {
        super(context);
        paintInit();
    }

    public HoynRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        paintInit();
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //getCurrentIndex
                for (int i = 0; i < childCount; i++) {
                    if (getChildAt(i).getId() == getCheckedRadioButtonId())
                        currentIndex = i;
                }
                down_x = ev.getX();
                animatInit();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isAnimating) {
                    //showCircle animation
                    if (isChangeState != isHeaderShow) {
                        isShowCircleAnimation = true;
                        if (isHeaderShow) {
                            //open the circle when the header is show complete
                            circleAnimationStart(0, circle.getRadius(), 300, false, ev);
                        } else {
                            //close the circle when the header is not show or the height more than the headerView's height * 2;
                            circleAnimationStart(circle.getRadius(), 0, 300, true, ev);
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
                                animationStart(currentIndex, new AnimatorListener(currentChild, preChild));
                            }
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                off_left = 0;
                off_right = 0;
                //close the circle when finger up
                circleAnimationStart(circle.getRadius(), 0, 300, true, ev);
                invalidate();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * create circle animation
     *
     * @param from
     * @param to
     * @param duration
     * @param isCreate
     * @param ev       get the down_x when animation is end;
     */
    private void circleAnimationStart(final float from, final float to, final int duration, final boolean isCreate, final MotionEvent ev) {
        ObjectAnimator mRadiusAnimator = ObjectAnimator.ofFloat(this, "radius", from,
                to);
        mRadiusAnimator.setDuration(duration);
        mRadiusAnimator
                .setInterpolator(new AccelerateDecelerateInterpolator());
        mRadiusAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isShowCircle = !isCreate;
                isAnimating = false;
                isChangeState = isHeaderShow;
                isShowCircleAnimation = false;
                if (ev != null)
                    down_x = ev.getX();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                isAnimating = false;
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        mRadiusAnimator.start();
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
    private void animationStart(final int index, OnAnimatorListener onAnimatorListener) {
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
                int mOff_x = Math.abs(currentX - endX) / (animatorDuration / animatorInterval);
                int mPull_x = mOff_left > mOff_right ? mOff_left : mOff_right;
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
        if (isHeaderShow) {
            if (isShowCircleAnimation) {
                canvas.drawCircle(circle.getX(), circle.getY(), radius, mPaint);
                return;
            }
            if (isShowCircle && !isShowCircleAnimation) {
                //get the short edge
                int width = circle.getX() < circle.getY() ? circle.getX() : circle.getY();
                //In order to pull the circle,get two semi-circle , one is left, on is right.
                final RectF rectLeft = new RectF(width - circle.getRadius() - off_left, width - circle.getRadius(), width + circle.getRadius() + off_left, width + circle.getRadius());
                final RectF rectRight = new RectF(width - circle.getRadius() - off_right, width - circle.getRadius(), width + circle.getRadius() + off_right, width + circle.getRadius());
                rectLeft.offset(circle.getX() - circle.getRadius(), circle.getY() - circle.getRadius());
                rectRight.offset(circle.getX() - circle.getRadius(), circle.getY() - circle.getRadius());
                //draw the circle
                canvas.drawArc(rectLeft, 90, 180, true, mPaint);
                canvas.drawArc(rectRight, 270, 180, true, mPaint);
            }
        } else {
            if (isShowCircleAnimation) {
                canvas.drawCircle(circle.getX(), circle.getY(), radius, mPaint);
                return;
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //get children
        childCount = getChildCount();
        circleList.clear();
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
                    //calculate the current circle centerPoint and radius
                    circle = new Circle(center_x, center_y, width < height ? width : height / 2);
                }
            }
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
