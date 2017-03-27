
import java.util.Arrays;

import static java.lang.Math.min;


public class Features {

    public static int getNumberOfFeatures() {
        return 15 + (State.COLS - 1) + (State.COLS -2); //noFeatures + columnHeightWeights + columnDifferenceWeights
    }

    // FEATURE 1 + 2
    // 1: Filled Spot count - The number of filled spots on the game board
    // 2: Weighted Filled Spot Count - Similar to the above, but spots in row i counts i times
    public static double[] calculateFeature1_2(int[] top, int[][] field) {

        int sum = 0;
        int weightedSum = 0;
        double[] bothSums = new double[2];

        for (int i = 0; i < State.ROWS; i++){
            for (int j = 0; j < State.COLS; j++){

                if (field[i][j]> 0){
                    sum++;
                    weightedSum += i;
                }
            }
        }
        // Example of a normalization
        // TODO: decide if we should keep it!
        bothSums[0] = sum/State.COLS;
        bothSums[1] = weightedSum/State.COLS;
        return bothSums;
    }

    //FEATURE 3
    //Maximum Height - The height of the tallest column on the game board
    public static double calculateFeature3(int[] top, int[][] field) {
        double maxHeight = 0;

        for (int height : top)
            maxHeight = Math.max(maxHeight, height);

        return maxHeight;
    }

    //FEATURE 4
    // Min heights - minimum height of the boards columns, Min(ColumnHeight[0], .. ,ColumnHeight[n])
    public static Integer calculateFeature4(int[] top, int[][] field) {
        Integer minHeight = State.ROWS;  // sets the minvalue to the maxvalue in the beginning
        for (int columnHeight: top) {
            if (columnHeight < minHeight) {
                minHeight = columnHeight;
            }
        }
        return minHeight;
    }

    // FEATURE 6
    // Max height difference - maximum difference of height between two columns
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

    //FEATURE 8
    //Deepest Hole - The depth of the deepest hole (an unfillable spot = has at least a block above it)
    public static int calculateFeature8(int[] top, int[][] field) {

        for(int r = 0; r < State.ROWS; r++) {
            for(int c = 0; c < State.COLS; c++) {
                if (field[r][c]==0 && top[c] > r)
                    return r;
            }
        }

        return 0;
    }

    // FEATURE 9
    //Amount of Holes - The number of enclosed clusters/caves of holes
    public static double[] calculateFeature7_9(int[] top, int[][] field) {
        boolean [][] visited = new boolean[State.ROWS][State.COLS]; //2d array of false values
        int clusterCount = 0; // we start at cluster #0
        int nrOfHoleSpots = 0;
        int clearColumns;

        for (int row = 0; row<field.length; row++){
            clearColumns = 0;
            for (int col=0; col<field[0].length; col++){
                if (row > top[col])
                    clearColumns++;

                if(isSafe(field, row, col, visited, top))   {
                    clusterCount++;
                    nrOfHoleSpots += localClusterSearch(field, row, col, visited, top);
                }
            }
            if(clearColumns == State.COLS)
                break;
        }
        // We could calculate both in the same feature here!
        double [] result = new double[2];
        result[0] = nrOfHoleSpots;
        result[1] = clusterCount;
        return result;
    }

    private static int localClusterSearch(int[][] field, int row, int col, boolean[][] visited, int[] top) {

        /*
        //Try 1:
        //the we try to visit each of the four adjacent cells that are above, below, left, right.
        int rowNbr[] = {1,-1,0,0};
        int colNbr[] = {0,0,1,-1};
        // Mark this cell as visited
        visited[row][col] = true;
        // Recur for all connected neighbours
        for (int k = 0; k < rowNbr.length; ++k){
            if (isSafe(field, row + rowNbr[k], col + colNbr[k], visited, top)){
                localClusterSearch(field, row + rowNbr[k], col + colNbr[k], visited, top);
            }
        }
        */
        // Try 2:
        if (!isSafe(field, row, col, visited, top))
            return 0;

        // Mark this cell as visited
        visited[row][col] = true;
        return 1 + localClusterSearch(field, row, col+1 , visited, top) + localClusterSearch(field, row+1, col , visited, top)
                + localClusterSearch(field, row-1, col , visited, top) +localClusterSearch(field, row, col-1 , visited, top);

    }

    private static boolean isSafe(int field[][], int row, int col, boolean visited[][], int[] top) {
        //topmost row is only a validation row for some lose function....
        if ((row >= 0) && (row < field.length-1) && (col >= 0) && (col < field[0].length) && row<top[col]){
            return (field[row][col]==0 && !visited[row][col]);
        }
        return false;
    }

    //FEATURE 10
    //Well Count - The number of uncovered holes that are 3 or more blocks deep
    public static double calculateFeature10(int[] top, int[][] field) {

        int numberOfWells = 0;

        for (int i = 1; i<top.length; i++) {

            if (((i-1) == 0) || ((top[i-1] - top[i]) >= 3)) {
                if (i == (top.length-1) || ((top[i] - top[i+1]) <= -3)) {
                    numberOfWells++;
                }
            }
        }

        return numberOfWells;
    }

    // FEATURE 11
    // Sum of all well depths
    public static double calculateFeature11(int[] top, int[][] field) {

        double sumOfWells = 0;
        int diff1, diff2;

        for (int i = 0; i<top.length; i++) {
            diff1 = (i == 0 ) ? (State.ROWS - top[i]) : Math.abs(top[i-1] - top[i]);
            diff2 = (i == top.length-1 ) ? (State.ROWS - top[i]) : Math.abs(top[i+1] - top[i]);

            if (diff1 >= 3 && diff2 >= 3) {
                sumOfWells += min(diff1, diff2);
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

    //FEATURE 14
    //Column heights - height of each column
    //only converting int to double here....
    public static double[] calculateFeature14(int[] top, int[][] field) {
        double[] topDouble = new double[top.length];
        for (int i = 0; i<top.length; i++ ){
            topDouble[i] = top[i];
        }
        return topDouble;
    }

    public static double dotProduct(double[] X, double[] Y){
        double sumOfWeightedHeights = 0;
        for (int i=0; i <X.length; i++){
            sumOfWeightedHeights += X[i]*Y[i];
        }
        return sumOfWeightedHeights;
    }

    //FEATURE 15
    //Column difference - height difference between each pair of adjacent columns
    public static double[] calculateFeature15(int[] top, int[][] field) {

        double[] weightedSumHeightDiff = new double[top.length-1];

        for (int i = 1; i<top.length; i++) {
            weightedSumHeightDiff[i-1] = Math.abs(top[i-1] - top[i]);
        }

        return weightedSumHeightDiff;
    }

    static long[] featureTimeTaken = new long[15];
    static long featureTimesRunned = 0;
    public static double calculateUtility(int[][] field, int[] top, double[] weights, int rowsCleared) {

        //calculate feature values
		/*
        long startTime = System.nanoTime();
		double feature1 = Features.calculateFeature1(top, field);
        featureTimeTaken[0] += (System.nanoTime() - startTime);

        startTime = System.nanoTime();
        double feature2 = Features.calculateFeature2(top, field);
        featureTimeTaken[1] += (System.nanoTime() - startTime);
		*/
        long startTime = System.nanoTime();
        double[] feature1_2 = Features.calculateFeature1_2(top, field);
        featureTimeTaken[0] += (System.nanoTime() - startTime);

        startTime = System.nanoTime();
        double feature3 = Features.calculateFeature3(top, field);
        featureTimeTaken[2] += (System.nanoTime() - startTime);

        startTime = System.nanoTime();
        double feature4 = Features.calculateFeature4(top, field);
        featureTimeTaken[3] += (System.nanoTime() - startTime);

//      startTime = System.nanoTime();
//		double feature5 = Features.calculateFeature5(top, field);
//      featureTimeTaken[4] += (System.nanoTime() - startTime);

        startTime = System.nanoTime();
        double feature6 = Features.calculateFeature6(top, field);
        featureTimeTaken[5] += (System.nanoTime() - startTime);
/*
        startTime = System.nanoTime();
		double feature7 = Features.calculateFeature7(top, field);
        featureTimeTaken[6] += (System.nanoTime() - startTime);

        startTime = System.nanoTime();
		double feature8 = Features.calculateFeature8(top, field);
        featureTimeTaken[7] += (System.nanoTime() - startTime);

        startTime = System.nanoTime();
		double feature9 = Features.calculateFeature9(top, field);
        featureTimeTaken[8] += (System.nanoTime() - startTime);
*/
        startTime = System.nanoTime();
        double feature8 = Features.calculateFeature8(top, field);
        featureTimeTaken[7] += (System.nanoTime() - startTime);

        startTime = System.nanoTime();
        double[] feature7_9 = Features.calculateFeature7_9(top, field);
        featureTimeTaken[8] += (System.nanoTime() - startTime);

        startTime = System.nanoTime();
        double feature10 = Features.calculateFeature10(top, field);
        featureTimeTaken[9] += (System.nanoTime() - startTime);

        startTime = System.nanoTime();
        double feature11 = Features.calculateFeature11(top, field);
        featureTimeTaken[10] += (System.nanoTime() - startTime);

        startTime = System.nanoTime();
        double feature12 = Features.calculateFeature12(top, field);
        featureTimeTaken[11] += (System.nanoTime() - startTime);

        startTime = System.nanoTime();
        double feature13 = Features.calculateFeature13(top, field);
        featureTimeTaken[12] += (System.nanoTime() - startTime);

        startTime = System.nanoTime();
        double[] feature14 = Features.calculateFeature14(top, field);
        featureTimeTaken[13] += (System.nanoTime() - startTime);

        startTime = System.nanoTime();
        double[] feature15 = Features.calculateFeature15(top, field);
        featureTimeTaken[14] += (System.nanoTime() - startTime);
        featureTimesRunned++;


        double[] columnHeightWeights = Arrays.copyOfRange(weights, 14, (14 + field[0].length));
        double[] colDiffWeights = Arrays.copyOfRange(weights, (14 + field[0].length + 1), (14 + 2*field[0].length));

        // apply weights
        return weights[0]
                + weights[1]*feature1_2[0]
                + weights[2]*feature1_2[1]
                + weights[3]*feature3
                + weights[4]*feature4
                + weights[5]*rowsCleared
                + weights[6]*feature6
                + weights[7]*feature7_9[0]
                + weights[8]*feature8
                + weights[9]*feature7_9[1]
                + weights[10]*feature10
                + weights[11]*feature11
                + weights[12]*feature12
                + weights[13]*feature13
                + Features.dotProduct(columnHeightWeights, feature14)
                + Features.dotProduct(colDiffWeights, feature15);

    }

    public static void printRuntimeStatistics(){
        long [] timeTaken = featureTimeTaken;
        long timeRan = featureTimesRunned;
        long totalruntime  = 0;

        for (long time: timeTaken){
            totalruntime += time;
        }

        for (int i=0; i<timeTaken.length;i++){
            try {
                System.out.println("method " +(i + 1) + " - Avg time taken: " + timeTaken[i] / timeRan + "ns , % of runtime: " + timeTaken[i] * 100 / totalruntime + "%");
            } catch (ArithmeticException e){
                System.out.println("method " +(i + 1) + " - " + e.toString());
            }
        }
    }



}
