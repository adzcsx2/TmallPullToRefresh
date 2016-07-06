package com.hoyn.tmallpulltorefresh;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
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

    private static final String TAG = "Group";
    private float down_x;
    private int childCount;
    private int currentIndex;
    private Paint mPaint;
    private int mColor = 0xFFE61A5F;
    private Path path;

    private int off_left = 0, off_right = 0;

    private Circle circle;
    private List<Circle> circleList;

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
                break;
            case MotionEvent.ACTION_MOVE:
                int off_x = (int) (ev.getX() - down_x);
                View child = getChildAt(currentIndex);
                if (child == null) {
                    return super.dispatchTouchEvent(ev);
                }
                int width = child.getWidth();
                //Calculation of tensile strength
                if(off_x<0&&Math.abs(off_x)<width/2&&currentIndex!=0){
                    off_left = off_x/2;
                    off_right = 0;
                }else if (off_x>0&&Math.abs(off_x)<width/2&&currentIndex<childCount-1){
                    off_right =  off_x/2;
                    off_left = 0;
                }else{
                    off_left = 0;
                    off_right = 0;
                }
                invalidate();
                if (Math.abs(off_x) > width / 2) {
                    down_x = ev.getX();
                    //touch to right
                    if (off_x > 0 && currentIndex < childCount - 1)
                        currentIndex++;
                        //touch to left
                    else if (off_x < 0 && currentIndex > 0)
                        currentIndex--;
                    View mChild = getChildAt(currentIndex);
                    if (mChild != null && mChild instanceof RadioButton) {
                        //First set current radiobutton be checked
                        ((RadioButton) mChild).setChecked(true);
                        circle = circleList.get(currentIndex);
                        invalidate();
                        //Next set previous radiobuton be unchecked;
                        if (child instanceof RadioButton && mChild != child) {
                            ((RadioButton) child).setChecked(false);
                        }
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * control a circle slip to the index circle
     * @param index
     */
    private void animationStart(int index){
        
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = circle.getX() < circle.getY() ? circle.getX() : circle.getY();

        RectF rectLeft = new RectF(width - circle.getRadius() + off_left, width - circle.getRadius(), width + circle.getRadius() - off_left, width + circle.getRadius());
        RectF rectRight = new RectF(width - circle.getRadius() - off_right, width - circle.getRadius(), width + circle.getRadius() +off_right, width + circle.getRadius());
        rectLeft.offset(circle.getX() - circle.getRadius(), circle.getY() - circle.getRadius());
        rectRight.offset(circle.getX() - circle.getRadius(), circle.getY() - circle.getRadius());
        canvas.drawArc(rectLeft, 90, 180, true, mPaint);
        canvas.drawArc(rectRight, 270, 180, true, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        childCount = getChildCount();
        circleList = new ArrayList<>();
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

        invalidate();


    }

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

}
