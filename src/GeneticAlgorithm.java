
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithm {

    private int noWeights;
    private double maxWeight;
    private int populationSize;
    private int noGenerations;

    private double crossoverRate;
    private double mutationRate;
    private int tournamentSize;
    private boolean elitism;

    private static Random random = new Random();
    int cores;

    public GeneticAlgorithm(int noWeights, double maxWeight, int populationSize, int noGenerations, double crossoverRate, double mutationRate, int tournamentSize, boolean elitism, int cores) {
        this.noWeights = noWeights;
        this.maxWeight = maxWeight;
        this.populationSize = populationSize;
        this.noGenerations = noGenerations;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.tournamentSize = tournamentSize;
        this.elitism = elitism;
        this.cores = cores;
    }

    private Population evolvePopulation(Population pop) {
        Population newPopulation = new Population(pop.size(), false, noWeights, maxWeight, cores);

        if (elitism)
            newPopulation.setIndividual(0, pop.getFittest());

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
        Individual newSol = new Individual(noWeights, maxWeight);
        Individual stronger = indiv1;
        Individual weaker = indiv2;
        if (indiv1.getFitness() < indiv2.getFitness()) {
            stronger = indiv2;
            weaker = indiv1;
        }
        // Loop through genes
        for (int i = 0; i< noWeights; i++) {
            // Crossover
            // probability>0.5 -> get more genes from the stronger parent
            if (Math.random() <= crossoverRate) {
                newSol.setGene(i, stronger.getGene(i));
            } else {
                newSol.setGene(i, weaker.getGene(i));
            }
        }
        return newSol;
    }

    private void mutate(Individual indiv) {
        //Loop through genes
        for (int i = 0; i< noWeights; i++) {
            if (Math.random() <= mutationRate) {
                // Create random gene
                double gene = random.nextGaussian()*maxWeight;
                indiv.setGene(i, gene);
            }
        }
    }

    // Tournament is picking a random sample of a chosen size and choosing the fittest out of that
    private Individual tournamentSelection(Population pop) {
        // Create a tournament population
        Population tournament = new Population(tournamentSize, false, noWeights, maxWeight,cores);
        for (int i = 0; i<tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.size());
            tournament.setIndividual(i, pop.getIndividual(randomId));
        }
        return tournament.getFittest();
    }

    public void execute(ListOfResults resultList) {

        Population myPop = new Population(populationSize, true, noWeights, maxWeight, cores);

        //double[][] generationsWeights = new double[noGenerations][noWeights];
        //int[] generationsResults =  new int[noGenerations];
        //long[] generationsTime = new long[noGenerations];
        long startTime = System.nanoTime();

        for (int generation = 0; generation<noGenerations; generation++) {

            myPop = evolvePopulation(myPop);
            Individual bestInd = myPop.getFittest();

            //generationsResults[generation] = bestInd.getFitness();
            //generationsWeights[generation] = bestInd.getGenes();
            //Extra info for parallellization eval.
            resultList.generationsResults.add(bestInd.getFitness());
            resultList.generationsPopulation.add(populationSize);
            resultList.generationsNumberOfCores.add(cores);
            resultList.generationCount.add(generation+1);

            long iterTime = (System.nanoTime() - startTime)/1000000000;
            startTime = System.nanoTime();
            resultList.generationsTime.add(iterTime);

            System.out.print(generation+1 + "(" + bestInd.getFitness() + "r," + iterTime+ "s), ");

            if (generation==noGenerations-1) {
                System.out.println("");
                myPop.printStats(generation);
            }
        }
        System.out.println("------------------------------------------------");
        //saveToCsv.writeCsvFile("geneticRun", generationsResults, generationsWeights, generationsTime);
    }

    public static void main(String[] args) {
        int noWeights = Features.getNumberOfWeights();
        double maxWeight = 5;
        int populationSize = 50;
        int noGenerations = 50;

        double crossoverRate = 0.7;
        double mutationRate = 0.02;
        int tournamentSize = 5;
        boolean elitism = true;

        long globalStartTime = System.nanoTime();
        long minInMs = 60000000000L;

        int cores = Runtime.getRuntime().availableProcessors();

        ListOfResults resultDump = new ListOfResults();
        int [] numberOfCores={1,4,12};
        int [] numberOfPop={20,30,40};

        for (int i=0; i<10; i++) {
            //GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(noWeights, maxWeight, populationSize, noGenerations, crossoverRate, mutationRate, tournamentSize, elitism, cores);
            //parallell..
            for (int core : numberOfCores){
                for (int pop : numberOfPop){
                    System.out.println("coreCount - " + core);
                    System.out.println("populationCount - " + pop);
                    System.out.println();
                    //Run it for 5 times so that we can get an average.
                    for (int j=0; j<5; j++) {
                        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(noWeights, maxWeight, pop, noGenerations, crossoverRate, mutationRate, tournamentSize, elitism, core);
                        geneticAlgorithm.execute(resultDump);
                        System.out.print("Total evolution time: " + ((System.nanoTime() - globalStartTime) / minInMs) + " minutes");
                    }
                    //saving a csv dump every 5th call.
                    saveToCsvParalellEvaluation.writeCsvFile("geneticRun-50gen", resultDump);
                }
            }
        }
    }
}


class ListOfResults{
    List<Integer> generationsResults;
    List<Long> generationsTime;
    List<Integer> generationsNumberOfCores;
    List<Integer> generationsPopulation;
    List<Integer> generationCount;

    ListOfResults(){
        this.generationsResults =  new ArrayList<Integer>();
        this.generationsTime = new ArrayList<Long>();
        this.generationsNumberOfCores = new ArrayList<Integer>();
        this.generationsPopulation = new ArrayList<Integer>();
        this.generationCount = new ArrayList<Integer>();
    }

}