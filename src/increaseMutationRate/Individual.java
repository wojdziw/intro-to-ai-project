package increaseMutationRate;
// One individual = one weight vector
// Gene = weight for a particular feature

import java.util.Random;

public class Individual {

    private double[] weights;
    private int fitness = 0;

    public static Random random = new Random();

    public Individual(int noWeights, double maxWeight) {
        weights = new double[noWeights];
            for (int i=0; i<noWeights; i++) {
                weights[i] = random.nextGaussian()*maxWeight;
            }
    }

    public double getGene(int index) {
        return weights[index];
    }

    public void setGene(int index, double value) {
        weights[index] = value;
    }

    public int getFitness() {

        if (fitness==0) {
            int N = 10;
            fitness = PlayerSkeleton.playNGames(weights, false, false, N);
        }

        return fitness;
    }

    public double[] getGenes() {
        return weights;
    }

    public void printGenes() {
        double[] genes = getGenes();
        for (int i=0; i<genes.length; i++) {
            double gene = genes[i];
            System.out.print(i + ": " + String.format("%.2f", gene) + " ");
        }
        System.out.println("");
    }

}