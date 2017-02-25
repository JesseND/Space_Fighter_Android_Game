package com.example.spacefighter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;


public class GameView  extends SurfaceView implements Runnable{

    //boolean variable to track if the game is playing or not
    volatile boolean playing;

    //the game thread
    private Thread gameThread = null;

    //adding the player to this class
    private Player player;

    //These objects will be used for drawing
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    //Adding enemies object array
    private Enemy[] enemies;

    //Adding 3 enemies you may increase the size
    private int enemyCount = 3;

    //Adding an stars list
    private ArrayList<Star> stars = new ArrayList<Star>();

    //defining a boom object to display blast
    private Boom boom;

    //class constructor
    public GameView(Context context, int screenX, int screenY){
        super(context);

        //Initializing player objects
        player = new Player(context, screenX, screenY);

        //initializing drawing objects
        surfaceHolder = getHolder();
        paint = new Paint();

        //adding 100 stars you may increase the number
        int starNums = 100;
        for(int i=0; i<starNums; i++){
            Star s = new Star(screenX, screenY);
            stars.add(s);
        }

        //initializing enemy object array
        enemies = new Enemy[enemyCount];
        for(int i =0; i<enemyCount; i++){
            enemies[i] = new Enemy(context, screenX, screenY);
        }
        //initializing boom object
        boom = new Boom(context);

    }

    @Override
    public void run() {
     while(playing){
         //to update the frame
         update();
         //to draw the frame
         draw();
         //to control
         control();
     }
    }

    private void update(){

        //setting boom outside the screen
        boom.setX(-250);
        boom.setY(-250);


      //updating player position
        player.update();
        for(Star s: stars){
            s.update(player.getSpeed());
        }

        //updating the enemy coordinate with respect to player speed
        for(int i=0; i<enemyCount; i++){
            enemies[i].update(player.getSpeed());

            //if collision occurs with player
            if(Rect.intersects(player.getDetectCollision(), enemies[i].getDetectCollision())){

                //displaying boom at that location
                boom.setX(enemies[i].getX());
                boom.setY(enemies[i].getY());

                //moving enemy outside the left edge
                enemies[i].setX(-200);
            }

        }
    }

    private void draw(){
        //checking if surface is valid
        if(surfaceHolder.getSurface().isValid()){
            //locking the canvas
            canvas = surfaceHolder.lockCanvas();
            //drawing a background color for canvas
            canvas.drawColor(Color.BLACK);

            //setting the paint color to white to draw the stars
            paint.setColor(Color.WHITE);

            //drawing all stars
            for(Star s: stars){
                paint.setStrokeWidth(s.getStarWidth());
                canvas.drawPoint(s.getX(), s.getY(), paint);
            }

            //drawing the player
            canvas.drawBitmap(
                    player.getBitmap(),
                    player.getX(),
                    player.getY(),
                    paint
            );
            //drawing the enemies
            for(int i=0; i<enemyCount; i++){
                canvas.drawBitmap(
                        enemies[i].getBitmap(),
                        enemies[i].getX(),
                        enemies[i].getY(),
                        paint
                );
            }
            //drawing boom image
            canvas.drawBitmap(
                    boom.getBitmap(),
                    boom.getX(),
                    boom.getY(),
                    paint
            );

            //unlock the canvas
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void control(){
        try{
            gameThread.sleep(17);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public void pause(){
        //when the game is paused
        //setting the variable to false
        playing = false;
        try{
            gameThread.join();
        }catch (InterruptedException e){
        }
    }

    public void resume(){
        //when the game is resumed
        //starting the thread again
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch(motionEvent.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_UP:
                //When the user presses on the screen
                //stopping the boosting when the screen is released
                player.stopBoosting();
                break;
            case MotionEvent.ACTION_DOWN:
                //When the user releases the screen
                //boosting the space jet when screen is pressed
                player.setBoosting();
                break;
        }
        return true;
    }
}
