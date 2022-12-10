package edu.wm.cs.cs301.DuohanXu.gui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import edu.wm.cs.cs301.DuohanXu.R;

/**
 *Sixth class for the activity: oops, you lose...
 *
 * @author DuohanXu
 */
public class LosingActivity extends AppCompatActivity {
    private String tag = "LosingActivity";
    private String reason;
    private int pathlegnth;
    private int energyconsumed;
    private int distance2exit;

    /**
     * Creates the activity by assigning view elements their jobs
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_losing);
        Log.v(tag,"created");

        /**
         * Load information from the bundle to set on the screen
         */
        Bundle bundle = getIntent().getExtras();
        pathlegnth = DataContainer.getPathlength();
        energyconsumed = DataContainer.getEnergyConsumption();
        distance2exit = PlayManuallyActivity.shortestPathLength;
        if (energyconsumed >=3500){
            reason="Lost Reason: Lack of energy";
        }
        else{
        reason="Lost Reason: Robot is broken";
        }
        /**
         * Connect texts in xml to LosingActivity that show message indicating playing result
         */
        TextView pathView = (TextView) findViewById(R.id.pathView);
        TextView energyView = (TextView) findViewById(R.id.energyView);
        TextView distanceView = (TextView) findViewById(R.id.distanceView);
        TextView reasonView = (TextView) findViewById(R.id.reasonView);

        pathView.setText("Path Length: " + pathlegnth);
        distanceView.setText("Shortest distance from entrance to exit: " + distance2exit);
        energyView.setText("Energy consumed: " + energyconsumed);
        reasonView.setText("Reason of failure: " + reason);

        /**
         * Set up a button for the player to go back to the first page
         */
        Button back = (Button) findViewById(R.id.buttonTryAgain);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.v(tag, "Going back to title page");
                Intent back2Title = new Intent(getApplicationContext(), AMazeActivity.class);
                startActivity(back2Title);
            }
        });

        /**
         * Vibrate when losing the game
         */
        Vibrator v= (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(500);
        }

    }

    /**
     * Navigate back to the title page once the back button is clicked
     */
    @Override
    public void onBackPressed(){
        Log.v(tag, "back button pressed in WinningActivity");
        Intent intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);
    }
}
