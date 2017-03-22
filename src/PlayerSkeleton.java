/*
	A try on 2-ply deep search.
*/

import java.util.Arrays;
import java.util.Scanner;

public class PlayerSkeleton {
	
	public int pickMove(State s, int[][] legalMoves, double[] weights) {

		int bestMove = 0;
		double bestUtility = -Double.MAX_VALUE;

		DummyState dummy = new DummyState();

		int nextPiece = s.nextPiece;

		for (int move=0; move<legalMoves.length; move++) {
			// Calculate the next state and check if the game would have ended
			SimulatedState simulatedState = UtilityHelpers.calculateSimulatedState(s, legalMoves[move], nextPiece);

			if (!simulatedState.wouldGameFinish()) {

				// If not, simulate the move
				int[][] newField = simulatedState.getField();
				int[] newTop = simulatedState.getTop();
				int rowsCleared = simulatedState.getRowsCleared();

				// Calculate utility for Layer 1
				double topUtility = calculateUtility(newField, newTop, weights, rowsCleared);

				double avgL2Utility = 0.0;

				// Find the best combination with a second move (2-ply deep)
				for (int secondPiece = 0; secondPiece < s.N_PIECES; secondPiece++){

					double bestL2UtilityForPiece = 0.0;

					// Get legal moves for second piece (somehow...)
					int[][] secondLegalMoves = dummy.getLegalMoves(secondPiece);

					for (int secondMove = 0; secondMove<secondLegalMoves.length; secondMove++) {

						// Add combination of pieces and moves to arrays
						int[] bothPieces = {nextPiece, secondPiece};
						int [][] bothMoves ={legalMoves[move], secondLegalMoves[secondMove]};

						SimulatedState secondState = UtilityHelpers.calc2_plySimulatedState(s, bothMoves, bothPieces);

						// Check if game doesn't end with the two moves
						if (!secondState.wouldGameFinish()) {

							// If not, simulate the move
							int[][] newField2 = secondState.getField();
							int[] newTop2 = secondState.getTop();
							int rowsCleared2 = secondState.getRowsCleared();

							// Calculate utility for state after 2 moves
							double temp = calculateUtility(newField2, newTop2, weights, rowsCleared2);

							// Find the best move for this piece
							if(temp > bestL2UtilityForPiece)
								bestL2UtilityForPiece = temp;
						}
					}

					avgL2Utility += bestL2UtilityForPiece;
				}

				// Take the average of the best moves for every possible piece
				avgL2Utility = avgL2Utility / s.N_PIECES;


				// Find the best move by combining utility of 1st state and 2nd state
				if ((avgL2Utility+topUtility) > bestUtility) {
					bestMove = move;
					bestUtility = (avgL2Utility+topUtility);
				}
			}
		}

		return bestMove;
	}

	static long[] featureTimeTaken = new long[15];
    static long featureTimesRunned = 0;
	static long featureTimesRunnedPerIteration = 0;
	public static double calculateUtility(int[][] field, int[] top, double[] weights, int rowsCleared) {
		
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

        // Get number of times ran for this iteration
        long iterRun = featureTimesRunned - featureTimesRunnedPerIteration;
		featureTimesRunnedPerIteration = featureTimesRunned;


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
		System.out.println("Total times 'calculateUtility' called this iteration: " + iterRun);
    }

}


