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
    private static final int noGenerations = 40;

    private static final double uniformRate = 0.5;
    private static final double mutationRate = 0.015;
    private static int tournamentSize = 5;
    private static final boolean elitism = true;

    public static Population evolvePopulation(Population pop) {
        Population newPopulation = new Population(pop.size(), false, noFeatures, maxWeight);

        if (elitism)
            newPopulation.setIndividual(0, pop.getFittest());

        //Improve selection part!?

        int elitismOffset = elitism ? 1 : 0;

        // Loop over the population size and create new individuals with crossover
        for (int i = elitismOffset; i<pop.size(); i++) {
            Individual indiv1 = tournamentSelection(pop); // Find the fitest among 5 random individuals
            Individual indiv2 = tournamentSelection(pop);

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
    private static Individual tournamentSelection(Population pop) {
        // Create a tournament population
        Population tournament = new Population(tournamentSize, false, noFeatures, maxWeight);
        for (int i = 0; i<tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.size());
            tournament.setIndividual(i, pop.getIndividual(randomId));
        }
        return tournament.getFittest();
    }
	
	public static void main(String[] args) {
		
		// Calculate all features each time
		// but only use certain ones in calculation

		// get all values for C(noGenerations, noWeights)
		
		Combination combi = new Combination(15, 4);
		
		while (combi.hasMore()) {
			for(int i : combi.next()) {
				System.out.print(i + " ");
			}
			System.out.println();
		}
		

		
		
		

	}

}
