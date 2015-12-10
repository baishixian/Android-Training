package com.sunteng.roundprogress;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    private RoundProgressBar roundProgressBar;
    private int time;
    private TimerTask timerTask;
    private Timer timer = new Timer();
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        roundProgressBar = (RoundProgressBar) findViewById(R.id.rpd);
        time = 10;
        roundProgressBar.setMax(time);
        roundProgressBar.setProgress(time);

        button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roundProgressBar.show();
                button.setClickable(false);
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roundProgressBar.stop();
                button.setClickable(true);
            }
        });
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setClickable(true);
                roundProgressBar.stop();
                time = 10;
                roundProgressBar.setMax(time);
                roundProgressBar.setProgress(time);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        roundProgressBar.stop();
    }
}
