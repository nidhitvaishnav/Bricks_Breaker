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


public class GamePlay extends SurfaceView implements SurfaceHolder.Callback {
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
    public MediaPlayer background;
    private int columnNum = 6;
    private int rowNum = 6;

    private GameThread gameThread;
    private Paddle paddle;
    private Ball ball;
    int remainingLife=5;

    private List<Brick> brickArray = new CopyOnWriteArrayList<Brick>();
    GameActivity gameActivity = new GameActivity();

    private int bricksHit = 0;
//    Vibrator v;
    private boolean initialized = false;
    Bitmap scaled;

    @SuppressWarnings("deprecation")
    public GamePlay(Context context ) {
        super(context);

        getHolder().addCallback(this);
        gameThread = new GameThread(this);
        paint.setColor(Color.BLACK);

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        sensorManager.registerListener(sensorListener, orientationSensor, SensorManager.SENSOR_DELAY_GAME);

        paddleHit_sound = MediaPlayer.create(context, R.raw.paddle_hit);
        brickHit_sound = MediaPlayer.create(context, R.raw.brick_hit);
        gameOver_sound = MediaPlayer.create(context, R.raw.game_over);
        win_sound = MediaPlayer.create(context, R.raw.game_win);
        loseLife_sound = MediaPlayer.create(context, R.raw.lose_life);
        background = MediaPlayer.create(context, R.raw.background);
        background.setLooping(true);
//        v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }
    public void setLife(int setLife){
        remainingLife=setLife;
    }

    private SensorEventListener sensorListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            orientation = (int) event.values[2];
            Log.i("Orientation changed:", "Orientation = " + orientation);
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    public void surfaceCreated(SurfaceHolder holder) {
        if (!gameThread.isAlive()) {
            gameThread.setRunStatus(true);
            gameThread.start();

            background.start();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int newWidth, int newHeight) {
        width = newWidth;
        height = newHeight;

        paddle = new Paddle(getResources(), remainingLife);
        ball = new Ball(getResources(), (int) (paddle.getX() + (paddle.getWidth() / 2)), (int) paddle.getY());
        ball.setY(paddle.getY() - ball.getHeight());

        Brick.setWidth(width / columnNum);
        Brick.setHeight((height / 3) / rowNum); // bricks will cover 1/3 of the screen


        // Create Brick Array by using RowNum and ColumnNum
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < columnNum; j++) {
                brickArray.add(new Brick(getResources(), (int) (j * Brick.getWidth()), (int) (i * Brick.getHeight())));
            }
        }
        initialized = true;
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (gameThread.isAlive()) {
            gameThread.setRunStatus(false);
        }
        if (sensorManager != null) {
            sensorManager.unregisterListener(sensorListener);
        }
        if (paddleHit_sound != null)
            paddleHit_sound.release();
        if (brickHit_sound != null)
            brickHit_sound.release();
        if (background != null)
            background.release();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ball.start();
        return super.onTouchEvent(event);
    }

    public void doDraw(Canvas canvas) {

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY);

        if (paddle == null)
            return;

        if (paddle.isAlive() == true) {
            if (initialized) {
                paddle.doDraw(canvas);
                synchronized (brickArray) {
                    for (Brick brick : brickArray) {
                        if (brick.isAlive() == true) {
                            brick.doDraw(canvas);
                        }
                    }
                }
            }

            if (ball != null) {
                ball.doDraw(canvas);
            }
            paint.setTextSize(40);

            canvas.drawText("Score: " + score, 25, height - 15, paint);
            paint.setColor(Color.YELLOW);

            canvas.drawText("Remaining Lives: " + paddle.getRemainingLives(), width - 400, height - 15, paint);
        }
        // END OF THE GAME
        else {

            if (bricksHit == (rowNum * columnNum)) {   // WINNING THE GAME

                background.stop();
                win_sound.start();
                win_sound.setLooping(false);
//                v.vibrate(100);
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
            } else   // LOSING THE GAME
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

    public void animate(long passedTime, Canvas canvas) {
        if (ball != null) {
            if (paddle.isAlive() == true)
            {
                paddle.animate(orientation * (-1));
                // Level 2

                if(bricksHit==6)
                {
                    level=2;
                    paddle.level2(getResources(), canvas);
                }

                // Level 3
                if(bricksHit==12)
                {
                    level=3;
                    paddle.level3(getResources(), canvas);
                }

                // Level 4

                if(bricksHit==19)
                {
                    level=4;
                    ball.setSpeedX(Ball.initSpeedX+2);
                    ball.setSpeedY(Ball.initSpeedY-2);
                }

                // Level 5

                if(bricksHit==27)
                {
                    level=5;
                    paddle.level5(getResources(), canvas);
                }
                if (ball.isMovin() == true)
                    ball.animate(passedTime);
                else
                    ball.setX(paddle.getX() + (paddle.getWidth() / 2));
            }
            if (ball.isMovin() == true) {
                if ((ball.getY() + ball.getHeight()) > (paddle.getY())) {
                    if (((ball.getX() + ball.getWidth()) > (paddle.getX())) &&
                            ((ball.getX()) < (paddle.getX() + paddle.getWidth()))) {
                        ball.setSpeedY(-1 * ball.getSpeedY());
                        ball.setY(paddle.getY() - ball.getHeight());
                        if (paddleHit_sound != null)
                            paddleHit_sound.start();
                    }
                    else
                    {
                        paddle.loseOneLife();
                        if (paddle.getRemainingLives() != 0) {
                            loseLife_sound.start();
//                            // Vibrate for x milliseconds
//                            v.vibrate(1500);
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
                synchronized (brickArray) {
                    for (Brick brick : brickArray) {

                        if (brick.isAlive() == true) {
                            if (((ball.getX() + ball.getWidth()) > (brick.getX())) &&
                                    ((ball.getX()) < (brick.getX() + Brick.getWidth())) &&
                                    ((ball.getY() + ball.getHeight()) > (brick.getY())) &&
                                    ((ball.getY()) < (brick.getY() + Brick.getHeight()))) {
                                brick.hit();

                                bricksHit++;
                                if (level==1) score=bricksHit;

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

                                if (bricksHit == (columnNum * rowNum)) {
                                    paddle.gameOver();
                                    break;
                                }

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
