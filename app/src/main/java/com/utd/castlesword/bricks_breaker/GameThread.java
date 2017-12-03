package com.utd.castlesword.bricks_breaker;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by CastleSword on 02-12-2017.
 */

public class GameThread extends Thread {
    private GamePlay gamePlay;
    private SurfaceHolder surfaceHolder;
    private boolean runStatus = false;
    private long startTime;
    private long passedTime=0;

    public GameThread(GamePlay screen) {
        gamePlay = screen;
        surfaceHolder = gamePlay.getHolder();
    }

    @Override
    public void run() {
        Canvas canvas = null;

        while (runStatus) {

            startTime = System.currentTimeMillis();
            canvas = surfaceHolder.lockCanvas();
            if (canvas != null) {

                gamePlay.animate(passedTime, canvas);
                gamePlay.doDraw(canvas);
                passedTime = System.currentTimeMillis() - startTime;
                surfaceHolder.unlockCanvasAndPost(canvas);

            }
        }
    }

    public void setRunStatus(boolean newRunStatus) {
        runStatus = newRunStatus;
    }



}
