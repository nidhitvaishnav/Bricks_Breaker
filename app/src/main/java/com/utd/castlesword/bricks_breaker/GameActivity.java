package com.utd.castlesword.bricks_breaker;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**  Created by: Nidhi Vaishnav
 *    Subject : Human computer Interaction
 */

public class GameActivity extends AppCompatActivity {

    int life;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //get intent
        Intent intent = getIntent();
        String difficulty = intent.getStringExtra(getString(R.string.difficulty_intent));

        //based on difficulty level, set life
        if (difficulty.equals("Easy")){
            life = 4;
        }
        else if(difficulty.equals("Medium")){
            life = 3;
        }
        else if (difficulty.equals("Hard")){
            life = 2;
        }
        else{
            life = 100;
        };

        //get frame layout
        FrameLayout gameFrameLayout = new FrameLayout(this);

        //create object of GamePlay class and set attributes
        GamePlay gamePlay = new GamePlay (this);
        gamePlay.setLife(life);
        gamePlay.setBackgroundColor(Color.BLACK);

        gamePlay.setZOrderOnTop(true);
        gamePlay.getHolder().setFormat(PixelFormat.TRANSLUCENT);

        //add gamePlay to frameLayout
        gameFrameLayout.addView(gamePlay);
        setContentView(gameFrameLayout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

}

