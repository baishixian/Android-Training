package android.bai.crashhandler;

import android.app.Application;

/**
 * Created by Administrator on 2015/12/3.
 */
public class CrashAppcation extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }


}
