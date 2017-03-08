
public class PlayerSkeleton {
	
	
	/* Features
	 * 
	 * Fei: 5, 10, 15
	 * 
	 */
	
	//FEATURE 5
	//Height differences - sum of the height differences between adjacent columns
	/*
	 * TODO: can we remove this feature and just keep feature 15 instead?
	 */
	public static double calculateFeature5(State s) {
		
		double sumHeightDiff = 0;
		
		int[] top = s.getTop(); //holds the height of each column
		
		for (int i = 1; i<top.length; i++) {
			sumHeightDiff += top[i-1] - top[i];
		}
		
		return sumHeightDiff;
	}
	
	//FEATURE 10
	//Well Count - The number of uncovered holes that are 3 or more blocks deep
	public static int calculateFeature10(State s) {
		
		int numberOfWells = 0;
		int[] top = s.getTop(); //holds the height of each column
		
		for (int i = 1; i<top.length; i++) {
			int diff = Math.abs(top[i-1] - top[i]);
			if (diff >= 3) {
				numberOfWells++;
			}
		}
		
		System.out.println("Number of wells: "+numberOfWells);
		return numberOfWells;
	}
	
	//FEATURE 15
	//Column difference - height difference between each pair of adjacent columns
	/*
	 * TODO: do we really need both sum height differences and individual column differences?
	 * Instead of returning 15 distinct values, I have applied the individual column weights directly in this function, and opted to return the sum
	 * i.e. This function will return the weighted sum of column weights (instead of 9 unique column height differences)
	 * This way we can get rid of feature 5, and instead just have this weighted version of column heights instead
	 * An additional weight for the sum of column heights can still be added in calculateUtility()
	 */
	public static double calculateFeature15(State s, double[] colDiffWeights) {
		
		double weightedSumHeightDiff = 0;
		
		//top[] holds the height of each column
		int[] top = s.getTop();
				
		for (int i = 1; i<top.length; i++) {
			weightedSumHeightDiff += colDiffWeights[i-1] * (top[i-1] - top[i]);
		}
		
		return weightedSumHeightDiff;
	}
	
	
	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves) {
		
		return 0;
		
	}

	public static double calculateUtility(State s) {
		int noFeatures = 15;

		double[] weights = new double[noFeatures];
		
		//Set weights
		/*
		 * TODO: maybe start our naming for features from 0 instead of 1? 
		 * - that will make the naming of our weights more consistent
		 */
		weights[4] = 0.4;
		weights[9] = 0.2;
		weights[14] = 0.6;
		
		//Column Difference weights
		//Used for feature 15 when calculating importance of individual column-pairs (otherwise why split calculate individual pairings?)
		double[] colDiffWeights = new double[State.COLS-1];
		colDiffWeights[0] = 0.0;
		colDiffWeights[1] = 0.0;
		colDiffWeights[2] = 0.0;
		colDiffWeights[3] = 0.0;
		colDiffWeights[4] = 0.0;
		colDiffWeights[5] = 0.0;
		colDiffWeights[6] = 0.0;
		colDiffWeights[7] = 0.0;
		colDiffWeights[8] = 0.0;
		
		//calculate feature values
		double feature5 = calculateFeature5(s);
		int feature10 = calculateFeature10(s);
		double feature15 = calculateFeature15(s, colDiffWeights);

		//apply weights
		double utility = weights[4]*feature5 + weights[9]*feature10 + weights[14]*feature15;
		
		return utility;
	}
	
	public static void main(String[] args) {
		State s = new State();
		new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
			s.draw();
			s.drawNext(0,0);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			System.out.println(calculateUtility(s));
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}
	
	//TESTING METHOD
	//prints out the values of s.top (height of each column) and s.field (game board)
	public void testPrintBoard(State s) {
		
		int[] top = s.getTop();
		int[][] field = s.getField();
		
		System.out.println("Printing Top:");
		for (int i = 0; i<top.length; i++) {
			System.out.print(top[i] + " ");
		}
		System.out.println();
		
		System.out.println("Printing field:");
		for (int i = 0; i<field.length; i++) {
			for(int j = 0; j<field[i].length; j++) {
				System.out.print(field[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
		
	}
	
	
}
