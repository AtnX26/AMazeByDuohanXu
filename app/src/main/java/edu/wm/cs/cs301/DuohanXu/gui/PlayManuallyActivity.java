package edu.wm.cs.cs301.DuohanXu.gui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import edu.wm.cs.cs301.DuohanXu.R;

public class PlayManuallyActivity extends AppCompatActivity {
    private String tag = "PlayManuallyActivity";
    private MazePanel mazePanel;
    private int pathlegnth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_manually);
        final MazePanel panel = (MazePanel) findViewById(R.id.ManualMazePanel);
        panel.setManorAni(true);
        pathlegnth = 0;


        Button buttonLeft = (Button) findViewById(R.id.buttonLeft);
        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(tag, "left clicked");
            }
        });

        Button buttonRight = (Button) findViewById(R.id.buttonRight);
        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(tag, "right clicked");
            }
        });

        Button buttonForward = (Button) findViewById(R.id.buttonForward);
        buttonForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(tag, "forward clicked");
                pathlegnth++;
            }
        });

        Button buttonJump = (Button) findViewById(R.id.buttonJump);
        buttonJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(tag, "jump clicked");
                pathlegnth++;
            }
        });

        Button ShortCut = (Button) findViewById(R.id.ShortCut);
        ShortCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(tag, "ShortCut clicked, jump to the winning state");
                startWinningActivity(this);
            }
        });

        ToggleButton showMap = (ToggleButton) findViewById(R.id.toggleMap);
        showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showMap.isChecked() == false){
                    Log.v(tag, "Map show off");
                }
                else{
                    Log.v(tag, "Map show on");
                }
            }
        });

        ToggleButton showSolution = (ToggleButton) findViewById(R.id.toggleSolution);
        showSolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showSolution.isChecked() == false){
                    Log.v(tag, "Solution show off");
                }
                else{
                    Log.v(tag, "Solution show on");
                }
            }
        });

        ToggleButton showWall = (ToggleButton) findViewById(R.id.toggleWalls);
        showWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showWall.isChecked() == false){
                    Log.v(tag, "Wall show off");
                }
                else{
                    Log.v(tag, "Wall show on");
                }
            }
        });

        final SeekBar mapSize = (SeekBar) findViewById(R.id.seekBarMapSize);
        mapSize.setMin(1);
        mapSize.setProgress(10);
        mapSize.setMax(20);
        mapSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int MapSize = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                MapSize = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.v(tag, "Map size changed to "+ MapSize);
            }
        });
    }

    private void startWinningActivity(View.OnClickListener view){
        Intent intent = new Intent(this, WinningActivity.class);
        int energyConsumed = -1;
        int distance2Exit = 100;
        Bundle bundle = getIntent().getExtras();
        bundle.putInt("Path length", pathlegnth);
        bundle.putInt("Energy consumed", energyConsumed);
        bundle.putInt("Distance to exit", distance2Exit);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        Log.v(tag, "back button pressed, going back to AMazeActivity");
        Intent intent = new Intent(getApplicationContext(), AMazeActivity.class);
        startActivity(intent);

    };
}
