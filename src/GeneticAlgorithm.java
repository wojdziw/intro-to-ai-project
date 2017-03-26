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
    private static final double maxWeight = 5;
    private static final int populationSize = 50;
    private static final int noGenerations = 50;

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
            Individual indiv1 = tournamentSelection(pop); // Find the fittest among 5 random individuals
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
        double[][] generationsWeights = new double[noGenerations][noFeatures];
        int[][] generationsResults =  new int[noGenerations][2];

        long startTime = System.nanoTime();
        Population myPop = new Population(populationSize, true, noFeatures, maxWeight);
        Individual bestInd = myPop.getIndividual(0);

        for (int generation = 0; generation< noGenerations; generation++) {

            bestInd = myPop.getFittest();

            int fitness=bestInd.getFitness();
            generationsResults[generation][0] = fitness;
            System.out.println("Generation: " + (generation+1) + " Fittest: " + fitness);
            bestInd.printGenes();
            //fitness=bestInd.getFitness(); // Will return same value always! (Regardless if we use bestInd or myPop.getFittest() again)
            //generationsResults[generation][1] = fitness;
            //System.out.println("   second calculation: " + fitness);

            // Time every iteration
            long iterTime = (System.nanoTime() - startTime)/1000000000;
            System.out.println("Time for iteration: " + iterTime + "s");
            startTime = System.nanoTime();

            PlayerSkeleton.printRuntimeStatistics();
            System.out.println("- - - - - - - - - - - - - -");
            generationsWeights[generation]=bestInd.getGenes();
            myPop = evolvePopulation(myPop);
        }

        // TODO: print used constants as well!?
        System.out.println("Writing over results and weights to csv");
        saveToCsv.writeCsvFile("geneticRun", generationsResults, generationsWeights);
        int rowsCleared = PlayerSkeleton.playAGame(bestInd.getGenes(), true, false);
        System.out.println("Rows cleared: " + rowsCleared);
        System.out.println();

    }

}


