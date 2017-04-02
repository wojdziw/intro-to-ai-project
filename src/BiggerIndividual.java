// One individual = one weight vector
// Gene = weight for a particular feature

import java.util.Random;

public class BiggerIndividual {

    private Integer[] weights;
    private int fitness = 0;

    double maxWeight = 5;
    int populationSize = 50;
    int noGenerations = 10;

    double crossoverRate = 0.7;
    double mutationRate = 0.015;
    int tournamentSize = 5;
    boolean elitism = true;

    public static Random random = new Random();

    public BiggerIndividual(int noWeights) {
        weights = new Integer[noWeights];
        for (int i=0; i<weights.length; i++)
            weights[i] = -1;
        int i=0;
        while(i!=noWeights) {
            int weight = 1+(int)(Math.random() * 15);
            if (!alreadyIn(weight, weights)) {
                weights[i] = weight;
                i++;
            }
        }
    }

    public int getGene(int index) {
        return weights[index];
    }

    public void setGene(int index, int value) {
        weights[index] = value;
    }

    public int getFitness() {

        if (fitness==0) {
            GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(Features.getNumberOfWeights(weights), maxWeight, populationSize, noGenerations, crossoverRate, mutationRate, tournamentSize, elitism, weights);
            fitness = geneticAlgorithm.execute();
        }

        return fitness;
    }

    public Integer[] getGenes() {
        return weights;
    }

    public void printGenes() {
        Integer[] genes = getGenes();
        for (int i=0; i<genes.length; i++) {
            double gene = genes[i];
            System.out.print(i + ": " + String.format("%.2f", gene) + " ");
        }
        System.out.println("");
    }

    public static boolean alreadyIn(int a, Integer[] array) {
        for (int b: array) {
            if (a==b)
                return true;
        }
        return false;
    }

    public boolean alreadyInGenes(int a) {
        for (int b: weights) {
            if (a==b)
                return true;
        }
        return false;
    }

}