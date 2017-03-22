package optimizeFeatures;
import java.util.Map;
import java.util.TreeMap;

import de.bezier.math.combinatorics.Combination;

/*
 * Test class to find out the optimal four features 
 * 
 */
public class OptimizeFourFeatures {

	//variables for optimizing features:
	private static final int noWeights = 4;
	
	private static final int noFeatures = 15 + (State.COLS - 1) + (State.COLS -2); //noFeatures + columnHeightWeights + columnDifferenceWeights
    private static final double maxWeight = 5;
    private static final int populationSize = 50;
    private static final int noGenerations = 5;

    private static final double uniformRate = 0.5;
    private static final double mutationRate = 0.015;
    private static int tournamentSize = 5;
    private static final boolean elitism = true;

    public static Population evolvePopulation(Population pop, int[] weightsToApply) {
        Population newPopulation = new Population(pop.size(), false, noFeatures, maxWeight);

        if (elitism)
            newPopulation.setIndividual(0, pop.getFittest(weightsToApply));

        //Improve selection part!?

        int elitismOffset = elitism ? 1 : 0;

        // Loop over the population size and create new individuals with crossover
        for (int i = elitismOffset; i<pop.size(); i++) {
            Individual indiv1 = tournamentSelection(pop, weightsToApply); // Find the fitest among 5 random individuals
            Individual indiv2 = tournamentSelection(pop, weightsToApply);

            Individual newIndiv = crossover(indiv1, indiv2);
            newPopulation.setIndividual(i, newIndiv);
        }

        // Mutate population
        for (int i=elitismOffset; i<newPopulation.size(); i++)
            mutate(newPopulation.getIndividual(i));

        return newPopulation;
    }

    private static Individual crossover(Individual indiv1, Individual indiv2) {
        Individual newSol = new Individual(noFeatures, maxWeight);
        // Loop through genes
        for (int i = 0; i< noFeatures; i++) {
            // Crossover
            if (Math.random() <= uniformRate) {
                newSol.setGene(i, indiv1.getGene(i));
            } else {
                newSol.setGene(i, indiv2.getGene(i));
            }
        }
        return newSol;
    }

    private static void mutate(Individual indiv) {
        //Loop through genes
        for (int i = 0; i< noFeatures; i++) {
            if (Math.random() <= mutationRate) {
                // Create random gene
                double gene = maxWeight * Math.random();
                indiv.setGene(i, gene);
            }
        }
    }

    // Tournament is picking a random sample of a chosen size and choosing the fittest out of that
    private static Individual tournamentSelection(Population pop, int[] weightsToApply) {
        // Create a tournament population
        Population tournament = new Population(tournamentSize, false, noFeatures, maxWeight);
        for (int i = 0; i<tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.size());
            tournament.setIndividual(i, pop.getIndividual(randomId));
        }
        return tournament.getFittest(weightsToApply);
    }
	
	public static void main(String[] args) {
		
		//Calculate all features each time
		//but only use certain ones in calculation

		//get all values for C(noGenerations, noWeights)
		Combination combi = new Combination(15, 4);
		
		int noSavedCombinations = 5;
		
		int[] bestCombi = null;
		int[] currentFeatureSet;
		
		TreeMap<Integer, int[]> bestCombinations = new TreeMap<Integer, int[]>();
	
		
		while (combi.hasMore()) {
			
			currentFeatureSet = combi.next();
			
			if (bestCombi == null) {
				bestCombi = currentFeatureSet;
			}
			
			System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
			System.out.println("Currently trying feature combination:");
			for(int i : currentFeatureSet) {
				System.out.print(i + " ");
			}
			System.out.println("\n- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -\n");
			
 			
			double[][] generationsWeights = new double[noGenerations][noFeatures];
	        int[][] generationsResults =  new int[noGenerations][2];

	        long startTime = System.nanoTime();
	        Population myPop = new Population(populationSize, true, noFeatures, maxWeight);
	        Individual bestInd = myPop.getIndividual(0);

	        
	        for (int generation = 0; generation< noGenerations; generation++) {

	            bestInd = myPop.getFittest(currentFeatureSet);

	            int fitness=bestInd.getFitness(currentFeatureSet);
	            generationsResults[generation][0] = fitness;
	            System.out.println("Generation: " + (generation+1) + " Fittest: " + fitness);
	            //fitness=bestInd.getFitness(); // Will return same value always! (Regardless if we use bestInd or myPop.getFittest() again)
	            //generationsResults[generation][1] = fitness;
	            //System.out.println("   second calculation: " + fitness);

	            // Time every iteration
	            long iterTime = (System.nanoTime() - startTime)/1000000000;
	            System.out.println("Time for iteration: " + iterTime + "s");
	            startTime = System.nanoTime();

	            System.out.println("- - - - - - - - - - - - - -");
	            generationsWeights[generation]=bestInd.getGenes();
	            myPop = evolvePopulation(myPop, currentFeatureSet);
	        }

	        // TODO: print used constants as well!?
//	        System.out.println("Writing over results and weights to csv");
//	        saveToCsv.writeCsvFile("geneticRun", generationsResults, generationsWeights);
	        int rowsCleared = PlayerSkeleton.playAGame(bestInd.getGenes(), true, false, currentFeatureSet);
	        System.out.println("Rows cleared: " + rowsCleared);
	        System.out.println();
	        
	        
	        //update the best feature set
	        if(bestCombinations.isEmpty()) {
	        	bestCombinations.put(rowsCleared, currentFeatureSet);
	        } else {
	        	
	        	 for (Map.Entry<Integer, int[]> entry : bestCombinations.descendingMap().entrySet()) {
	 	        	
	 	        	//going from largest key to smallest, if rowsCleared is greater than an existing key or the map is not full, add the entry
	 	        	if (rowsCleared > entry.getKey() || bestCombinations.size() < noSavedCombinations) {
	 	 
	 	        		bestCombinations.put(rowsCleared, currentFeatureSet);
	 	        		
	 	        		//if max size of map is reached, remove the smallest entry
	 	        		if(bestCombinations.size() > noSavedCombinations) {
	 	        			bestCombinations.remove(bestCombinations.firstKey());
	 	        		}
	 	        		break;
	 	        	}
	 	        
	 	        }
	        }
	       
	        
	        System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
			System.out.printf("The top %s features are as follows:\n", noSavedCombinations);
			System.out.printf("%-20s%-20s\n", "Fitness Score","Features");
			for (Map.Entry<Integer, int[]> entry : bestCombinations.descendingMap().entrySet()) {
				System.out.printf("%-20s", entry.getKey());
				for(int i : entry.getValue()) {
					System.out.print(i + " ");
				}
				System.out.println();
			}
			System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -\n\n");
			
	        
		}
		

	}

}
