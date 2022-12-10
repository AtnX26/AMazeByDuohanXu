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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.wm.cs.cs301.DuohanXu.R;

/**
 *Initial class for the activity: home of the maze app
 *
 * @author DuohanXu
 */

public class AMazeActivity extends AppCompatActivity {
    private Intent intent;
    private int skilllevel;
    private int seed = 1;
    private String tag = "AMazeActivity";
    private Spinner spinner;
    private Spinner roomSpinner;
    private boolean Room;
    private String Algorithm;

    /**
     * Creates the activity by assigning view elements their jobs
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amaze);
        Log.v(tag,"created");

        /**
         * Control for the seekbar to adjust complexity
         */
        SeekBar seekbar = findViewById(R.id.complexity);
        seekbar.setMax(9);
        seekbar.setProgress(0);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int passedValue = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                passedValue = progress;
                skilllevel = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.v(tag, "Maze level is :" + passedValue);

            }
        });

        /**
         * Use two private methods below to set up two spinners
         */
        spinner = setSpinner();
        roomSpinner = setRoomSpinner();

        /**
         * Set up revisitButton, creating event when clicked
         */
        Button revisitButton = (Button) findViewById(R.id.Revisit);
        revisitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RevisitData.getSeed() == 0){
                Toast.makeText(getApplicationContext(), "No data from last exploration!", Toast.LENGTH_SHORT).show();}
                else{
                Intent intent = new Intent(getApplicationContext(), GeneratingActivity.class);
                Bundle bundle = new Bundle();
                Log.v(tag, "Revisit button pressed, proceed to the next activity");
                /**
                Put information from the last game into the bundle and pass it to the next activity
                 */
                //A default seed is used here; should get the seed from the container in P7
                bundle.putInt("Seed", seed);
                bundle.putInt("Complexity",RevisitData.getSkillLevel());
                bundle.putString("Algorithm",RevisitData.getAlgorithm());
                bundle.putBoolean("Room", RevisitData.getRoom());
                bundle.putBoolean("Revisit", true);
                intent.putExtras(bundle);
                startActivity(intent);}
            }
        });

        /**
         * Set up exploreButton, creating event when clicked
         */
        Button exploreButton = (Button) findViewById(R.id.Explore);
        exploreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GeneratingActivity.class);
                /**
                Record information from this user interface
                 */
                Log.v(tag, "Explore button pressed, proceed to the next activity");
                Bundle bundle = new Bundle();
                /**
                 * Make everything new to the bundle, will use a random method to set up the
                 * seed in P7
                 */
                if(roomSpinner.getSelectedItem().toString().equals("Yes")){
                    Room = true;
                } else{
                    Room = false;
                }
                Algorithm = spinner.getSelectedItem().toString();
                DataContainer.setMazeAlgorithm(Algorithm);
                //Now just give it a new seed
                int newSeed = 3;
                DataContainer.setSkillLevel(skilllevel);
                bundle.putInt("Seed", newSeed);
                bundle.putInt("Complexity",seekbar.getProgress());
                bundle.putString("Algorithm",spinner.getSelectedItem().toString());
                bundle.putString("Room", roomSpinner.getSelectedItem().toString());
                bundle.putBoolean("Revisit", false);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    /**
     * The private method for algorithm spinner
     * @return Spinner
     */
    private Spinner setSpinner(){
        List<String> mazeAlgorithms = new ArrayList<String>();
        mazeAlgorithms.add("DFS");
        mazeAlgorithms.add("Prim");
        mazeAlgorithms.add("Boruvka");

        /**
         * Use an ArrayAdapter object to assign the spinner options
         * Read the selection once the player makes an input
         */
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

    /**
     * The private method for room spinner
     * @return Spinner
     */
    private Spinner setRoomSpinner(){
        List<String> roomsNoRooms = new ArrayList<String>();
        roomsNoRooms.add("Yes");
        roomsNoRooms.add("No");

        /**
         * Use an ArrayAdapter object to assign the spinner options
         * Read the selection once the player makes an input
         */
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