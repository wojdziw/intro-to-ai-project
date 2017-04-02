import java.util.Random;

public class BiggerGenetic {

    private int noWeights;
    private double maxWeight;
    private int populationSize;
    private int noGenerations;

    private double crossoverRate;
    private double mutationRate;
    private int tournamentSize;
    private boolean elitism;
    private Integer[] subsetArray;

    private static Random random = new Random();

    public BiggerGenetic(int noWeights, double maxWeight, int populationSize, int noGenerations, double crossoverRate, double mutationRate, int tournamentSize, boolean elitism, Integer[] subsetArray) {
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

    private BiggerPopulation evolvePopulation(BiggerPopulation pop) {
        BiggerPopulation newPopulation = new BiggerPopulation(pop.size(), false, noWeights, maxWeight, subsetArray);

        if (elitism)
            newPopulation.setIndividual(0, pop.getFittest());

        int elitismOffset = elitism ? 1 : 0;

        // Loop over the population size and create new individuals with crossover
        for (int i = elitismOffset; i<pop.size(); i++) {
            BiggerIndividual indiv1 = tournamentSelection(pop); // Find the fittest among 5 random individuals
            BiggerIndividual indiv2 = tournamentSelection(pop);

            BiggerIndividual newIndiv = crossover(indiv1, indiv2);
            newPopulation.setIndividual(i, newIndiv);
        }

        // Mutate population
        for (int i=elitismOffset; i<newPopulation.size(); i++)
            mutate(newPopulation.getIndividual(i));
        return newPopulation;
    }

    private BiggerIndividual crossover(BiggerIndividual indiv1, BiggerIndividual indiv2) {
        BiggerIndividual newSol = new BiggerIndividual(noWeights, maxWeight, subsetArray);
        BiggerIndividual stronger = indiv1;
        BiggerIndividual weaker = indiv2;
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

    private void mutate(BiggerIndividual indiv) {
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
    private BiggerIndividual tournamentSelection(BiggerPopulation pop) {
        // Create a tournament population
        BiggerPopulation tournament = new BiggerPopulation(tournamentSize, false, noWeights, maxWeight, subsetArray);
        for (int i = 0; i<tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.size());
            tournament.setIndividual(i, pop.getIndividual(randomId));
        }
        return tournament.getFittest();
    }

    public void execute() {

        BiggerPopulation myPop = new BiggerPopulation(populationSize, true, noWeights, maxWeight, subsetArray);

        //System.out.println("NrOfCores: " + myPop.getCores()); // Check if it finds all cores

        //double[][] generationsWeights = new double[noGenerations][noWeights];
        //int[][] generationsResults =  new int[noGenerations][2];

        long startTime = System.nanoTime();


        for (int generation = 0; generation<noGenerations; generation++) {

            myPop = evolvePopulation(myPop);

            //generationsResults[generation][0] = myPop.getFittest().getFitness();
            //generationsWeights[generation]=myPop.getFittest().getGenes();

            long iterTime = (System.nanoTime() - startTime)/1000000000;
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

        double maxWeight = 5;
        int populationSize = 50;
        int noGenerations = 10;

        double crossoverRate = 0.7;
        double mutationRate = 0.015;
        int tournamentSize = 5;
        boolean elitism = true;
        Integer[] subsetArray = {0,7,9,11,15};
        int noWeights = Features.getNumberOfWeights(subsetArray);

        BiggerGenetic biggerGenetic = new BiggerGenetic(noWeights, maxWeight, populationSize, noGenerations, crossoverRate, mutationRate, tournamentSize, elitism, subsetArray);
        biggerGenetic.execute();
    }
}
