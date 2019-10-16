package com.bsx.jetpacksample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.bsx.jetpacksample.ui.UserFragment;

/**
 * MainActivity
 *
 * @author baishixian
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, UserFragment.newInstance())
                    .commitNow();
        }
    }
}
