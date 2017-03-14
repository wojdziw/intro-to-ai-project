// One individual = one weight vector
// Gene = weight for a particular feature

public class Individual {

    private double[] weights;
    private double[] oldRun = {0.7215727531979685,0.629375417834268,-1.5903849538619526,0.5997901536484984,-1.2370721728394585,-2.4297072487864635,0.5871795583595759,-2.4272853310161047,-1.1718072414441014,-2.157307261291666,0.7218920801958619,-2.055454976707055,-1.7648005797035609,-2.765182531776981,1.7567019269806847,-2.493205219456943,-0.4758245984874532,-0.9011726261139698,-0.7546060965685168,-0.16815877164789017,-1.2704622765169789,0.31296646659737015,-1.7752386007295764,0.7388577480297623,0.5155311604774838,0.3408700659912487,-0.3778960248863784,-1.0957250202847608,-2.304975635423392,0.05813441818746934,-1.2800548175952877,-0.9050876151110219,0.0};
    boolean useOldRun = true;

    public Individual(int noFeatures, double maxWeight) {
        // one more than the number of features to have the bias term
        weights = new double[noFeatures+1];
        if (!useOldRun){
            for (int i=0; i<noFeatures; i++) {
                int plusMinus = Math.random() > 0.5 ? -1 : 1;
                weights[i] = plusMinus * maxWeight * Math.random();
            }
        } else {
            weights=oldRun;
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