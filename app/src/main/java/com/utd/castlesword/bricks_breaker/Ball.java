package com.utd.castlesword.bricks_breaker;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.util.Random;

/**  Created by: Nidhi Vaishnav
 *    Subject : Human computer Interaction
 */


public class Ball {
    private float x, y;

    private int speedX, speedY;

    public static int initSpeedX = 7;
    public static int initSpeedY = -7;

    private Bitmap bitmap;

    private boolean isMoving = false;

    private int ballArr[] = {R.drawable.ball, R.drawable.fireball};

    Ball(Resources res, int initX, int initY) {
        //take any ball randomly
        int rndIndex = new Random().nextInt(ballArr.length);
        bitmap = BitmapFactory.decodeResource(res,ballArr[rndIndex]);
        //initialize co-ordinates and speed of ball
        x = initX;
        y = initY;
        speedX = initSpeedX;
        speedY = initSpeedY;
    }

    //draw ball
    void doDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x, y, null);
    }

    //logice for ball movement
    void animate(long passedTime) {
        x += speedX * (passedTime / 15f);
        y += speedY * (passedTime / 15f);
        cornerBounce();
    }

    //if ball hit corner, bounce it
    void cornerBounce() {
        if (x <= 0) {
            speedX = -speedX;
            x = 0;
        } else if (x + bitmap.getWidth() >= GamePlay.width) {
            speedX = -speedX;
            x = GamePlay.width - bitmap.getWidth();
        }

        if (y <= 0) {
            speedY = -speedY;
            y = 0;
        }
    }
    int getSpeedX() {
        return speedX;
    }

    int getSpeedY() {
        return speedY;
    }
    float setSpeedX(int newSpeed) {
        speedX = newSpeed;
        return speedX;
    }

    float setSpeedY(int newSpeed) {
        speedY = newSpeed;
        return speedY;
    }

    float getX() {
        return x;
    }

    float getY() {
        return y;
    }

    void setX(float newX) {
        x = newX;
    }

    void setY(float newY) {
        y = newY;
    }

    int getWidth() {
        return bitmap.getWidth();
    }

    int getHeight() {
        return bitmap.getHeight();
    }

    void stop() {
        isMoving = false;
    }

    void start() {
        isMoving = true;
    }

    boolean isMoving() {
        return isMoving;
    }
}
