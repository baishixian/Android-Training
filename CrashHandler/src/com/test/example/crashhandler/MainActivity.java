package com.test.example.crashhandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Intent intent = new Intent(this, Case1.class);
		// this.startActivity(intent);
		// Intent intent = new Intent(this, Case2.class);
		// this.startActivity(intent);
		Intent intent = new Intent(this, Case1.class);
		this.startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
