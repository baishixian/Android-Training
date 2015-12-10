package android.bai.crashhandler;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                int i = 10 / 0;
                break;
            case R.id.button2:
                try {
                    int j = 10 / 0;
                } catch (ArithmeticException e) {
                    Toast.makeText(this.getApplicationContext(),"出现算数异常",Toast.LENGTH_SHORT).show();
                    Log.e("error: ---->>", e.getMessage());
                }
                break;
            case R.id.button3:
                CrashHandler.getInstance().setSystemDefaulHandler();
                int k = 10 / 0;
                break;
            default: break;
        }
    }
}
