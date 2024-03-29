/*

The most recent version of stuff as I commit it (Friday 11/03) is in the GeneticAlgorithm file
Here you can test the working of the Utility Function calculator and the move chooser

LOOK AT LINE 104 FOR TESTING YOUR FUNCTIONS

*/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class PlayerSkeleton{


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

/*
	public int pickMove(State s, int[][] legalMoves, double[] weights) {

		int bestMove = 0;
		double bestUtility = -Double.MAX_VALUE;
		int nextPiece = s.nextPiece;

        int cores = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(cores);
		//create a list to hold the Future object associated with Callable
		List<Future<PairResultMove>> list = new ArrayList<Future<PairResultMove>>();
		//Create MyCallable instance

		for (int move=0; move<legalMoves.length; move++) {

			Callable<PairResultMove> callable = new MyCallableMove(s, legalMoves[move], nextPiece, weights, move);
			//submit Callable tasks to be executed by thread pool
			Future<PairResultMove> future = executor.submit(callable);
			//add Future to the list, we can get return value using Future
			list.add(future);
		}

		for(Future<PairResultMove> fut : list){
			try {
				//print the return value of Future
				// because Future.get() waits for task to get completed
				// Find the move maximizing the utility
                //System.out.println(fut.get().getMove()+" utility: " +fut.get().getUtility());
				if (fut.get().getUtility() > bestUtility) {
					bestMove = fut.get().getMove();
					bestUtility = fut.get().getUtility();
				}

			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		//shut down the executor service now
		shutdownAndAwaitTermination(executor);
		return bestMove;
	}
	*/

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

	// Returns the score (number of rows cleared) based on the strategy i.e. the weights of the utility function
	public static int playNGames(double[] weights, boolean drawing, boolean waitForEnter, int N) {
		int fitness = 0;
		// Averaging over N games to mitigate the impact of the random piece choice
		for (int i=0; i<N; i++)
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

	void shutdownAndAwaitTermination(ExecutorService pool) {
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
				pool.shutdownNow();
				System.out.println("Closed a pool before termination");// Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(60, TimeUnit.SECONDS))
					System.err.println("Pool did not terminate");
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			System.out.println(ie.getMessage());
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}
}



class MyCallableMove implements Callable<PairResultMove> {
	State s;
	int[] move;
	int nextPiece;
    double[] weights;
    int order;

	MyCallableMove(State s, int[] move, int nextpiece, double[] weights, int order) {
		this.s = s;
		this.move = move;
		this.nextPiece = nextpiece;
        this.weights = weights;
        this.order = order;
	}

	@Override
	public PairResultMove call() throws Exception {
		// Calculate the next state and check if the game would have ended
		SimulatedState simulatedState = UtilityHelpers.calculateSimulatedState(s, move, nextPiece);
		if (!simulatedState.wouldGameFinish()) {
			// If not, simulate the move
			int[][] newField = simulatedState.getField();
			int[] newTop = simulatedState.getTop();
			int rowsCleared = simulatedState.getRowsCleared();

			// Passed rowsCleared into calculateUtility to apply weight to it
			double utility = Features.calculateUtility(newField, newTop, weights, rowsCleared);

            return new PairResultMove(utility, order);
		}

		return new PairResultMove(-Double.MAX_VALUE, order);
	}
}

class PairResultMove{
	private final int order;
	private final double utility;

	PairResultMove(double utility, int order){
		this.order = order;
		this.utility = utility;
	}

	public Double getUtility(){
		return this.utility;
	}

	public int getMove(){
	    return this.order;
    }

}
