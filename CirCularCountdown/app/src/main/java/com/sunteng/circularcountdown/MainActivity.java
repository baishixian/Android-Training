package com.sunteng.circularcountdown;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private CircularProgressBar mCircularProgressBar;
    private int mDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCircularProgressBar = new CircularProgressBar(this);
        ((LinearLayout)findViewById(R.id.main)).addView(mCircularProgressBar);

        mDuration = 15;
        mCircularProgressBar.setDuration(mDuration);
        findViewById(R.id.bt_start).setOnClickListener(this);
        findViewById(R.id.bt_stop).setOnClickListener(this);
        findViewById(R.id.bt_resume).setOnClickListener(this);
        findViewById(R.id.bt_cancle).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_start:
                mCircularProgressBar.show();
                break;
           case R.id.bt_stop:
                mCircularProgressBar.pause();
                break;
            case R.id.bt_resume:
                mCircularProgressBar.resume();
                break;
            case R.id.bt_cancle:
                mCircularProgressBar.show(8);
                break;

        }
    }

}
