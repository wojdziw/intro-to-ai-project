
import java.util.*;

import static java.lang.Math.min;


public class Features {

    private static int no_weights = 1 + 13 + State.COLS + State.COLS-1;
    public static double[] maxFeatures = new double[no_weights];
    public static double[] meanFeatures = new double[no_weights];
    public static double[] iterations = new double[no_weights];


    //1 + 13 + (State.COLS) + (State.COLS-1) - this is the most weights we can have
    // These list down the weights related to each of the features
    public static Map<Integer, List<Integer>> featureWeights;

    private static List<Integer> range1 = new ArrayList<>(State.COLS);
    private static List<Integer> range2 = new ArrayList<>(State.COLS-1);

    {
        for (int i = 0; i < State.COLS-1; i++) {
            range1.add(i, 14+i);
            range2.add(i, 13+State.COLS+i);
        }
        range1.add(State.COLS-1, 13+State.COLS);
    }
    static {
        featureWeights = new HashMap<>();
        featureWeights.put(0, Arrays.asList(0));
        featureWeights.put(1, Arrays.asList(1));
        featureWeights.put(2, Arrays.asList(2));
        featureWeights.put(3, Arrays.asList(3));
        featureWeights.put(4, Arrays.asList(4));
        featureWeights.put(5, Arrays.asList(5));
        featureWeights.put(6, Arrays.asList(6));
        featureWeights.put(7, Arrays.asList(7));
        featureWeights.put(8, Arrays.asList(8));
        featureWeights.put(9, Arrays.asList(9));
        featureWeights.put(10, Arrays.asList(10));
        featureWeights.put(11, Arrays.asList(11));
        featureWeights.put(12, Arrays.asList(12));
        featureWeights.put(13, Arrays.asList(13));
        featureWeights.put(14, range1);
        featureWeights.put(15, range2);
    }
    // HERE WE CHOOSE THE SUBSET OF FEATURES WE WANT TO TEST
    // Add features by choosing appropriate lists in here
    public static List<Integer> subset = new ArrayList<>();
    static {
        subset.addAll(featureWeights.get(0));
        //subset.addAll(featureWeights.get(1));
        //subset.addAll(featureWeights.get(2));
        //subset.addAll(featureWeights.get(3));
        //subset.addAll(featureWeights.get(4));
        //subset.addAll(featureWeights.get(5));
        //subset.addAll(featureWeights.get(6));
        subset.addAll(featureWeights.get(7));
        //subset.addAll(featureWeights.get(8));
        subset.addAll(featureWeights.get(9));
        subset.addAll(featureWeights.get(10));
        subset.addAll(featureWeights.get(11));
        //subset.addAll(featureWeights.get(12));
        //subset.addAll(featureWeights.get(13));
        //subset.addAll(featureWeights.get(14));
        subset.addAll(featureWeights.get(15));
    }

    // THIS INCLUDES THE BIAS
    public static int getNumberOfWeights() {
        return subset.size();
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

        iterations[1]+=1;
        maxFeatures[1] = Math.max(maxFeatures[1], bothSums[0]);
        meanFeatures[1] = ((iterations[1]-1)/iterations[1])* meanFeatures[1]+bothSums[0]/iterations[1];

        iterations[2]+=1;
        maxFeatures[2] = Math.max(maxFeatures[2], bothSums[1]);
        meanFeatures[2] = ((iterations[2]-1)/iterations[2])* meanFeatures[2]+bothSums[1]/iterations[2];

        return bothSums;
    }

    //FEATURE 3
    //Maximum Height - The height of the tallest column on the game board
    public static double calculateFeature3(int[] top, int[][] field) {
        double maxHeight = 0;

        for (int height : top)
            maxHeight = Math.max(maxHeight, height);

        iterations[3]+=1;
        maxFeatures[3] = Math.max(maxFeatures[3], maxHeight);
        meanFeatures[3] = ((iterations[3]-1)/iterations[3])* meanFeatures[3]+maxHeight/iterations[3];

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

        iterations[4]+=1;
        maxFeatures[4] = Math.max(maxFeatures[4], minHeight);
        meanFeatures[4] = ((iterations[4]-1)/iterations[4])* meanFeatures[4]+minHeight/iterations[4];

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

        iterations[6]+=1;
        maxFeatures[6] = Math.max(maxFeatures[6], maxHeightDiff);
        meanFeatures[6] = ((iterations[6]-1)/iterations[6])* meanFeatures[6]+maxHeightDiff/iterations[6];

        return maxHeightDiff;
    }

    //FEATURE 8
    //Deepest Hole - The depth of the deepest hole (an unfillable spot = has at least a block above it)
    public static int calculateFeature8(int[] top, int[][] field) {

        boolean found = false;
        int deepestHole = 0;

        for(int r = 0; r < State.ROWS && !found; r++) {
            for(int c = 0; c < State.COLS && !found; c++) {
                if (field[r][c]==0 && top[c] > r) {
                    deepestHole = r;
                    found = true;
                }
            }
        }

        iterations[8]+=1;
        maxFeatures[8] = Math.max(maxFeatures[8], deepestHole);
        meanFeatures[8] = ((iterations[8]-1)/iterations[8])* meanFeatures[8]+deepestHole/iterations[8];

        return deepestHole;
    }

    // FEATURE 7 + 9
    // 7: Nr of spots in holes
    // 9: Amount of Holes - The number of enclosed clusters/caves of holes
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

        iterations[7]+=1;
        maxFeatures[7] = Math.max(maxFeatures[7], result[0]);
        meanFeatures[7] = ((iterations[7]-1)/iterations[7])* meanFeatures[7]+result[0]/iterations[7];

        iterations[9]+=1;
        maxFeatures[9] = Math.max(maxFeatures[9], result[1]);
        meanFeatures[9] = ((iterations[9]-1)/iterations[9])* meanFeatures[9]+result[1]/iterations[9];

        return result;
    }

    private static int localClusterSearch(int[][] field, int row, int col, boolean[][] visited, int[] top) {

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

        iterations[10]+=1;
        maxFeatures[10] = Math.max(maxFeatures[10], numberOfWells);
        meanFeatures[10] = ((iterations[10]-1)/iterations[10])* meanFeatures[10]+numberOfWells/iterations[10];

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

        iterations[11]+=1;
        maxFeatures[11] = Math.max(maxFeatures[11], sumOfWells);
        meanFeatures[11] = ((iterations[11]-1)/iterations[11])* meanFeatures[11]+sumOfWells/iterations[11];

        return sumOfWells;
    }

    // FEATURE 12
    // Game Status - Based on the game status, 1 for a losing state, 0 otherwise
    // TODO; remove, will always return 0
    public static double calculateFeature12(int[] top, int[][] field) {
        int result = 0;

        for(int i : top){
            if (i >= State.COLS){
                result = 1;
                break;
            }
        }

        iterations[12]+=1;
        maxFeatures[12] = Math.max(maxFeatures[12], result);
        meanFeatures[12] = ((iterations[12]-1)/iterations[12])* meanFeatures[12]+result/iterations[12];

        return result;

    }

    //FEATURE 13
    //Column sum - Aggregated height of all the columns.
    public static double calculateFeature13(int[] top, int[][] field) {
        double columnSum = 0;

        for (int height : top)
            columnSum += height;

        iterations[13]+=1;
        maxFeatures[13] = Math.max(maxFeatures[13], columnSum);
        meanFeatures[13] = ((iterations[13]-1)/iterations[13])* meanFeatures[13]+columnSum/iterations[13];

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

        for (int i=0; i<top.length; i++) {
            iterations[14+i]+=1;
            maxFeatures[14+i] = Math.max(maxFeatures[14+i], topDouble[i]);
            meanFeatures[14+i] = ((iterations[14+i]-1)/iterations[14+i])* meanFeatures[14+i]+topDouble[i]/iterations[14+i];
        }


        return topDouble;
    }

    public static double dotProduct(double[] X, double[] Y){
        double sumOfWeightedHeights = 0;
        if(X.length != Y.length){
            System.out.println("Vector sizes in dotProduct doesn't match");
        }
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

        for (int i=0; i<top.length-1; i++) {
            iterations[24+i]+=1;
            maxFeatures[24+i] = Math.max(maxFeatures[24+i], weightedSumHeightDiff[i]);
            meanFeatures[24+i] = ((iterations[24+i]-1)/iterations[24+i])* meanFeatures[24+i]+weightedSumHeightDiff[i]/iterations[24+i];
        }

        return weightedSumHeightDiff;
    }

    // subset size should be the same as the number of weights
    // no time statistics sadly
    public static double calculateUtility(int[][] field, int[] top, double[] weights, int rowsCleared) {

        // Here are all the features
        double[] feature1_2 = subset.contains(1) || subset.contains(2) ? Features.calculateFeature1_2(top, field) : new double[2];
        double feature3 = subset.contains(3) ? Features.calculateFeature3(top, field) : 0.0;
        double feature4 = subset.contains(4) ? Features.calculateFeature4(top, field) : 0.0;
        double feature6 = subset.contains(6) ? Features.calculateFeature6(top, field) : 0.0;
        double feature8 = subset.contains(8) ? Features.calculateFeature8(top, field) : 0.0;
        double[] feature7_9 = subset.contains(7) || subset.contains(9) ? Features.calculateFeature7_9(top, field) : new double[2];
        double feature10 = subset.contains(10) ? Features.calculateFeature10(top, field) : 0.0;
        double feature11 = subset.contains(11) ? Features.calculateFeature11(top, field) : 0.0;
        double feature12 = subset.contains(12) ? Features.calculateFeature12(top, field) : 0.0;
        double feature13 = subset.contains(13) ? Features.calculateFeature13(top, field) : 0.0;
        double[] feature14 = subset.contains(14) ? Features.calculateFeature14(top, field) : new double[field[0].length];
        double[] feature15 = subset.contains(24) ? Features.calculateFeature15(top, field) : new double[field[0].length-1];

        // Plus 1 for bias
        int numberOfAllWeights = 1 + 13 + (State.COLS) + (State.COLS-1); //=1+13+10+9=33
        //int numberOfAllWeights = 33;

        // The weights inputted only refer to the subset
        // Need to convert that to a weight vector referring to all the features
        // For the ones that are not in the subset, the weight is zero
        double[] allWeights = new double[numberOfAllWeights];
        int subsetIdx = 0;

        for (int i=0; i<allWeights.length; i++) {
            if (subsetIdx<subset.size()) {
                if (i==subset.get(subsetIdx)) {
                    allWeights[i] = weights[subsetIdx++];
                    continue;
                }
            }
            allWeights[i] = 0;
        }

        double[] columnHeightWeights = Arrays.copyOfRange(allWeights, 14, (14 + field[0].length));
        double[] colDiffWeights = Arrays.copyOfRange(allWeights, (14 + field[0].length), (14 + 2*field[0].length-1));

        // apply weights
        return allWeights[0]
                + allWeights[1]*feature1_2[0]
                + allWeights[2]*feature1_2[1]
                + allWeights[3]*feature3
                + allWeights[4]*feature4
                + allWeights[5]*rowsCleared
                + allWeights[6]*feature6
                + allWeights[7]*feature7_9[0]
                + allWeights[8]*feature8
                + allWeights[9]*feature7_9[1]
                + allWeights[10]*feature10
                + allWeights[11]*feature11
                + allWeights[12]*feature12
                + allWeights[13]*feature13
                + Features.dotProduct(columnHeightWeights, feature14)
                + Features.dotProduct(colDiffWeights, feature15);
    }

    static long[] featureTimeTaken = new long[15];
    static long featureTimesRunned = 0;
    public static double calculateOldUtility(int[][] field, int[] top, double[] weights, int rowsCleared) {

        //calculate feature values
        long startTime = System.nanoTime();
        double[] feature1_2 = Features.calculateFeature1_2(top, field);
        featureTimeTaken[0] += (System.nanoTime() - startTime);

        startTime = System.nanoTime();
        double feature3 = Features.calculateFeature3(top, field);
        featureTimeTaken[2] += (System.nanoTime() - startTime);

        startTime = System.nanoTime();
        double feature4 = Features.calculateFeature4(top, field);
        featureTimeTaken[3] += (System.nanoTime() - startTime);

        startTime = System.nanoTime();
        double feature6 = Features.calculateFeature6(top, field);
        featureTimeTaken[5] += (System.nanoTime() - startTime);

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
        double[] colDiffWeights = Arrays.copyOfRange(weights, (14 + field[0].length), (14 + field[0].length + field[0].length-1));

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
