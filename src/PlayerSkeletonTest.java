/*

The most recent version of stuff as I commit it (Friday 11/03) is in the GeneticAlgorithm file
Here you can test the working of the Utility Function calculator and the move chooser

LOOK AT LINE 104 FOR TESTING YOUR FUNCTIONS

*/

import java.util.Arrays;
import java.util.Scanner;

public class PlayerSkeletonTest {
	
	public int pickMove(State s, int[][] legalMoves, double[] weights) {

		int bestMove = 0;
		double bestUtility = -Double.MAX_VALUE;

		int nextPiece = s.nextPiece;

		for (int move=0; move<legalMoves.length; move++) {
			// Calculate the next state and check if the game would have ended
			SimulatedState simulatedState = UtilityHelpers.calculateSimulatedState(s, legalMoves[move], nextPiece);

			if (!simulatedState.wouldGameFinish()) {

				// If not, simulate the move
				int[][] newField = simulatedState.getField();
				int[] newTop = simulatedState.getTop();
				int rowsCleared = simulatedState.getRowsCleared();

				// Passed rowsCleared into calculateUtility to apply weight to it
				double utility = calculateUtility(newField, newTop, weights, rowsCleared);

				// Find the move maximizing the utility
				if (utility > bestUtility) {
					bestMove = move;
					bestUtility = utility;
				}
			}

		}

		return bestMove;
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

	// Returns the score (number of rows cleared) based on the strategy i.e. the weights of the utility function
	public static int playAGame(double[] weights, boolean drawing, boolean waitForEnter) {
		State s = new State();
		if (drawing)
			new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();

		// In case we want to use enter to proceed with blocks (determined by the waitForEnter input)
		Scanner scanner = new Scanner(System.in);

		while(!s.hasLost()) {

			if (waitForEnter)
				scanner.nextLine();
            int nextMove = p.pickMove(s,s.legalMoves(), weights);

			s.makeMove(nextMove);

			//double[] feature1_2 = Features.calculateFeature1_2(s.getTop(), s.getField());
			//System.out.println("Feature1_2's values are: " + feature1_2[0] + " and: " + feature1_2[1]);
			//double[] feature7_9 = Features.calculateFeature7_9(s.getTop(), s.getField());
			//System.out.println("Feature7_9's values are: " + feature7_9[0] + " and: " + feature7_9[1]);

			if (drawing) {
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				s.draw();
				s.drawNext(0,0);
			}
		}
		scanner.close();

		return s.getRowsCleared();
	}

	public static void main(String[] args) {

		// Initialize the feature weight to something
		// we have noFeatures+1 vector to have the extra bias term


		int noFeatures = 15;
		int noColumns = State.COLS;
		double maxWeight = 3;
		double[] weights = new double[noFeatures + 1 + (noColumns-1) + (noColumns-2)]; //noFeatures + bias + columnHeightWeights + columnDifferenceWeights
		for (int i=0; i<weights.length; i++) {
			int plusMinus = Math.random() > 0.5 ? -1 : 1;
			weights[i] = plusMinus * maxWeight * Math.random();
		}

		// Play one game
		int rowsCleared = playAGame(weights, true, false);

		System.out.println("You have completed "+rowsCleared+" rows.");
        printRuntimeStatistics();
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


