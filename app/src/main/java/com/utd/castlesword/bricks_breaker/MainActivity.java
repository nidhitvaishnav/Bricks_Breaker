package com.utd.castlesword.bricks_breaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickEasyBtn(View view){

        Intent playGame = new Intent(getApplicationContext(),GameActivity.class);
        playGame.putExtra(getString(R.string.difficulty_intent), ((Button)view).getId());
        startActivity(playGame);
    }

    public void onClickMediumBtn(View view){
        Intent playGame = new Intent(getApplicationContext(),GameActivity.class);
        playGame.putExtra(getString(R.string.difficulty_intent), ((Button)view).getId());
        startActivity(playGame);
    }

    public void onClickHardBtn(View view){
        Intent playGame = new Intent(getApplicationContext(),GameActivity.class);
        playGame.putExtra(getString(R.string.difficulty_intent), ((Button)view).getId());
        startActivity(playGame);
    }

    public void onClickLeaderboardBtn(View view){
        Intent selectedContactIntent = new Intent(getApplicationContext(),LeaderBoardActivity.class);
        startActivity(selectedContactIntent);
    }

    public void onClickExitBtn(View view){
        finish();
        System.exit(0);
    }
}
