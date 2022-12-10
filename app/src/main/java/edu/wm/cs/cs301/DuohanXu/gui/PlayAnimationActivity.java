package edu.wm.cs.cs301.DuohanXu.gui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import edu.wm.cs.cs301.DuohanXu.R;
import edu.wm.cs.cs301.DuohanXu.generation.Maze;
import edu.wm.cs.cs301.DuohanXu.gui.Robot.Direction;
/**
 *Fourth class for the activity: playing the maze with robot and animation
 *
 * @author DuohanXu
 */
public class PlayAnimationActivity extends AppCompatActivity {
    private String tag = "PlayAnimationActivity";
    private static final String KEY = "my message key";
    private static final String FAILURE_KEY = "sensor has failed";
    private MazePanel panel;
    private static final int MAX_MAP_SIZE = 80;  //max size that the map can be
    private static final int MIN_MAP_SIZE = 1;  //min size that the map can be
    private int pathlegnth;
    private int energyConsumed;
    private int distance2Exit;
    private int mapSize = 15;
    private int animationSpeed = 10;
    private boolean started = false;
    StatePlaying statePlaying;
    boolean[] currentSensors;
    boolean[] reliableSensor;
    public static Handler myHandler;
    private ProgressBar remainingEnergy;
    private TextView remainingEnergyText;
    private Button sensor1,sensor2,sensor3,sensor4;
    private boolean isWizard = false;  // tells if the driver is the wizard algorithm
    private Maze maze;
    private int shortestPathLength;
    private String sensorConfig;

    private DistanceSensor leftSensor;  // robot's left sensor
    private DistanceSensor rightSensor;  // robot's right sensor
    private DistanceSensor frontSensor;  // robot's front sensor
    private DistanceSensor backSensor;  // robot's back sensor
    private Robot robot;
    private Wizard wizard;
    private RobotDriver wallFollower;

    private static final int MEAN_TIME_BETWEEN_FAILURES = 4000;  // average time between sensor failures
    private static final int MEAN_TIME_TO_REPAIR = 2000;  // average time to repair sensor failures
    /**
     * Creates the activity by assigning view elements their jobs
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_animation);
        Log.v(tag, "created");

        maze = GeneratingActivity.maze;
        int[] startPos = maze.getStartingPosition();
        shortestPathLength = maze.getDistanceToExit(startPos[0], startPos[1]) - 1;
        /**
         * Start drawing process encapsulated in the MazePanel class
         */
        remainingEnergyText = (TextView) findViewById(R.id.textView10);
        sensor1 = (Button) findViewById(R.id.Sensor1);
        sensor2 = (Button) findViewById(R.id.Sensor2);
        sensor3 = (Button) findViewById(R.id.Sensor3);
        sensor4 = (Button) findViewById(R.id.Sensor4);

        setProgressBar();

        sensorConfig = DataContainer.getRobotConfig();
        currentSensors = new boolean[]{true, true, true, true};
        setRobotSensors(sensorConfig);

        statePlaying = new StatePlaying();
        panel = (MazePanel) findViewById(R.id.AnimationMazePanel);
        //Set the boolean to false so that the panel will deliver the image with two polygons
        //and two rectangles.
        statePlaying.setPlayAnimationActivity(this);
        statePlaying.start(panel);

        pathlegnth = 0;

        String driver = DataContainer.getDriverConfig();
        if(driver.equals("Wizard")){
            setWizardPlaying();
        }
        else{
            setWallFollowerPlaying();
        }
        setSizeOfMap();
        setAnimationSpeed();
        /**
         * The four buttons indicating the status of sensors;
         * Red for broken, green for operational
         * Will implement methods to set up its colors accordingly
         * in P7, now just assigned backgroundTint in xml to show different colors
         */


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
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.v(tag, "Map size changed to " + MapSize);
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
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.v(tag, "Driver speed changed to " + speed);
            }
        });

        /**
         * Button for starting or pausing the robot
         * If the robot has stopped, change the text to "Start Driver"
         * If the robot has started, change the text to "Stop Driver"
         * The default value is set to "Start Driver"
         */
        ToggleButton playPauseButton = (ToggleButton) findViewById(R.id.buttonStart);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playPauseButton.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Pause", Toast.LENGTH_SHORT).show();
                    Log.v("Pause toast", "Pressed Pause");
                    if(isWizard){
                        wizard.terminateThread();
                    }
                    else{
                        wallFollower.terminateThread();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Start", Toast.LENGTH_SHORT).show();
                    Log.v("Start toast", "Pressed Start");
                    if(isWizard){
                        try{
                            wizard.drive2Exit();
                        }
                        catch(Exception e){
                            sendLosingMessage(panel);
                        }
                    }
                    else{
                        try{

                            setWallFollowerPlaying();
                        }
                        catch(Exception e){
                            sendLosingMessage(panel);
                        }
                    }
                }
            }
        });

        myHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                Bundle bundle = msg.getData();
                int remainingEnergyMessage = bundle.getInt(KEY, -1);
                boolean status1 = bundle.getBoolean("front");
                boolean status2 = bundle.getBoolean("back");
                boolean status3 = bundle.getBoolean("left");
                boolean status4 = bundle.getBoolean("right");
                String[] sensorInfo = bundle.getStringArray(FAILURE_KEY);
                if(remainingEnergyMessage != -1){
                    if (isWizard){
                    updateProgressBar(remainingEnergyMessage);}
                    else{
                        updateProgressBar(remainingEnergyMessage);
                        updateSensorStatus(status1,1);
                        updateSensorStatus(status2,2);
                        updateSensorStatus(status3,3);
                        updateSensorStatus(status4,4);
                    }
                }
                Log.v(tag, remainingEnergyMessage + "");
                boolean lostGame = bundle.getBoolean("lost", false);
                if(lostGame){
                    sendLosingMessage(panel);
                }
            }
        };
        /**
         * The toggle for showing up the whole maze with solution and visible walls
         */
        ToggleButton showMap = (ToggleButton) findViewById(R.id.toggleMap2);
        showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showMap.isChecked() == false) {
                    statePlaying.handleUserInput(Constants.UserInput.TOGGLELOCALMAP, 1);
                    Log.v(tag, "Map show off");
                } else {
                    statePlaying.handleUserInput(Constants.UserInput.TOGGLELOCALMAP, 1);
                    Log.v(tag, "Map show on");
                }
            }
        });


    }

    /////////private methods below/////////


    private void setProgressBar(){
        remainingEnergy = (ProgressBar) findViewById(R.id.robotEnergy);
        remainingEnergy.setMax(3500);
        remainingEnergy.setProgress(3500);

        remainingEnergyText.setText("Remaining Energy: " + remainingEnergy.getProgress());
    }

    private void updateProgressBar(int remainingEnergy){
        final TextView remainingEnergyText = (TextView) findViewById(R.id.textView10);
        this.remainingEnergy.setProgress(remainingEnergy);
        remainingEnergyText.setText("Remaining Energy: " + this.remainingEnergy.getProgress());
    }
    private void updateSensorStatus(boolean Status, int i){
        switch (i){
            case 1:
                if (Status) {sensor1.setTextColor(Color.GREEN);}
                else {sensor1.setTextColor(Color.RED);}
                break;
            case 2:
                if (Status) {sensor2.setTextColor(Color.GREEN);}
                else {sensor2.setTextColor(Color.RED);}
                break;
            case 3:
                if (Status) {sensor3.setTextColor(Color.GREEN);}
                else {sensor3.setTextColor(Color.RED);}
                break;
            case 4:
                if (Status) {sensor4.setTextColor(Color.GREEN);}
                else {sensor4.setTextColor(Color.RED);}
                break;
        }
    }

    private void setAnimationSpeed(){
        final SeekBar animationSpeed1 = (SeekBar) findViewById(R.id.seekBarDriverSpeed);
        //final TextView skillLevelText = (TextView) findViewById(R.id.skillLevelTextView);
        animationSpeed1.setMax(20);
        animationSpeed1.setProgress(animationSpeed);
        animationSpeed1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int tempAnimationSpeed = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tempAnimationSpeed = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setTheAnimationSpeed(tempAnimationSpeed);
            }
        });
    }
    private void setTheAnimationSpeed(int speed){
        animationSpeed = speed;
        if(wizard!= null){
            wizard.setAnimationSpeed(animationSpeed);
        }
        else{
            wallFollower.setAnimationSpeed(animationSpeed);
        }
        Log.v(tag, "Animation Speed: " + animationSpeed);
    }
    private void setSizeOfMap(){
        final SeekBar mapSize1 = (SeekBar) findViewById(R.id.seekBarMap);
        //final TextView skillLevelText = (TextView) findViewById(R.id.skillLevelTextView);
        mapSize1.setMin(1);
        mapSize1.setMax(MAX_MAP_SIZE);
        mapSize1.setProgress(mapSize);
        mapSize1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int tempMapSize = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tempMapSize = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setMapSize(tempMapSize);
            }
        });
    }
    private void setMapSize(int size){
        mapSize = size;
        statePlaying.setMapScale(mapSize);
        Log.v(tag, "Map Size: " + mapSize);
    }

    private void setWizardPlaying() {
        Log.v(tag, "setting Wizard as Animated Driver");
        isWizard = true;
        ReliableRobot robot = new ReliableRobot();
        wizard = new Wizard();
        robot.setStatePlaying(statePlaying);
        robot.setPlayAnimationActivity(this);
        wizard.setRobot(robot);
        wizard.setMaze(maze);
        try {
            boolean wizard1 = wizard.drive2Exit();
        }
        catch(Exception e) {
            Log.d("exception e", e.getMessage());
            sendLosingMessage(panel);
        }
    }
    private void setWallFollowerPlaying() {
        boolean resume = false;
        Log.v(tag, "setting WallFollower as Animated Driver");
        if(wallFollower == null) {
            currentSensors = new boolean[]{true, true, true, true};
            robot = new UnreliableRobot();
            wallFollower = new WallFollower();
            robot.setStatePlaying(statePlaying);
            robot.setPlayAnimationActivity(this);
        }
        else{
            resume = true;
        }
        if(reliableSensor != null) {
            for(int i = 0; i < reliableSensor.length; i++) {
                if(reliableSensor[i]) {
                    switch(i) {
                        case 0:
                            setSensor("front", true);
                            break;
                        case 1:
                            setSensor("left", true);
                            break;
                        case 2:
                            setSensor("right", true);
                            break;
                        case 3:
                            setSensor("back", true);
                            break;
                    }
                }
                else {
                    if(i-1 >= 0 && !reliableSensor[i-1] || i-2 >= 0 && !reliableSensor[i-2] || i-3 >= 0 && !reliableSensor[i-3]) {
                        try {
                            Thread.sleep(1300);
                        }
                        catch(Exception e) {
                            System.out.println("wallFollowerPlaying in Controller.java: Error with sleeping");
                        }
                    }

                    switch(i) {
                        case 0:
                            setSensor("front", false);
                            break;
                        case 1:
                            setSensor("left", false);
                            break;
                        case 2:
                            setSensor("right", false);
                            break;
                        case 3:
                            setSensor("back", false);
                            break;
                    }
                }
            }
        }
        else {
            setSensor("front", true);
            setSensor("left", true);
            setSensor("right", true);
            setSensor("back", true);
        }
        wallFollower.setRobot(robot);
        wallFollower.setMaze(GeneratingActivity.maze);
        try {
            wallFollower.drive2Exit();
        }
        catch(Exception e) {
            sendLosingMessage(panel);
        }
    }
    /**
     * Private helper method for the short cut to WinningActivity
     * Since no actual information is generated, use preset values to
     * pass to WinningActivity
     *
     * @param view
     */
    private void startWinningActivity(View.OnClickListener view) {
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
    public void sendWinningMessage(View view){
        Intent intent = new Intent(this, WinningActivity.class);
        int energyConsump = 0;
        int pathLength = 0;
        if(isWizard){
            pathLength = wizard.getPathLength()-1;
            energyConsump = (int)wizard.getEnergyConsumption();
        }
        else{
            pathLength = wallFollower.getPathLength()-1;
            energyConsump = (int)wallFollower.getEnergyConsumption();
        }
        DataContainer.setPathlength(pathLength);
        DataContainer.setEnergyConsumption(energyConsump);
        startActivity(intent);
    }
    /**
     * Private helper method for the short cut to LosingActivity
     * Since no actual information is generated, use preset values to
     * pass to LosingActivity
     *
     * @param view
     */
    private void startLosingActivity(View.OnClickListener view) {
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
    public void sendLosingMessage(View view){
        Intent intent = new Intent(this, LosingActivity.class);
        int energyConsump = 0;
        int pathLength = 0;
        if(isWizard){
            pathLength = wizard.getPathLength();
            energyConsump = (int)wizard.getEnergyConsumption();
        }
        else{
            pathLength = wallFollower.getPathLength();
            energyConsump = (int)wallFollower.getEnergyConsumption();
        }
        DataContainer.setPathlength(pathLength);
        DataContainer.setEnergyConsumption(energyConsump);
        startActivity(intent);
    }


    /**
     * Navigate back to the title page once the back button is clicked
     */
    @Override
    public void onBackPressed() {
        if(isWizard){
            try {
                wizard.terminateThread();
            }
            catch(Exception e){}
        }
        else{
            try {
                wallFollower.terminateThread();
            }
            catch(Exception e){}
        }
        Log.v(tag, "back button pressed, going back to AMazeActivity");
        Intent intent = new Intent(getApplicationContext(), AMazeActivity.class);
        startActivity(intent);

    }

    ;

    /**
     * This method makes the sensors on the screen
     * show up as green if they are reliable
     * and red if they are unreliable
     *
     * @param robot with specified sensors picked by the user
     */
    private void setRobotSensors(String robot) {
        Log.v(tag, "Robot; " + robot);
        reliableSensor = new boolean[]{true, true, true, true};
        if (robot.equals("Mediocre")) {
            reliableSensor[1] = false;
            reliableSensor[2] = false;
        } else if (robot.equals("Soso")) {
            reliableSensor[0] = false;
            reliableSensor[3] = false;
        } else if (robot.equals("Shaky")) {
            reliableSensor = new boolean[]{false, false, false, false};
        }
    }

    public void setSensor(String whichSensor, boolean reliable) {
        if(whichSensor.equalsIgnoreCase("left")) {
            if(reliable) {
                leftSensor = new ReliableSensor();
                leftSensor.setSensorDirection(Direction.LEFT);
                leftSensor.setMaze(maze);
                robot.addDistanceSensor(leftSensor, Direction.LEFT);
            }
            else {
                leftSensor = new UnreliableSensor();
                leftSensor.setSensorDirection(Direction.LEFT);
                leftSensor.setMaze(GeneratingActivity.maze);
                leftSensor.setWhichSensor("Left");
                robot.addDistanceSensor(leftSensor, Direction.LEFT);
                robot.startFailureAndRepairProcess(Direction.LEFT, MEAN_TIME_BETWEEN_FAILURES, MEAN_TIME_TO_REPAIR);
            }
        }
        else if(whichSensor.equalsIgnoreCase("right")) {
            if(reliable) {
                rightSensor = new ReliableSensor();
                rightSensor.setSensorDirection(Direction.RIGHT);
                rightSensor.setMaze(maze);
                robot.addDistanceSensor(rightSensor, Direction.RIGHT);
            }
            else {
                rightSensor = new UnreliableSensor();
                rightSensor.setSensorDirection(Direction.RIGHT);
                rightSensor.setMaze(maze);
                rightSensor.setWhichSensor("Right");
                robot.addDistanceSensor(rightSensor, Direction.RIGHT);
                robot.startFailureAndRepairProcess(Direction.RIGHT, MEAN_TIME_BETWEEN_FAILURES, MEAN_TIME_TO_REPAIR);
            }
        }
        else if(whichSensor.equalsIgnoreCase("front")) {
            if(reliable) {
                frontSensor = new ReliableSensor();
                frontSensor.setSensorDirection(Direction.FORWARD);
                frontSensor.setMaze(maze);
                robot.addDistanceSensor(frontSensor, Direction.FORWARD);
            }
            else {
                frontSensor = new UnreliableSensor();
                frontSensor.setSensorDirection(Direction.FORWARD);
                frontSensor.setMaze(maze);
                frontSensor.setWhichSensor("Front");
                robot.addDistanceSensor(frontSensor, Direction.FORWARD);
                robot.startFailureAndRepairProcess(Direction.FORWARD, MEAN_TIME_BETWEEN_FAILURES, MEAN_TIME_TO_REPAIR);
            }
        }
        else if(whichSensor.equalsIgnoreCase("back")) {
            if(reliable) {
                backSensor = new ReliableSensor();
                backSensor.setSensorDirection(Direction.BACKWARD);
                backSensor.setMaze(maze);
                robot.addDistanceSensor(backSensor, Direction.BACKWARD);
            }
            else {
                backSensor = new UnreliableSensor();
                backSensor.setSensorDirection(Direction.BACKWARD);
                backSensor.setMaze(maze);
                backSensor.setWhichSensor("Back");
                robot.addDistanceSensor(backSensor, Direction.BACKWARD);
                robot.startFailureAndRepairProcess(Direction.BACKWARD, MEAN_TIME_BETWEEN_FAILURES, MEAN_TIME_TO_REPAIR);
            }
        }
    }
}