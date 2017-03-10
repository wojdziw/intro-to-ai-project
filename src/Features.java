public class Features {
    //Column Difference weights
    //Used for feature 15 when calculating importance of individual column-pairs (otherwise why split calculate individual pairings?)
    private static final double[] colDiffWeights = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    //FEATURE 3
    //Maximum Height - The height of the tallest column on the game board
    public static double calculateFeature3(int[] top) {
        double maxHeight = 0;

        for (int height : top)
            maxHeight = Math.max(maxHeight, height);

        return maxHeight;
    }

    //FEATURE 5
    //Height differences - sum of the height differences between adjacent columns
	/*
	 * TODO: can we remove this feature and just keep feature 15 instead?
	 */
    public static double calculateFeature5(int[] top) {

        double sumHeightDiff = 0;

        for (int i = 1; i<top.length; i++) {
            sumHeightDiff += Math.abs(top[i-1] - top[i]);
        }

        return sumHeightDiff;
    }

    //FEATURE 8
    //Deepest Hole - The depth of the deepest hole (a width-1 hole with filled spots on both sides)
    public static int calculateFeature8(int[][] field) {

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

    //FEATURE 10
    //Well Count - The number of uncovered holes that are 3 or more blocks deep
    public static int calculateFeature10(int[] top) {

        int numberOfWells = 0;

        for (int i = 1; i<top.length; i++) {
            int diff = Math.abs(top[i-1] - top[i]);
            if (diff >= 3) {
                numberOfWells++;
            }
        }

        return numberOfWells;
    }

    //FEATURE 13
    //Column sum - Aggregated height of all the columns.
    public static double calculateFeature13(int[] top) {
        double columnSum = 0;

        for (int height : top)
            columnSum += height;

        return columnSum;
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
    public static double calculateFeature15(int[] top) {

        double weightedSumHeightDiff = 0;

        for (int i = 1; i<top.length; i++) {
            weightedSumHeightDiff += colDiffWeights[i-1] * (Math.abs(top[i-1] - top[i]));
        }

        return weightedSumHeightDiff;
    }
}
