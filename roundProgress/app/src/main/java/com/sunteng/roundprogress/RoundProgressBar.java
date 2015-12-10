package com.sunteng.roundprogress;

import android.content.Context;
import android.content.res.TypedArray;
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
public class RoundProgressBar extends View {
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
    public static final int FRAME  = 100;
    private static int FrameTime = FRAME;

    /**
     * 倒数定时器
     */
    private TimerTask timerTask;
    private Timer timer;

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint = new Paint();

        //获取自定义属性数组
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs , R.styleable.RoundProgressBar);

        //设置自定义属性和默认值
        roundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.GRAY);
        roundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor, Color.WHITE);
        textColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressTextColor, Color.GRAY);
        textSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundProgressTextSize, 20);
        roundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 8);
        max = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 30);
        textIsDisplayable = mTypedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable, true);
        style = mTypedArray.getInt(R.styleable.RoundProgressBar_style, 0);

        //回收释放
        mTypedArray.recycle();

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
        paint.setStrokeWidth(0);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
        int percent = progress ;  //中间的进度
        float textWidth = paint.measureText(percent / FRAME  + "");   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间

        if(textIsDisplayable && percent != 0 && style == STROKE){
            canvas.drawText(percent / FRAME  + "", centre - textWidth / 2, centre + textSize/2, paint); //画出进度
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
                    canvas.drawArc(oval, 0, 360 * progress / max, false, paint);  //根据进度画圆弧
                }
                break;
            }
            case FILL:{
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                if(progress !=0 && max > 0)
                    canvas.drawArc(oval, 0, 360 * progress / max, true, paint);  //根据进度画圆弧
                break;
            }
        }
    }

    /**
     * 设置进度的最大值
     * @param m
     */
    public synchronized void setMax(int m) {
        if(max < 0){
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = m * FRAME;
        FrameTime = max;
    }

    /**
     * 获取进度的最大值
     */
    public synchronized int getMax() {
        return max;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     * @param p
     */
    public synchronized void setProgress(int p) {

        if(progress < 0){
            throw new IllegalArgumentException("progress not less than 0");
        }

        progress = p * FRAME;

        if(progress > max){
            progress = max;
        }
        if(progress <= max){
            this.progress = p ;
            postInvalidate();
        }

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
     * @param p
     */
    private synchronized void refreshProgress(int p) {

        if(progress < 0){
            throw new IllegalArgumentException("progress not less than 0");
        }

        if(progress > max){
            progress = max;
        }
        if(progress <= max){
            this.progress = p ;
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
     * 开始到时计时
     */
    public void show() {

        if (timer == null) {
            timer = new Timer();
        }

        if (timerTask == null) {
            timerTask = new MyTimerTask();
        }

        if(timer != null && timerTask != null ){
             timer.schedule(timerTask, 0L, 1000L / FRAME);
        }

    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            RoundProgressBar.this.refreshProgress(FrameTime--);
            if (FrameTime == -1) {
                //倒计时为0的时候做自己需要处理的业务逻辑
                cancelRestTimerTask();
            }
        }
    }


    /**
     * 暂停倒数计时
     */
    public void stop() {
        cancelRestTimerTask();
    }


    /**
     * 取消RestTimerTask
     */
    private void cancelRestTimerTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }
}