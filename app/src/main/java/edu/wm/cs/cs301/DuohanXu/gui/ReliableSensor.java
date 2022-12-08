package edu.wm.cs.cs301.DuohanXu.gui;

import generation.CardinalDirection;
import generation.Maze;
import gui.Robot.Direction;

/**
 * Class: ReliableSensor
 * 
 * Responsibilities: the sensor detects distance from the wall using current position and other information. 
 * 
 * Collaborators: Maze, Floorplan
 * Implements DistanceSensor
 * 
 * Implementing classes: UnreliableSensor
 * @author DuohanXu
 *
 */

public class ReliableSensor implements DistanceSensor{

	protected float sensecost = 1;
	protected Maze maze;
	protected Direction direction;
	protected boolean working;
	
	public ReliableSensor() {
		/**
		 * Sets up the ReliableSensor class
		 */
		working = true;
	}
	
	@Override
	public int distanceToObstacle(int[] currentPosition, CardinalDirection currentDirection, float[] powersupply)
			throws Exception {
		/**
		 * @throws exception if the parameters is not valid.
		 * Then check if the power is enough for the robot.
		 * If ok, 
		 * Will set up a helper method to count the distance 
		 * from the wall.
		 */
		if (currentPosition == null || currentDirection == null || powersupply == null) {
			throw new IllegalArgumentException();
		}
		if (powersupply[0] < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (!working) {
			throw new Exception("SensorFailure");
		}
		if (powersupply[0] < sensecost) {
			throw new Exception("PowerFailure");
		}

		if (currentPosition[0] < 0 || currentPosition[1] < 0 || currentPosition[0]>= maze.getWidth() ||  currentPosition[1] >= maze.getHeight()) {
			throw new IllegalArgumentException();
		}
		int x = currentPosition[0];
		int y = currentPosition[1];


		CardinalDirection detectDir = null;
		switch(direction) {
		case LEFT:
			detectDir = currentDirection.rotateClockwise();
			break;
		case RIGHT:
			detectDir = currentDirection.rotateClockwise().oppositeDirection();
			break;
		case FORWARD:
			detectDir = currentDirection;
			break;
		case BACKWARD:
			detectDir = currentDirection.oppositeDirection();
			break;
		}
		int dx = detectDir.getDxDyDirection()[0];
		int dy = detectDir.getDxDyDirection()[1];


		int count = 0;
		while (!maze.hasWall(x, y, detectDir)) {
			count++;
			x += dx;
			y += dy;
			if (x < 0 ||y < 0 || x >= maze.getWidth() || y >= maze.getHeight()) {
				return Integer.MAX_VALUE;
			}
		}
		powersupply[0] = powersupply[0]-sensecost;

		return count;
	}

	@Override
	public void setMaze(Maze maze) {
		/**
		 * Sets up the maze.
		 * @param maze is used for setting up.
		 */
		if (maze == null||maze.getFloorplan() == null) {
			throw new IllegalArgumentException();
		}
		this.maze = maze;
	}

	@Override
	public void setSensorDirection(Direction mountedDirection) {
		/**
		 * Sets up the direction for the sensor.
		 * @param direction is used for setting up.
		 * @throws IllegalArgumentException if parameter is null
		 */
		if (mountedDirection == null) {
			throw new IllegalArgumentException();
		}
		direction = mountedDirection;
	}

	@Override
	public float getEnergyConsumptionForSensing() {
		/**
		 * Always returns the cost of sensing.
		 * @return float representing the energy cost for sensing.
		 */
		return sensecost;
	}

	@Override
	public void startFailureAndRepairProcess(int meanTimeBetweenFailures, int meanTimeToRepair)
			throws UnsupportedOperationException {
		/**
		 * @throws an exception if @param integers indicates that its time to
		 * start failure and repair process.
		 */
		throw new UnsupportedOperationException();
	}

	@Override
	public void stopFailureAndRepairProcess() throws UnsupportedOperationException {
		/**
		 * @throws an exception if @param integers indicates that its time to
		 * stop failure and repair process.
		 */
		throw new UnsupportedOperationException();
	}

	/**
	 * A helper method to show if the sensor is working.
	 */
	@Override
	public boolean getWorking() {
		return working;
	}

}
