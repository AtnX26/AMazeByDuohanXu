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

public class WinningActivity extends AppCompatActivity {
    private String tag = "WinningActivity";
    private int pathlegnth;
    private int energyconsumed;
    private int distance2exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winning);
        Log.v(tag,"created");

        Bundle bundle = getIntent().getExtras();
        pathlegnth = bundle.getInt("Path length");
        energyconsumed = bundle.getInt("Energy consumed");
        distance2exit = bundle.getInt("Distance to exit");

        TextView pathView = (TextView) findViewById(R.id.pathLengthView);
        TextView energyView = (TextView) findViewById(R.id.energyConsumedView);
        TextView distanceView = (TextView) findViewById(R.id.distance2ExitView);

        pathView.setText("Path Length: " + pathlegnth);
        distanceView.setText("Shortest distance from entrance to exit: " + distance2exit);
        if (energyconsumed != -1){
            energyView.setText("Energy consumed: " + energyconsumed);
        }

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

    @Override
    public void onBackPressed(){
        Log.v(tag, "back button pressed in WinningActivity");
        Intent intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);
    }

}
