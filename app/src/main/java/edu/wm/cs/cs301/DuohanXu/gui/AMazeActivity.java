package edu.wm.cs.cs301.DuohanXu.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.SeekBar;

import edu.wm.cs.cs301.DuohanXu.R;

public class AMazeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amaze);

        SeekBar seekbar = (SeekBar) findViewById(R.id.seekbar);
    }
}