package edu.wm.cs.cs301.DuohanXu.gui;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import edu.wm.cs.cs301.DuohanXu.generation.Maze;
import edu.wm.cs.cs301.DuohanXu.gui.Robot.Direction;
import edu.wm.cs.cs301.DuohanXu.generation.CardinalDirection;
import edu.wm.cs.cs301.DuohanXu.gui.Robot.Turn;

/**
 * Class: Wizard
 * 
 * Responsibilities: it drives a robot to find an exit using cheating
 * methods to get maze information directly from the Maze object.
 * 
 * Collaborators: Maze, ReliableRobot/UnreliableRobot
 * 
 * Implementing classes: WallFollower
 * @author DuohanXu
 *
 */

public class Wizard implements RobotDriver{
	protected Robot robot;
	protected Maze maze;
	private Thread wizardThread;
	public static boolean getsToExit = false;
	private String TAG = "Wizard";
	private int speed = 400;
	private boolean lost;
	private ReliableSensor sensor;
	private float batteryLevel1;
	private int[] speedOptions;

	public Wizard() {
		robot = null;
		maze = null;
		speedOptions = new int[]{2000, 1500, 1000, 900, 800, 700, 600, 500, 450, 400, 350, 300, 250, 200, 150, 100, 50, 25, 10, 5, 1};
		lost = false;
	}

	@Override
	public void setRobot(Robot r) {
		/**
		 * Sets up a robot for the wizard using @param robot.
		 */
		robot = r;
		
	}

	@Override
	public void setMaze(Maze maze) {
		/**
		 * Sets up a maze for the wizard using @param maze.
		 */
		this.maze = maze;
	}



	/**
	 * Creates a new background thread for the
	 * driver to run on, which calls the drive1Step2Exit method
	 * until the robot reaches the exit or runs out of energy/crashes
	 * and controls how fast the animation speed is
	 * @param d distance to the exit
	 */
	public void runThread(int d) throws Exception{
		final int distance = d;
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				while (distance >= 0 && wizardThread != null) {
					try {
						boolean facingExit = drive1Step2Exit();
						if(!facingExit) {
							while(!robot.canSeeThroughTheExitIntoEternity(Direction.FORWARD)) {
								robot.rotate(Turn.LEFT);
						    }
							robot.move(1);
							Log.v(TAG, "about to return true");
							setGetsToExit(true);
							Log.v(TAG, "gets to Exit: " + getsToExit);
							break;
						}
					}
					catch(Exception e){
						Log.v(TAG, "Error: Robot has run out of energy or crashed");
						wizardThread = null;
						lost = true;
					}
					Log.v(TAG, "Running thread inside run method");
					try{
						Thread.sleep(speed);
					}
					catch (InterruptedException e){
						System.out.println("Thread was interrupted");
					}
					Message message = new Message();
					Bundle bundle = new Bundle();
					bundle.putBoolean("lost", lost);
					bundle.putInt("my message key", (int)robot.getBatteryLevel());
					message.setData(bundle);
					PlayAnimationActivity.myHandler.sendMessage(message);
				}
				Log.v(TAG, "Thread is done running");
			}
		};
		Log.v(TAG, "making new thread");
		wizardThread = new Thread(runnable);
		wizardThread.start();
	}

	@Override
	public void terminateThread(){
		if(wizardThread != null){
			wizardThread.interrupt();
			wizardThread = null;
		}
	}

	@Override
	public boolean drive2Exit() throws Exception {
		/**
		 * Drives the robot to the exit using drive1Step2Exit one step
		 * each time.
		 * Uses a while loop to loop the process and stops when the
		 * robot is near the exit, turns to the exit, and then walks
		 * out of the maze.
		 * @return true if driver successfully reaches the exit, false otherwise
		 * @throws Exception thrown if robot stopped due to some problem, e.g. 
		 * lack of energy
		 */
		try {
			Log.v(TAG, "inside drive2exit method in wizard");
			int[] Pos = robot.getCurrentPosition();
			Log.v(TAG, "Pos[0]: " + Pos[0] + " Pos[1]: " + Pos[1]);
			int distance = maze.getDistanceToExit(Pos[0], Pos[1]);
			runThread(distance);
			return getsToExit;
		}
		catch(IndexOutOfBoundsException e) {
			Log.v(TAG, "Error: Current Position Not In Maze5");
			System.out.println("Error: Current Position Not In Maze5");
		}
		return false;
	}


	/**
	 * Move each time according to the direction of its neighbor.
	 *  @return true if it moved the robot to an adjacent cell, false otherwise
	 * @throws Exception thrown if robot stopped due to some problem, e.g.
	 * lack of energy
	 */
	@Override
	public boolean drive1Step2Exit() throws Exception {
		try {
			Log.v(TAG, "inside drive1step2exit method in wizard");
			int[] currentPos = robot.getCurrentPosition();
			int[] neighbor = maze.getNeighborCloserToExit(currentPos[0], currentPos[1]) ;
			if (null == neighbor)
				return false;
			makeRobotFaceCorrectDirectionForNextMove(currentPos, neighbor);
			robot.move(1);
			if(robot.hasStopped()) {
				Log.v(TAG, "OH NO: Robot has run out of energy or crashed");
				throw new Exception("OH NO: Robot has run out of energy or crashed");
			}
			if(robot.isAtExit()) {
				try {
					currentPos = robot.getCurrentPosition();
					sensor = new ReliableSensor();
					sensor.setMaze(maze);
					batteryLevel1 = robot.getBatteryLevel();
					float[] batteryLevel = new float[]{batteryLevel1};
					while(sensor.distanceToObstacle(currentPos, robot.getCurrentDirection(), batteryLevel) != Integer.MAX_VALUE){
						robot.rotate(Turn.LEFT);
						if(robot.hasStopped()) {
							Log.v(TAG, "OH NO: Robot has run out of energy or crashed2");
							throw new Exception("OH NO: Robot has run out of energy or crashed");
						}
						batteryLevel[0] = robot.getBatteryLevel();
					}
					if(sensor.distanceToObstacle(currentPos, robot.getCurrentDirection(), batteryLevel) == Integer.MAX_VALUE) {
						return false;
					}
				}
				catch(IndexOutOfBoundsException e) {
					Log.v(TAG, "Error: Current Position Not In Maze");
					System.out.println("Error: Current Position Not In Maze");
				}
			}
		}
		catch(IndexOutOfBoundsException e){
			Log.v(TAG, "Error: Current Position Not In Maze2");
			System.out.println("Error: Current Position Not In Maze");
		}
		return true;
	}

	/**
	 * Turns the robot so that it faces the next spot in the
	 * solution to get to the exit, allowing the robot to
	 * move forward one space closer to the exit.
	 * @param currentPos position of the robot
	 * @param neighbor of the next step in the solution
	 */
	private void makeRobotFaceCorrectDirectionForNextMove(int[] currentPos, int[] neighbor) {
		switch(robot.getCurrentDirection()) {
			case East :
				if(currentPos[1] + 1 < maze.getHeight() && neighbor[1] == currentPos[1] + 1) {
					robot.rotate(Turn.LEFT);
				}
				else if(currentPos[1] - 1 >= 0 && neighbor[1] == currentPos[1] - 1) {
					robot.rotate(Turn.RIGHT);
				}
				else if(currentPos[0] - 1 < maze.getWidth()&& neighbor[0] == currentPos[0] - 1) {
					robot.rotate(Turn.AROUND);
				}
				break;
			case West :
				if(currentPos[1] + 1 < maze.getHeight() && neighbor[1] == currentPos[1] + 1) {
					robot.rotate(Turn.RIGHT);
				}
				else if(currentPos[1] - 1 >= 0 && neighbor[1] == currentPos[1] - 1) {
					robot.rotate(Turn.LEFT);
				}
				else if(currentPos[0] + 1 < maze.getWidth()&& neighbor[0] == currentPos[0] + 1) {
					robot.rotate(Turn.AROUND);
				}
				break;
			case North :
				if(currentPos[0] + 1 < maze.getWidth() && neighbor[0] == currentPos[0] + 1) {
					robot.rotate(Turn.LEFT);
				}
				else if(currentPos[0] - 1 >= 0 && neighbor[0] == currentPos[0] - 1) {
					robot.rotate(Turn.RIGHT);
				}
				else if(currentPos[1] + 1 < maze.getHeight()&& neighbor[1] == currentPos[1] + 1) {
					robot.rotate(Turn.AROUND);
				}
				break;
			case South :
				if(currentPos[0] + 1 < maze.getWidth() && neighbor[0] == currentPos[0] + 1) {
					robot.rotate(Turn.RIGHT);
				}
				else if(currentPos[0] - 1 >= 0 && neighbor[0] == currentPos[0] - 1) {
					robot.rotate(Turn.LEFT);
				}
				else if(currentPos[1] - 1 < maze.getHeight()&& neighbor[1] == currentPos[1] - 1) {
					robot.rotate(Turn.AROUND);
				}
				break;
		}
	}

	
	@Override
	public float getEnergyConsumption() {
		/**
		 * @return the initial battery level minus current battery level.
		 */
		return 3500 - robot.getBatteryLevel();
	}

	@Override
	public int getPathLength() {
		/**
		 * @return the distance traveled by reading the odometer.
		 */
		return robot.getOdometerReading();
	}
	private void setGetsToExit(boolean atExit){
			getsToExit = atExit;
	}
	public void setAnimationSpeed(int speed){
		this.speed = speedOptions[speed];
	}
}
