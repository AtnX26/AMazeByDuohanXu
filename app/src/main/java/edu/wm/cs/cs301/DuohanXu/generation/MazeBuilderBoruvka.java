package edu.wm.cs.cs301.DuohanXu.generation;

import java.util.Random;
import java.util.ArrayList;

//reference: https://en.wikipedia.org/wiki/Bor%C5%AFvka%27s_algorithm
public class MazeBuilderBoruvka extends MazeBuilder implements Runnable {
	private int[] randomnumbers;
	private int[][] weight;
	private int[][][] array3d;
	
	public MazeBuilderBoruvka() {
		
	}
	
	
	/**
	 * generates a set of random numbers and store them in an array
	 * after setting up random numbers, it passes the value in the array to an 2d array as a record of weights
	 * notice that the array is expanded to its 2 times of height so that it can store wallboard information from all four directions
	 */
	public void setUpWeights() {
		int k = 0;
		randomnumbers = new int[width*(height*2-1)];
        for (int i = 0; i < randomnumbers.length; i++)
        {
        	randomnumbers[i] = i+1;//initiate the list
        }

        Random randomGenerator = new Random();
        int randomIndex; 
        int randomValue; 
        for(int i = 0; i < randomnumbers.length; i++)
        {
             randomIndex = randomGenerator.nextInt(randomnumbers.length);
             randomValue = randomnumbers[randomIndex];
             randomnumbers[randomIndex] = randomnumbers[i];
             randomnumbers[i] = randomValue;
        }
        for(int i = 0; i< weight.length; i++) {
        	for(int j = 0; j<weight[i].length;j++) {
        		weight[i][j] = randomnumbers[k];
        		k++;
        	}
        }
	}
	
	/**
	 * initialize an int weight = 0, so it returns 0 if it is on the border line
	 * split the case into four conditions as the CardinalDirection changes
	 * 
	 */
	public int getEdgeWeight(int x, int y, CardinalDirection cd) {
		int edgeweight = 0;
		Wallboard wallboard = new Wallboard(x, y, cd);
		if (floorplan.isPartOfBorder(wallboard)) {
			return edgeweight;
		}
		else{

		if(cd == CardinalDirection.North) {
			edgeweight = weight[x][y*2-1];
		}	
		if(cd == CardinalDirection.South) {
			edgeweight = weight[x][y*2+1];
		}
		if(cd == CardinalDirection.East) {
			edgeweight = weight[x+1][y*2];
		}
		if(cd == CardinalDirection.West) {
			edgeweight = weight[x][y*2];
		}
		
		return edgeweight;
		}
	}
	
	
	/**
	 * setUpWeights, and initiate an 3-d array to store numbers
	 * create an arraylist for wallboards using a private method
	 * use a while loop to 
	 * 
	 * 
	 */
	@Override
	protected void generatePathways() {
		weight = new int[width][height*2-1];
		setUpWeights();
		
		int num = 0;
		array3d = new int[width][height][2];
		for(int i = 0; i<array3d.length;i++) {
			for(int j = 0; j<array3d[i].length;j++) {
				array3d[i][j][0]=num;
				array3d[i][j][1]=-1;
				num ++;
			}
		}
		
		final ArrayList<Wallboard> candidates = new ArrayList<Wallboard>();
		createArrayList(candidates);
		boolean complete = false;
		while(complete == false) {
		for(int i = 0; i<candidates.size();i++) {
				int x = candidates.get(i).getX();
				int y = candidates.get(i).getY();
				int set = array3d[x][y][0];
				int nextX = candidates.get(i).getNeighborX();
				int nextY = candidates.get(i).getNeighborY();
				int anoSet = array3d[nextX][nextY][0];
				if (set != anoSet) {
					int asd = i;
					int sdf = array3d[x][y][1];
					if(isPreferredOver(asd, sdf, candidates)==true) {
					setCheapestEdge(set, asd);
					}
					int dfg = array3d[nextX][nextY][1];
					if(isPreferredOver(asd, dfg, candidates)==true) {
						setCheapestEdge(anoSet, asd);
					}
				}
			}
		if(checkNoneEdge() == true){
			complete = true;
		}
		else {
			complete = false;
			breakWall(candidates);
			
		}
	
		}
	}
	
	private boolean checkNoneEdge() {
		for(int j = 0; j<array3d.length; j++) {
			for(int k = 0; k<array3d[j].length; k++) {
				if (array3d[j][k][1]!=-1) {
				return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * the method mentioned above that creates an ArrayList containing wallboards by looping and checking if it can be added (that is, not added on the border)
	 */
	private void createArrayList(ArrayList<Wallboard> walls) {
		for (int i = 0; i < width; i++){
			for (int j = 0; j < height; j++){
					Wallboard wall1 = new Wallboard(i, j, CardinalDirection.East);
					if (floorplan.canTearDown(wall1)==true){
						walls.add(wall1);
					}
					Wallboard wall2 = new Wallboard(i, j, CardinalDirection.South);
					if (floorplan.canTearDown(wall2)==true){
						walls.add(wall2);
					}
			}
		}
	}
	
	/**
	 * a private method that evaluates if an edge is preferred over another
	 */
	private boolean isPreferredOver(int a, int b, ArrayList<Wallboard> candidates) {
		

		Wallboard candidatea = candidates.get(a);
		int candidatea_x = candidatea.getX();
		int candidatea_y = candidatea.getY();
		CardinalDirection candidatea_cd = candidatea.getDirection();
		Wallboard candidateb = candidates.get(b);
		int candidateb_x = candidateb.getX();
		int candidateb_y = candidateb.getY();
		CardinalDirection candidateb_cd = candidateb.getDirection();
		if( b == -1 || getEdgeWeight(candidatea_x, candidatea_y, candidatea_cd) < getEdgeWeight(candidateb_x, candidateb_y, candidateb_cd)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	/**
	 * let the second level of the 3-d array become the index of the cheapest edge to the given edge
	 */
	private void setCheapestEdge(int set, int index) {
		for(int i = 0; i < array3d.length;i++) {
			for(int j = 0; j < array3d[i].length;j++) {
				if(array3d[i][j][0]==set) {
					array3d[i][j][1] = index;
				}
			}
		}
	}
	
	/**
	 * delete given wallboards if necessary
	 */
	
	private void breakWall(ArrayList<Wallboard> candidates) {
		for(int i = 0; i<array3d.length;i++) {
			for(int j = 0; j<array3d[i].length;j++) {
				if (array3d[i][j][1]!=-1) {
					int index = array3d[i][j][1];
					int set = array3d[i][j][0];
					Wallboard wall = candidates.get(index);
					int nextx = wall.getNeighborX();
					int nexty = wall.getNeighborY();
					int anoset = array3d[nextx][nexty][0];
					if(floorplan.canTearDown(candidates.get(index))) {
						floorplan.deleteWallboard(candidates.get(index));
						merge(set,anoset);
					}
				}
			}
		}
	}
	
	/**
	 * merge two sets
	 */
	private void merge(int set, int anoset) {

		for(int i = 0; i<array3d.length;i++) {
			for(int j =0; j<array3d[i].length;j++) {
				if (array3d[i][j][0]==anoset || array3d[i][j][0]==set) {
					array3d[i][j][0]=set;
					array3d[i][j][1]=-1;
				}
			}
		}
	}

	
}
