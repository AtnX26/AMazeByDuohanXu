package edu.wm.cs.cs301.DuohanXu.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;

import edu.wm.cs.cs301.DuohanXu.R;

public class AMazeActivity extends AppCompatActivity {
    private Intent intent;

    private int skilllevel;
    private int seed;
    private String tag = "AMazeActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amaze);
        Log.v(tag,"created");

        //Control for the seekbar to adjust complexity
        SeekBar seekbar = findViewById(R.id.complexity);
        seekbar.setMax(9);
        seekbar.setProgress(0);
        int seekBarValue= seekbar.getProgress();
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int passedValue = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                passedValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.v(tag, "Maze level is :" + passedValue);

            }
        });


    }


}