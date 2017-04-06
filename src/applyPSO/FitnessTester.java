package applyPSO;
/*
Credit: https://gandhim.wordpress.com/2010/04/04/particle-swarm-optimization-pso-sample-code-using-java/
*/

public class FitnessTester {
    public static void main(String[] args) {
        double[] weights = {15,-11,-13,-1,-4,-4,-3,-4,-3,-4,-3,-5,-2,-3};
        int n = 50;
        System.out.println(PlayerSkeleton.playNGames(weights, false, false, n));
    }
}
