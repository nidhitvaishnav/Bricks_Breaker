package com.utd.castlesword.bricks_breaker;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;



public class Paddle {
    private float x, y;

    private float height;
    private float width;

    private Bitmap bitmap;

    private boolean isAlive;
    private int remaininglives;

    Paddle(Resources res, int setLife) {
        bitmap = BitmapFactory.decodeResource(res, R.drawable.paddle1);
        height = bitmap.getHeight();
        width = bitmap.getWidth();
        x = (GamePlay.width / 2) - (width / 2);
        y = GamePlay.height - 100;

        isAlive= true;
        remaininglives = setLife;
    }
    void doDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x, y, null);
    }

    void animate(int orientation) {
        if (orientation < 0) {
            x=x-14;
        }
        else if (orientation > 0) {
            x=x+14;
        }
        if (x < 0) {
            x = 0;
        }
        if (x + width > GamePlay.width) {
            x = GamePlay.width - width;
        }

    }
    void loseOneLife() {
        if (remaininglives-- == 1) {
            gameOver();
        }
        x = (GamePlay.width / 2) - (width / 2);
        y = GamePlay.height - 50;
    }

    boolean isAlive() {
        return isAlive;
    }

    void gameOver() {
        isAlive = false;
    }

    int getRemainingLives() {
        return remaininglives;
    }
    float getWidth(){
        return width;
    }
    float getHeight(){
        return height;
    }

    float getX() {
        return x;
    }

    float getY() {
        return y;
    }

    float level2(Resources res, Canvas canvas){
        bitmap = BitmapFactory.decodeResource(res, R.drawable.paddle2);
        canvas.drawBitmap(bitmap, x, y, null);
        width = bitmap.getWidth();

        return width;
    }
    float level3(Resources res, Canvas canvas){
        bitmap = BitmapFactory.decodeResource(res, R.drawable.paddle3);
        canvas.drawBitmap(bitmap, x, y, null);
        width = bitmap.getWidth();

        return width;
    }
    float level4(Resources res, Canvas canvas) {
        bitmap = BitmapFactory.decodeResource(res, R.drawable.paddle4);
        canvas.drawBitmap(bitmap, x, y, null);
        width = bitmap.getWidth();

        return width;
    }

    float level5(Resources res, Canvas canvas){
        bitmap = BitmapFactory.decodeResource(res, R.drawable.paddle5);
        canvas.drawBitmap(bitmap, x, y, null);
        width = bitmap.getWidth();

        return width;
    }

}
