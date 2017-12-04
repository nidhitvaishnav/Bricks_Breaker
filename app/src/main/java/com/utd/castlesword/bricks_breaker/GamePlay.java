package com.utd.castlesword.bricks_breaker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**  Created by: Nidhi Vaishnav
 *    Subject : Human computer Interaction
 */

public class GamePlay extends SurfaceView implements SurfaceHolder.Callback {
    //initializing variables
    private int score = 0;
    private Paint paint = new Paint();
    private int level = 1;

    public static float width; //screen width
    public static float height; //screen height

    private SensorManager sensorManager;
    private Sensor orientationSensor;
    private int orientation;
    private MediaPlayer paddleHit_sound;
    private MediaPlayer brickHit_sound;
    private MediaPlayer gameOver_sound;
    private MediaPlayer win_sound;
    private MediaPlayer loseLife_sound;
    public MediaPlayer background_sound;
    private int brickCols = 6;
    private int brickRows = 6;

    private GameThread gameThread;
    private Paddle paddle;
    private Ball ball;
    private int remainingLife;

    private List<Brick> brickList = new CopyOnWriteArrayList<Brick>();
    GameActivity gameActivity = new GameActivity();

    private int nBrickHits = 0;

    private boolean initialized = false;

    @SuppressWarnings("deprecation")
    //creating constructor of GamePlay class
    public GamePlay(Context context ) {
        super(context);
        getHolder().addCallback(this);

        //creating a thread object of GameThread
        gameThread = new GameThread(this);
//        paint.setColor(Color.BLACK);

        //initializing MediaPlayer for sound effect
        paddleHit_sound = MediaPlayer.create(context, R.raw.paddle_hit);
        brickHit_sound = MediaPlayer.create(context, R.raw.brick_hit);
        gameOver_sound = MediaPlayer.create(context, R.raw.game_over);
        win_sound = MediaPlayer.create(context, R.raw.game_win);
        loseLife_sound = MediaPlayer.create(context, R.raw.lose_life);
        background_sound = MediaPlayer.create(context, R.raw.background);
        background_sound.setLooping(true);

        //initializing sensor objects
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(sensorListener, orientationSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    //This method sets number of remainingLives based on difficulty level
    public void setLife(int setLife){
        remainingLife=setLife;
    }

    //This is a SensorEventListener which senses the change in orientation
    private SensorEventListener sensorListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            orientation = (int) event.values[2];
            Log.i("Orientation is changed:", "new orientation = " + orientation);
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    //implementing surfaceCreated() method, starting gameThread and background music
    public void surfaceCreated(SurfaceHolder holder) {
        if (!gameThread.isAlive()) {
            //thread
            gameThread.setRunStatus(true);
            gameThread.start();
            //sound
            background_sound.start();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int objWidth, int objHeight) {
        width = objWidth;
        height = objHeight;

        //creating paddle, ball and bricks
        paddle = new Paddle(getResources(), remainingLife);
        ball = new Ball(getResources(), (int) (paddle.getX() + (paddle.getWidth() / 2)), (int) paddle.getY());
        ball.setY(paddle.getY() - ball.getHeight());

        Brick.setWidth(width / brickCols);
        Brick.setHeight((height / 3) / brickRows); // bricks will cover 1/3 of the screen


        // Create Brick Array by using RowNum and ColumnNum
        for (int i = 0; i < brickRows; i++) {
            for (int j = 0; j < brickCols; j++) {
                brickList.add(new Brick(getResources(), (int) (j * Brick.getWidth()), (int) (i * Brick.getHeight())));
            }
        }
        initialized = true;
    }


    public void surfaceDestroyed(SurfaceHolder holder) {
        //stopping gameThread when surface has been destroyed
        if (gameThread.isAlive()) {
            gameThread.setRunStatus(false);
        }
        //release MediaPlayer objects
        if (paddleHit_sound != null)
            paddleHit_sound.release();
        if (brickHit_sound != null)
            brickHit_sound.release();
        if (background_sound != null)
            background_sound.release();

        //unRegistering sensor to reduce power consumption
        if (sensorManager != null) {
            sensorManager.unregisterListener(sensorListener);
        }
    }

    //starting game once user touch the ball
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ball.start();
        return super.onTouchEvent(event);
    }

    //initial and final screens
    public void doDraw(Canvas canvas) {

        //setting background transperent
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY);

        //boundary condition,
        if (paddle == null)
            return;

        //initialize and draw bricks and paddle
        if (paddle.isAlive() == true) {
            if (initialized) {
                paddle.doDraw(canvas);
                synchronized (brickList) {
                    for (Brick brick : brickList) {
                        if (brick.isAlive() == true) {
                            brick.doDraw(canvas);
                        }
                    }
                }
            }

            //create ball
            if (ball != null) {
                ball.doDraw(canvas);
            }

            //score and remaining lives
            paint.setTextSize(40);

            canvas.drawText("Score: " + score, 25, height - 15, paint);
            paint.setColor(Color.YELLOW);

            canvas.drawText("Remaining Lives: " + paddle.getRemainingLives(), width - 400, height - 15, paint);
        }
        // gameplay
        else {

            //if game ends display end screen
            //win condition
            if (nBrickHits == (brickRows * brickCols)) {   // WINNING THE GAME

                background_sound.stop();
                win_sound.start();
                win_sound.setLooping(false);

                canvas.drawColor(Color.BLACK);
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.win);
                canvas.drawBitmap(bmp, (canvas.getWidth() - 480) / 5, (canvas.getHeight() - 640) / 5, null);
                canvas.drawText("Score: " + score * (paddle.getRemainingLives()), 25, height - 15, paint);
                paint.setColor(Color.GREEN);
                canvas.drawText("Level: " + level, 300, height - 15, paint);
                paint.setColor(Color.WHITE);
                canvas.drawText("Remaining Lives: " + paddle.getRemainingLives(), width - 400, height - 15, paint);
                canvas.drawText("Restarting The Game... ", width - 400, height - 80, paint);

                Thread timer = new Thread() {
                    public void run() {
                        try {
                            sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            System.exit(0);
                        }
                    }
                };
                timer.start();
            }
            //lose condition
            else
            {
                gameOver_sound.start();
                gameOver_sound.setLooping(false);
                canvas.drawColor(Color.BLACK);
                Bitmap bmp2 = BitmapFactory.decodeResource(getResources(), R.drawable.lose);
                canvas.drawBitmap(bmp2, (canvas.getWidth() - 480) / 5, (canvas.getHeight() - 640) / 5, null);
                canvas.drawText("Score: " + score * (paddle.getRemainingLives()), 25, height - 15, paint);
                paint.setColor(Color.GREEN);
                canvas.drawText("Level: " + level, 300, height - 15, paint);
                paint.setColor(Color.WHITE);
                canvas.drawText("Restarting The Game... ", width - 400, height - 80, paint);
                canvas.drawText("Remaining Lives: " + paddle.getRemainingLives(), width - 400, height - 15, paint);

                Thread timer = new Thread() {
                    public void run() {
                        try {
                            sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {

                            System.exit(0);
                        }
                    }
                };
                timer.start();
            }
        }
    }

    //logic of game play
    public void animate(long passedTime, Canvas canvas) {
        if (ball != null) {
            if (paddle.isAlive() == true)
            {
                paddle.animate(orientation * (-1));
                // Level 2

                if(nBrickHits ==7)
                {
                    level=2;
                    paddle.level2(getResources(), canvas);
                }

                // Level 3
                if(nBrickHits ==14)
                {
                    level=3;
                    paddle.level3(getResources(), canvas);
                }

                // Level 4

                if(nBrickHits ==20)
                {
                    level=4;
                    ball.setSpeedX(Ball.initSpeedX+2);
                    ball.setSpeedY(Ball.initSpeedY-2);
                }

                // Level 5

                if(nBrickHits ==26)
                {
                    level=5;
                    paddle.level5(getResources(), canvas);
                }
                //setting movement of ball
                if (ball.isMoving() == true)
                    ball.animate(passedTime);
                else
                    ball.setX(paddle.getX() + (paddle.getWidth() / 2));
            }
            if (ball.isMoving() == true) {
                if ((ball.getY() + ball.getHeight()) > (paddle.getY())) {
                    //if ball hit the paddle
                    if (((ball.getX() + ball.getWidth()) > (paddle.getX())) &&
                            ((ball.getX()) < (paddle.getX() + paddle.getWidth()))) {
                        ball.setSpeedY(-1 * ball.getSpeedY());
                        ball.setY(paddle.getY() - ball.getHeight());
                        if (paddleHit_sound != null)
                            paddleHit_sound.start();
                    }
                    //if ball fall
                    else
                    {
                        paddle.loseLife();
                        if (paddle.getRemainingLives() != 0) {
                            loseLife_sound.start();
                        }
                        ball.stop();
                        ball.setX(paddle.getX() + (paddle.getWidth() / 2));
                        ball.setY(paddle.getY() - ball.getHeight());
                        ball.setSpeedX(ball.getSpeedX());
                        ball.setSpeedY(ball.getSpeedY());
                    }
                }
            }
            if (paddle.isAlive() == true) {
                synchronized (brickList) {
                    for (Brick brick : brickList) {

                        if (brick.isAlive() == true) {
                            if (((ball.getX() + ball.getWidth()) > (brick.getX())) &&
                                    ((ball.getX()) < (brick.getX() + Brick.getWidth())) &&
                                    ((ball.getY() + ball.getHeight()) > (brick.getY())) &&
                                    ((ball.getY()) < (brick.getY() + Brick.getHeight()))) {
                                brick.hit();

                                nBrickHits++;
                                if (level==1) score= nBrickHits;

                                else if(level==2){
                                    score=score+2;
                                }
                                else if (level==3){
                                    score=score+3;
                                }
                                else if (level==4){
                                    score=score+4;
                                }

                                else if (level==5){
                                    score=score+5;
                                }
                                if (brickHit_sound != null)
                                    brickHit_sound.start();

                                if (nBrickHits == (brickCols * brickRows)) {
                                    paddle.gameOver();
                                    break;
                                }

                                //logic for brick hit
                                float a, b, c, d, min;

                                a = Math.abs((ball.getX() + ball.getWidth()) - (brick.getX()));
                                b = Math.abs((ball.getX()) - (brick.getX() + Brick.getWidth()));
                                c = Math.abs((ball.getY() + ball.getHeight()) - (brick.getY()));
                                d = Math.abs((ball.getY()) - (brick.getY() + Brick.getHeight()));

                                min = Math.min(Math.min(a, b), Math.min(c, d));
                                if (min == a) {
                                    ball.setSpeedX(-1 * ball.getSpeedX());
                                    ball.setX(brick.getX() - ball.getWidth());
                                } else if (min == b) {
                                    ball.setSpeedX(-1 * ball.getSpeedX());
                                    ball.setX(brick.getX() + Brick.getWidth());
                                } else if (min == c) {
                                    ball.setSpeedY(-1 * ball.getSpeedY());
                                    ball.setY(brick.getY() - ball.getHeight());
                                } else if (min == d) {
                                    ball.setSpeedY(-1 * ball.getSpeedY());
                                    ball.setY(brick.getY() + Brick.getHeight());
                                }
                                break;

                            }
                        }
                    }
                }
            }
        }
    }
}
