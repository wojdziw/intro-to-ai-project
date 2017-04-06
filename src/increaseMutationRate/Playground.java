package increaseMutationRate;
public class Playground {
    public static void main(String[] args) {

        int noFeatures = 15 + (State.COLS - 1) + (State.COLS -2);
        double[] weights = new double[noFeatures+1];//33
        for (int i=0; i<noFeatures+1; i++) { // +1 so we initialize the last weight as well
            weights[i] = Double.parseDouble(args[i]);
        }
        double fitness = 0;
        int n=10;
        for (int i=0; i<n; i++)
            fitness += PlayerSkeleton.playAGame(weights, false,false);
        fitness /= n;
        System.out.print(fitness);

    }
}
