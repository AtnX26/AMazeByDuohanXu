package edu.wm.cs.cs301.DuohanXu.gui;

import edu.wm.cs.cs301.DuohanXu.generation.CardinalDirection;
import edu.wm.cs.cs301.DuohanXu.generation.Maze;
import edu.wm.cs.cs301.DuohanXu.gui.Robot.Direction;

/**
 * Class: UnreliableSensor
 * 
 * Responsibilities: Unlike the reliable one, the unreliable sensor 
 * is unreliable and fails. Accordingly, it will have a self repair 
 * process to let the robot continue its exploration in the maze.
 * 
 * Collaborators: Maze, Floorplan
 * Extends ReliableSensor
 * Implements DistanceSensor and ReliableSensor
 * 
 * @author DuohanXu
 *
 */
public class UnreliableSensor extends ReliableSensor implements DistanceSensor, Runnable{
	/**
	 * Implements Runnable to offer the sensor a thread that could stop
	 * it from operating while repairing. It can also resume the sensor
	 * when repairing is done.
	 */
	
	protected Thread sensorThread;
	private int repairTime = 2000;
	private int failTime = 4000;
	private String whichSensor;
	
	
	
	public UnreliableSensor() {
		super();
	}
	
	/**
	 * Run the thread. Sets the sensor to fail when it's time to fail
	 * and repairs it.
	 */
	@Override
	public void run() {
        boolean running = true;
        while (running) {
        	//Alternates the state of the sensor by giving it times to fail and repair.
        	//Sleeps for certain time and trigger the failing or repairing process.

            try {
                Thread.sleep(failTime);
            } catch (InterruptedException e) {
                running = false;
            }
            setWorking(false);

            try {
                Thread.sleep(repairTime);
            } catch (InterruptedException e) {
                running = false; //stops it from running
            }
            setWorking(true);


            
        }
    }
	
	/**
	 * Start the failure and repair thread. 
	 * Run the thread by calling the start method.
	 */
	@Override
	public void startFailureAndRepairProcess(int meanTimeBetweenFailures, int meanTimeToRepair)
			throws UnsupportedOperationException {
		if (sensorThread == null) {
			sensorThread = new Thread(this);
			repairTime = meanTimeToRepair;
			failTime = meanTimeBetweenFailures;
			setWorking(true);
			sensorThread.start();
		}
	}

	/**
	 * Stops the failure and repair thread.
	 * 
	 */
	@Override
	public void stopFailureAndRepairProcess() throws UnsupportedOperationException {
		setWorking(true);
		sensorThread.interrupt();
		
	}

	/**
	 * A helper method to show if the sensor is working.
	 */
	@Override
	public boolean getWorking() {
		return working;
	}
	
	/**
	 * A helper method to set the state of working.
	 */
	public void setWorking(boolean works) {
		working = works;
	}

	@Override
	public void setWhichSensor(String sensor){
		whichSensor = sensor;
	}
}
