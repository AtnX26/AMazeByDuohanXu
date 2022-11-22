package edu.wm.cs.cs301.DuohanXu.gui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import edu.wm.cs.cs301.DuohanXu.R;

/**
 *Fourth class for the activity: playing the maze with robot and animation
 *
 * @author DuohanXu
 */
public class PlayAnimationActivity extends AppCompatActivity {
    private String tag = "PlayAnimationActivity";
    private MazePanel mazePanel;
    private int pathlegnth;
    private int energyConsumed;
    private int distance2Exit;
    private boolean started = false;

    /**
     * Creates the activity by assigning view elements their jobs
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_animation);
        Log.v(tag,"created");

        /**
         * Start drawing process encapsulated in the MazePanel class
         */
        final MazePanel panel = (MazePanel) findViewById(R.id.AnimationMazePanel);
        //Set the boolean to false so that the panel will deliver the image with two polygons
        //and two rectangles.
        panel.setManorAni(false);

        pathlegnth = 0;

        /**
         * The four buttons indicating the status of sensors;
         * Red for broken, green for operational
         * Will implement methods to set up its colors accordingly
         * in P7, now just assigned backgroundTint in xml to show different colors
         */
        //Green ones
        Button sensor1 = (Button) findViewById(R.id.Sensor1);
        Button sensor2 = (Button) findViewById(R.id.Sensor2);
        //Red ones
        Button sensor3 = (Button) findViewById(R.id.Sensor3);
        Button sensor4 = (Button) findViewById(R.id.Sensor4);

        /**
         * Seek bar for adjusting the map size. Communicate with the MazePanel later in P7
         */
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

        /**
         * Seek bar for adjusting the speed of animation. Communicate with the robot later in P7
         */
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

        /**
         * Button for starting or pausing the robot
         * If the robot has stopped, change the text to "Start Driver"
         * If the robot has started, change the text to "Stop Driver"
         * The default value is set to "Start Driver"
         */
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

        /**
         * The toggle for showing up the whole maze with solution and visible walls
         */
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

        /**
         * Short cut to the winning stage
         * Once clicked, use a private method to communicate information
         * with WinningActivity to show messages on the screen
         */
        Button Go2Winning = (Button) findViewById(R.id.buttonGo2Winning);
        Go2Winning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(tag, "Go2Winning clicked, jump to the winning state");
                startWinningActivity(this);
            }
        });

        /**
         * Short cut to the losing stage
         * Once clicked, use a private method to communicate information
         * with LosingActivity to show messages on the screen
         */
        Button Go2Losing = (Button) findViewById(R.id.buttonGo2Losing);
        Go2Losing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(tag, "Go2Losing clicked, jump to the losing state");
                startLosingActivity(this);
            }
        });

        /**
         * Progress bar to show remaining robot energy
         * Set to 0 because of the context that the robot runs out of energy,
         * passing the reason "Lack of energy" to LosingActivity
         * Will communicate with actual robot class in P7
         */
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.robotEnergy);
        progressBar.setMax(100);
        progressBar.setProgress(0);

    }

    /**
     * Private helper method for the short cut to WinningActivity
     * Since no actual information is generated, use preset values to
     * pass to WinningActivity
     * @param view
     */
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

    /**
     * Private helper method for the short cut to LosingActivity
     * Since no actual information is generated, use preset values to
     * pass to LosingActivity
     * @param view
     */
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
