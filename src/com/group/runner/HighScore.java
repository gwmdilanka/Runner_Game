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
import android.widget.TextView;

public class HighScore extends Activity {
	TextView higherScore,LName,LScore;
	int score,hscore,lscore;
	String lName;
	public static final String HIGHER = "MyPreferencesFile";	

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_highscore);
		
		//Back to the main menu
		Button backBtn = (Button)findViewById(R.id.back_btn);
		backBtn.setOnClickListener(new OnClickListener() {
	    	
	    	public void onClick(View v) {
	    		Intent BackToMenu = new Intent(HighScore.this, MainActivity.class);
	            startActivity(BackToMenu);        		
	    		finish();
	    	}
		});
		
		//Save the higher score to the HigheScore class
		SharedPreferences pref = getApplicationContext().getSharedPreferences(HIGHER, MODE_PRIVATE);
	    Editor editor = pref.edit();
	    
	    //Default values of the variables
	    score=pref.getInt("score", 0);
	    lscore=score;//Get the score and equals to the last score before compare with higher
	    hscore=pref.getInt("hscore", 0);
	    lName = pref.getString("name", "");
	    
	    if(score>hscore)
	    {
	    	editor.putInt("hscore", score);
	    	editor.putString("name",lName);
    	    editor.commit(); 
	    }
	    hscore=pref.getInt("hscore", 0);
	    	    
	    higherScore=(TextView) findViewById(R.id.highscore_txt);
	    higherScore.setText("Highscore :"+hscore);
	    
	    //Set the Last player name and last score to the TextView
	    LName = (TextView) findViewById(R.id.lname_txt);
	    LName.setText("Name :"+lName); 
	    
	    //Set the Last player score to the TextView
	    LScore = (TextView) findViewById(R.id.lscore_txt);
	    LScore.setText("Score :"+lscore);	    
	}
}
