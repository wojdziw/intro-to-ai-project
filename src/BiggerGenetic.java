import java.util.Random;

public class BiggerGenetic {

    private int noWeights;
    private int populationSize;
    private int noGenerations;

    private double crossoverRate;
    private double mutationRate;
    private int tournamentSize;
    private boolean elitism;

    private static Random random = new Random();

    public BiggerGenetic(int noWeights, int populationSize, int noGenerations, double crossoverRate, double mutationRate, int tournamentSize, boolean elitism) {
        this.noWeights = noWeights;
        this.populationSize = populationSize;
        this.noGenerations = noGenerations;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.tournamentSize = tournamentSize;
        this.elitism = elitism;
    }

    private BiggerPopulation evolvePopulation(BiggerPopulation pop) {
        BiggerPopulation newPopulation = new BiggerPopulation(pop.size(), false, noWeights);

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

    // THIS ISN"T RIGHT
    private BiggerIndividual crossover(BiggerIndividual indiv1, BiggerIndividual indiv2) {
        BiggerIndividual newSol = new BiggerIndividual(noWeights);
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
                if (!newSol.alreadyInGenes(stronger.getGene(i))) {
                    newSol.setGene(i, stronger.getGene(i));
                } else {
                    newSol.setGene(i, weaker.getGene(i));
                }

            } else {
                if (!newSol.alreadyInGenes(stronger.getGene(i))) {
                    newSol.setGene(i, weaker.getGene(i));
                } else {
                    newSol.setGene(i, stronger.getGene(i));
                }
            }
        }
        return newSol;
    }

    private void mutate(BiggerIndividual indiv) {
        int i=0;
        while(i!=noWeights) {
            int weight = 1+(int)(Math.random() * 15);
            if (!indiv.alreadyInGenes(weight)) {
                if (Math.random() <= mutationRate) {
                    indiv.setGene(i, weight);
                    i++;
                }
            }
        }
        indiv.sortGenes();
    }

    // Tournament is picking a random sample of a chosen size and choosing the fittest out of that
    private BiggerIndividual tournamentSelection(BiggerPopulation pop) {
        // Create a tournament population
        BiggerPopulation tournament = new BiggerPopulation(tournamentSize, false, noWeights);
        for (int i = 0; i<tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.size());
            tournament.setIndividual(i, pop.getIndividual(randomId));
        }
        return tournament.getFittest();
    }

    public void execute() {

        BiggerPopulation myPop = new BiggerPopulation(populationSize, true, noWeights);

        //System.out.println("NrOfCores: " + myPop.getCores()); // Check if it finds all cores

        Integer[][] generationsWeights = new Integer[noGenerations][noWeights];
        int[][] generationsResults =  new int[noGenerations][2];

        long startTime = System.nanoTime();


        for (int generation = 0; generation<noGenerations; generation++) {

            myPop = evolvePopulation(myPop);

            generationsResults[generation][0] = myPop.getFittest().getFitness();
            generationsWeights[generation]=myPop.getFittest().getGenes();

            saveToCsv.writeCsvFile("geneticRun", generationsResults, generationsWeights);

            long iterTime = (System.nanoTime() - startTime)/1000000000;
            startTime = System.nanoTime();


            //System.out.println(generation+1 + "(" + myPop.getFittest().getFitness() + "r," + iterTime+ "s), ");
            myPop.printStats(generation);
        }
        //System.out.println("------------------------------------------------");

        //saveToCsv.writeCsvFile("geneticRun", generationsResults, generationsWeights);
    }


    public static void main(String[] args) {

        double maxWeight = 5;
        int populationSize = 30;
        int noGenerations = 30;

        double crossoverRate = 0.7;
        double mutationRate = 0.015;
        int tournamentSize = 5;
        boolean elitism = true;

        BiggerGenetic biggerGenetic = new BiggerGenetic(5, populationSize, noGenerations, crossoverRate, mutationRate, tournamentSize, elitism);
        biggerGenetic.execute();

    }
}
