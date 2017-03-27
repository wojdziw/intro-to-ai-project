/*

The most recent version of stuff as I commit it (Friday 11/03) is in the GeneticAlgorithm file
Here you can test the working of the Utility Function calculator and the move chooser

LOOK AT LINE 104 FOR TESTING YOUR FUNCTIONS

*/

import java.util.Arrays;
import java.util.Scanner;

public class PlayerSkeleton {
	
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
				double utility = Features.calculateUtility(newField, newTop, weights, rowsCleared);

				// Find the move maximizing the utility
				if (utility > bestUtility) {
					bestMove = move;
					bestUtility = utility;
				}
			}

		}

		return bestMove;
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

	// Returns the score (number of rows cleared) based on the strategy i.e. the weights of the utility function
	public static int playNGames(double[] weights, boolean drawing, boolean waitForEnter, int N) {
		int n = 10;
		int fitness = 0;
		// Averaging over N games to mitigate the impact of the random piece choice
		for (int i=0; i<n; i++)
			fitness += PlayerSkeleton.playAGame(weights, false, false);

		// Get average
		return fitness/N;
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
        Features.printRuntimeStatistics();
	}

}


