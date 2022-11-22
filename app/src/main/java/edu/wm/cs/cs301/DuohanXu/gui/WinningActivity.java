package edu.wm.cs.cs301.DuohanXu.gui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import edu.wm.cs.cs301.DuohanXu.R;

/**
 *Fifth class for the activity: win the game!
 *
 * @author DuohanXu
 */
public class WinningActivity extends AppCompatActivity {
    private String tag = "WinningActivity";
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
        setContentView(R.layout.activity_winning);
        Log.v(tag,"created");

        /**
         * Load information from the bundle to set on the screen
         */
        Bundle bundle = getIntent().getExtras();
        pathlegnth = bundle.getInt("Path length");
        energyconsumed = bundle.getInt("Energy consumed");
        distance2exit = bundle.getInt("Distance to exit");

        /**
         * Connect texts in xml to WinningActivity that show message indicating playing result
         */
        TextView pathView = (TextView) findViewById(R.id.pathLengthView);
        TextView energyView = (TextView) findViewById(R.id.energyConsumedView);
        TextView distanceView = (TextView) findViewById(R.id.distance2ExitView);

        pathView.setText("Path Length: " + pathlegnth);
        distanceView.setText("Shortest distance from entrance to exit: " + distance2exit);
        if (energyconsumed != -1){
            energyView.setText("Energy consumed: " + energyconsumed);
        }

        /**
         * Set up a button for the player to go back to the first page
         */
        Button back = (Button) findViewById(R.id.buttonBack);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.v(tag, "Going back to title page");
                Intent back2Title = new Intent(getApplicationContext(), AMazeActivity.class);
                startActivity(back2Title);
            }
        });

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
