package com.utd.castlesword.bricks_breaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/*  Created by: Nidhi Vaishnav
    Subject : Human computer Interaction
**/

//This is the first screen of the game, from which user can select difficulty
public class MainActivity extends AppCompatActivity {

    //onCreate method initialize the screen of the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //onClickEasyBtn will transfer user to GameActivity with easy difficulty level
    public void onClickEasyBtn(View view){
        Intent playGame = new Intent(getApplicationContext(),GameActivity.class);
        playGame.putExtra(getString(R.string.difficulty_intent), "Easy");
        startActivity(playGame);
    }

    //onClickMediumBtn will transfer user to GameActivity with Medium difficulty level
    public void onClickMediumBtn(View view){
        Intent playGame = new Intent(getApplicationContext(),GameActivity.class);
        playGame.putExtra(getString(R.string.difficulty_intent), "Medium");
        startActivity(playGame);
    }

    //onClickHardBtn will transfer user to GameActivity with Hard difficulty level
    public void onClickHardBtn(View view){
        Intent playGame = new Intent(getApplicationContext(),GameActivity.class);
        playGame.putExtra(getString(R.string.difficulty_intent), "Hard");
        startActivity(playGame);
    }

//    //TODO: Impliment leaderBorad activity
//    public void onClickLeaderboardBtn(View view){
//        Intent selectedContactIntent = new Intent(getApplicationContext(),LeaderBoardActivity.class);
//        startActivity(selectedContactIntent);
//    }

    //It will exit the game
    public void onClickExitBtn(View view){
        finish();
        System.exit(0);
    }
}
