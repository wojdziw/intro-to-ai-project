import java.util.LinkedList;
import java.util.Vector;

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
    private double growthRateThreshold; //rate of growth at which to switch to particle swarm optimization (aka lowest acceptable threshold)
    LinkedList<Integer> fitnessOfGenerations;
    private int growthRange; //number of fitness scores to use to calculate the growth rate
    private int psoIterations;

    private static Random random = new Random();

    //public GeneticAlgorithm(int noWeights, double maxWeight, int populationSize, int noGenerations, double crossoverRate, double mutationRate, int tournamentSize, boolean elitism) {
     public GeneticAlgorithm(int noWeights, double maxWeight, int populationSize, int noGenerations, double crossoverRate, double mutationRate, int tournamentSize, boolean elitism, double growthRateThreshold, int growthRange, int psoIterations){
        this.noWeights = noWeights;
        this.maxWeight = maxWeight;
        this.populationSize = populationSize;
        this.noGenerations = noGenerations;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.tournamentSize = tournamentSize;
        this.elitism = elitism;
        this.growthRateThreshold = growthRateThreshold;
        this.fitnessOfGenerations = new LinkedList<Integer>();
       	this.growthRange = growthRange;
       	this.psoIterations = psoIterations;
    }

    private Population evolvePopulation(Population pop) {
        Population newPopulation = new Population(pop.size(), false, noWeights, maxWeight);

        //check if rate of growth has reached a plateau - if growth rate < growthRateThreshold
        //if yes, apply particle swarm to get new set of weights
        
        if(fitnessOfGenerations.size() == growthRange && (((double)fitnessOfGenerations.getLast() - (double)fitnessOfGenerations.getFirst())/(double)fitnessOfGenerations.getFirst()) < growthRateThreshold) {
        	// apply particle swarm 
        	
        	//TEST
        	System.out.println("Entering PSO");
        	
        	ParticleSwarmAlgorithm particleSwarm = new ParticleSwarmAlgorithm(pop.getFittest().getGenes(), pop.size(), psoIterations);
        	particleSwarm.execute();
        	
        	//TEST
        	System.out.println("Back to genetic algorithm");
        	
        	
        	Vector<Particle> swarm = particleSwarm.getSwarm();
        	
        	//transform resulting particles from swarm to individuals for a new population
        	for (int i = 0; i<swarm.size(); i++) {
        		Individual indiv = new Individual(swarm.get(i));
        		newPopulation.setIndividual(i, indiv);
        	}
        		     	
        } else {
        	// keep using genetic algorithm
        	
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
        	
        }

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
        Population tournament = new Population(tournamentSize, false, noWeights, maxWeight);
        for (int i = 0; i<tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.size());
            tournament.setIndividual(i, pop.getIndividual(randomId));
        }
        return tournament.getFittest();
    }

    public void execute() {

        Population myPop = new Population(populationSize, true, noWeights, maxWeight);

        //System.out.println("NrOfCores: " + myPop.getCores()); // Check if it finds all cores

        //double[][] generationsWeights = new double[noGenerations][noWeights];
        //int[][] generationsResults =  new int[noGenerations][2];

        long startTime = System.nanoTime();
        int oldBestFitness = 0;

        for (int generation = 0; generation<noGenerations; generation++) {

            myPop = evolvePopulation(myPop);

            System.out.print(generation + " ");
            if (generation==noGenerations-1) {
                System.out.println("");
                myPop.printStats(generation);
            }
            
            int generationFitness = myPop.getFittest().getFitness();
            //updatefitnessOfGenerations
            
            fitnessOfGenerations.add(generationFitness);
            if(fitnessOfGenerations.size() > growthRange) {
            	fitnessOfGenerations.removeFirst();
            }

            //generationsResults[generation][0] = myPop.getFittest().getFitness();
            //generationsWeights[generation]=myPop.getFittest().getGenes();

            long iterTime = (System.nanoTime() - startTime)/1000000000;
            System.out.print("Time for iteration: " + iterTime + "s");
            System.out.print(" -- Fitness: " + generationFitness);
            System.out.println(" -- Growth rate: " + ((double)(fitnessOfGenerations.getLast() - (double)fitnessOfGenerations.getFirst())/(double)fitnessOfGenerations.getFirst()) );
            
            //print weights if fitness > 50,000
            if (generationFitness > 50000 && generationFitness > oldBestFitness) {
                oldBestFitness = generationFitness;
                
                System.out.print(" - Weights: ");
                myPop.getFittest().printGenes();
            	for(int i = 0; i<myPop.getFittest().getGenes().length; i++) {
            		System.out.print("[" + i + "]" + myPop.getFittest().getGene(i) + " ");
            	}
            	System.out.println();
            	
            }

            startTime = System.nanoTime();

            System.out.print(generation+1 + "(" + myPop.getFittest().getFitness() + "r," + iterTime+ "s), ");

            if (generation==noGenerations-1) {
                System.out.println("");
                myPop.printStats(generation);
            }
        }
        System.out.println("------------------------------------------------");

        //saveToCsv.writeCsvFile("geneticRun", generationsResults, generationsWeights);
    }

    public static void main(String[] args) {
        int noWeights = Features.getNumberOfWeights();
        double maxWeight = 5;
        int populationSize = 50;
        int noGenerations = 30;

        double crossoverRate = 0.7;
        double mutationRate = 0.005;
        int tournamentSize = 5;
        boolean elitism = true;
        double growthRateThreshold = 0.2;
        int growthRange = 3;
        int psoIterations = 3;

        for (int i=0; i<2; i++) {
        	GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(noWeights, maxWeight, populationSize, noGenerations, crossoverRate, mutationRate, tournamentSize, elitism, growthRateThreshold, growthRange, psoIterations);
            //GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(noWeights, maxWeight, populationSize, noGenerations, crossoverRate, mutationRate, tournamentSize, elitism);
            geneticAlgorithm.execute();

        }


    }

}







