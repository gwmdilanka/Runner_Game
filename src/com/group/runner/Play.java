package com.group.runner;

import com.group.runner.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



public class Play extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
		
		//Back to the main menu
		Button backBtn = (Button)findViewById(R.id.back_btn);
		backBtn.setOnClickListener(new OnClickListener() {
	    	
	    	public void onClick(View v) {
	    		Intent BackToMenu = new Intent(Play.this, MainActivity.class);
	            startActivity(BackToMenu);        		
	    		finish();
	    	}
		});
		
		//Go to game view
		Button startBtn = (Button)findViewById(R.id.start_btn);
		startBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//Get the player name
			    EditText name = (EditText) findViewById(R.id.player_txtfield);
			    
			    Intent GoToGame = new Intent(Play.this, Game.class);
			    GoToGame.putExtra("playerName",name.getText().toString());
			    startActivity(GoToGame);        		
			    finish();
			}
		});				
	}
}
