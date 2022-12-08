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

/**
 *Third class for the activity: playing the maze manually
 *
 * @author DuohanXu
 */

public class PlayManuallyActivity extends AppCompatActivity {
    private String tag = "PlayManuallyActivity";
    private static final int MAX_MAP_SIZE = 80;  //max size that the map can be
    private static final int MIN_MAP_SIZE = 1;  //min size that the map can be
    private MazePanel mazePanel;
    private int pathlegnth;
    private int mapSize = 15;
    StatePlaying statePlaying;
    /**
     * Creates the activity by assigning view elements their jobs
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_manually);
        Log.v(tag,"created");

        /**
         * Start drawing process encapsulated in the MazePanel class
         */
        final MazePanel panel = (MazePanel) findViewById(R.id.ManualMazePanel);
        //Set the boolean to true so that the panel will deliver the image with a ball and
        //two rectangles.
        panel.setManorAni(true);
        Log.v(tag,"MazePanel started");

        pathlegnth = 0;


        /**
         * Four navigation buttons below: the player can use these buttons to operate
         * the maze game by going forward, turning to the left or right, and jump over
         * the wall
         */
        Button buttonLeft = (Button) findViewById(R.id.buttonLeft);
        buttonLeft.setOnClickListener(new View.OnClickListener() {
            //Create new event when clicked; only shows log for P6
            @Override
            public void onClick(View v) {
                statePlaying.handleUserInput(Constants.UserInput.LEFT, 1);
                Log.v(tag, "left clicked");
            }
        });

        Button buttonRight = (Button) findViewById(R.id.buttonRight);
        buttonRight.setOnClickListener(new View.OnClickListener() {
            //Create new event when clicked; only shows log for P6
            @Override
            public void onClick(View v) {
                statePlaying.handleUserInput(Constants.UserInput.RIGHT, 1);
                Log.v(tag, "right clicked");
            }
        });

        Button buttonForward = (Button) findViewById(R.id.buttonForward);
        buttonForward.setOnClickListener(new View.OnClickListener() {
            //Create new event when clicked; only shows log for P6
            @Override
            public void onClick(View v) {
                statePlaying.handleUserInput(Constants.UserInput.UP, 1);
                Log.v(tag, "forward clicked");
                pathlegnth++;
            }
        });

        Button buttonJump = (Button) findViewById(R.id.buttonJump);
        buttonJump.setOnClickListener(new View.OnClickListener() {
            //Create new event when clicked; only shows log for P6
            @Override
            public void onClick(View v) {
                statePlaying.handleUserInput(Constants.UserInput.JUMP, 1);
                Log.v(tag, "jump clicked");
                pathlegnth++;
            }
        });

        /**
         * The short cut to the WinningActivity
         */
        Button ShortCut = (Button) findViewById(R.id.ShortCut);
        ShortCut.setOnClickListener(new View.OnClickListener() {
            //Create new event when clicked; only shows log for P6
            @Override
            public void onClick(View v) {
                Log.v(tag, "ShortCut clicked, jump to the winning state");
                startWinningActivity(this);
            }
        });

        /**
         * Three toggles that adjust the visibility of maze ,etc.
         */
        ToggleButton showMap = (ToggleButton) findViewById(R.id.toggleMap);
        showMap.setOnClickListener(new View.OnClickListener() {
            //Create new event when clicked; only shows log for P6
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
            //Create new event when clicked; only shows log for P6
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
            //Create new event when clicked; only shows log for P6
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

        /**
         * Seek bar for adjusting the map size. Communicate with the MazePanel later in P7
         */
        final SeekBar mapSize = (SeekBar) findViewById(R.id.seekBarMapSize);
        mapSize.setMin(MIN_MAP_SIZE);
        mapSize.setProgress(15);
        mapSize.setMax(MAX_MAP_SIZE);
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
                setMapSize(MapSize);
                Log.v(tag, "Map size changed to "+ MapSize);
            }
        });
    }

    private void setMapSize(int size){
        mapSize = size;
        statePlaying.setMapScale(mapSize);
        //Log.v("Map Size: " + mapSize);
    }
    /**
     * Private helper method for the short cut
     * @param view
     */
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

    /**
     * Navigate back to the title page once the back button is clicked
     */
    @Override
    public void onBackPressed(){
        Log.v(tag, "back button pressed, going back to AMazeActivity");
        Intent intent = new Intent(getApplicationContext(), AMazeActivity.class);
        startActivity(intent);

    };
}
