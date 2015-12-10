package com.test.example.crashhandler;

import android.app.Activity;
import android.os.Bundle;

import com.taobao.statistic.Arg;
import com.taobao.statistic.TBS;

public class Case1 extends Activity implements
		TBS.CrashHandler.OnCrashCaughtListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * 注意：请记得配置，在有的机器上，SDK获取不到当前工程的service配置情况，务必请配置以下的内容 <service
		 * android:name="com.taobao.statistic.module.data.Yolanda"
		 * android:process=":utremote" > </service>
		 * 
		 * 说明：初始化 代码放置在哪里都没关系，不过原则上，必须在用之前需要初始化好
		 * setEnvironment这个接口必须在Main线程里调用，而且这个接口必须是第一个被调用
		 */
		TBS.setEnvironment(this.getApplicationContext());
		TBS.setKey("12518383", "b80b55670b292776478c2713c5edd0b6");
		// 如果您需要对Crash之后做自定义的结果收集，或收到通知，那么
		TBS.CrashHandler.setOnCrashCaughtListener(this);
		/**
		 * turnDebug在Release的时候必须关闭掉，因此好的实践是 if(config.isDebug()){
		 * TBS.turnDebug(); }
		 */
		TBS.turnDebug();
		TBS.init();
		// 强制触发Crash
		// 因为Usertrack是异步初始化的
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int i = 5 / 0;
	}

	@Override
	protected void onDestroy() {
		TBS.uninit();
		super.onDestroy();
	}

	@Override
	public Arg OnCrashCaught(Thread arg0, Throwable arg1, Arg arg2) {
		/*
		 * 第一种情况：我们仅仅收到通知，在这里您可以新建一个友好的提示框，让用户反馈
		 */
		// Log.e("Case1","出错了");
		/**
		 * 第二种情况：我们可以修改收集日志的信息 这里我们可以对OOM情况下，对内存等信息的抓取
		 * 这里arg0中每个变量的值仅为null或字符串（String）
		 */
		// if (arg0 != null) {
		// /**
		// * obj1对应记录的arg1
		// */
		// arg0.setObj1("这个是我自定义的内容");
		// return arg0;
		// }
		/**
		 * 第三种情况:您可以自己定义一个Arg返回
		 */
		 Arg arg = new Arg("内容1", "内容2", "内容3", "alvin=ok");
		 return arg;
		//return null;
	}

}
