
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
    private Integer[] subsetArray;

    public static Object lock = new Object();

    private static Random random = new Random();

    public GeneticAlgorithm(int noWeights, double maxWeight, int populationSize, int noGenerations, double crossoverRate, double mutationRate, int tournamentSize, boolean elitism, Integer[] subsetArray) {
        this.noWeights = noWeights;
        this.maxWeight = maxWeight;
        this.populationSize = populationSize;
        this.noGenerations = noGenerations;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.tournamentSize = tournamentSize;
        this.elitism = elitism;
        this.subsetArray = subsetArray;
    }

    private Population evolvePopulation(Population pop) {
        Population newPopulation = new Population(pop.size(), false, noWeights, maxWeight, subsetArray);

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
        Individual newSol = new Individual(noWeights, maxWeight, subsetArray);
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
        Population tournament = new Population(tournamentSize, false, noWeights, maxWeight, subsetArray);
        for (int i = 0; i<tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.size());
            tournament.setIndividual(i, pop.getIndividual(randomId));
        }
        return tournament.getFittest();
    }

    public int execute() {

        Population myPop = new Population(populationSize, true, noWeights, maxWeight, subsetArray);

        //System.out.println("NrOfCores: " + myPop.getCores()); // Check if it finds all cores

        //double[][] generationsWeights = new double[noGenerations][noWeights];
        //int[][] generationsResults =  new int[noGenerations][2];



        for (int generation = 0; generation<noGenerations; generation++) {

            myPop = evolvePopulation(myPop);

            //generationsResults[generation][0] = myPop.getFittest().getFitness();
            //generationsWeights[generation]=myPop.getFittest().getGenes();

            synchronized(lock) {
                System.out.print("[");
                for (int i=0; i<subsetArray.length-1; i++)
                    System.out.print(subsetArray[i]+",");
                System.out.print(subsetArray[subsetArray.length-1]);
                System.out.print("]");

                System.out.print("(g" + generation +","+ myPop.getFittest().getFitness() + "rows) ");

                if (generation==noGenerations-1)
                    System.out.print(" - finished!");

                System.out.println("");
            }
        }
        //saveToCsv.writeCsvFile("geneticRun", generationsResults, generationsWeights);
        return myPop.getFittest().getFitness();
    }

    public static void main(String[] args) {

        double maxWeight = 5;
        int populationSize = 50;
        int noGenerations = 10;

        double crossoverRate = 0.7;
        double mutationRate = 0.015;
        int tournamentSize = 5;
        boolean elitism = true;
        Integer[] subsetArray = {0,7,9,10,11,15};
        int noWeights = Features.getNumberOfWeights(subsetArray);

        for (int i=0; i<20; i++) {
            GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(noWeights, maxWeight, populationSize, noGenerations, crossoverRate, mutationRate, tournamentSize, elitism, subsetArray);
            geneticAlgorithm.execute();


        }


    }

}







