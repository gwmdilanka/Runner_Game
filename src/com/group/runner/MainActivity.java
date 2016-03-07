package com.group.runner;

import com.group.runner.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    //Explicit intent to connect class play
	public void play(View v)
	{
		Intent intent=new Intent(this,Play.class);
		startActivity(intent);
		finish();
	}
	
	//Explicit intent to connect class Credit
	public void credit(View v)
	{		
		Intent intent = new Intent(this,Credit.class);
		startActivity(intent);
		finish();
	}
	
	//Explicit intent to connect class Help
	public void help(View v)
	{		
		Intent intent = new Intent(this, Help.class);
		startActivity(intent);
		finish();
	}
	
	//Explicit intent to connect class HighScore
	public void highScore(View v)
	{
		Intent intent = new Intent(this, HighScore.class);
		startActivity(intent);
		finish();
	}
	
	//Explicit intent to connect class Settings
	public void settings(View v)
	{		
		Intent intent = new Intent(this, Settings.class);
		startActivity(intent);
		finish();
	}
		
	//Exit the game	
	public void exitApplication(View v)
	{
    	finish();
    	System.exit(0);  
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
