package com.sunteng.roundprogress;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 倒计时圆圈进度条
 * Created by baishixian on 2015/12/8.
 */
public class RoundProgressBar2 extends View {
    /**
     * 画笔
     */
    private Paint paint;
    /**
     * 圆环颜色
     */
    private int roundColor;
    /**
     * 圆环进度颜色
     */
    private int roundProgressColor;
    /**
     * 圆环中间字体颜色
     */
    private int textColor;
    /**
     * 圆环中间字体大小
     */
    private float textSize;
    /**
     * 圆环宽度
     */
    private float roundWidth;
    /**
     * 进度最大值
     */
    private int max;
    /**
     * 当前进度
     */
    private int progress;
    /**
     * 是否显示中间文字
     */
    private boolean textIsDisplayable;
    /**
     * 圆环进度条样式
     */
    private int style;
    public static final int STROKE = 0;
    public static final int FILL = 1;

    /**
     * 倒数定时器
     */
    private TimerTask timerTask;
    private Timer timer;
    private boolean mStartedState = false;

    public RoundProgressBar2(Context context) {
        this(context, null);
    }

    public RoundProgressBar2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint = new Paint();

        //设置自定义属性和默认值
        roundColor = Color.GRAY;
        roundProgressColor = Color.WHITE;
        textColor = Color.WHITE;
        textSize = 20 ;
        roundWidth = 8;
        max = 15;
        textIsDisplayable = true;
        style = 0;


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画最外层的大圆环
        int centre = getWidth()/2; //获取圆心的x坐标
        int radius = (int) (centre - roundWidth/2); //圆环的半径
        paint.setColor(roundColor); //设置圆环的颜色
        paint.setStyle(Paint.Style.STROKE); //设置空心
        paint.setStrokeWidth(roundWidth); //设置圆环的宽度
        paint.setAntiAlias(true);  //消除锯齿
        canvas.drawCircle(centre, centre, radius, paint); //画出圆环


        //画进度百分比
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体

        int percent = max - progress ;  //中间的进度

        int timeText = percent / 1000;

        float textWidth = paint.measureText(timeText + "");   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间

        if(textIsDisplayable && percent != 0 && style == STROKE){
            canvas.drawText(timeText  + "", centre - textWidth / 2, centre + textSize/2, paint); //画出进度
        }

        //画圆弧 ，画圆环的进度
        paint.setStrokeWidth(roundWidth); //设置圆环的宽度
        paint.setColor(roundProgressColor);  //设置进度的颜色
        RectF oval = new RectF(centre - radius, centre - radius, centre
                + radius, centre + radius);  //用于定义的圆弧的形状和大小的界限
        switch (style) {
            case STROKE:{
                paint.setStyle(Paint.Style.STROKE);
                if(max > 0 ){
                    canvas.drawArc(oval, -90, 360 * progress / max, false, paint);  //根据进度画圆弧
                }
                break;
            }
            case FILL:{
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                if(progress !=0 && max > 0)
                    canvas.drawArc(oval, -90, 360 * progress / max, true, paint);  //根据进度画圆弧
                break;
            }
        }
    }

    /**
     * 设置进度的最大值
     * @param progress
     */
    public synchronized void setMaxProgress(int progress) {
        this.max = progress;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     * @param progress
     */
    public synchronized void setProgress(int progress) {
        if(progress > max){
            progress = max;
        }
        this.progress = progress ;
        postInvalidate();

    }

    /**
     * 获取进度.需要同步
     * @return
     */
    public synchronized int getProgress() {
        return progress;
    }


    /**
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     * @param progress
     */
    private synchronized void refreshProgress(int progress) {

        if(progress < 0){
            throw new IllegalArgumentException("progress not less than 0");
        }

        if(progress > max){
            progress = max;
        }
        if(progress <= max){
            this.progress = progress ;
            postInvalidate();
        }

    }

    public void setCricleColor(int cricleColor) {
        this.roundColor = cricleColor;
    }

    public void setCricleProgressColor(int cricleProgressColor) {
        this.roundProgressColor = cricleProgressColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public void setRoundWidth(float roundWidth) {
        this.roundWidth = roundWidth;
    }


    /**
     * 开始倒计时进度条，间隔
     *
     * @param progress 设置计时器进度条当前进度时间 单位s 秒
     * @param maxProgress 设置计时器进度条最大时间 单位s 秒
     */
    public void show(int progress, int maxProgress) {

        if (progress < 0) {
            maxProgress = 0;
        }

        if (progress > maxProgress){
            progress = maxProgress;
        }

        cancel();

        /**
         * 剩余播放进度
         */
        setProgress(progress);
        setMaxProgress(maxProgress);

        if(maxProgress > 0){
            if (timer == null) {
                timer = new Timer();
            }

            if (timerTask == null) {
                timerTask = new MyTimerTask();
            }

            mStartedState = true;

            // 每隔10ms刷新进度条
            timer.schedule(timerTask, 800L, 1000);
        }
        if(this.getVisibility() == View.INVISIBLE ){
            this.setVisibility(VISIBLE);
        }

    }

    /**
     * 暂停倒数计时
     */
    public void cancel() {
        cancelRestTimerTask();
    }

    public boolean isStarted() {
        return mStartedState;
    }


    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {

            progress = progress + 1000;
            refreshProgress(progress);
            if (progress >= max) {
                //倒计时为最大的时候做自己需要处理的业务逻辑
                cancelRestTimerTask();
            }
        }
    }


    /**
     * 取消RestTimerTask
     */
    public void cancelRestTimerTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        mStartedState = false;
    }
}