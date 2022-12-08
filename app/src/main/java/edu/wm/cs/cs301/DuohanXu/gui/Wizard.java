package edu.wm.cs.cs301.DuohanXu.gui;

import generation.Maze;
import gui.Robot.Direction;
import generation.CardinalDirection;
import gui.Robot.Turn;

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
	
	public Wizard() {
		robot = null;
		maze = null;
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
		while (!robot.hasStopped()) {
			int[] pos;
			try {
				drive1Step2Exit();
				pos = robot.getCurrentPosition();
			} catch (Exception e) {
				throw new Exception();
			}
			
			
			if (robot.isAtExit()) {
				exitTheMaze();
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean drive1Step2Exit() throws Exception {
		/**
		 * Move each time according to the direction of its neighbor.
		 *  @return true if it moved the robot to an adjacent cell, false otherwise
		 * @throws Exception thrown if robot stopped due to some problem, e.g. 
		 * lack of energy
		 */
		int[] currPos = robot.getCurrentPosition();
		int[] neighbor = maze.getNeighborCloserToExit(currPos[0], currPos[1]);
		CardinalDirection currDir = robot.getCurrentDirection();
		int[] change = {neighbor[0]-currPos[0], neighbor[1]-currPos[1]};
		switch (currDir) {
		case North:
			if (change[0] == -1) robot.rotate(Turn.RIGHT);
			else if (change[0] == 1) robot.rotate(Turn.LEFT);
			else if (change[1] == 1) robot.rotate(Turn.AROUND);
			break;
		case South:
			if (change[0] == -1) robot.rotate(Turn.LEFT);
			else if (change[0] == 1) robot.rotate(Turn.RIGHT);
			else if (change[1] == -1) robot.rotate(Turn.AROUND);
			break;
		case East:
			if (change[0] == -1) robot.rotate(Turn.AROUND);
			else if (change[1] == -1) robot.rotate(Turn.RIGHT);
			else if (change[1] == 1) robot.rotate(Turn.LEFT);
			break;
		case West:
			if (change[0] == 1) robot.rotate(Turn.AROUND);
			else if (change[1] == -1) robot.rotate(Turn.LEFT);
			else if (change[1] == 1) robot.rotate(Turn.RIGHT);
			break;
		}
		if (maze.hasWall(currPos[0], currPos[1], currDir)) {}//{robot.jump();}
		else {robot.move(1);}	
		if (robot.hasStopped()) throw new Exception();		
		return true;
	}
	
	/**
	 * A little helper method to turn the robot facing the exit.
	 */
	private void exitTheMaze() throws Exception{
		while(!robot.canSeeThroughTheExitIntoEternity(Direction.FORWARD)) {
			robot.rotate(Turn.LEFT);
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

}
