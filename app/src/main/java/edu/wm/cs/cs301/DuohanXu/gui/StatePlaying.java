package edu.wm.cs.cs301.DuohanXu.gui;

import edu.wm.cs.cs301.DuohanXu.gui.Constants.UserInput;

import java.util.logging.Logger;

import edu.wm.cs.cs301.DuohanXu.generation.CardinalDirection;
import edu.wm.cs.cs301.DuohanXu.generation.Floorplan;
import edu.wm.cs.cs301.DuohanXu.generation.Maze;


/**
 * Class handles the user interaction
 * while the game is in the third stage
 * where the user plays the game.
 * This class is part of a state pattern for the
 * Controller class.
 * 
 * It implements a state-dependent behavior that controls
 * the display and reacts to key board input from a user. 
 * At this point user keyboard input is first dealt
 * with a key listener in control and then 
 * and then handed over 
 * by way of the handleUserInput method.
 * 
 * Responsibilities:
 * Keep track of the current position and direction in the game.
 * Show the first person view and the map view,
 * Accept input for manual operation (left, right, up, down etc),  
 * Update the graphics, recognize termination.
 *
 * This code contains refactored code from Maze.java by Paul Falstad, 
 * www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 */
public class StatePlaying{
	
	
	/**
	 * The logger is used to track execution and report issues.
	 * Collaborators are the UI and the MazeFactory.
	 * Level INFO: logs mitigated issues such as unexpected user input
	 * Level FINE: logs information flow in and out of its fields.
	 */
	private static final Logger LOGGER = Logger.getLogger(StatePlaying.class.getName());

	/**
	 * The first person view determines what is seen on the screen with a first person perspective.
	 * This includes the background of two rectangles that cover the whole area.
	 * Drawing polygons for walls is the main contribution.
	 */
	private FirstPersonView firstPersonView;
	/**
	 * The view determines what is seen on the screen for a top view.
	 * Drawing the maze from above as a lines for walls, the current position and direction, 
	 * and the solution as a yellow line is the man contribution.
	 * The map is drawn on top of the first person view.
	 */
	private Map mapView;
	/**
	 * The compass rose provides additional guidance on the current direction
	 * and visualizes it with a compass rose. It draws on top of the 
	 * first person view. As map and compass rose compete for space on the 
	 * screen, one can show at most one of the two at any point in time.
	 */
	private CompassRose cr; 
	   
    /**
     * The panel is the capability to draw on the screen.
     */
    private MazePanel panel;
    
    /**
     * Control is the context class of the State pattern.
     * The reference is needed to pull some pieces of information
     * plus switch control to the next state, which 
     * is the maze generating state.
     */
    //private Control control;

    /**
     * The robot that interacts with the controller starting from P3
     */
    //Robot robot;
    /**
     * The driver that interacts with the robot starting from P3
     */
    //RobotDriver driver;

    /**
     * Maze holds the main information on where walls are.
     */
    Maze maze ;

    private PlayAnimationActivity playAnimationActivity;
    private PlayManuallyActivity playManuallyActivity;
    private boolean showMaze;           // toggle switch to show overall maze on screen
    private boolean showSolution;       // toggle switch to show solution in overall maze on screen
    private boolean mapMode; // true: display map of maze, false: do not display map of maze
    // mapMode is toggled by user keyboard input, causes a call to drawMap during play mode
    private boolean playManually = false;

    // current position and direction with regard to MazeConfiguration
    int px, py ; // current position on maze grid (x,y)
    int dx, dy;  // current direction
    CardinalDirection cd;

    int angle; // current viewing angle, east == 0 degrees
    int walkStep; // counter for intermediate steps within a single step forward or backward
    Floorplan seenCells; // a matrix with cells to memorize which cells are visible from the current point of view
    // the FirstPersonView obtains this information and the Map uses it for highlighting currently visible walls on the map
    
    // debug stuff
    //private boolean deepdebug = false;
    //private boolean allVisible = false;
    //private boolean newGame = false;

    /** 
     * Started is used to enforce ordering constraint on method calls.
     * start() must be called before keyDown()
     * to make sure control variable has been set.
     * initial setting: false, start sets it to true.
     */
    boolean started;  
  
    /**
     * Constructor uses default settings but does not deliver a fully operation instance,
     * requires a call to start() and setMaze().
     */
    public StatePlaying() {
    	// initialization of some fields is delayed and done in start method
    	firstPersonView = null; // initialized in start method
    	mapView = null; // initialized in start method
    	panel = null; // provided by start method
    	//control = null; // provided by start method
    	started = false; // method start has not been called yet

    	maze = null; // provided by set method

    	// visibility settings, default values
    	showMaze = false ;
    	showSolution = false ;
    	mapMode = false;

    	// initial position and direction in absence of maze
    	// position at [0,0], direction is east
    	px = 0;
    	py = 0;
    	cd = CardinalDirection.East;

    	seenCells = null;
    	cr = null;
    }
    /**
     * Provides the maze to play.
     * @param maze a fully operational complete maze ready to play
     */
    public void setMaze(Maze maze) {
        this.maze = maze;
    }

    public void setPlayAnimationActivity(PlayAnimationActivity playAnimationActivity){
        this.playAnimationActivity = playAnimationActivity;
    }

    public void setPlayManuallyActivity(PlayManuallyActivity playManuallyActivity){
        this.playManuallyActivity = playManuallyActivity;
        playManually = true;
    }
    /**
     * Start the actual game play by showing the playing screen.
     * If the panel is null, all drawing operations are skipped.
     * * This mode of operation is useful for testing purposes,
     * i.e., a dryrun of the game without the graphics part.
     * @param panel is part of the UI and visible on the screen, needed for drawing
     */
    public void start(MazePanel panel) {
        maze = GeneratingActivity.maze;
        assert null != maze : "StatePlaying.start: maze must exist!";
    	
        started = true;
        // keep the reference to the controller to be able to call method to switch the state
        //control = controller;
        // keep the reference to the panel for drawing
        this.panel = panel;
        //
        // adjust visibility settings to default values
        showMaze = false ;
        showSolution = false ;
        mapMode = false;

        // adjust internal state of maze model
        // init data structure for visible walls
        seenCells = new Floorplan(maze.getWidth()+1,maze.getHeight()+1) ;
        // set the current position and direction consistently with the viewing direction
        setPositionDirectionViewingDirection();

        if (panel != null) {
        	startDrawer();
        }
        else {
        	// else: dry-run without graphics, most likely for testing purposes
        	printWarning();
        }
        /**
         * The code below initiates the robot driving process if the controller has them.
         *
        robot = controller.getRobot();
        driver = controller.getDriver();
        String sens = controller.getReOrUnre();
        String f = "";
        String l = "";
        String r = "";
        String b = "";
        if (sens != ""){
        f = sens.substring(0,1);
        l = sens.substring(1,2);
        r = sens.substring(2,3);
        b = sens.substring(3);
        }
        if (robot != null) {
        	assert(driver!=null): "Error: must have a driver!";
        	showMaze = true;
        	showSolution = true;
        	mapMode = true;
        	if ((f.equals("0"))||(l.equals("0"))||(r.equals("0"))||(b.equals("0"))) {
        		robot = new UnreliableRobot();
        		robot.setController(control);
        		
        		DistanceSensor fsensor = f.equals("0")? new UnreliableSensor():new ReliableSensor();
        		fsensor.setMaze(maze);
        		robot.addDistanceSensor(fsensor, Direction.FORWARD);
        		DistanceSensor lsensor = l.equals("0")? new UnreliableSensor():new ReliableSensor();
        		lsensor.setMaze(maze);
        		robot.addDistanceSensor(lsensor, Direction.LEFT);
        		DistanceSensor rsensor = r.equals("0")? new UnreliableSensor():new ReliableSensor();
        		rsensor.setMaze(maze);
        		robot.addDistanceSensor(rsensor, Direction.RIGHT);
        		DistanceSensor bsensor = b.equals("0")? new UnreliableSensor():new ReliableSensor();
        		bsensor.setMaze(maze);
        		robot.addDistanceSensor(bsensor, Direction.BACKWARD);
        		driver.setMaze(maze);
        		driver.setRobot(robot);
        		startSensor();
        		drive();
        	}
        	else {
        		robot.setController(control);
        		for (Direction d: Direction.values()) {
        			DistanceSensor sensor = new ReliableSensor();
        			sensor.setMaze(maze);
        			robot.addDistanceSensor(sensor, d);
        		}
        		driver.setMaze(maze);
        		driver.setRobot(robot);
        		drive();
        }
        }*/
    }

    /**
    private void startSensor() {
    	Runnable process = () -> {
    		for (Direction dir : Direction.values()) {
    			try {
    				robot.startFailureAndRepairProcess(dir, 4000, 2000);
    				Thread.sleep(1300);
    			} catch (UnsupportedOperationException ex) {
    				continue;
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
    	}
    	};
    	Thread sensorThread = new Thread(process);
    	sensorThread.start();
    }
    
    private void resetRobot() {
    	for (Direction dir : Direction.values()) {
			try {
				robot.stopFailureAndRepairProcess(dir);
			} catch (UnsupportedOperationException ex) {
				continue;
			} 
		
    	}
    	if (robot instanceof ReliableRobot) {
    		control.robot = new ReliableRobot();
    	}
    	else {
    		control.robot = new UnreliableRobot();
    	}
    }
    private void drive() {
    	try {
    		if (driver.drive2Exit()) {
    			if (driver != control.getDriver()) {
    				switchToTitle();
    			}
    			else {
    				int energyConsumption = (int) driver.getEnergyConsumption();
    				resetRobot();
    				switchFromPlayingToWinning(driver.getPathLength(), energyConsumption, true);
    			}
    		} else {
    			int energyConsumption = (int) driver.getEnergyConsumption();
    			resetRobot();
				switchFromPlayingToWinning(driver.getPathLength(), energyConsumption, false);
    		}
    	}catch (Exception e) {
    		int energyConsumption = (int) driver.getEnergyConsumption();
    		resetRobot();
			switchFromPlayingToWinning(driver.getPathLength(), energyConsumption, false);
    	}
    }
    */
    
    /**
     * Initializes the drawer for the first person view
     * and the map view and then draws the initial screen
     * for this state.
     */
	protected void startDrawer() {
		cr = new CompassRose();
		cr.setPositionAndSize(Constants.VIEW_WIDTH/2,
				(int)(0.1*Constants.VIEW_HEIGHT),35);

		firstPersonView = new FirstPersonView(Constants.VIEW_WIDTH,
				Constants.VIEW_HEIGHT, Constants.MAP_UNIT,
				Constants.STEP_SIZE, seenCells, maze.getRootnode()) ;
		
		mapView = new Map(seenCells, 15, maze) ;
		// draw the initial screen for this state
		//draw(cd.angle(), 0);
        draw();
	}
    /**
     * Internal method to set the current position, the direction
     * and the viewing direction to values consistent with the 
     * given maze.
     */
	private void setPositionDirectionViewingDirection() {
        int[] start = maze.getStartingPosition() ;
        setCurrentPosition(start[0],start[1]) ;
        //cd = CardinalDirection.East;
        angle = 0; // angle matches with east direction,
        // hidden consistency constraint!
        setDirectionToMatchCurrentAngle();
        // initial direction is east, check this for sanity:
        assert(dx == 1);
        assert(dy == 0);
	}
 
	/**
     * Switches the controller to the final screen
     * @param pathLength gives the length of the path
     *
    public void switchFromPlayingToWinning(int pathLength) {
    	// need to instantiate and configure the winning state
        StateWinning currentState = new StateWinning();
        
        // The playing state needs 
        // 1) the path length
        // 
        currentState.setPathLength(pathLength);
        
        LOGGER.fine("Control switches from playing to winning screen, game completed.");
        
        // update the context class with the new state
        // and hand over control to the new state
        control.setState(currentState);
        currentState.start(control, panel);
    }

    public void switchFromPlayingToWinning(int pathLength, int energyConsumption, boolean win) {
    	//resetRobot();
    	StateWinning currentState = new StateWinning();
    	currentState.setPathLength(pathLength);
    	LOGGER.fine("Control switches from playing to winning screen, game completed.");
    	currentState.setBooleanWin(win);
    	currentState.setEnergyConsumption(energyConsumption);
        control.setState(currentState);
        currentState.start(control, panel);
    }
    */
    /**
     * Switches the controller to the initial screen.
     *
    public void switchToTitle() {
       	// need to instantiate and configure the title state
        StateTitle currentState = new StateTitle();
        
        LOGGER.fine("Control switches from playing to title screen, game play interrupted.");
        
        // update the context class with the new state
        // and hand over control to the new state

        control.setState(currentState);
        currentState.start(control, panel);
    }
    */

    /**
     * The method provides an appropriate response to user keyboard input. 
     * The control calls this method to communicate input and delegate its handling.
     * Method requires {@link #start(MazePanel) start} to be
     * called before.
     * @param userInput provides the feature the user selected
     * @param value is not used in this state, exists only for consistency across State classes
     * @return false if not started yet otherwise true
     */
    public boolean handleUserInput(UserInput userInput, int value) {
    	// user input too early, not sure how this could happen
        if (!started) {
        	LOGGER.info("Premature keyboard input:" + userInput + "with value " + value + ", ignored for mitigation");
            return false;
        }

        // react to input for directions and interrupt signal (ESCAPE key)  
        // react to input for displaying a map of the current path or of the overall maze (on/off toggle switch)
        // react to input to display solution (on/off toggle switch)
        // react to input to increase/reduce map scale
        switch (userInput) {
        case START: // misplaced, do nothing
            break;
        case UP: // move forward
        	LOGGER.fine("Move 1 step forward");
            walk(1);
            // check termination, did we leave the maze?
            if (isOutside(px,py)) {
            	// TODO: provide actual path length
                //switchFromPlayingToWinning(0);
                if(playManually){
                    playManuallyActivity.sendWinningMessage(panel);
                }
                else{
                    playAnimationActivity.sendWinningMessage(panel);
                }
                break;
            }
            break;
        case LEFT: // turn left
        	LOGGER.fine("Turn left");
            rotate(1);
            break;
        case RIGHT: // turn right
        	LOGGER.fine("Turn right");
            rotate(-1);
            break;
        case DOWN: // move backward
        	LOGGER.fine("Move 1 step backward");
            walk(-1);
            // check termination, did we leave the maze?
            if (isOutside(px,py)) {
            	// TODO: provide actual path length
                //switchFromPlayingToWinning(0);
                if(playManually){
                    playManuallyActivity.sendWinningMessage(panel);
                }
                else{
                    playAnimationActivity.sendWinningMessage(panel);
                }
            }
            break;
        case RETURNTOTITLE: // escape to title screen
            //switchToTitle();
            break;
        case JUMP: // make a step forward even through a wall
        	LOGGER.fine("Jump 1 step forward");
            // go to position if within maze
        	//int[] tmpDxDy = cd.getDxDyDirection();
        	//if (maze.isValidPosition(px + tmpDxDy[0], py + tmpDxDy[1])) {
                //setCurrentPosition(px + tmpDxDy[0], py + tmpDxDy[1]) ;
               // draw(cd.angle(), 0) ;
            //}

            if (maze.isValidPosition(px + dx, py + dy)) {
            setCurrentPosition(px + dx, py + dy) ;
            draw() ;
            }
            break;
        case TOGGLELOCALMAP: // show local information: current position and visible walls
            // precondition for showMaze and showSolution to be effective
            // acts as a toggle switch
            mapMode = !mapMode;
            draw() ;
            break;
        case TOGGLEFULLMAP: // show the whole maze
            // acts as a toggle switch
            showMaze = !showMaze;
            draw() ;
            break;
        case TOGGLESOLUTION: // show the solution as a yellow line towards the exit
            // acts as a toggle switch
            showSolution = !showSolution;
            draw() ;
            break;
        case ZOOMIN: // zoom into map
        	mapView.incrementMapScale();
            draw() ;
            break ;
        case ZOOMOUT: // zoom out of map
        	mapView.decrementMapScale();
            draw() ;
            break ;
        } // end of internal switch statement for playing state
        return true;
    }

    public void setMapScale(int scale){
        mapView.setScale(scale);
        draw();
    }
    /**
     * Draws the current content on panel to show it on screen.
     */
    protected void draw() {
    	 
    	if (panel == null) {
    		printWarning();
    		return;
    	}
    	// draw the first person view and the map view if wanted
    	firstPersonView.draw(panel, px, py, walkStep, angle, maze.getPercentageForDistanceToExit(px, py)) ;
        if (isInMapMode()) {
			mapView.draw(panel, px, py, angle, walkStep, isInShowMazeMode(),isInShowSolutionMode()) ;
		}
		// update the screen with the buffer graphics
        panel.commit() ;
    }
    float getPercentageForDistanceToExit() {
        return maze.getDistanceToExit(px, py) /
                ((float) maze.getMazedists().getMaxDistance());
    }

    /**
     * Prints the warning about a missing panel only once
     */
    boolean printedWarning = false;
    protected void printWarning() {
    	if (printedWarning)
    		return;
    	LOGGER.info("No panel for drawing during executing, dry-run game without graphics!");
    	printedWarning = true;
    }
    ////////////////////////////// set methods ///////////////////////////////////////////////////////////////
    ////////////////////////////// Actions that can be performed on the maze model ///////////////////////////
    protected void setCurrentPosition(int x, int y) {
        px = x ;
        py = y ;
    }
    private void setCurrentDirection(int x, int y) {
        dx = x ;
        dy = y ;
    }
    /**
     * Sets fields dx and dy to be consistent with
     * current setting of field angle.
     */
    private void setDirectionToMatchCurrentAngle() {
        setCurrentDirection((int) Math.cos(radify(angle)), (int) Math.sin(radify(angle))) ;
    }
    ////////////////////////////// get methods ///////////////////////////////////////////////////////////////
    protected int[] getCurrentPosition() {
        int[] result = new int[2];
        result[0] = px;
        result[1] = py;
        return result;
    }
    protected CardinalDirection getCurrentDirection() {
        //return cd;
        return CardinalDirection.getDirection(dx, dy);
    }
    boolean isInMapMode() { 
        return mapMode ; 
    } 
    boolean isInShowMazeMode() { 
        return showMaze ; 
    } 
    boolean isInShowSolutionMode() { 
        return showSolution ; 
    } 
    public Maze getMaze() {
        return maze ;
    }
    //////////////////////// Methods for move and rotate operations ///////////////
    final double radify(int x) {
        return x*Math.PI/180;
    }
    /**
     * Determines if one can walk in the given direction
     * @param dir is the direction of interest, either 1 or -1
     * @return true if there is no wall in this direction, false otherwise
     */
    protected boolean wayIsClear(int dir) {
        switch (dir) {
        case 1: // forward
            cd = getCurrentDirection();
        	return !maze.hasWall(px, py, cd);
        case -1: // backward
            cd = getCurrentDirection().oppositeDirection();
            return !maze.hasWall(px, py, cd.oppositeDirection());
        default:
            throw new RuntimeException("Unexpected direction value: " + dir);
        }
    }
    /**
     * Draws and waits. Used to obtain a smooth appearance for rotate and move operations
     */
    private void slowedDownRedraw() {
    	LOGGER.fine("Drawing intermediate figures: angle " + angle + ", walkStep " + walkStep);
        draw() ;
        try {
            Thread.sleep(25);
        } catch (Exception e) { 
        	// may happen if thread is interrupted
        	// no reason to do anything about it, ignore exception
        }
    }
 	
    /**
     * Performs a rotation with 4 intermediate views, 
     * updates the screen and the internal direction
     * @param dir for current direction, values are either 1 or -1
     */
    private synchronized void rotate(int dir) {
        final int originalAngle = angle;//angle;
        final int steps = 4;
        //int angle = originalAngle; // just in case for loop is skipped
        for (int i = 0; i != steps; i++) {
            // add 1/4 of 90 degrees per step 
            // if dir is -1 then subtract instead of addition
            angle = originalAngle + dir*(90*(i+1))/steps; 
            angle = (angle+1800) % 360;
            // draw method is called and uses angle field for direction
            // information.
			slowedDownRedraw();
        }
        // update maze direction only after intermediate steps are done
        // because choice of direction values are more limited.
        //cd = CardinalDirection.getDirection(angle);
        setDirectionToMatchCurrentAngle();
        logPosition(); // debugging
        drawHintIfNecessary(); 
    }
	
    /**
     * Moves in the given direction with 4 intermediate steps,
     * updates the screen and the internal position
     * @param dir, only possible values are 1 (forward) and -1 (backward)
     */
    private synchronized void walk(int dir) {
    	// check if there is a wall in the way
        if (!wayIsClear(dir))
            return;

        // walkStep is a parameter of FirstPersonView.draw()
        // it is used there for scaling steps
        // so walkStep is implicitly used in slowedDownRedraw
        // which triggers the draw operation in 
        // FirstPersonView and Map
        for (int step = 0; step != 4; step++) {
            walkStep += dir;
            slowedDownRedraw();
        }
        // update position to neighbor
        //int[] tmpDxDy = cd.getDxDyDirection();
        //setCurrentPosition(px + dir*tmpDxDy[0], py + dir*tmpDxDy[1]) ;
        setCurrentPosition(px + dir*dx, py + dir*dy) ;
        walkStep = 0;
        logPosition(); // debugging
        drawHintIfNecessary();  
    }

    /**
     * Checks if the given position is outside the maze
     * @param x coordinate of position
     * @param y coordinate of position
     * @return true if position is outside, false otherwise
     */
    private boolean isOutside(int x, int y) {
        return !maze.isValidPosition(x, y) ;
    }
    /**
     * Draw a visual cue to help the user unless the 
     * map is on display anyway. 
     * This is the map if current position faces a dead end
     * otherwise it is a compass rose.
     */
    private void drawHintIfNecessary() {
    	if (isInMapMode())
    		return; // no need for help
    	// in testing environments, there is sometimes no panel to draw on
    	// or the panel is unable to deliver a graphics object
    	// check this and quietly move on if drawing is impossible
    	if ((panel == null || panel.getBufferGraphics() == null)) {
    		printWarning();
    		return;
    	}
    	// if current position faces a dead end, show map with solution
    	// for guidance
    	if (isFacingDeadEnd()) {
        	//System.out.println("Facing deadend, help by showing solution");
        	mapView.draw(panel, px, py, angle, walkStep, true, true) ;
        }
    	else {
    		// draw compass rose
    		cr.setCurrentDirection(getCurrentDirection());
    		cr.paintComponent(panel);
    	}
    	panel.commit();
    }

    private boolean isFacingDeadEnd() {
        return (!isOutside(px,py) &&
                GeneratingActivity.maze.hasWall(px, py, getCurrentDirection()) &&
                GeneratingActivity.maze.hasWall(px, py, getCurrentDirection().oppositeDirection().rotateClockwise()) &&
                GeneratingActivity.maze.hasWall(px, py, getCurrentDirection().rotateClockwise()));
    }
 
    /////////////////////// Methods for debugging ////////////////////////////////
    
    private void dbg(String str) {
        LOGGER.fine(str);
    }
    
    private void logPosition() {
    	 int[] tmpDxDy = cd.getDxDyDirection();
    	 LOGGER.fine("x="+px+",y="+py+",dx="+tmpDxDy[0]+",dy="+tmpDxDy[1]+",angle="+cd.angle());
    	//LOGGER.fine("x="+px+",y="+py+",dx="+dx+",dy="+dy+",angle="+angle);
    	/*
        if (!deepdebug)
            return;
        dbg("x="+viewx/Constants.MAP_UNIT+" ("+
                viewx+") y="+viewy/Constants.MAP_UNIT+" ("+viewy+") ang="+
                angle+" dx="+dx+" dy="+dy+" "+viewdx+" "+viewdy);
                */
    }
    
}



