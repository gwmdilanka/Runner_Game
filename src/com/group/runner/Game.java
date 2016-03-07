package com.group.runner;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class Game extends Activity {

	MediaPlayer MPlayerBackground,MPlayerJump,MPlayercoin,MPlayerfire, MPlayerpower;
	GameLoop gameLoopThread;
	GameView gameView;
	public static final String HIGHER = "MyPreferencesFile";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Manage Call state
		TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		TelephonyMgr.listen(new TeleListener(),PhoneStateListener.LISTEN_CALL_STATE);
		
		//Remove the title bar from the game window
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(new GameView(this));			
	}
	
	public class GameView extends SurfaceView implements SurfaceHolder.Callback {
		
		private Bitmap background;	
		private Bitmap exitBtnImg,pauseBtnImg;
		private Bitmap sprite1,sprite2,sprite3;
		private Bitmap fire,coin,powerBulb;
		
		private int displayWidth, displayHeight;
		private int fireSpeed = 0, coinSpeed = 0,powerSpeed=0;
		private int moveBgroundX=0;
		private int x=0,delay=0;;//run sprite changing values	
		private int volume, pauseTimes=0;
		private int getx,gety;//x and y points of the finger touch		
		private int score,lifeBar = 100; // health
		
		private boolean show = false, gameover = false, restart = false, sound = true;//Flags
		
		private String lName; //To store the name of the last score player
		
		private Paint scorePaint = new Paint();//For score
		private Paint lifeBarPaint = new Paint(); //For lifeBar
		private Paint gameOverPaint = new Paint();//For game over		

		@SuppressWarnings("deprecation")
		@SuppressLint("NewApi")
		public GameView(Context context) {
			super(context);
			gameLoopThread = new GameLoop(this);
			getHolder().addCallback(this);
			
			//Getting the screen size 
            Display display = getWindowManager().getDefaultDisplay();
            displayWidth = display.getWidth();
            displayHeight = display.getHeight();
            
            //Starting speeds at the middle of the screen
            coinSpeed=displayWidth/2;
			fireSpeed=displayWidth/2;
			powerSpeed = 3*displayWidth/4;
            
            background = BitmapFactory.decodeResource(getResources(), R.drawable.jungle2);
            exitBtnImg = BitmapFactory.decodeResource(getResources(), R.drawable.exit_img);
            pauseBtnImg = BitmapFactory.decodeResource(getResources(), R.drawable.pause_img);
            sprite1 = BitmapFactory.decodeResource(getResources(), R.drawable.sprite1);
            sprite2 = BitmapFactory.decodeResource(getResources(), R.drawable.sprite2);
            sprite3 = BitmapFactory.decodeResource(getResources(), R.drawable.sprite3);
            fire = BitmapFactory.decodeResource(getResources(), R.drawable.fire_img);
            coin = BitmapFactory.decodeResource(getResources(), R.drawable.coin_img);
            powerBulb = BitmapFactory.decodeResource(getResources(), R.drawable.bulb_img);
            
            background = Bitmap.createScaledBitmap(background, 2*displayWidth,displayHeight, true);
            exitBtnImg = Bitmap.createScaledBitmap(exitBtnImg,50,50,true);  //Size of the exit button to draw
            pauseBtnImg = Bitmap.createScaledBitmap(pauseBtnImg,50,50,true); //Size of the pause button to draw
            sprite1 = Bitmap.createScaledBitmap(sprite1, displayWidth/9,displayHeight/7, true);
            sprite2 = Bitmap.createScaledBitmap(sprite2, displayWidth/9,displayHeight/7, true);
            sprite3 = Bitmap.createScaledBitmap(sprite3, displayWidth/9,displayHeight/7, true);
            coin = Bitmap.createScaledBitmap(coin, displayWidth/20,displayHeight/24, true);
            fire = Bitmap.createScaledBitmap(fire, displayWidth/20,displayHeight/24, true);
            powerBulb = Bitmap.createScaledBitmap(powerBulb, displayWidth/20,displayHeight/24, true);
                       
            //Playing background sound
            MPlayerBackground=MediaPlayer.create(Game.this,R.raw.background_music); 
            
            //Playing jump sound
            MPlayerJump = MediaPlayer.create(Game.this,R.raw.sprite_jump);
            
            //Playing coin sound
            MPlayercoin = MediaPlayer.create(Game.this, R.raw.coin_sound);
            
            //Playing fire sound
            MPlayerfire = MediaPlayer.create(Game.this, R.raw.fire_sound);
            
            //Playing power sound
            MPlayerpower = MediaPlayer.create(Game.this, R.raw.power_sound);
            
		}		
		
		@SuppressLint("WrongCall")
		@Override
		public void surfaceCreated(SurfaceHolder holder) {	
      	  gameLoopThread.setRunning(true);
      	  gameLoopThread.start();
			
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {			
		}
		
		@SuppressWarnings("deprecation")		
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {			
			gameLoopThread.setRunning(false);
			gameLoopThread.getThreadGroup().interrupt();			
		}

		//On touch event in the screen
		@Override
		public boolean onTouchEvent(MotionEvent event) {					
			if(event.getAction()==MotionEvent.ACTION_DOWN)
    	  	{
				show = true;				
				//Get the touch point
				getx=(int) event.getX();
	    	  	gety=(int) event.getY();
	    	  		
	    	  	//Exit from the game view
	    	  	if(getx<50&&gety<50)
	    	  	{
	    	  		//Save the higher score before exit the game and return to the menu
	    	  		saveNameScore();
	    	  		//Stop the background Music
			        MPlayerBackground.stop();			        
			        //Back to the main menu
			        backToMain();
	    	  	}
	    	  	
	    	  //Pause the running game
				if((getx>(displayWidth-50)&&gety<50&&pauseTimes==0))
		  		{	  			
		  			gameLoopThread.setPause(1);	  			
		  			MPlayerBackground.pause();
		  			pauseTimes=1;
		  		}
		  		else if(getx>(displayWidth-50)&&gety<50&&pauseTimes==1)
		  		{
		  			gameLoopThread.setPause(0);		  		
		  			MPlayerBackground.start();
		  			MPlayerBackground.setLooping(true);
		  			pauseTimes=0;
		  		}		
				//Restart the game by touch on "RESTART" word
				if(getx>(displayWidth/4)&&gety<displayHeight/4)
				{
					if(lifeBar<=0)
    	  			{
    	  				gameLoopThread.setPause(0);
    	  				MPlayerBackground.start();
    	  				MPlayerBackground.setLooping(true);
						lifeBar=100;
						score=0;    	  			
    	  			}					
    	  		}
    	  	}
			return true;
		}
		
		@SuppressLint("WrongCall")
		@Override
		protected void onDraw(Canvas canvas) {			
			super.onDraw(canvas);			
			
			//canvas color is Black
			canvas.drawColor(Color.BLACK);   	  	
    	  	
	    	/**simply sets of volume values that stored persistently. 
            Persistently which mean data you stored in the SharedPreferences 
            are still exist even if you stop the application or turn off the device**/
        	SharedPreferences pref = getApplicationContext().getSharedPreferences(HIGHER, MODE_PRIVATE);
        	Editor editor = pref.edit();
        	volume=pref.getInt("vloume", 0);
        	if(volume==0)
        	{
        		sound=false;
        	}
        	
        	else if(sound==true)//If user checked in volume
        	{
        		MPlayerBackground.start();
        		MPlayerBackground.setLooping(true); //Continuously playing background music
        	}
        	else
        	{
        		MPlayerBackground.stop();//Stop the background music        			
        	}        	
        	
        	//Move the back ground left side of the screen by 10
        	movingBackground(canvas);
	    	
	    	//Animate sprite	    	
	    	runSprite(canvas);	    	
	    	
	    	//Jump Sprite at the time of user touch the screen
	    	jumpSprite(canvas);	    	 
	    	
	    	//Draw moving fire barrier
	    	drawMovingFire(canvas);	    	
   		 	
   		 	//Draw coin
	    	drawMovingCoin(canvas);   		 	
   		 	
   		 	//Draw power bulb
	    	drawMovingPowerBulb(canvas);   		 	
   		 	
   		 	//Display score on the screen
	    	displayScore(canvas);   		 	
    	    
    	    //Display Player name which is entered in the Play class
    	    Intent in = getIntent();
    	    lName= in.getExtras().getString("playerName");    	    
    	    canvas.drawText("Player : " +lName, 1*displayWidth/4, 25, scorePaint);
    	    
    	    //Display liftBar on the screen
    	    displayLifeBar(canvas);    	    
		   	    	
		    //GameOver if the lifeBar equals zero
		    if(lifeBar<=0)
		    {
		    	gameover=true;		    	
		    	gameLoopThread.setPause(1);//pause the loop
		    	MPlayerBackground.pause();//pause the sound
		    	
		    	//Record the score and the name to the memory		    	
		    	editor.putInt("score", score);
		    	editor.putString("name", lName);
	    	    editor.commit(); 
	    	    
	    	    //Display game over state on the screen
	    	    displayGameOverState(canvas);	    	   
		    }		    
			//Draw exit button
			canvas.drawBitmap(exitBtnImg, 0, 0, null);
			//Draw pause button
			canvas.drawBitmap(pauseBtnImg, (displayWidth-50),0, null);//To draw image right side of the corner of the screen					
		}	
		
		/**********Animate Background*******/
		
		//Move the back ground left side of the screen by 10
		private void movingBackground(Canvas c)
		{
			moveBgroundX=moveBgroundX-10;
	    	if(moveBgroundX ==-displayWidth)
	    	{
	    		//Re-start drawing background again
	    		moveBgroundX=0;
	    		c.drawBitmap(background, moveBgroundX, 0, null);    		
	    	}
	    	else
	    	{	    		
	    		c.drawBitmap(background, moveBgroundX, 0, null);    		
	    	}			
		}		
		/**********Animate running sprite********/
 
		//Every time add one for x at the time of loop running.
		private void runSprite(Canvas c)
		{
			x+=1;
	    	if(x==4)
	    	{
	    		x=1;
	    	}
	    	if(show==false)//User touch the screen then running images are not showing.
	    	{
	    		if(x%2==0)
	    		{
	    			//If remainder is equals 0 then draw sprite 1
	    			c.drawBitmap(sprite1, displayWidth/16, 13*displayHeight/18, null);
	    		}
	    		else
	    		{
	    			//If remainder is not equals 0 then draw sprite 2
	    			c.drawBitmap(sprite2, displayWidth/16, 13*displayHeight/18, null);
	    		}
	    		//Collision with the fire barrier
	    		collisionFire();	    		
	    	}
		}
		
		//Jump Sprite		
		private void jumpSprite(Canvas c)
		{
			if(show==true)
	    	 {
	    		 if(sound==true)
	    		 {
	    			 MPlayerJump.start();
	    		 }
	    		 //Draw sprite jump image
	    		 c.drawBitmap(sprite3, displayWidth/16, 2*displayHeight/4, null);
 				 
	    		 //Collision detection with coin and add 10 points to score 				  
	    		 collisionCoin();
	    		    
	    		 
 				 //Collision detection with the powerBulb and 25 points deducting from the life bar
 				 collisionPowerBulb();
 				 
 				 //Delaying jump
 				  delay+=1;
 				  if(delay==4)
 				  {
 					  show=false;//Show again the running sprite
 					  delay=0;
 				  }
	    	 }			
		}
		/**********Draw moving barriers********/
		
		//Draw moving fire barrier
		private void drawMovingFire(Canvas c)
		{
			fireSpeed=fireSpeed-20;   		 	
   		 	//Equal fire speed again to the screen display width
   		 	if(fireSpeed==-displayWidth/2)
   		 	{
   		 		fireSpeed=displayWidth;
   		 		c.drawBitmap(fire, fireSpeed, 13*displayHeight/18, null);
   		 	}
   		 	else
   		 	{
   		 		c.drawBitmap(fire, fireSpeed, 13*displayHeight/18, null);
   		 	}
			
		}
		
		//Draw moving coin moving
		private void drawMovingCoin(Canvas c)
		{
			coinSpeed=coinSpeed-5;
			if(coinSpeed==-displayWidth/2)
			{
				coinSpeed=displayWidth;//start drawing right edge of the screen
		    	c.drawBitmap(coin, coinSpeed, 2*displayHeight/4, null);	
			}
			else
			{
				c.drawBitmap(coin, coinSpeed, 2*displayHeight/4, null);	
			}  
		}
		
		//Draw moving power bulb
		private void drawMovingPowerBulb(Canvas c)
		{
			powerSpeed=powerSpeed-15;
   		 	c.drawBitmap(powerBulb, powerSpeed, 2*displayHeight/4, null);
    		if(powerSpeed<0)
    		{
    			powerSpeed=3*displayWidth/4;
    		}			
		}		
		
		/**********Collision detections with barriers********/
		
		//Collision with the fire barrier
    	private void collisionFire()
    	{
    		if(fireSpeed<=displayWidth/8&&fireSpeed>=displayWidth/16)
			  {
				 fireSpeed=displayWidth;
				 lifeBar-=25;
				 if(sound==true)
				 {
					 MPlayerfire.start();
				 }
			  }	 		
    	}    	
    	
    	//Collision detection with coin and add 10 points to score
    	private void collisionCoin()
    	{
    		if(coinSpeed<=displayWidth/8&&coinSpeed>=displayWidth/16)
    		{
    			if(sound==true)
    			{
    				MPlayercoin.start();
    			}
    			coinSpeed=displayWidth/2;
    			score+=10;
    		}
    	}
    	
    	//Collision detection with the powerBulb and 25 points deducting from the life bar
    	private void collisionPowerBulb()
    	{
    		if(powerSpeed<=displayWidth/8&&powerSpeed>=displayWidth/16)
			  {
				  if(sound==true)
				  {
					MPlayerpower.start();
				  }
				  powerSpeed=2*displayWidth;
				  lifeBar+=25; 					
			  }    		
    	}
    	
    	/**********Saving score and name********/
    	
    	//Saving the score and name to the SharedPreferences    	
    	private void saveNameScore()
    	{
    		SharedPreferences pref = getApplicationContext().getSharedPreferences(HIGHER, MODE_PRIVATE);
    		Editor editor = pref.edit();
    		editor.putInt("score", score);
    		editor.putString("name", lName);
    		editor.commit(); 		
    	}
    	
    	//Back to the main menu
    	private void backToMain()
    	{
    		Intent intent = new Intent(Game.this, MainActivity.class);
    		startActivity(intent);    		
    		System.exit(0);
    	}   
    	
    	//Display score on the screen
    	private void displayScore(Canvas c)
    	{
    		scorePaint.setColor(Color.WHITE);
   		 	scorePaint.setAntiAlias(true);
   		 	scorePaint.setFakeBoldText(true);
   		 	scorePaint.setTextSize(30);
   		 	scorePaint.setTextAlign(Align.LEFT);
    	    c.drawText("Score : "+score, displayWidth/2, 25, scorePaint);    		
    	}
    	
    	/**********Drawing text on the game view********/
    	
    	//Display liftBar on the screen
    	private void displayLifeBar(Canvas c)
    	{
    		lifeBarPaint.setColor(Color.GREEN);
    	    lifeBarPaint.setStrokeWidth(5);
    	    lifeBarPaint.setTextSize(30);
    	    lifeBarPaint.setAntiAlias(true);
    	    lifeBarPaint.setFakeBoldText(true);
		    c.drawText("Life Bar : "+lifeBar, 0, (displayHeight/6)-4, lifeBarPaint);
		    c.drawRect(0, (displayHeight/6), lifeBar, displayHeight/8+60, lifeBarPaint);    		
    	}
    	//Display game over state on the screen
    	private void displayGameOverState(Canvas c)
    	{
    		gameOverPaint.setColor(Color.YELLOW);
    	    gameOverPaint.setAntiAlias(true);
    	    gameOverPaint.setFakeBoldText(true);
    	    gameOverPaint.setTextSize(30);
    	    gameOverPaint.setTextAlign(Align.CENTER);
	    	c.drawText("GAME OVER", displayWidth/2, displayHeight/2, gameOverPaint);
	    	c.drawText("YOUR SCORE : "+score, displayWidth/2, displayHeight/4, gameOverPaint);
	    	c.drawText("RESTART", displayWidth/4, displayHeight/4, gameOverPaint);
	    	c.drawBitmap(background, displayWidth, displayHeight, null);    		
    	}
	}
	
	/**********Telephone Manager********/
	//Game is not crash at the time of call coming
	public class TeleListener extends PhoneStateListener 
	{
		public void onCallStateChanged(int state,String incomingNumber)
		{
			if(state==TelephonyManager.CALL_STATE_RINGING)
			{
				//Stop the background music and back to the main menu
				MPlayerBackground.stop();
				System.exit(0); 
			}
		}
	}
	
	

}


