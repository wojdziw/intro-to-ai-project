package optimizeFeatures;
/*

The most recent version of stuff as I commit it (Friday 11/03) is in the GeneticAlgorithm file
Here you can test the working of the Utility Function calculator and the move chooser

LOOK AT LINE 104 FOR TESTING YOUR FUNCTIONS

*/

import java.util.Arrays;
import java.util.Scanner;

public class PlayerSkeleton {
	
	public int pickMove(State s, int[][] legalMoves, double[] weights, int[] weightsToApply) {

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
				double utility = calculateUtility(newField, newTop, weights, rowsCleared, weightsToApply);

				// Find the move maximizing the utility
				if (utility > bestUtility) {
					bestMove = move;
					bestUtility = utility;
				}
			}

		}

		return bestMove;
	}

    static long featureTimesRunned = 0;
	public static double calculateUtility(int[][] field, int[] top, double[] weights, int rowsCleared, int[] weightsToApply) {
		
		
		double[] columnHeightWeights = Arrays.copyOfRange(weights, 14, (14 + field[0].length));
		double[] colDiffWeights = Arrays.copyOfRange(weights, (14 + field[0].length + 1), (14 + 2*field[0].length));
		
		double[] featureResults = new double[16]; //remove hardcoding?
		
		//calculate feature values
		//store values in featureResults
		double[] feature1_2 = Features.calculateFeature1_2(top, field);
		featureResults[1] = feature1_2[0];
		featureResults[2] = feature1_2[1];

        featureResults[3] = Features.calculateFeature3(top, field);
        
        featureResults[4] = Features.calculateFeature4(top, field);

        featureResults[5] = rowsCleared;
        
        featureResults[6] = Features.calculateFeature6(top, field);

        featureResults[8] = Features.calculateFeature8(top, field);

		double[] feature7_9 = Features.calculateFeature7_9(top, field);
		featureResults[7] = feature7_9[0];
		featureResults[9] = feature7_9[1];
		
        featureResults[10] = Features.calculateFeature10(top, field);

        featureResults[11] = Features.calculateFeature11(top, field);

        featureResults[12] = Features.calculateFeature12(top, field);
        
        featureResults[13] = Features.calculateFeature13(top, field);

		double[] feature14 = Features.calculateFeature14(top, field);
		featureResults[14] = Features.dotProduct(columnHeightWeights, feature14);
		
		double[] feature15 = Features.calculateFeature15(top, field);
		featureResults[15] = Features.dotProduct(colDiffWeights, feature15);
        featureTimesRunned++;
		
		
		// apply weights
		double finalWeightValue = 0;
		for (int i : weightsToApply) {
			finalWeightValue += weights[i+1]*featureResults[i+1]; //since weightsToApply ranges from 0 to 1
		}
		
		return finalWeightValue;
		

	}

	// Returns the score (number of rows cleared) based on the strategy i.e. the weights of the utility function
	public static int playAGame(double[] weights, boolean drawing, boolean waitForEnter, int[] weightsToApply) {
		State s = new State();
		if (drawing)
			new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();

		// In case we want to use enter to proceed with blocks (determined by the waitForEnter input)
		Scanner scanner = new Scanner(System.in);

		while(!s.hasLost()) {

			if (waitForEnter)
				scanner.nextLine();
            int nextMove = p.pickMove(s,s.legalMoves(), weights, weightsToApply);

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
		//int rowsCleared = playAGame(weights, true, false);

		//System.out.println("You have completed "+rowsCleared+" rows.");

	}




}


