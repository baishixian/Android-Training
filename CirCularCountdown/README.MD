#android 圆圈进度条，中间有倒计时数字
//构造器
CircularProgressBar mCircularProgressBar = new CircularProgressBar(this);

//设置倒计时长
int Duration = 10;
mCircularProgressBar.setDuration(mDuration);

//管理方法
mCircularProgressBar.show();
mCircularProgressBar.cancle();
mCircularProgressBar.dismiss();
//需要api > 19
mCircularProgressBar.pause();
mCircularProgressBar.resume();

//基本属性设置
setTextSize（int size）;
setTextColor(int color);
setTextDisplay(boolean display);//设置是否显示文本

setProgressColor(int color)
setThumbEnabled(boolean display) //转动处标记是否打开
setProgressBackgroundColor(int color)
setMarkerEnabled(boolean display) //进度条标记是否打开
… …