/*

    Credit: http://www.theprojectspot.com/tutorial-post/creating-a-genetic-algorithm-for-beginners/3

    1. Initialization
    2. Evaluation
    3. Selection
    4. Crossover
    5. Mutation
    6. Repeat!

    Population - many different weight vectors
    Individual - one weight vector
    Fitness - number of cleared rows when a game is played

    WOJ'S REMARKS:
        -> This works decently - manages to clear a few hundred rows each time
           Update - a few hundred rows cleared each time when the fitness is averaged over several trials
        -> Please tweak it however you want, it should get much much better as we implement more features

    WOJ'S DOUBTS:
        -> I was not quite sure how to initialize an Individual (weight vector)
           It is random within a range defined by (-maxWeight, +maxWeight)
           But then it might have to be more diverse
        -> I really hope the algorithm does something better than random guessing

*/

public class GeneticAlgorithm {

    private static final int noFeatures = 15 + (State.COLS - 1) + (State.COLS -2); //noFeatures + columnHeightWeights + columnDifferenceWeights
    private static final double maxWeight = 3;
    private static final int populationSize = 15;
    private static final int noGenerations = 5;

    private static final double uniformRate = 0.5;
    private static final double mutationRate = 0.015;
    private static int tournamentSize = 5;
    private static final boolean elitism = true;

    public static Population evolvePopulation(Population pop) {
        Population newPopulation = new Population(pop.size(), false, noFeatures, maxWeight);

        if (elitism)
            newPopulation.setIndividual(0, pop.getFittest());

        int elitismOffset = elitism ? 1 : 0;

        // Loop over the population size and create new individuals with crossover
        for (int i = elitismOffset; i<pop.size(); i++) {
            Individual indiv1 = tournamentSelection(pop);
            Individual indiv2 = tournamentSelection(pop);

            Individual newIndiv = crossover(indiv1, indiv2);
            newPopulation.setIndividual(i, newIndiv);
            // Remove added individual from population?
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
                double gene = maxWeight *Math.random();
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

        Population myPop = new Population(populationSize, true, noFeatures, maxWeight);
        for (int generation = 0; generation< noGenerations; generation++) {

            System.out.println("Generation: " + generation + " Fittest: " + myPop.getFittest().getFitness());
            System.out.println("   second calculation: " + myPop.getFittest().getFitness());
            System.out.println("- - - - - - - - - - - - - -");

            myPop = evolvePopulation(myPop);
        }

        int rowsCleared = PlayerSkeleton.playAGame(myPop.getFittest().getGenes(), true, false);
        System.out.println("Rows cleared: " + rowsCleared);

    }
}


