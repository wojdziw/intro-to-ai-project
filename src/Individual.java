// One individual = one weight vector
// Gene = weight for a particular feature

public class Individual {

    private double[] weights;

    public Individual(int noFeatures, double maxWeight) {
        // one more than the number of features to have the bias term
        weights = new double[noFeatures+1];
        for (int i=0; i<noFeatures; i++) {
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
        int fitness = 0;
        int n = 10;

        // Averaging over N games to mitigate the impact of the random piece choice
        for (int i=0; i<n; i++)
            fitness += PlayerSkeleton.playAGame(weights, false, false);

        return fitness/n;
    }

    public double[] getGenes() {
        return weights;
    }

    public void printGenes() {
        for (double gene : getGenes())
            System.out.print(gene + " ");
        System.out.println("");
    }
}