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

public class LosingActivity extends AppCompatActivity {
    private String tag = "LosingActivity";
    private String reason;
    private int pathlegnth;
    private int energyconsumed;
    private int distance2exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_losing);
        Bundle bundle = getIntent().getExtras();
        pathlegnth = bundle.getInt("Path length");
        energyconsumed = bundle.getInt("Energy consumed");
        distance2exit = bundle.getInt("Distance to exit");
        reason = bundle.getString("Lose reason");

        TextView pathView = (TextView) findViewById(R.id.pathView);
        TextView energyView = (TextView) findViewById(R.id.energyView);
        TextView distanceView = (TextView) findViewById(R.id.distanceView);
        TextView reasonView = (TextView) findViewById(R.id.reasonView);

        pathView.setText("Path Length: " + pathlegnth);
        distanceView.setText("Shortest distance from entrance to exit: " + distance2exit);
        energyView.setText("Energy consumed: " + energyconsumed);
        reasonView.setText("Reason of failure: " + reason);

        Button back = (Button) findViewById(R.id.buttonTryAgain);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.v(tag, "Going back to title page");
                Intent back2Title = new Intent(getApplicationContext(), AMazeActivity.class);
                startActivity(back2Title);
            }
        });

        Vibrator v= (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(500);
        }

    }

    @Override
    public void onBackPressed(){
        Log.v(tag, "back button pressed in WinningActivity");
        Intent intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);
    }
}
