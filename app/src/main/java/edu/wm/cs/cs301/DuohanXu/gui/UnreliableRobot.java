package edu.wm.cs.cs301.DuohanXu.gui;


/**
 * Class: UnreliableRobot
 * 
 * Responsibilities: the robot simulates a player making decisions 
 * when playing the maze game. It executes movement, senses the maze
 * environment, and keeps track on the battery level. It implements
 * the UnreliableSensor as the source of information.
 * 
 * Collaborators:ReliableSensor, Controller
 * Extends ReliableRobot
 * Implements Robot
 * 
 * @author DuohanXu
 */
public class UnreliableRobot extends ReliableRobot implements Robot{
	
	/**
	 * Everything else is the same except the repair
	 * methods.
	 */
	
	/**
	 * Method starts a concurrent, independent failure and repair
	 * process that makes the sensor fail and repair itself.
	 * This creates alternating time periods of up time and down time.
	 * Up time: The duration of a time period when the sensor is in 
	 * operational is characterized by a distribution
	 * whose mean value is given by parameter meanTimeBetweenFailures.
	 * Down time: The duration of a time period when the sensor is in repair
	 * and not operational is characterized by a distribution
	 * whose mean value is given by parameter meanTimeToRepair.
	 * 
	 * This an optional operation. If not implemented, the method
	 * throws an UnsupportedOperationException.
	 * 
	 * @param direction the direction the sensor is mounted on the robot
	 * @param meanTimeBetweenFailures is the mean time in seconds, must be greater than zero
	 * @param meanTimeToRepair is the mean time in seconds, must be greater than zero
	 * @throws UnsupportedOperationException if method not supported
	 */
	@Override
	public void startFailureAndRepairProcess(Direction direction, int meanTimeBetweenFailures, int meanTimeToRepair) {
		switch (direction) {
		case LEFT:
			leftsensor.startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
			break;
		case RIGHT:
			rightsensor.startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
			break;
		case FORWARD:
			frontsensor.startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
			break;
		case BACKWARD:
			backsensor.startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
			break;
		}
	}
	
	/**
	 * This method stops a failure and repair process and
	 * leaves the sensor in an operational state.
	 * 
	 * It is complementary to starting a 
	 * failure and repair process. 
	 * 
	 * Intended use: If called after starting a process, this method
	 * will stop the process as soon as the sensor is operational.
	 * 
	 * If called with no running failure and repair process, 
	 * the method will return an UnsupportedOperationException.
	 * 
	 * This an optional operation. If not implemented, the method
	 * throws an UnsupportedOperationException.
	 * 
	 * @param direction the direction the sensor is mounted on the robot
	 * @throws UnsupportedOperationException if method not supported
	 */
	@Override
	public void stopFailureAndRepairProcess(Direction direction) {
		switch (direction) {
		case LEFT:
			leftsensor.stopFailureAndRepairProcess();
			break;
		case RIGHT:
			rightsensor.stopFailureAndRepairProcess();
			break;
		case FORWARD:
			frontsensor.stopFailureAndRepairProcess();
			break;
		case BACKWARD:
			backsensor.stopFailureAndRepairProcess();
			break;
		}
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
