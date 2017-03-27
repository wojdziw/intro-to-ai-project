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

*/

public class GeneticAlgorithm {

    private static final int noWeights = Features.getNumberOfWeights();
    private static final double maxWeight = 5;
    private static final int populationSize = 50;
    private static final int noGenerations = 50;

    private static final double uniformRate = 0.5;
    private static final double mutationRate = 0.015;
    private static int tournamentSize = 5;
    private static final boolean elitism = true;

    public static Population evolvePopulation(Population pop) {
        Population newPopulation = new Population(pop.size(), false, noWeights, maxWeight);

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
        Individual newSol = new Individual(noWeights, maxWeight);
        // Loop through genes
        for (int i = 0; i< noWeights; i++) {
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
        for (int i = 0; i< noWeights; i++) {
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
        Population tournament = new Population(tournamentSize, false, noWeights, maxWeight);
        for (int i = 0; i<tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.size());
            tournament.setIndividual(i, pop.getIndividual(randomId));
        }
        return tournament.getFittest();
    }

    public static void execute() {

        Population myPop = new Population(populationSize, true, noWeights, maxWeight);

        double[][] generationsWeights = new double[noGenerations][noWeights];
        int[][] generationsResults =  new int[noGenerations][2];

        long startTime = System.nanoTime();

        for (int generation = 0; generation< noGenerations; generation++) {

            myPop = evolvePopulation(myPop);
            myPop.printStats(generation);

//            generationsResults[generation][0] = myPop.getFittest().getFitness();
//            generationsWeights[generation]=myPop.getFittest().getGenes();
//
//            long iterTime = (System.nanoTime() - startTime)/1000000000;
//            System.out.println("Time for iteration: " + iterTime + "s");
//            startTime = System.nanoTime();

        }

//        saveToCsv.writeCsvFile("geneticRun", generationsResults, generationsWeights);
    }

    public static void main(String[] args) {

        execute();
    }

}


