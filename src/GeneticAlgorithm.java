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

    private int noWeights;
    private double maxWeight;
    private int populationSize;
    private int noGenerations;

    private double uniformRate;
    private double mutationRate;
    private int tournamentSize;
    private boolean elitism;
    private FitnessCalculator fitnessCalculator;

    public GeneticAlgorithm(int noWeights, double maxWeight, int populationSize, int noGenerations, double uniformRate, double mutationRate, int tournamentSize, boolean elitism, FitnessCalculator fitnessCalculator) {
        this.noWeights = noWeights;
        this.maxWeight = maxWeight;
        this.populationSize = populationSize;
        this.noGenerations = noGenerations;
        this.uniformRate = uniformRate;
        this.mutationRate = mutationRate;
        this.tournamentSize = tournamentSize;
        this.elitism = elitism;
        this.fitnessCalculator = fitnessCalculator;
    }

    private Population evolvePopulation(Population pop) {
        Population newPopulation = new Population(pop.size(), false, noWeights, maxWeight, fitnessCalculator);

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

    private Individual crossover(Individual indiv1, Individual indiv2) {
        Individual newSol = new Individual(noWeights, maxWeight, fitnessCalculator);
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

    private void mutate(Individual indiv) {
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
    private Individual tournamentSelection(Population pop) {
        // Create a tournament population
        Population tournament = new Population(tournamentSize, false, noWeights, maxWeight, fitnessCalculator);
        for (int i = 0; i<tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.size());
            tournament.setIndividual(i, pop.getIndividual(randomId));
        }
        return tournament.getFittest();
    }

    public void execute() {

        Population myPop = new Population(populationSize, true, noWeights, maxWeight, fitnessCalculator);

        //double[][] generationsWeights = new double[noGenerations][noWeights];
        //int[][] generationsResults =  new int[noGenerations][2];

        //long startTime = System.nanoTime();

        for (int generation = 0; generation< noGenerations; generation++) {

            myPop = evolvePopulation(myPop);
            myPop.printStats(generation);

            //generationsResults[generation][0] = myPop.getFittest().getFitness();
            //generationsWeights[generation]=myPop.getFittest().getGenes();

            //long iterTime = (System.nanoTime() - startTime)/1000000000;
            //System.out.println("Time for iteration: " + iterTime + "s");
            //startTime = System.nanoTime();

            System.out.println("------------------------------------------------");
        }

        //saveToCsv.writeCsvFile("geneticRun", generationsResults, generationsWeights);
    }

    public static void main(String[] args) {
        int noWeights = Features.getNumberOfWeights();
        double maxWeight = 5;
        int populationSize = 50;
        int noGenerations = 3;

        double uniformRate = 0.5;
        double mutationRate = 0.015;
        int tournamentSize = 5;
        boolean elitism = true;

        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(noWeights, maxWeight, populationSize, noGenerations, uniformRate, mutationRate, tournamentSize, elitism, new PlayerSkeleton());
        geneticAlgorithm.execute();
    }

}


