public class Features {
    //Column Difference weights
    //Used for feature 15 when calculating importance of individual column-pairs (otherwise why split calculate individual pairings?)
    private static final double[] colDiffWeights = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};

    // FEATURE 1
    // Filled Spot count - The number of filled spots on the game board
    public static double calculateFeature1(int[] top, int[][] field) {
        int sum = 0;

        for (int[] row : field ){
            for (int value : row){
                if (value > 0)
                    sum++;
            }
        }

        return sum;
    }

    // FEATURE 2
    // Weighted Filled Spot Count - Similar to the above, but spots in row i counts i times as much as blocks in row 1 
    public static double calculateFeature2(int[] top, int[][] field) {
    	int sum = 0;
    	int i = 1;
    	for (int i = 0; i < State.ROWS; i++){
    		for (int j = 0; j < State.COLS; j++){
    			if (field[i][j]> 0)
    				sum+= i;
    		}
    		i++;
    	}
    	return sum;
    }

    //FEATURE 3
    //Maximum Height - The height of the tallest column on the game board
    public static double calculateFeature3(int[] top, int[][] field) {
        double maxHeight = 0;

        for (int height : top)
            maxHeight = Math.max(maxHeight, height);

        return maxHeight;
    }

    // FEATURE 4

    public static double calculateFeature4(int[] top, int[][] field) {
        return 0;
    }

    //FEATURE 5
    //Height differences - sum of the height differences between adjacent columns
	/*
	 * TODO: can we remove this feature and just keep feature 15 instead?
	 */
    public static double calculateFeature5(int[] top, int[][] field) {

        double sumHeightDiff = 0;

        for (int i = 1; i<top.length; i++) {
            sumHeightDiff += Math.abs(top[i-1] - top[i]);
        }

        return sumHeightDiff;
    }

    // FEATURE 6
    // Max height difference
    public static double calculateFeature6(int[] top, int[][] field) {
        double maxHeightDiff = 0;
        double diff;

        for (int i = 1; i<top.length; i++) {
            diff = Math.abs(top[i-1] - top[i]);
            if(diff > maxHeightDiff)
                maxHeightDiff = diff;
        }

        return maxHeightDiff;
    }

    // FEATURE 7
    //Hole Count - The number of unfilled spots that have at least one filled spot above them
    public static double calculateFeature7(int[] top, int[][] field) {
        int holes = 0;
        
        for(int i = 0; i<State.ROWS-1; i++){
        	for(int j = 0;j<State.COLS; j++){
        		for(int k = i+1; k<State.ROWS; k++){
        			if(field[i][j] == 0 && field[k][j]>0){
        				holes++;
        				k=State.ROWS-1;
        			}
        		}
        	}
        }
        
        return holes;
    }

    //FEATURE 8
    //Deepest Hole - The depth of the deepest hole (a width-1 hole with filled spots on both sides)
    public static int calculateFeature8(int[] top, int[][] field) {

        for(int r = 0; r < State.ROWS; r++) {
            for(int c = 0; c < State.COLS; c++) {
                int current = field[r][c];
                int left = c==0 ? 1 : field[r][c-1];
                int right = c==State.COLS-1 ? 1 : field[r][c+1];

                if (current==0 && left!=0 && right!=0)
                    return r;
            }
        }

        return 0;
    }

    // FEATURE 9
    public static double calculateFeature9(int[] top, int[][] field) {
        return 0;
    }

    //FEATURE 10
    //Well Count - The number of uncovered holes that are 3 or more blocks deep
    public static double calculateFeature10(int[] top, int[][] field) {

        double numberOfWells = 0;

        for (int i = 1; i<top.length; i++) {
            int diff = Math.abs(top[i-1] - top[i]);
            if (diff >= 3) {
                numberOfWells++;
            }
        }

        return numberOfWells;
    }

    // FEATURE 11
    // Sum of all well depths (Well = width-1 or not?)
    // What about when they are not in the top? - We should define wells from scratch!
    public static double calculateFeature11(int[] top, int[][] field) {

        double sumOfWells = 0;
        int diff1, diff2;

        for (int i = 0; i<top.length; i++) {
            diff1 = (i == 0 ) ? (State.ROWS - top[i]) : Math.abs(top[i-1] - top[i]);
            diff2 = (i == top.length-1 ) ? (State.ROWS - top[i]) : Math.abs(top[i+1] - top[i]);

            if (diff1 >= 3 && diff2 >= 3) {
                sumOfWells++;
            }
        }

        return sumOfWells;
    }

    // FEATURE 12
    // Game Status - Based on the game status, 1 for a losing state, 0 otherwise
    public static double calculateFeature12(int[] top, int[][] field) {
        for(int i : top){
        	if (i >= State.COLS){
        		return 1;
        	}
        }
        return 0;
        	
    }

    //FEATURE 13
    //Column sum - Aggregated height of all the columns.
    public static double calculateFeature13(int[] top, int[][] field) {
        double columnSum = 0;

        for (int height : top)
            columnSum += height;

        return columnSum;
    }

    // FEATURE 14
    public static double calculateFeature14(int[] top, int[][] field) {
        return 0;
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
    public static double calculateFeature15(int[] top, int[][] field) {

        double weightedSumHeightDiff = 0;

        for (int i = 1; i<top.length; i++) {
            weightedSumHeightDiff += colDiffWeights[i-1] * (Math.abs(top[i-1] - top[i]));
        }

        return weightedSumHeightDiff;
    }
}
