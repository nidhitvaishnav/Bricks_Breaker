package com.utd.castlesword.bricks_breaker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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



public class GamePlay extends SurfaceView implements SurfaceHolder.Callback {
    private int level=1;
    private int score=0;
    private Paint paint = new Paint();

    public static float width; //screen width
    public static float height; //screen height
    
}
