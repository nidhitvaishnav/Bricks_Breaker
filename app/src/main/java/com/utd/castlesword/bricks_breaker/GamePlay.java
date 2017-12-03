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
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static android.R.attr.level;


public class GamePlay extends SurfaceView implements SurfaceHolder.Callback {
    private int score = 0;
    private Paint paint = new Paint();

    public static float width; //screen width
    public static float height; //screen height

    private SensorManager sensorManager;
    private Sensor orientationSensor;
    private int orientation;
    private MediaPlayer mpPaddle;
    private MediaPlayer mpBrick;
    private MediaPlayer gameOver;
    private MediaPlayer win;
    private MediaPlayer loseLife;
    public MediaPlayer background;
    private int columnNum = 6;
    private int rowNum = 6;

    private GameThread gameThread;
    private Paddle paddle;
    private Ball ball;

    private List<Brick> brickArray = new CopyOnWriteArrayList<Brick>();
    GameActivity gameActivity = new GameActivity();

    private int bricksHit = 0;
    Vibrator v;
    private boolean initialized = false;
    Bitmap scaled;

    public GamePlay(Context context) {
        super(context);
        getHolder().addCallback(this);
        gameThread = new GameThread(this);
        paint.setColor(Color.BLACK);

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        sensorManager.registerListener(sensorListener, orientationSensor, SensorManager.SENSOR_DELAY_GAME);

        mpPaddle = MediaPlayer.create(context, R.raw.paddle_hit);
        mpBrick = MediaPlayer.create(context, R.raw.brick_hit);
        gameOver = MediaPlayer.create(context, R.raw.game_over);
        win = MediaPlayer.create(context, R.raw.game_win);
        loseLife = MediaPlayer.create(context, R.raw.lose_life);
        background = MediaPlayer.create(context, R.raw.background);
        background.setLooping(true);
        v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    private SensorEventListener sensorListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            orientation = (int) event.values[2];
            Log.i("Orientation changed!!", "Orientation = " + orientation);
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

        paddle = new Paddle(getResources());
        ball = new Ball(getResources(), (int) (paddle.getX() + (paddle.getWidth() / 2)), (int) paddle.getY());
        ball.setY(paddle.getY() - ball.getHeight());

        Brick.setWidth(width / columnNum);
        Brick.setHeight((height / 3) / rowNum); //ekranin duseyde ucte birini kaplasin // bricks will cover 1/3 of the screen


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
        if (mpPaddle != null)
            mpPaddle.release();
        if (mpBrick != null)
            mpBrick.release();
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
            paint.setTextSize(24);

            canvas.drawText("Score: " + score, 25, height - 15, paint);
            paint.setColor(Color.YELLOW);

            canvas.drawText("Remaining Lives: " + paddle.getRemainingLives(), width - 250, height - 15, paint);
        }
        // END OF THE GAME
        else {

            if (bricksHit == (rowNum * columnNum)) {   // WINNING THE GAME

                background.stop();
                win.start();
                win.setLooping(false);
                v.vibrate(100);
                canvas.drawColor(Color.BLACK);
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.win);
                canvas.drawBitmap(bmp, (canvas.getWidth() - 480) / 5, (canvas.getHeight() - 640) / 5, null);
                canvas.drawText("Score: " + score * (paddle.getRemainingLives()), 25, height - 15, paint);
                paint.setColor(Color.GREEN);
                canvas.drawText("Level: " + level, 300, height - 15, paint);
                paint.setColor(Color.WHITE);
                canvas.drawText("Remaining Lives: " + paddle.getRemainingLives(), width - 250, height - 15, paint);
                canvas.drawText("Restarting The Game... ", width - 280, height - 80, paint);

                Thread timer = new Thread() {
                    public void run() {
                        try {
                            sleep(4000);
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
                gameOver.start();
                gameOver.setLooping(false);
                canvas.drawColor(Color.BLACK);
                Bitmap bmp2 = BitmapFactory.decodeResource(getResources(), R.drawable.lose);
                canvas.drawBitmap(bmp2, (canvas.getWidth() - 480) / 5, (canvas.getHeight() - 640) / 5, null);
                canvas.drawText("Score: " + score * (paddle.getRemainingLives()), 25, height - 15, paint);
                paint.setColor(Color.GREEN);
                canvas.drawText("Level: " + level, 300, height - 15, paint);
                paint.setColor(Color.WHITE);
                canvas.drawText("Restarting The Game... ", width - 280, height - 80, paint);
                canvas.drawText("Remaining Lives: " + paddle.getRemainingLives(), width - 250, height - 15, paint);

                Thread timer = new Thread() {
                    public void run() {
                        try {
                            sleep(5000);
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
            if (paddle.isAlive() == true) {
                paddle.animate(orientation * (-1));

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
                        if (mpPaddle != null)
                            mpPaddle.start();
                    } else {
                        paddle.loseOneLife();
                        if (paddle.getRemainingLives() != 0) {
                            loseLife.start();
                            // Vibrate for x milliseconds
                            v.vibrate(1500);
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
                                score = score + 3;
                                if (mpBrick != null)
                                    mpBrick.start();

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
