package edu.wm.cs.cs301.DuohanXu.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import edu.wm.cs.cs301.DuohanXu.R;

public class AMazeActivity extends AppCompatActivity {
    private Intent intent;

    private int skilllevel;
    private int seed;
    private String tag = "AMazeActivity";
    private Spinner spinner;
    private Spinner roomSpinner;


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
        spinner = setSpinner();
        roomSpinner = setRoomSpinner();
        Button revisitButton = (Button) findViewById(R.id.Revisit);
        revisitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GeneratingActivity.class);
                Bundle bundle = new Bundle();
                Log.v(tag, "Revisit button pressed, proceed to the next activity");
                /*
                Put information from the last game into the bundle and pass it to the next activity
                 */
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        Button exploreButton = (Button) findViewById(R.id.Explore);
        exploreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GeneratingActivity.class);
                /*
                Record information from this user interface
                 */
                Log.v(tag, "Explore button pressed, proceed to the next activity");
                Bundle bundle = new Bundle();
                /*
                Put information from the last game into the bundle and pass it to the next activity
                 */
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private Spinner setSpinner(){
        List<String> mazeAlgorithms = new ArrayList<String>();
        mazeAlgorithms.add("DFS");
        mazeAlgorithms.add("Prim");
        mazeAlgorithms.add("Boruvka");

        Spinner spinner = (Spinner) findViewById(R.id.builderSpinner);
        ArrayAdapter<String> mazeSelectAdapter = new ArrayAdapter <String>(this, android.R.layout.simple_spinner_item, mazeAlgorithms);
        spinner.setAdapter(mazeSelectAdapter);
        spinner.setSelection(0,true);
        mazeSelectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v(tag, "Selected " + spinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return spinner;
    }

    private Spinner setRoomSpinner(){
        List<String> roomsNoRooms = new ArrayList<String>();
        roomsNoRooms.add("Yes");
        roomsNoRooms.add("No");

        Spinner roomSpinner = (Spinner) findViewById(R.id.roomSpinner);
        ArrayAdapter<String> roomAdapter = new ArrayAdapter <String>(this,
                android.R.layout.simple_spinner_item, roomsNoRooms);
        roomSpinner.setAdapter(roomAdapter);
        roomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v(tag, "Selected " + roomSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return roomSpinner;
    }
}