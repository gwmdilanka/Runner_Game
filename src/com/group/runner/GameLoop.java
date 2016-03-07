package com.group.runner;

import com.group.runner.Game.GameView;

import android.annotation.SuppressLint;
import android.graphics.Canvas;

public class GameLoop extends Thread {

    private GameView view;    
    private boolean running = false;
	boolean isPaused;
    
    public GameLoop(GameView view) {
          this.view = view;          
    }

    public void setRunning(boolean run) {

          running = run;
    }
    
    public void setPause(int i)
    {
        synchronized (view.getHolder()) 
        {

			if(i==0)
        	{
        		isPaused=false;
        	}
        	if(i==1)
        	{
        		isPaused = true;
        	}
         }
    }
    @SuppressLint("WrongCall")
	@Override
	  public void run() {
	 	   long ticksPS = 100;
	 	   long startTime = 0;
	 	   long sleepTime;
	          while (running) {
	        	  //pause and resume
	        	  
				if (isPaused) 
				{
	                  try 
	                  {
	                      Thread.sleep((long)50);
	                  } 
	                  catch (InterruptedException e) 
	                  {
	                    e.printStackTrace();
	                  }
				}
	            else
	            {
	                 Canvas c = null;
	                 startTime = System.currentTimeMillis();
	                 try {
	                        c = view.getHolder().lockCanvas();

	                        synchronized (view.getHolder()) 
	                        {
	                        	view.onDraw(c);
	                        }

	                 	  } 
	                 finally 
	                 {
	                	 if (c != null) 
	                        {
	                        	view.getHolder().unlockCanvasAndPost(c);
	                        }
	                 }
	            }
				
				//Sleep time of the game
				sleepTime = ticksPS-(System.currentTimeMillis() - startTime); 
				try 
				{
					if (sleepTime > 0)
						sleep(sleepTime);
					else
						sleep(10);
				}
				catch (Exception e) {}
	          }
    }
}  
