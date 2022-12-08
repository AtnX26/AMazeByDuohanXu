package edu.wm.cs.cs301.DuohanXu.gui;

import generation.CardinalDirection;
import generation.Maze;
import gui.Robot.Direction;
import gui.Robot.Turn;

/**
 * Class: WallFollower
 * 
 * Responsibilities: the follower drives the robot to the exit
 * by following a wall to the exit. 
 * 
 * Collaborators: ReliableRobot, UnreliableRobot
 * Implements RobotDriver
 * 
 * @author DuohanXu
 *
 */
public class WallFollower implements RobotDriver {
	protected Robot robot;
	protected Maze maze = null;

	int counter;
	public WallFollower() {
		
		robot = null;
		counter = 0;
	}
	@Override
	public void setRobot(Robot r) {
		robot = r;
		
	}

	@Override
	public void setMaze(Maze maze) {
		/**
		 * Should not have a maze instance since cheating is not allowed here
		 */
	}

	/**
	 * Drives the robot towards the exit. 
	 */
	@Override
	public boolean drive2Exit() throws Exception {
		// drive1Step2Exit
		// until can see exit
		while (true) {
			if (!drive1Step2Exit())
				return true;
		}
	}
	
	/**
	 *Performs a single step forward. 
	 *When the robot has yet reached the exit, perform a 
	 *movement according to the detection result. Move forward
	 *when the distance to the left wall is 0 unless reaching to
	 *the front wall; then consider turning right and repeat.
	 */
	@Override
	public boolean drive1Step2Exit() throws Exception {
		//If not at the exit, tries to walk with sensors triggered
		if (!robot.isAtExit()) {
			int dist = trigger(Direction.LEFT);
			return walking(dist, Direction.LEFT);
		}
		//If at the exit, tries to rotate towards the exit
		else {
			if (!exitRotation()) {
				throw new Exception();
			}
		}
		return false;
		
	}
	
	/**
	 * Returns the distance to the wall by triggering a sensor.
	 * It first tries to get  
	 * @param direction
	 * @return
	 */
	private int trigger(Direction target) {
		if (robot.hasStopped()) {
			return -1;
		}
		//tries to get distance information with the sensor
		try {return robot.distanceToObstacle(target);}
		//if failed, tries strategy 1 to face the available sensor towards target direction
		catch (UnsupportedOperationException ex) {
			CardinalDirection cardinalDirDesired = getDirection(target);
			//tries other currently available sensors
			for (Direction dirAvail : Direction.values()) {
				try {
					robot.distanceToObstacle(dirAvail);
					//if cannot rotate, return -1 so that it throws an exception
					if (!rotateRobot(dirAvail, cardinalDirDesired)) return -1;
					int distance = robot.distanceToObstacle(dirAvail);
					//rotate back
					if (!rotateRobot(target, cardinalDirDesired)) return -1;
					return distance;
					//if none of them is operating, starts strategy 2
				} catch (UnsupportedOperationException ex2) {continue;}
			}
			
			//if failed again, tries strategy 2 to wait for reparation
			try {Thread.sleep(2000);} 
			catch (InterruptedException ex3) {}
			
			//if still can't rotate back, returns -1
			if (!rotateRobot(target, cardinalDirDesired)) return -1;
			return robot.distanceToObstacle(target);
		}	
		
	}
	
	/**
	 * Gets the absolute direction of the sensor (or any desired direction)
	 * by checking the robot's current direction.
	 * @param direction
	 * @return
	 */
	private CardinalDirection getDirection(Direction direction) {
		CardinalDirection currentDirection = robot.getCurrentDirection();
		switch (direction) {
		case FORWARD:
			return currentDirection;
		case LEFT:
			return currentDirection.rotateClockwise();
		case BACKWARD:
			return currentDirection.oppositeDirection();
		case RIGHT:
			return currentDirection.oppositeDirection().rotateClockwise();
		default:
			return null;
		}
	}
	
	/**
	 * Rotates the robot to the direction of the sensor so that once it finds the
	 * path, it just faces at it.
	 * @param dirOfSensor
	 * @param desireDirection
	 * @return
	 */
	private boolean rotateRobot(Direction dirOfSensor, CardinalDirection desireDirection) {
		CardinalDirection currentDirection = getDirection(dirOfSensor);
		if (desireDirection==currentDirection) {
			return true;
		} else if (desireDirection==currentDirection.rotateClockwise()) {
			if (robot.getBatteryLevel()<3) return false;
			robot.rotate(Turn.LEFT);
		} else if (desireDirection==currentDirection.oppositeDirection()) {
			if (robot.getBatteryLevel()<6) return false;
			robot.rotate(Turn.AROUND);
		} else {
			if (robot.getBatteryLevel()<3) return false;
			robot.rotate(Turn.RIGHT);
		}
		return true;
	}
	
	/**
	 * A private method for driving the robot alongside the wall
	 * @param dist
	 * @param direction
	 * @return
	 * @throws Exception
	 */
	private boolean walking(int dist, Direction direction) throws Exception{
		if (dist < 0) {
			throw new Exception();
		}
		if (dist == 0){
			//if there is a wall to the left, continue walking
			if (direction==Direction.LEFT) {
				counter = 0;
				int distToFront = trigger(Direction.FORWARD);
				return walking(distToFront, Direction.FORWARD);
			}
			//if there is a wall in front of the robot, consider turing to the right
			else if (direction==Direction.FORWARD) {
				int distToRight = trigger(Direction.RIGHT);
				return walking(distToRight, Direction.RIGHT);
			} 
			//turns around
			else {
				robot.rotate(Turn.AROUND);
				if (robot.getBatteryLevel()<6) throw new Exception();
				robot.move(1);
				//If the move is successful, return true.
				return true;
			}
		}
		//if can see the exit, tries to cross the line
		if (dist == Integer.MAX_VALUE) {
			if (!crossTheExit(direction)) throw new Exception();
			return false;
		}
		//if no condition applies, it means a single foward moving.
		if (!driveRobot(direction)) {throw new Exception();}
		return true;
	}
	
	/**
	 * A method to perform the actual moving process. If the parameter
	 * passed only tells the robot to go forward, it simply moves forward.
	 * If the direction requires the robot to rotate, it performs the rotation
	 * and count them. Once it turns too many times and yet to find the way
	 * out, it triggers a method to escape.
	 * @param direction
	 * @return
	 */
	private boolean driveRobot(Direction direction) {
		//turn left, and triggers escape if necessary
		if (direction==Direction.LEFT) {
			if (counter>=2) {
				if (!escapeRoom()) {return false;}
				return true;
			}
			robot.rotate(Turn.LEFT);
			counter ++;
		//no need for increment of the counter since turning right is imperative
		} else if (direction==Direction.RIGHT)
			robot.rotate(Turn.RIGHT);
		if (robot.getBatteryLevel()<6) return false;
		robot.move(1);
		return true;
	}
	
	/**
	 * If the robot has turned left for over 2 times, it is considered to be trapped in a room.
	 * 
	 * @return
	 */
	private boolean escapeRoom() {
		int distance = trigger(Direction.FORWARD);
		//return false if the distance is unavailable
		if (distance==-1) {return false;}
		//If not, drive the robot forward until it reaches the wall.
		if (distance>0) {robot.move(distance);}
		robot.rotate(Turn.RIGHT);

		//if it cannot follow another wall, return false
		if (trigger(Direction.LEFT)!=0) {return false;}
		return true;
	}
	
	/**
	 * Perform a movement to cross the exit. 
	 * @param direction
	 * @return
	 */
	private boolean crossTheExit(Direction direction) {
		if (robot.getBatteryLevel()<6) return false;
		if (direction==Direction.LEFT) {
			robot.rotate(Turn.LEFT);}
		else if (direction==Direction.RIGHT) {
			robot.rotate(Turn.RIGHT);}
		//move unless running out of the battery
		while (!robot.isAtExit()) {
			if (robot.getBatteryLevel()<6) return false;
			robot.move(1);
		}
		return true;
	}
	
	private boolean exitRotation() {
		//uses a for loop to determine the direction of the exit
		for (Direction dir : Direction.values()) {
			int distance = trigger(dir);
			if (distance<0) {return false;}
			//rotate to the exit
			if (distance==Integer.MAX_VALUE) {
				switch(dir) {
				case LEFT:
					if (robot.getBatteryLevel()<3) return false;
					robot.rotate(Turn.LEFT);
					break;
				case RIGHT:
					if (robot.getBatteryLevel()<3) return false;
					robot.rotate(Turn.RIGHT);
					break;
				case BACKWARD:
					if (robot.getBatteryLevel()<6) return false;
					robot.rotate(Turn.AROUND);
					break;
				case FORWARD: 
					break;
				}
				break;
			}
		}
		//there is no other way to lose the game at this point...
		return true;
	}
	
	@Override
	public float getEnergyConsumption() {
		return 3500 - robot.getBatteryLevel();
	}

	@Override
	public int getPathLength() {
		return robot.getOdometerReading();
	}

}
