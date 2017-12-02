package com.utd.castlesword.bricks_breaker;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.Random;



public class Brick {
    private float x, y;

    private static float height = 0;
    private static float width = 0;

    private boolean alive;

    private Bitmap bitmap;

    private int imgArr[] = {R.drawable.brick_blue, R.drawable.brick_red,
                            R.drawable.brick_green, R.drawable.brick_yellow,
                            R.drawable.brick_bonus};

    Brick(Resources res, int ilkX, int ilkY) {


        int rndIndex = new Random().nextInt(imgArr.length);

        bitmap = BitmapFactory.decodeResource(res, imgArr[rndIndex]);

        x = ilkX;
        y = ilkY;
        alive = true;
    }

    void doDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, null, new Rect((int)x, (int)y, (int)(x+width), (int)(y+height)), null);
    }

    void hit() {
        alive = false;
    }

    boolean isAlive() {
        return alive;
    }

    static void setWidth(float widths){
        width = widths;
    }

    static void setHeight(float heights){
        height = heights;
    }

    static float getWidth(){
        return width;
    }

    static float getHeight(){
        return height;
    }

    float getX() {
        return x;
    }

    float getY() {
        return y;
    }
}
