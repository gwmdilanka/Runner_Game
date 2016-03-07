package com.group.runner;

import com.group.runner.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class Settings extends Activity{
	
	public static final String HIGHER = "MyPreferencesFile";
	CheckBox volumeChk;
	int volume;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		//Back to the main menu
		Button backBtn = (Button)findViewById(R.id.back_btn);
		backBtn.setOnClickListener(new OnClickListener() {
	    	
	    	public void onClick(View v) {
	    		Intent BackToMenu = new Intent(Settings.this, MainActivity.class);
	            startActivity(BackToMenu);        		
	    		finish();
	    	}
		});
		
		volumeChk = (CheckBox)findViewById(R.id.volumeChk);		
		SharedPreferences pref = getApplicationContext().getSharedPreferences(HIGHER, MODE_PRIVATE);
	    Editor editor = pref.edit();	  
	    
	    //Get the default value for the volume
	    volume=pref.getInt("vloume", 0);
	    
	    //If user checked on the volume check box then the set the check box true
	    if(volume==1)
	    {
	    	volumeChk.setChecked(true);
	    }
	}
	
	//Display text whether check box is checked
	public void volume(View v) {
		volumeChk = (CheckBox)v;
        SharedPreferences pref = getApplicationContext().getSharedPreferences(HIGHER, MODE_PRIVATE);
	    Editor editor = pref.edit();
        if(volumeChk.isChecked())
        {
        	editor.putInt("vloume", 1);
    	    editor.commit(); 
    	    //Display text on the screen that music is on
    	    Toast.makeText(this,"volume on", Toast.LENGTH_LONG).show();
        }
        else
        {
        	editor.putInt("vloume", 0); 
        	editor.commit(); 
        	Toast.makeText(this,"volume off", Toast.LENGTH_LONG).show();     	    
        }
    }
}
