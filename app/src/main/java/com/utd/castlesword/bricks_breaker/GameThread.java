package com.utd.castlesword.bricks_breaker;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**  Created by: Nidhi Vaishnav
*    Subject : Human computer Interaction
*/

public class GameThread extends Thread {
    private GamePlay gamePlay;
    private SurfaceHolder surfaceHolder;
    private boolean runStatus = false;
    private long startTime;
    private long passedTime=0;

    //Constructor is initializing gamePlay
    public GameThread(GamePlay myGame) {
        gamePlay = myGame;
        surfaceHolder = gamePlay.getHolder();
    }

    //run game
    @Override
    public void run() {
        Canvas canvas = null;

        //create animation by redrawing every time
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
