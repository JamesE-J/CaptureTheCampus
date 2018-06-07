package com.edmondson_jones.james.capturethecampus;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        final Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        Button loginActivityButton = (Button)findViewById((R.id.menuButtonOne));
        loginActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.startActivity(loginIntent);
            }
        });

        final Intent scoreboardIntent = new Intent(MainActivity.this, ScoreboardActivity.class);
        Button scoreboardActivityButton = (Button)findViewById((R.id.menuButtonTwo));
        scoreboardActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.startActivity(scoreboardIntent);
            }
        });

        final Intent helpAndCreditsIntent = new Intent(MainActivity.this, HelpAndCreditsActivity.class);
        ImageButton helpAndCreditsActivityButton = (ImageButton)findViewById((R.id.menuButtonSmall));
        helpAndCreditsActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.startActivity(helpAndCreditsIntent);
            }
        });
    }

}
