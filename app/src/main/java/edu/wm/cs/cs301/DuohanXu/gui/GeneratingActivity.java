package edu.wm.cs.cs301.DuohanXu.gui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import edu.wm.cs.cs301.DuohanXu.R;

public class GeneratingActivity extends AppCompatActivity {

    private Handler handler;
    private View view;
    private static int progress;
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private RadioGroup radioDriver;
    private RadioButton driverButton;
    private String selectedDriver;
    private String selectedRobot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Not used now, but will work with Project 7
        Bundle bundle = getIntent().getExtras();
        setContentView(R.layout.activity_generating);

        radioDriver = findViewById(R.id.radioDriver);





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
                if (selectedDriver.equals("Wall Follower")||selectedDriver.equals("Wizard")) {
                    Intent intent = new Intent(getApplicationContext(), PlayAnimationActivity.class);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), PlayManuallyActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
    }



