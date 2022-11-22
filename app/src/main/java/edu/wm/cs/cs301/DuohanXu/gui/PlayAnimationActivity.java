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

public class PlayAnimationActivity extends AppCompatActivity {
    private String tag = "PlayAnimationActivity";
    private MazePanel mazePanel;
    private int pathlegnth;
    private int energyConsumed;
    private int distance2Exit;
    private boolean started = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_animation);
        final MazePanel panel = (MazePanel) findViewById(R.id.AnimationMazePanel);
        panel.setManorAni(false);
        pathlegnth = 0;

        final SeekBar mapSize = (SeekBar) findViewById(R.id.seekBarMap);
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

        final SeekBar driverSpeed = (SeekBar) findViewById(R.id.seekBarDriverSpeed);
        driverSpeed.setMin(1);
        driverSpeed.setProgress(10);
        driverSpeed.setMax(20);
        driverSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int speed = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                speed = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.v(tag, "Driver speed changed to "+ speed);
            }
        });

        Button buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (started == false) {
                    buttonStart.setText("Stop Driver");
                    Log.v(tag, "Start driver clicked");
                    started = true;
                }
                else{
                    buttonStart.setText("Start Driver");
                    Log.v(tag, "Stop driver clicked");
                    started = false;
                }
            }
        });

        ToggleButton showMap = (ToggleButton) findViewById(R.id.toggleMap2);
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

        Button Go2Winning = (Button) findViewById(R.id.buttonGo2Winning);
        Go2Winning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(tag, "Go2Winning clicked, jump to the winning state");
                startWinningActivity(this);
            }
        });

        Button Go2Losing = (Button) findViewById(R.id.buttonGo2Losing);
        Go2Losing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(tag, "Go2Losing clicked, jump to the losing state");
                startLosingActivity(this);
            }
        });

    }
    private void startWinningActivity(View.OnClickListener view){
        Intent intent = new Intent(this, WinningActivity.class);
        Bundle bundle = getIntent().getExtras();
        pathlegnth = 30;
        energyConsumed = 1000;
        distance2Exit = 80;
        bundle.putInt("Path Length", pathlegnth);
        bundle.putInt("Energy consumed", energyConsumed);
        bundle.putInt("Distance to exit", distance2Exit);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void startLosingActivity(View.OnClickListener view){
        Intent intent = new Intent(this, LosingActivity.class);
        Bundle bundle = getIntent().getExtras();
        pathlegnth = 20;
        energyConsumed = 3500;
        distance2Exit = 100;
        String reason = "Lack of energy";
        bundle.putInt("Path length", pathlegnth);
        bundle.putInt("Energy consumed", energyConsumed);
        bundle.putInt("Distance to exit", distance2Exit);
        bundle.putString("Lose reason", reason);
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
