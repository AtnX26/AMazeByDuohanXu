package edu.wm.cs.cs301.DuohanXu.gui;

import edu.wm.cs.cs301.DuohanXu.generation.CardinalDirection;
import edu.wm.cs.cs301.DuohanXu.generation.Maze;
import edu.wm.cs.cs301.DuohanXu.gui.Constants.UserInput;
import edu.wm.cs.cs301.DuohanXu.gui.Robot.Direction;

/**
 * Class: ReliableRobot
 * 
 * Responsibilities: the robot simulates a player making decisions 
 * when playing the maze game. It executes movement, senses the maze
 * environment, and keeps track on the battery level. It implements
 * the ReliableSensor or UnreliableSensor as the source of information.
 * 
 * Collaborators:ReliableSensor, Controller
 * Implements Robot
 * 
 * @author DuohanXu
 */

public class ReliableRobot implements Robot{
	
	protected DistanceSensor leftsensor;
	protected DistanceSensor rightsensor;
	protected DistanceSensor frontsensor;
	protected DistanceSensor backsensor;
	private StatePlaying statePlaying;
	private PlayAnimationActivity playAnimationActivity;
	protected float[] battery;
	protected int odometer;
	//protected Control control;
	protected Maze maze;
	
	protected final float jumpcost = 40;
	protected final float rotatecost = 3;
	protected final float movecost = 6;
	protected final float sensecost = 1;
	protected final float initialenergy = 3500;
	
	protected boolean stopped;
	

	/**
	 * The Constructor initializes the ReliableRobot class. 
	 */
	public ReliableRobot() {
		leftsensor = null;
		rightsensor = null;
		frontsensor = null;
		backsensor = null;
		
		resetOdometer();
		battery = new float[1];
		battery[0] = initialenergy;
		stopped = false;

	}
	
	/**
	 * Sets up the controller using Control class.
	 * @param statePlaying for the robot to set up with
	 * @throws IllegalArgumentException while the controller is not correctly set up
	 * (for example, a null value)
	 */
	@Override
	public void setStatePlaying(StatePlaying statePlaying) {
		
		if (statePlaying == null || GeneratingActivity.maze == null){
			throw new IllegalArgumentException();
		}
		this.statePlaying = statePlaying;
		maze = GeneratingActivity.maze;
	}


	void setPlayAnimationActivity(PlayAnimationActivity playAnimationActivity){
		this.playAnimationActivity = playAnimationActivity;
	}
	/**
	 * Adds DistanceSensor to the robot according to the given direction.
	 * Use case switching to determine which sensor should be added. 
	 * @param sensor and direction that should be used
	 */
	@Override
	public void addDistanceSensor(DistanceSensor sensor, Direction mountedDirection) {
		
		switch(mountedDirection) {
			case LEFT:
				leftsensor = sensor;
				leftsensor.setMaze(maze);
				leftsensor.setSensorDirection(mountedDirection);
				break;
			case RIGHT:
				rightsensor = sensor;
				rightsensor.setMaze(maze);
				rightsensor.setSensorDirection(mountedDirection);
				break;
			case FORWARD:
				frontsensor = sensor;
				frontsensor.setMaze(maze);
				frontsensor.setSensorDirection(mountedDirection);
				break;
			case BACKWARD:
				backsensor = sensor;
				backsensor.setMaze(maze);
				backsensor.setSensorDirection(mountedDirection);
				break;
			
		}
		
	}

	/**
	 * Gets the current position information as a pair of coordinators.
	 * @return an array [x,y] for coordinators. 
	 * @throw exception if x or y is greater or smaller than their bounds.
	 */
	@Override
	public int[] getCurrentPosition() throws Exception {
		
		int[] pos = statePlaying.getCurrentPosition();
		if(pos[0] > maze.getWidth() || pos[1] > maze.getHeight() || pos[0] < 0 || pos [1] < 0) {
			throw new Exception("Out of the maze!");
		}
		return pos;
	}

	/**
	 * Gets the current direction where the robot is facing.
	 * @return the direction from the controller.
	 */
	@Override
	public CardinalDirection getCurrentDirection() {

		return statePlaying.getCurrentDirection();
	}

	/**
	 * Gets the battery level as a float from the robot.
	 * @return a float representing the battery level.
	 */
	@Override
	public float getBatteryLevel() {

		return battery[0];
	}

	/**
	 * Sets the battery level as a float from the parameter.
	 * @param level float representing the battery level.
	 * @throws IllegalArgumentException if level is negative.
	 */
	@Override
	public void setBatteryLevel(float level) {
		if (level < 0) {
			throw new IllegalArgumentException();
		}
		battery[0] = level;
	}

	/**
	 * Gets the energy consumption for a full rotation.
	 * @return a float representing the cost of full rotation.
	 */
	@Override
	public float getEnergyForFullRotation() {

		return 4*rotatecost;
	}

	/**
	 * Gets the energy consumption for a step forward.
	 * @return a float representing the cost of a step forward.
	 */
	@Override
	public float getEnergyForStepForward() {

		return movecost;
	}

	/**
	 * Gets the distance traveled by the robot.
	 * @return an int representing how many cells the robot has travelled.
	 */
	@Override
	public int getOdometerReading() {

		return odometer;
	}

	/**
	 * Resets traveled distance to zero to start/restart the robot.
	 */
	@Override
	public void resetOdometer() {

		odometer = 0;
	}

	/**
	 * Turns the robot to the given direction, stops the robot if out of energy.
	 * Uses case switching to distinguish movements passed from turn, then sets
	 * the battery level and perform key actions accordingly.
	 * @param turn enum that decides the type of turning the robot will execute.
	 */
	@Override
	public void rotate(Turn turn) {

		if (getBatteryLevel() < rotatecost || (turn== Turn.AROUND &&getBatteryLevel() < rotatecost*2 )) {
			stopped = true;
			setBatteryLevel(0);
			return;
		}
		switch(turn) {
			case LEFT:
				statePlaying.handleUserInput(UserInput.LEFT, 0);
				setBatteryLevel(getBatteryLevel()-rotatecost);
				break;
			case RIGHT:
				statePlaying.handleUserInput(UserInput.RIGHT, 0);
				setBatteryLevel(getBatteryLevel()-rotatecost);
				break;
			case AROUND:
				statePlaying.handleUserInput(UserInput.RIGHT, 0);
				statePlaying.handleUserInput(UserInput.RIGHT, 0);
				setBatteryLevel(getBatteryLevel()-rotatecost*2);
				break;
			
		}
		if (getBatteryLevel() == 0) {
			stopped = true;
		}
	}
	
	/**
	 * Moves the robot for a distance. Makes the robot stop in case of running out
	 * of battery. 
	 * @param distance integer for distance to cover
	 * @throws IllegalArgumentException if distance not positive
	 */
	@Override
	public void move(int distance) throws IllegalArgumentException{
		
		if (distance < 0) {throw new IllegalArgumentException();}
		if (getBatteryLevel() < movecost) {
			stopped = true;
			setBatteryLevel(0);
			return;
		}
		int[] pos;
		while (distance>0) {
			try {
				pos = getCurrentPosition();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
			if (maze.hasWall(pos[0], pos[1], getCurrentDirection())) {
				
				stopped = true;
				break;
			}
			if (getBatteryLevel() >= movecost) {
				statePlaying.handleUserInput(UserInput.UP, 0);
				setBatteryLevel(getBatteryLevel() - movecost);
				odometer += 1;
			}
			else {
				stopped = true;
				setBatteryLevel(0);
				break;
			}
			distance--;
		}
		
	}

	/**
	 * Drives the robot to jump over the wall that it is facing at.
	 * It determines whether the wall can be jumped over; if not, 
	 * it stops the robot or throws a exception accordingly.
	 * Increases odometer.
	 */
	@Override
	public void jump() {
	
		if (getBatteryLevel() < jumpcost) {
			setBatteryLevel(0);
			stopped = true;
			return;
		}
		int[] jumppos = new int[2];
		int[] pos = statePlaying.getCurrentPosition();
		switch(statePlaying.getCurrentDirection()) {
		case North:
			jumppos[0] = pos[0];
			jumppos[1] = pos[1]-1;
		case South:
			jumppos[0] = pos[0];
			jumppos[1] = pos[1]+1;
		case East:
			jumppos[0] = pos[0]+1;
			jumppos[1] = pos[1];
		case West:
			jumppos[0] = pos[0]-1;
			jumppos[1] = pos[1];
		}
		if(!maze.isValidPosition(jumppos[0], jumppos[1])) {
			setBatteryLevel(0);
			stopped = true;
			return;
		}
		statePlaying.handleUserInput(UserInput.JUMP, 0);
		battery[0] = battery[0] - jumpcost;
		odometer++;
	}

	/**
	 * Uses the current position to compare whether the current 
	 * location is an exit in the maze using isExitPosition from 
	 * the floorplan.
	 * @return a boolean that indicates if the robot is at the
	 * exit of the maze.
	 */
	@Override
	public boolean isAtExit() {

		int[] pos;
		try {
			pos = getCurrentPosition();
		} catch (Exception e) {
			return false;
		}
		
		maze = statePlaying.getMaze();
		
		return maze.getFloorplan().isExitPosition(pos[0], pos[1]);
	}

	/**
	 * Uses the current position to show if the robot is in a room
	 * using isInRoom method from the maze.
	 * @return a boolean that indicates whether the robot is in a
	 * room.
	 */
	@Override
	public boolean isInsideRoom() {

		int[] pos;
		try {
			pos = getCurrentPosition();
		} catch (Exception e) {
			return false;
		}
		
		maze = statePlaying.getMaze();
		
		return maze.getFloorplan().isInRoom(pos[0], pos[1]);
	}

	/**
	 * Returns true if the robot has stopped, otherwise false.
	 * @return a boolean representing the current condition
	 * of movement.
	 */
	@Override
	public boolean hasStopped() {

		return stopped;
	}

	@Override
	public int distanceToObstacle(Direction direction) throws UnsupportedOperationException {
		/**
		 * Tells the distance to an obstacle (a wall) in the given direction.
		 * The direction is relative to the robot's current forward direction.
		 * Distance is measured in the number of cells towards that obstacle.
		 * @param direction specifies the direction of the sensor
		 * @return number of steps towards obstacle if obstacle is visible 
		 * in a straight line of sight, Integer.MAX_VALUE otherwise
		 * @throws UnsupportedOperationException if robot has no sensor in this direction
		 * or the sensor exists but is currently not operational.
		 */
		DistanceSensor sensor = null;
		switch(direction) {
		case LEFT:
			sensor = leftsensor;
			break;
		case RIGHT:
			sensor = rightsensor;
			break;
		case FORWARD:
			sensor = frontsensor;
			break;
		case BACKWARD:
			sensor = backsensor;
			break;
		}
		if (sensor == null) {
			throw new UnsupportedOperationException();
		}
		sensor.setMaze(maze);
		int distance;
		try {
			distance = sensor.distanceToObstacle(getCurrentPosition(), getCurrentDirection(), battery);
		} catch (Exception e) {
	
			String msg = e.getMessage();
			System.out.println(msg);
			if (msg == "PowerFailure") {
				setBatteryLevel(0);
				stopped = true;
			}
			throw new UnsupportedOperationException(msg);
		}
		
		return distance;
	}

	@Override
	public boolean canSeeThroughTheExitIntoEternity(Direction direction) throws UnsupportedOperationException {
		/**
		 * Returns true if the robot can drive through the exit in a straight line
		 * in the @param direction. Uses the robot's sensors to determine
		 * if there is a boundary wall as an obstacle, the sensors return the maximum
		 * value of the integer (which is 500) if there is no wall in the way. 
		 * @throws an exception if the sensor is lacked in the direction
		 * or its not operational
		 */
		DistanceSensor sensor = null;
		switch(direction) {
		case LEFT:
			sensor = leftsensor;
			break;
		case RIGHT:
			sensor = rightsensor;
			break;
		case FORWARD:
			sensor = frontsensor;
			break;
		case BACKWARD:
			sensor = backsensor;
			break;
		}
		

		if (sensor == null) {
			throw new UnsupportedOperationException("No sensor in this direction!");
		}


		return (distanceToObstacle(direction) == Integer.MAX_VALUE);

	}

	@Override
	public void startFailureAndRepairProcess(Direction direction, int meanTimeBetweenFailures, int meanTimeToRepair)
			throws UnsupportedOperationException {
		/**
		 * Not currently used, but uses @param to let the repair process start.
		 */
		throw new UnsupportedOperationException();
	}

	@Override
	public void stopFailureAndRepairProcess(Direction direction) throws UnsupportedOperationException {
		/**
		 * Not currently used, but uses @param to let the repair process stop.
		 */
		throw new UnsupportedOperationException();
	}


	/** 
	 * A helper method to return a sensor.
	 * @param direction
	 * @return sensor
	 */
	public DistanceSensor getDistanceSensor(Direction direction) {
		switch(direction) {
		case FORWARD: 
			return frontsensor;
		case BACKWARD:
			return backsensor;
		case LEFT:
			return leftsensor;
		case RIGHT:
			return rightsensor;
		}
		return null;
	}
}
	