package edu.wm.cs.cs301.DuohanXu.gui;



import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;
import java.util.List;

import edu.wm.cs.cs301.DuohanXu.R;

public class GeneratingActivity extends AppCompatActivity {

    private Handler handler;
    private View view;
    private static int progress;
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private String selectedDriver;
    private String selectedRobot;
    private String tag = "GeneratingActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Not used now, but will work with Project 7
        Bundle bundle = getIntent().getExtras();
        setContentView(R.layout.activity_generating);
        RadioGroup radioDriver = (RadioGroup)findViewById(R.id.radioDriver) ;
        RadioGroup radioRobot = (RadioGroup)findViewById(R.id.radioRobot) ;

        RadioButton rManual = (RadioButton)findViewById(R.id.radioManual);
        RadioButton rWizard = (RadioButton)findViewById(R.id.radioWizard);
        RadioButton rWallFollower = (RadioButton)findViewById(R.id.radioWallFollower);
        RadioButton rPremium = (RadioButton)findViewById(R.id.radioPremium);
        RadioButton rMediocre = (RadioButton)findViewById(R.id.radioMediocre);
        RadioButton rSoso = (RadioButton)findViewById(R.id.radioSoso);
        RadioButton rShaky = (RadioButton)findViewById(R.id.radioShaky);

        Button confirmDriver = (Button) findViewById(R.id.buttondriver);
        confirmDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rManual.isChecked()) {
                    Log.v(tag,"Manual driver selected");
                    selectedDriver = rManual.getText().toString();
                    if (progress<200) Toast.makeText(getBaseContext(), "Driver confirmed, please wait until the maze is generated", Toast.LENGTH_SHORT).show();
                }
                else if (rWizard.isChecked()) {
                    Log.v(tag,"Wizard driver selected");
                    selectedDriver = rWizard.getText().toString();
                    if (progress<200) Toast.makeText(getBaseContext(), "Driver confirmed, please wait until the maze is generated", Toast.LENGTH_SHORT).show();
                }
                else if (rWallFollower.isChecked()) {
                    Log.v(tag,"WallFollower driver selected");
                    selectedDriver = rWallFollower.getText().toString();
                    if (progress<200) Toast.makeText(getBaseContext(), "Driver confirmed, please wait until the maze is generated", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.v(tag,"No driver selected");
                    Toast.makeText(getBaseContext(), "Please confirm a driver!", Toast.LENGTH_SHORT).show();
                }
            }
        });



        Button confirmRobot = (Button) findViewById(R.id.buttonRobot);
        confirmRobot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rPremium.isChecked()) {
                    Log.v(tag,"Premium robot selected");
                    selectedRobot = rPremium.getText().toString();
                    if (progress<200) Toast.makeText(getBaseContext(), "Robot confirmed, please wait until the maze is generated", Toast.LENGTH_SHORT).show();
                }
                else if (rMediocre.isChecked()) {
                    Log.v(tag,"Mediocre robot selected");
                    selectedRobot = rMediocre.getText().toString();
                    if (progress<200) Toast.makeText(getBaseContext(), "Robot confirmed, please wait until the maze is generated", Toast.LENGTH_SHORT).show();
                }
                else if (rSoso.isChecked()) {
                    Log.v(tag,"Soso robot selected");
                    selectedRobot = rSoso.getText().toString();
                    if (progress<200) Toast.makeText(getBaseContext(), "Robot confirmed, please wait until the maze is generated", Toast.LENGTH_SHORT).show();
                }
                else if (rShaky.isChecked()) {
                    Log.v(tag,"Shaky robot selected");
                    selectedRobot = rShaky.getText().toString();
                    if (progress<200) Toast.makeText(getBaseContext(), "Robot confirmed, please wait until the maze is generated", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.v(tag,"No robot selected");
                    Toast.makeText(getBaseContext(), "Please confirm a robot!", Toast.LENGTH_SHORT).show();
                }
            }
        });




        progress = 0;
        progressBar = (ProgressBar) findViewById(R.id.generatingProgress);
        progressBar.setMax(200);
        handler = new Handler(Looper.getMainLooper());
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 200) {
                    progressStatus = doSomeWork();
                    handler.post(() -> progressBar.setProgress(progressStatus));
                }
                Log.v(tag, "Progress bar completed");


            }
            private int doSomeWork() {
                try { // ---simulate doing some work---
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return ++progress;
            }
        }).start();


        Button btn = (Button)findViewById(R.id.StartMaze);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(tag, "Start button clicked");
                if (progress<200) Toast.makeText(getBaseContext(), "Please wait, the maze is generating!", Toast.LENGTH_SHORT).show();
                else if (selectedDriver == null) Toast.makeText(getBaseContext(), "Please select the driver first!", Toast.LENGTH_SHORT).show();
                else if (selectedDriver.equals("Wall Follower")||selectedDriver.equals("Wizard")) {
                    if (selectedRobot == null){Toast.makeText(getBaseContext(), "Please select a robot first!", Toast.LENGTH_SHORT).show();}
                    else{
                    Intent intent = new Intent(getApplicationContext(), PlayAnimationActivity.class);
                    startActivity(intent);}
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), PlayManuallyActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
    @Override
    public void onBackPressed(){
        Log.v(tag, "back button pressed in Generating Activity");
        super.onBackPressed();
    }
    }



