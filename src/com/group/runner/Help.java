package com.group.runner;

import com.group.runner.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Help extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		//Back to the main menu
		Button backBtn = (Button)findViewById(R.id.back_btn);
		backBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent BackToMenu = new Intent(Help.this, MainActivity.class);
				startActivity(BackToMenu);   
				finish();
			}
		});
	}
}