/*

The most recent version of stuff as I commit it (Friday 11/03) is in the GeneticAlgorithm file
Here you can test the working of the Utility Function calculator and the move chooser

LOOK AT LINE 104 FOR TESTING YOUR FUNCTIONS

*/

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

				double utility = calculateUtility(newField, newTop, weights)+rowsCleared;

				// Find the move maximizing the utility
				if (utility > bestUtility) {
					bestMove = move;
					bestUtility = utility;
				}
			}
		}

		return bestMove;
	}

	public static double calculateUtility(int[][] field, int[] top, double[] weights) {
		
		//calculate feature values
		double feature1 = Features.calculateFeature1(top, field);
		double feature2 = Features.calculateFeature2(top, field);
		double feature3 = Features.calculateFeature3(top, field);
		double feature4 = Features.calculateFeature4(top, field);
		double feature5 = Features.calculateFeature5(top, field);
		double feature6 = Features.calculateFeature6(top, field);
		double feature7 = Features.calculateFeature7(top, field);
		double feature8 = Features.calculateFeature8(top, field);
		double feature9 = Features.calculateFeature9(top, field);
		double feature10 = Features.calculateFeature10(top, field);
		double feature11 = Features.calculateFeature11(top, field);
		double feature12 = Features.calculateFeature12(top, field);
		double feature13 = Features.calculateFeature13(top, field);
		double feature14 = Features.calculateFeature14(top, field);
		double feature15 = Features.calculateFeature15(top, field);

		
		// apply weights
		return weights[0]
				+ weights[1]*feature1
				+ weights[2]*feature2
				+ weights[3]*feature3
				+ weights[4]*feature4
				+ weights[5]*feature5
				+ weights[6]*feature6
				+ weights[7]*feature7
				+ weights[8]*feature8
				+ weights[9]*feature9
				+ weights[10]*feature10
				+ weights[11]*feature11
				+ weights[12]*feature12
				+ weights[13]*feature13
				+ weights[14]*feature14
				+ weights[15]*feature15;
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

			// YOU CAN TEST YOUR FEATURES HERE
			//System.out.println("Feature1's value is: " + Features.calculateFeature1(s.getTop(), s.getField()));

			if (drawing) {
				try {
					Thread.sleep(10);
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

		// Initialise the feature weight to something
		// we have noFeatures+1 vector to have the extra bias term

		// Use line 104 to test your features

		int noFeatures = 15;
		double maxWeight = 3;
		double[] weights = new double[noFeatures+1];
		for (int i=0; i<weights.length; i++) {
			int plusMinus = Math.random() > 0.5 ? -1 : 1;
			weights[i] = plusMinus * maxWeight * Math.random();
		}

		// Play one game
		int rowsCleared = playAGame(weights, true, false);

		System.out.println("You have completed "+rowsCleared+" rows.");
	}
	
}
