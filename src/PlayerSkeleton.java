import java.util.Scanner;

public class PlayerSkeleton {
	
	
	/* Features
	 * Woj: 3, 8, 13
	 * Fei: 5, 10, 15
	 *
	 */
	
	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves, double[] weights) {

		int bestMove = -1;
		double bestUtility = Double.MIN_VALUE;

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

				if (utility > bestUtility) {
					bestMove = move;
					bestUtility = utility;
				}
			}
		}

		return bestMove;
	}

	public static double calculateUtility(int[][] field, int[] top, double[] weights) {
		
		//Set weights
		/*
		 * TODO: maybe start our naming for features from 0 instead of 1? 
		 * - that will make the naming of our weights more consistent
		 */
		
		//calculate feature values
		double feature3 = Features.calculateFeature3(top);
		double feature5 = Features.calculateFeature5(top);
		double feature8 = Features.calculateFeature8(field);
		int feature10 = Features.calculateFeature10(top);
		double feature13 = Features.calculateFeature13(top);
		double feature15 = Features.calculateFeature15(top);
		
		//apply weights
		double utility = weights[2]*feature3 + weights[4]*feature5 + weights[7]*feature8 + weights[9]*feature10 + weights[12]*feature13 + weights[14]*feature15;
		
		return utility;
	}

	// Returns the score - number of rows cleared based on the strategy i.e. the weights of the utility function
	public static int playAGame(double[] weights) {
		State s = new State();
		new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();
		Scanner scanner = new Scanner(System.in);

		while(!s.hasLost()) {

			// Uncomment if you want to click enter after every move
			// scanner.nextLine();

			int nextMove = p.pickMove(s,s.legalMoves(), weights);

			if (nextMove==-1)
				break;

			s.makeMove(nextMove);
			s.draw();
			s.drawNext(0,0);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			System.out.println("New utility is: " + calculateUtility(s.getField(), s.getTop(), weights));
		}
		scanner.close();

		return s.getRowsCleared();
	}

	public static void main(String[] args) {

		// Initialise the feature weight to something
		int noFeatures = 15;
		double[] weights = new double[noFeatures];
		for (int i=0; i<weights.length; i++) {
			weights[i] = 1.0;
		}

		// Play one game
		int rowsCleared = playAGame(weights);

		System.out.println("You have completed "+rowsCleared+" rows.");
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
