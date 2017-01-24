package com.sunteng.roundprogress;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.LinearInterpolator;



/**
 * Created by baishixian on 15-12-17
 * baishixian@sunteng.com
 */
public class CircularProgressBar extends View {

    private static final int CIRCULAR_ROTATE_DEGREE = 360;

    private final RectF mCircleBounds = new RectF();

    private Paint mBackgroundColorPaint = new Paint();

    private int mCircleStrokeWidth = 10;

    private int mGravity = Gravity.CENTER;

    private int mHorizontalInset = 0;

    private boolean mIsInitializing = true;

    private Paint mMarkerColorPaint;

    private float mMarkerProgress = 0.0f;

    /**
     * the overdraw is true if the progress is over 1.0.
     */
    private boolean mOverrdraw = false;

    /**
     * The current progress.
     */
    private float mProgress = 0.0f;

    private int mProgressBackgroundColor;

    private int mProgressColor;

    private Paint mProgressColorPaint;

    private float mRadius;

    private Paint mThumbColorPaint = new Paint();

    private float mThumbPosX;

    private float mThumbPosY;

    private int mThumbRadius = 20;

    private float mTranslationOffsetX;

    private float mTranslationOffsetY;

    private int mVerticalInset = 0;

    private boolean mIsDisplayText = true;

    private Paint mTextPaint = new Paint();

    private int mDuration = 10;

    private ObjectAnimator mProgressBarAnimator;

    private int mTextColor = Color.WHITE;

    private int mTextSize = 15;

    /**
     * 进度条是否已开始状态
     */
    private boolean mStartedState = false;

    /**
     *保存恢复进度时旧的进度值  0.0 - 1.0
     */
    float mOldProgress = 0.0f;

    public CircularProgressBar(final Context context) {
        this(context, null);
    }

    public CircularProgressBar(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularProgressBar(final Context context, final AttributeSet attrs,
                               final int defStyle) {
        super(context, attrs, defStyle);
        setProgressColor(Color.WHITE);
        setProgressBackgroundColor(Color.TRANSPARENT);
        setProgress(0.0f);
        setMarkerProgress(0.0f);
        setWheelSize(8);
        setTextSize(10);
        mGravity = Gravity.CENTER;
        mThumbRadius = mCircleStrokeWidth * 2;

        updateBackgroundColor();

        updateMarkerColor();

        updateProgressColor();

        // the view has now all properties and can be drawn
        mIsInitializing = false;

    }


    @Override
    protected void onDraw(final Canvas canvas) {

        canvas.translate(mTranslationOffsetX, mTranslationOffsetY);
        final float progressRotation = getCurrentRotation();

        if (!mOverrdraw) {
            canvas.drawArc(mCircleBounds, CIRCULAR_ROTATE_DEGREE / 4 * 3, -(CIRCULAR_ROTATE_DEGREE - progressRotation), false,
                    mBackgroundColorPaint);
        }

        // draw the progress or a full circle if overdraw is true
        canvas.drawArc(mCircleBounds, CIRCULAR_ROTATE_DEGREE / 4 * 3, mOverrdraw ? CIRCULAR_ROTATE_DEGREE : progressRotation, false,
                mProgressColorPaint);

        if(isTextDisplay()){
            canvas.save();
            canvas.drawCircle(0, 0, mThumbPosX  - mThumbRadius/2, mThumbColorPaint);
            //画中间字体进度变化
            int percent = (int) ((1.0 - mProgress) * mDuration);  //中间的进度
            float textWidth = mTextPaint.measureText(percent + 1 + "");   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间
            //设置中间字体显示内容
            if(mProgress == 0.0 || mProgress == 1.0){
                canvas.drawText (percent   + "", 0 - textWidth /2, 0 + mTextSize / 2, mTextPaint) ; //画出进度
            }else{
                canvas.drawText (percent + 1 + "", 0 - textWidth /2, 0 + mTextSize / 2, mTextPaint) ; //画出进度
            }
            canvas.restore();
        }

    }

    private boolean isTextDisplay() {
        return mIsDisplayText;
    }

    public void setTextDisplay(boolean display) {
        mIsDisplayText = display;
    }


    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int height = getDefaultSize(
                getSuggestedMinimumHeight() + getPaddingTop() + getPaddingBottom(),
                heightMeasureSpec);
        final int width = getDefaultSize(
                getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight(),
                widthMeasureSpec);

        final int diameter;
        if (heightMeasureSpec == MeasureSpec.UNSPECIFIED) {
            // ScrollView
            diameter = width;
            computeInsets(0, 0);
        } else if (widthMeasureSpec == MeasureSpec.UNSPECIFIED) {
            // HorizontalScrollView
            diameter = height;
            computeInsets(0, 0);
        } else {
            diameter = Math.min(width, height);
            computeInsets(width - diameter, height - diameter);
        }

        setMeasuredDimension(diameter, diameter);

        final float halfWidth = diameter * 0.5f;

        final float drawedWith = mCircleStrokeWidth / 2f;

        mRadius = halfWidth - drawedWith - 0.5f;

        mCircleBounds.set(-mRadius, -mRadius, mRadius, mRadius);

        mThumbPosX = (float) (mRadius * Math.cos(0));
        mThumbPosY = (float) (mRadius * Math.sin(0));

        mTranslationOffsetX = halfWidth + mHorizontalInset;
        mTranslationOffsetY = halfWidth + mVerticalInset;

    }

    public float getMarkerProgress() {
        return mMarkerProgress;
    }

    public float getProgress() {
        return mProgress;
    }

    public void setMarkerProgress(final float progress) {
        mMarkerProgress = progress;
    }

    public void setProgress(final float progress) {
        if (progress == mProgress) {
            return;
        }
        if (progress == 1) {
            mOverrdraw = false;
            mProgress = 1;
        } else {
            if (progress >= 1) {
                mOverrdraw = true;
            } else {
                mOverrdraw = false;
            }
            mProgress = progress % 1.0f;
        }
        if (!mIsInitializing) {
            invalidate();
        }
    }

    private void setTextColor(int color) {
        mTextColor = color;
    }

    public void setProgressBackgroundColor(final int color) {
        mProgressBackgroundColor = color;

        updateMarkerColor();
        updateBackgroundColor();
    }

    public void setProgressColor(final int color) {
        mProgressColor = color;

        updateProgressColor();
    }

    public void setWheelSize(final int dimension) {
        mCircleStrokeWidth = dimension;

        // update the paints
        updateBackgroundColor();
        updateMarkerColor();
        updateProgressColor();
    }


    private void computeInsets(final int dx, final int dy) {
        int absoluteGravity = mGravity;

        switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.LEFT:
                mHorizontalInset = 0;
                break;
            case Gravity.RIGHT:
                mHorizontalInset = dx;
                break;
            case Gravity.CENTER_HORIZONTAL:
            default:
                mHorizontalInset = dx / 2;
                break;
        }
        switch (absoluteGravity & Gravity.VERTICAL_GRAVITY_MASK) {
            case Gravity.TOP:
                mVerticalInset = 0;
                break;
            case Gravity.BOTTOM:
                mVerticalInset = dy;
                break;
            case Gravity.CENTER_VERTICAL:
            default:
                mVerticalInset = dy / 2;
                break;
        }
    }

    private float getCurrentRotation() {
        return CIRCULAR_ROTATE_DEGREE * mProgress;
    }

    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    private void updateBackgroundColor() {
        mBackgroundColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundColorPaint.setColor(mProgressBackgroundColor);
        mBackgroundColorPaint.setStyle(Paint.Style.STROKE);
        mBackgroundColorPaint.setStrokeWidth(mCircleStrokeWidth);
        invalidate();
    }


    private void updateMarkerColor() {
        mMarkerColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMarkerColorPaint.setColor(mProgressBackgroundColor);
        mMarkerColorPaint.setStyle(Paint.Style.STROKE);
        mMarkerColorPaint.setStrokeWidth(mCircleStrokeWidth);
        invalidate();
    }


    private void updateProgressColor() {
        mProgressColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressColorPaint.setColor(mProgressColor);
        mProgressColorPaint.setStyle(Paint.Style.STROKE);
        mProgressColorPaint.setStrokeWidth(mCircleStrokeWidth);

        mThumbColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mThumbColorPaint.setColor(mProgressBackgroundColor);
        mThumbColorPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mThumbColorPaint.setStrokeWidth(mCircleStrokeWidth);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setStrokeWidth(0);
        mThumbColorPaint.setColor(mProgressBackgroundColor);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
        invalidate();
    }

    /**
     *显示旋转进度条
     * @param duration 旋转总时长
     */
    @Deprecated
    public void show(int duration) {

        if (duration < 0){
            duration = 0;
        }

        if (!isStarted()){
            if(this.getVisibility() == View.INVISIBLE ){
                this.setVisibility(VISIBLE);
            }
            setDuration(duration);
            mStartedState = true;
            this.setMarkerProgress(1.0f);
            this.setTextDisplay(true);
            this.setProgress(0.0f);
            animate(this, null, 1.0f, mDuration * 1000);
        }
    }

    /**
     *显示旋转进度条
     * @param lefttime  旋转剩余时间
     * @param duration  旋转总时长
     */
    public void show(int lefttime,int duration) {

        if (lefttime > duration){
            lefttime = duration;
        }
        cancel();
        setDuration(duration / 1000);
        if(lefttime > 0){
            mStartedState = true;
            mOldProgress = (duration - lefttime)*1.0f / (duration);
            this.setMarkerProgress(1.0f);
            this.setTextDisplay(true);
            this.setProgress(mOldProgress);
            animate(this, null, 1.0f, lefttime);
        }
        if(this.getVisibility() == View.INVISIBLE ){
            this.setVisibility(VISIBLE);
        }
    }

    private void animate(final CircularProgressBar progressBar, final Animator.AnimatorListener listener,
                         final float progress, final int duration) {
        mProgressBarAnimator = ObjectAnimator.ofFloat(progressBar, "progress", progress);
        /**
         * 设置补间动画的速度为匀速
         */
        mProgressBarAnimator.setInterpolator(new LinearInterpolator());
        mProgressBarAnimator.setDuration(duration);
        mProgressBarAnimator.addListener(mAnimatorListener);

        if (listener != null) {
            mProgressBarAnimator.addListener(listener);
        }

        mProgressBarAnimator.reverse();
        progressBar.setMarkerProgress(progress);
        mProgressBarAnimator.start();
    }

    Animator.AnimatorListener mAnimatorListener =  new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            animation.isRunning();
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (mProgressBarAnimator != null) {
                mProgressBarAnimator.removeAllListeners();
            }
            mStartedState = false;
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            mStartedState = false;
            if (mProgressBarAnimator != null) {
                mProgressBarAnimator.removeAllListeners();
            }
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            mStartedState = false;
        }
    };

    public void cancel() {
        if (mProgressBarAnimator != null) {
            mStartedState = false;
            mProgressBarAnimator.cancel();
            mProgressBarAnimator.removeAllListeners();
        }
    }



    public boolean isStarted(){
        return mStartedState;
    }
}
