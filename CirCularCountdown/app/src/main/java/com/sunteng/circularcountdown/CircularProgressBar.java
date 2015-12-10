package com.sunteng.circularcountdown;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;


/**
 * CircularProgressBar custom view.
 */
public class CircularProgressBar extends View {

    private static final String INSTANCE_STATE_SAVEDSTATE = "saved_state";

    private static final String INSTANCE_STATE_PROGRESS = "progress";

    private static final String INSTANCE_STATE_MARKER_PROGRESS = "marker_progress";

    private static final String INSTANCE_STATE_PROGRESS_BACKGROUND_COLOR = "progress_background_color";

    private static final String INSTANCE_STATE_PROGRESS_COLOR = "progress_color";

    private static final String INSTANCE_STATE_THUMB_VISIBLE = "thumb_visible";

    private static final String INSTANCE_STATE_MARKER_VISIBLE = "marker_visible";

    private static final int CIRCULAR_ROTATE_DEGREE = 360;

    /**
     * The rectangle enclosing the circle.
     */
    private final RectF mCircleBounds = new RectF();

    /**
     * the rect for the thumb square
     */
    private final RectF mSquareRect = new RectF();

    /**
     * the mTextPaint for the background.
     */
    private Paint mBackgroundColorPaint = new Paint();

    private int mCircleStrokeWidth = 10;

    private int mGravity = Gravity.CENTER;

    private int mHorizontalInset = 0;

    private boolean mIsInitializing = true;

    /**
     * flag if the marker should be visible
     */
    private boolean mIsMarkerEnabled = false;

    /**
     * indicates if the thumb is visible
     */
    private boolean mIsThumbEnabled = true;

    /**
     * The Marker color mTextPaint.
     */
    private Paint mMarkerColorPaint;

    /**
     * The Marker progress.
     */
    private float mMarkerProgress = 0.0f;

    /**
     * the overdraw is true if the progress is over 1.0.
     */
    private boolean mOverrdraw = false;

    /**
     * The current progress.
     */
    private float mProgress = 0.0f;

    /**
     * The color of the progress background.
     */
    private int mProgressBackgroundColor;

    /**
     * the color of the progress.
     */
    private int mProgressColor;

    /**
     * mTextPaint for the progress.
     */
    private Paint mProgressColorPaint;

    /**
     * Radius of the circle
     */
    private float mRadius;

    /**
     * The Thumb color mTextPaint.
     */
    private Paint mThumbColorPaint = new Paint();

    /**
     * The Thumb pos x.
     */
    private float mThumbPosX;

    /**
     * The Thumb pos y.
     */
    private float mThumbPosY;

    /**
     * The pointer width (in pixels).
     */
    private int mThumbRadius = 20;

    /**
     * The Translation offset x which gives us the ability to use our own coordinates system.
     */
    private float mTranslationOffsetX;

    /**
     * The Translation offset y which gives us the ability to use our own coordinates system.
     */
    private float mTranslationOffsetY;

    private int mVerticalInset = 0;

    private boolean mIsDisplayText = true;

    private Paint mTextPaint = new Paint();

    private int mDuration = 10;

    private ObjectAnimator mProgressBarAnimator;

    private int mTextColor = Color.WHITE;

    private int mTextSize = 15;

    /**
     * Instantiates a new holo circular progress bar.
     *
     * @param context the context
     */
    public CircularProgressBar(final Context context) {
        this(context, null);
    }

    /**
     * Instantiates a new holo circular progress bar.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public CircularProgressBar(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Instantiates a new holo circular progress bar.
     *
     * @param context  the context
     * @param attrs    the attrs
     * @param defStyle the def style
     */
    public CircularProgressBar(final Context context, final AttributeSet attrs,
                               final int defStyle) {
        super(context, attrs, defStyle);
        setProgressColor(Color.WHITE);
        setProgressBackgroundColor(Color.TRANSPARENT);
        setProgress(0.0f);
        setMarkerProgress(0.0f);
        setWheelSize(10);
        setTextSize(30);
        setThumbEnabled(true);
        setMarkerEnabled(true);
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
        canvas.drawArc(mCircleBounds,CIRCULAR_ROTATE_DEGREE / 4 * 3, mOverrdraw ? CIRCULAR_ROTATE_DEGREE : progressRotation, false,
                mProgressColorPaint);

        // draw the marker at the correct rotated position
        if (mIsMarkerEnabled) {
            final float markerRotation = getMarkerRotation();

            canvas.save();
            canvas.rotate(markerRotation - CIRCULAR_ROTATE_DEGREE / 4);
            canvas.drawLine((float) (mThumbPosX + mThumbRadius ), mThumbPosY,
                    (float) (mThumbPosX - mThumbRadius ), mThumbPosY, mMarkerColorPaint);
            canvas.restore();
        }

        if (isThumbEnabled()) {
            // draw the thumb square at the correct rotated position
            canvas.save();
            canvas.rotate(progressRotation - CIRCULAR_ROTATE_DEGREE / 4);
            // rotate the square by 45 degrees
            canvas.rotate(CIRCULAR_ROTATE_DEGREE / 8, mThumbPosX, mThumbPosY);
            mSquareRect.left = mThumbPosX - mThumbRadius / 3;
            mSquareRect.right = mThumbPosX + mThumbRadius / 3;
            mSquareRect.top = mThumbPosY - mThumbRadius / 3;
            mSquareRect.bottom = mThumbPosY + mThumbRadius / 3;
            canvas.drawRect(mSquareRect, mThumbColorPaint);
            canvas.restore();
        }

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

    /**
     * set display text model
     * @param display
     */
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
            // Default
            diameter = Math.min(width, height);
            computeInsets(width - diameter, height - diameter);
        }

        setMeasuredDimension(diameter, diameter);

        final float halfWidth = diameter * 0.5f;

        // width of the drawed circle (+ the drawedThumb)
        final float drawedWith;
        if (isThumbEnabled()) {
            drawedWith = mThumbRadius ;
        } else if (isMarkerEnabled()) {
            drawedWith = mCircleStrokeWidth;
        } else {
            drawedWith = mCircleStrokeWidth / 2f;
        }

        // -0.5f for pixel perfect fit inside the viewbounds
        mRadius = halfWidth - drawedWith - 0.5f;

        mCircleBounds.set(-mRadius, -mRadius, mRadius, mRadius);

        mThumbPosX = (float) (mRadius * Math.cos(0));
        mThumbPosY = (float) (mRadius * Math.sin(0));

        mTranslationOffsetX = halfWidth + mHorizontalInset;
        mTranslationOffsetY = halfWidth + mVerticalInset;

    }

    @Override
    protected void onRestoreInstanceState(final Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            setProgress(bundle.getFloat(INSTANCE_STATE_PROGRESS));
            setMarkerProgress(bundle.getFloat(INSTANCE_STATE_MARKER_PROGRESS));

            final int progressColor = bundle.getInt(INSTANCE_STATE_PROGRESS_COLOR);
            if (progressColor != mProgressColor) {
                mProgressColor = progressColor;
                updateProgressColor();
            }

            final int progressBackgroundColor = bundle
                    .getInt(INSTANCE_STATE_PROGRESS_BACKGROUND_COLOR);
            if (progressBackgroundColor != mProgressBackgroundColor) {
                mProgressBackgroundColor = progressBackgroundColor;
                updateBackgroundColor();
            }

            mIsThumbEnabled = bundle.getBoolean(INSTANCE_STATE_THUMB_VISIBLE);

            mIsMarkerEnabled = bundle.getBoolean(INSTANCE_STATE_MARKER_VISIBLE);

            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE_SAVEDSTATE));
            return;
        }

        super.onRestoreInstanceState(state);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE_SAVEDSTATE, super.onSaveInstanceState());
        bundle.putFloat(INSTANCE_STATE_PROGRESS, mProgress);
        bundle.putFloat(INSTANCE_STATE_MARKER_PROGRESS, mMarkerProgress);
        bundle.putInt(INSTANCE_STATE_PROGRESS_COLOR, mProgressColor);
        bundle.putInt(INSTANCE_STATE_PROGRESS_BACKGROUND_COLOR, mProgressBackgroundColor);
        bundle.putBoolean(INSTANCE_STATE_THUMB_VISIBLE, mIsThumbEnabled);
        bundle.putBoolean(INSTANCE_STATE_MARKER_VISIBLE, mIsMarkerEnabled);
        return bundle;
    }

    public int getCircleStrokeWidth() {
        return mCircleStrokeWidth;
    }

    public float getMarkerProgress() {
        return mMarkerProgress;
    }

    public float getProgress() {
        return mProgress;
    }

    public int getProgressColor() {
        return mProgressColor;
    }

    /**
     * @return true if the marker is visible
     */
    public boolean isMarkerEnabled() {
        return mIsMarkerEnabled;
    }

    /**
     * @return true if the marker is visible
     */
    public boolean isThumbEnabled() {
        return mIsThumbEnabled;
    }

    /**
     * Sets the marker enabled.
     */
    public void setMarkerEnabled(final boolean enabled) {
        mIsMarkerEnabled = enabled;
    }

    /**
     * Sets the marker progress.
     */
    public void setMarkerProgress(final float progress) {
        mIsMarkerEnabled = true;
        mMarkerProgress = progress;
    }

    /**
     * Sets the progress.
     */
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

    public void setThumbEnabled(final boolean enabled) {
        mIsThumbEnabled = enabled;
    }


    public void setWheelSize(final int dimension) {
        mCircleStrokeWidth = dimension;

        // update the paints
        updateBackgroundColor();
        updateMarkerColor();
        updateProgressColor();
    }


    @SuppressLint("NewApi")
    private void computeInsets(final int dx, final int dy) {
        int absoluteGravity = mGravity;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            absoluteGravity = Gravity.getAbsoluteGravity(mGravity, getLayoutDirection());
        }

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

    private float getMarkerRotation() {
        return CIRCULAR_ROTATE_DEGREE * mMarkerProgress;
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

    public void show() {
        if(this.getVisibility() == View.INVISIBLE ){
            this.setVisibility(VISIBLE);
        }
        this.setMarkerProgress(1.0f);
        this.setTextDisplay(true);
        this.setProgress(0.0f);
        animate(this, null, 1.0f, mDuration * 1000);
    }
    public void show(int duration) {
        float p = (duration * 1.0f / this.mDuration);
        this.setDuration(duration);
        this.setMarkerProgress(0.5f);
        this.setTextDisplay(true);
        this.setProgress(0.0f);
        animate(this, null,1.0f, mDuration * 1000);
    }

    public void dismiss() {
        this.cancle();
        try {
            this.setVisibility(INVISIBLE);
        }catch (Exception e){
            Log.e("CircularProgressBar" , e.getMessage());
        }
    }

    private void animate(final CircularProgressBar progressBar, final Animator.AnimatorListener listener,
                         final float progress, final int duration) {
            mProgressBarAnimator = ObjectAnimator.ofFloat(progressBar, "progress", progress);
            mProgressBarAnimator.setDuration(duration);
            if (listener != null) {
                mProgressBarAnimator.addListener(listener);
            }
            mProgressBarAnimator.reverse();
            progressBar.setMarkerProgress(progress);
            mProgressBarAnimator.start();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void pause() {
        if (mProgressBarAnimator != null) {
            mProgressBarAnimator.pause();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void resume() {
        if (mProgressBarAnimator != null) {
            mProgressBarAnimator.resume();
        }
    }

    public void cancle() {
        if (mProgressBarAnimator != null) {
            mProgressBarAnimator.cancel();
            mProgressBarAnimator.removeAllListeners();
            mProgressBarAnimator.end();
        }
    }
}
