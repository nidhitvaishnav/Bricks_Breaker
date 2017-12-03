package com.utd.castlesword.bricks_breaker;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class GameActivity extends AppCompatActivity {

    int life;
    int score;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //get intent
        Intent intent = getIntent();
        String difficulty = intent.getStringExtra(getString(R.string.difficulty_intent));

        if (difficulty=="Easy"){
            life = 3;
        }
        else if(difficulty=="Medium"){
            life = 2;
        }
        else if (difficulty=="Hard"){
            life = 1;
        }
        else;

        FrameLayout game = new FrameLayout(this);
        GamePlay gamePlay = new GamePlay (this);

        gamePlay.setZOrderOnTop(true);
        gamePlay.getHolder().setFormat(PixelFormat.TRANSLUCENT);

        game.addView(gamePlay);
        setContentView(game);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

}

