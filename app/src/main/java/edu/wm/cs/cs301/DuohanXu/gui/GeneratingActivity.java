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
import android.widget.TextView;
import android.widget.Toast;

import edu.wm.cs.cs301.DuohanXu.generation.Maze;
import edu.wm.cs.cs301.DuohanXu.generation.MazeFactory;

import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;
import java.util.List;

import edu.wm.cs.cs301.DuohanXu.R;
import edu.wm.cs.cs301.DuohanXu.generation.Order;

/**
 *Second class for the activity: loading page of the maze app
 *
 * @author DuohanXu
 */
public class GeneratingActivity extends AppCompatActivity implements Order {

    private Handler handler;
    private View view;
    private static int progress;
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private int percentdone;
    private int seed = 0;
    private int skillLevel;
    private boolean rooms;
    private String selectedDriver=null;
    private String selectedRobot;
    private String tag = "GeneratingActivity";
    protected MazeFactory factory;
    private Order.Builder builder;
    private Maze maze;
    /**
     * Creates the activity by assigning view elements their jobs
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Not used now, but will work with Project 7
        Bundle bundle = getIntent().getExtras();
        setContentView(R.layout.activity_generating);
        Log.v(tag,"created");
        readBundle(bundle);
        factory.order(this);

        /**
         * Set up a bunch of RadioGroup and RadioButton objects
         */
        RadioGroup radioDriver = (RadioGroup)findViewById(R.id.radioDriver) ;
        RadioGroup radioRobot = (RadioGroup)findViewById(R.id.radioRobot) ;

        RadioButton rManual = (RadioButton)findViewById(R.id.radioManual);
        RadioButton rWizard = (RadioButton)findViewById(R.id.radioWizard);
        RadioButton rWallFollower = (RadioButton)findViewById(R.id.radioWallFollower);
        RadioButton rPremium = (RadioButton)findViewById(R.id.radioPremium);
        RadioButton rMediocre = (RadioButton)findViewById(R.id.radioMediocre);
        RadioButton rSoso = (RadioButton)findViewById(R.id.radioSoso);
        RadioButton rShaky = (RadioButton)findViewById(R.id.radioShaky);
        TextView text = (TextView) findViewById(R.id.textView16);
        progressBar = (ProgressBar) findViewById(R.id.generatingProgress);

        /**
         * Set up a button to confirm selection.
         * The player has to press this button to complete selection on the RadioButton for drivers;
         * otherwise, no input will be recorded.
         */
        Button confirmDriver = (Button) findViewById(R.id.buttondriver);
        confirmDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rManual.isChecked()) {
                    Log.v(tag,"Manual driver selected");
                    selectedDriver = rManual.getText().toString();
                    if (progress<100) Toast.makeText(getBaseContext(), "Driver confirmed, please wait until the maze is generated", Toast.LENGTH_SHORT).show();
                }
                else if (rWizard.isChecked()) {
                    Log.v(tag,"Wizard driver selected");
                    selectedDriver = rWizard.getText().toString();
                    if (progress<100) Toast.makeText(getBaseContext(), "Driver confirmed, please wait until the maze is generated", Toast.LENGTH_SHORT).show();
                }
                else if (rWallFollower.isChecked()) {
                    Log.v(tag,"WallFollower driver selected");
                    selectedDriver = rWallFollower.getText().toString();
                    if (progress<100) Toast.makeText(getBaseContext(), "Driver confirmed, please wait until the maze is generated", Toast.LENGTH_SHORT).show();
                }
                else {
                    //Pops up a message to remind the player to select a driver
                    Log.v(tag,"No driver selected");
                    Toast.makeText(getBaseContext(), "Please select a driver!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        /**
         * Set up a button to confirm selection.
         * The player has to press this button to complete selection on the RadioButton for robots;
         * otherwise, no input will be recorded.
         */
        Button confirmRobot = (Button) findViewById(R.id.buttonRobot);
        confirmRobot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rPremium.isChecked()) {
                    Log.v(tag,"Premium robot selected");
                    selectedRobot = rPremium.getText().toString();
                    if (progress<100) Toast.makeText(getBaseContext(), "Robot confirmed, please wait until the maze is generated", Toast.LENGTH_SHORT).show();
                }
                else if (rMediocre.isChecked()) {
                    Log.v(tag,"Mediocre robot selected");
                    selectedRobot = rMediocre.getText().toString();
                    if (progress<100) Toast.makeText(getBaseContext(), "Robot confirmed, please wait until the maze is generated", Toast.LENGTH_SHORT).show();
                }
                else if (rSoso.isChecked()) {
                    Log.v(tag,"Soso robot selected");
                    selectedRobot = rSoso.getText().toString();
                    if (progress<100) Toast.makeText(getBaseContext(), "Robot confirmed, please wait until the maze is generated", Toast.LENGTH_SHORT).show();
                }
                else if (rShaky.isChecked()) {
                    Log.v(tag,"Shaky robot selected");
                    selectedRobot = rShaky.getText().toString();
                    if (progress<100) Toast.makeText(getBaseContext(), "Robot confirmed, please wait until the maze is generated", Toast.LENGTH_SHORT).show();
                }
                else {
                    //Pops up a message to remind the player to select a driver
                    Log.v(tag,"No robot selected");
                    Toast.makeText(getBaseContext(), "Please select a robot!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        /**
         * The progress bar for showing the progress of generating the maze.
         * In P6, implement a thread that imitates the generating process instead.
         */
        progress = 0;

        progressBar.setMax(100);
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                Bundle bundle = msg.getData();
                int progressBarMessage = bundle.getInt("progress");
                Log.v(tag, "Loading bar: " + progressBar.toString());
            }
        };
        /**
         * The thread that updates the progress bar every 50 ms.
         * Stops when the progress reaches 100. (Test thread)
         *
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus = doSomeWork();
                    handler.post(() -> progressBar.setProgress(progressStatus));
                }
                Log.v(tag, "Progress bar completed");
                /**
                 * If the maze is generated before the player makes a selection of the driver:
                 * Show a message as text above the start button.
                 *
                if (selectedDriver == null){

                    handler.post(() -> text.setText("Maze generated, don't forget to pick a driver!"));
                    Log.v(tag, "A reminder pops up");
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
        }).start();/

        /**
         * Set up the button to start playing the maze.
         * Read attributes from the parent class and determine whether PlayManuallyActivity
         * or PlayAnimationActivity will be called up.
         */
        Button btn = (Button)findViewById(R.id.StartMaze);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(tag, "Start button clicked");
                /**
                 * If clicked before the maze is delivered, pops up a message to remind the player.
                 */
                if (progress<100) {
                    Toast.makeText(getBaseContext(), "Please wait, the maze is generating!", Toast.LENGTH_SHORT).show();
                    Log.v(tag, "Waiting message popped up");
                }
                /**
                 * If the maze generation is completed but the driver has yet to be selected, pop up another message
                 */
                else if (selectedDriver == null) {
                    Toast.makeText(getBaseContext(), "Please select the driver first!", Toast.LENGTH_SHORT).show();
                    Log.v(tag, "Selection reminder popped up");
                }
                /**
                 * Handles scenarios in which PlayAnimationActivity will be called
                 */
                else if (selectedDriver.equals("Wall Follower")||selectedDriver.equals("Wizard")) {
                    if (selectedRobot == null){
                        Toast.makeText(getBaseContext(), "Please select a robot first!", Toast.LENGTH_SHORT).show();
                        Log.v(tag, "Selection reminder popped up");}
                    else{
                    Intent intent = new Intent(getApplicationContext(), PlayAnimationActivity.class);
                    bundle.putString("Driver", selectedDriver);
                    bundle.putString("Robot", selectedRobot);
                    intent.putExtras(bundle);
                    startActivity(intent);}
                }
                /**
                 * Handles scenarios in which PlayManuallyActivity will be called
                 */
                else {
                    Intent intent = new Intent(getApplicationContext(), PlayManuallyActivity.class);
                    bundle.putString("Driver", selectedDriver);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     *
     */
    private void readBundle(Bundle bundle){
        factory = new MazeFactory();
        switch(bundle.getString("Algorithm")){
            case "DFS":
                builder = Builder.DFS;
                break;
            case "Prim":
                builder = Builder.Prim;
                break;
            case "Boruvka":
                builder = Builder.Boruvka;
                break;

        }
        skillLevel = bundle.getInt("Complexity");
        String ifRooms = bundle.getString("Rooms");
        if (ifRooms == "Yes"){rooms = true}
        else {rooms = false}
        if(!deterministic&& revisit==false){
            Random rand = new Random();
            seed = rand.nextInt();
        }
        Revisit.setRevisit(skillLevel,bundle.getString("Maze Generator"),rooms,seed);
        if(revisit == true){
            seed = Revisit.getSeed();
        }
    }
    /**
     * Go back to the title
     */
    @Override
    public void onBackPressed(){
        Log.v(tag, "back button pressed in Generating Activity");
        factory.cancel();
        super.onBackPressed();
    }


    /**
     * Gives the required skill level, range of values 0,1,2,...,15.
     *
     * @return the skill level or size of maze to be generated in response to an order
     */
    @Override
    public int getSkillLevel() {
        return skillLevel;
    }

    /**
     * Gives the requested builder algorithm, possible values
     * are listed in the Builder enum type.
     *
     * @return the builder algorithm that is expected to be used for building the maze
     */
    @Override
    public Builder getBuilder() {
        return builder;
    }

    /**
     * Describes if the ordered maze should be perfect, i.e. there are
     * no loops and no isolated areas, which also implies that
     * there are no rooms as rooms can imply loops
     *
     * @return true if a perfect maze is wanted, false otherwise
     */
    @Override
    public boolean isPerfect() {
        return !rooms;
    }

    /**
     * Gives the seed that is used for the random number generator
     * used during the maze generation.
     *
     * @return the current setting for the seed value of the random number generator
     */
    @Override
    public int getSeed() {
        return seed;
    }

    /**
     * Delivers the produced maze.
     * This method is called by the factory to provide the
     * resulting maze as a MazeConfiguration.
     * It is a call back function that is called some time
     * later in response to a client's call of the order method.
     *
     * @param mazeConfig is the maze that is delivered in response to an order
     */
    @Override
    public void deliver(Maze mazeConfig) {
        this.maze = mazeConfig;
    }

    /**
     * Provides an update on the progress being made on
     * the maze production. This method is called occasionally
     * during production, there is no guarantee on particular values.
     * Percentage will be delivered in monotonously increasing order,
     * the last call is with a value of 100 after delivery of product.
     *
     * @param percentage of job completion
     */
    @Override
    public void updateProgress(int percentage) {
        Log.v(tag, "Updating progress loaded to " + percentage);
        if (this.percentdone < percentage && percentage <= 100) {
            this.percentdone = percentage;
            progressBar.setProgress(percentdone);
        }
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putInt("progress", this.percentdone);
        message.setData(bundle);
        handler.sendMessage(message);
    }
}



