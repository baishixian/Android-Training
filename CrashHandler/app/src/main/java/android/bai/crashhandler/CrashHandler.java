package android.bai.crashhandler;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/3.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";

    //系统默认的UncaughtExceptionHandler处理类
    private Thread.UncaughtExceptionHandler mDefaulHandler;

    //存储信息异常信息收集的Map
    private HashMap<String, String> infos = new HashMap<String , String>();

    //上下文
    private Context mContext;

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm;ss");

    private CrashHandler(){

    }

    private static  CrashHandler INSTANCE = new CrashHandler();

    public static CrashHandler getInstance(){
        return INSTANCE;
    }

    /**
     * 初始化
     */
    public void init(Context context){
        mContext = context;
        //获取系统默认的UncaughtException
        mDefaulHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置程序的UncaughtException为CrashHandler
        Thread.setDefaultUncaughtExceptionHandler(this);
    }



    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if(!handleException(ex) && mDefaulHandler != null){
            //如果用户没有处理，则由系统默认的来处理
            mDefaulHandler.uncaughtException(thread,ex);
        }else{
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e(TAG,"error: " , e);
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    public void setSystemDefaulHandler(){
       Thread.setDefaultUncaughtExceptionHandler(mDefaulHandler);
    }

    private boolean handleException(Throwable ex) {
        if(ex == null){
            return false;
        }

      new Thread(){
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext , "程序出现异常，即将退出" , Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
        conllectDeviceInfo(mContext);
        saveCrashInfo2File(ex);
        return true;
    }

    private void conllectDeviceInfo(Context ct) {
        try {
            PackageManager packageManager = ct.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(ct.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                String versionName = packageInfo.versionName == null ? "null" : packageInfo.versionName;
                String versionCode = packageInfo.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "EORRO: ", e);
        }
        //通过反射获取用户硬件信息
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            //暴力反射，使其可达
            field.setAccessible(true);
            try {
                infos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                Log.e(TAG, "ERROR; ", e);
            }
        }
    }

        /**
         * 保存错误信息到文件中
         *
         * @param ex
         * @return 返回文件名称,便于将文件传送到服务器
         */
        private String saveCrashInfo2File(Throwable ex) {

            StringBuffer sb = new StringBuffer();
            for (Map.Entry<String, String> entry : infos.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                sb.append(key + "=" + value + "\n");
            }

            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            printWriter.close();
            String result = writer.toString();
            sb.append(result);
            try {
                long timestamp = System.currentTimeMillis();
                String time = dateFormat.format(new Date());
                String fileName = "crash-" + time + "-" + timestamp + ".log";
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    String path = "/sdcard/crash/";
                    File dir = new File(path);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    FileOutputStream fos = new FileOutputStream(path + fileName);
                    fos.write(sb.toString().getBytes());
                    fos.close();
                }
                return fileName;
            } catch (Exception e) {
                Log.e(TAG, "an error occured while writing file...", e);
            }
            return null;
        }
}
