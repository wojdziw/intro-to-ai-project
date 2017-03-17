// One individual = one weight vector
// Gene = weight for a particular feature

public class Individual {

    private double[] weights;
    private int fitness = 0;

    public Individual(int noFeatures, double maxWeight) {
        // one more than the number of features to have the bias term
        weights = new double[noFeatures+1];
            for (int i=0; i<noFeatures+1; i++) {
                int plusMinus = Math.random() > 0.5 ? -1 : 1;
                weights[i] = plusMinus * maxWeight * Math.random();
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
            int n = 10;

            // Averaging over N games to mitigate the impact of the random piece choice
            for (int i=0; i<n; i++)
                fitness += PlayerSkeleton.playAGame(weights, false, false);

            // Get average
            fitness = fitness/n; // TODO: return median instead?
        }
        return fitness;
    }

    public double[] getGenes() {
        return weights;
    }

    public void printGenes() {
        int i = 0;
        for (double gene : getGenes()) {
            System.out.print(i + ": " + String.format("%.2f", gene) + " ");
            i++;
            if (i == 14) {
                System.out.println();
            }
        }
        System.out.println("");
    }

}