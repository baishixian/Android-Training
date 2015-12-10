package com.test.example.crashhandler;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.taobao.statistic.TBS;

public class Case3 extends Activity implements
		TBS.CrashHandler.OnDaemonThreadCrashCaughtListener {
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
		// 设置在Daemon线程Crash的时候，程序不停止
		// 会在Logcat中输出
		/**
		 * E/Thread: Uncaugh Exception(1474): TBSEngine:Usertrack has been
		 * captured, be sure to pay attention.
		 */
		TBS.CrashHandler.setContinueWhenDaemonThreadUncaughException();
		/**
		 * I/Daemon线程的Crash我已经收到通知(1512): 接下来可以尝试恢复生命
		 */
		TBS.CrashHandler.setOnDaemonCrashCaughtListener(this);
		 //TBS.CrashHandler.removeDaemonCrashCaughtListener("TestThread");
		 
		/**
		 * turnDebug在Release的时候必须关闭掉，因此好的实践是 if(config.isDebug()){
		 * TBS.turnDebug(); }
		 */
		TBS.turnDebug();
		TBS.init();
		TBS.CrashHandler.setOnDaemonCrashCaughtListener("TestThread", this);
		// 我们这里演示Daemon线程的Crash情况
		Thread t = new TestThread();
		t.setName("TestThread");
		t.setDaemon(true);
		t.start();
	}

	@Override
	protected void onDestroy() {
		TBS.uninit();
		super.onDestroy();
	}

	public class TestThread extends Thread {
		public void run() {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int i = 5 / 0;
		}
	}

	@Override
	public void OnDaemonThreadCrashCaught(Thread arg0) {
		if (arg0.getName().equals("TestThread")) {
			Log.i("Daemon线程的Crash我已经收到通知", "接下来可以尝试恢复生命");
		}
	}
}
