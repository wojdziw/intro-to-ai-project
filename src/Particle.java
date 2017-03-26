/*
Credit: https://gandhim.wordpress.com/2010/04/04/particle-swarm-optimization-pso-sample-code-using-java/
*/

public class Particle {
    private double fitnessValue;

    private double[] velocity;
    private double[] location;

    public Particle() {
        super();
    }

    public Particle(double fitnessValue, double[] velocity, double[] location) {
        super();
        this.fitnessValue = fitnessValue;
        this.velocity = velocity;
        this.location = location;
    }

    public double[] getVelocity() {
        return velocity;
    }

    public void setVelocity(double[] velocity) {
        this.velocity = velocity;
    }

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }

    public double getFitnessValue() {
        double[] weights = location;
        double fitness = 0;
        int n = 50;
        // Averaging over N games to mitigate the impact of the random piece choice
        for (int i=0; i<n; i++)
            fitness += PlayerSkeleton.playAGame(weights, false, false);
        // Get average
        fitness /= n;

        fitnessValue = fitness;
        return fitnessValue;
    }
}