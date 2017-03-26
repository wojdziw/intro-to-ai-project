/*
Credit: https://gandhim.wordpress.com/2010/04/04/particle-swarm-optimization-pso-sample-code-using-java/
*/

public class Particle {
    private double fitnessValue;
    private Velocity velocity;
    private Location location;

    public Particle() {
        super();
    }

    public Particle(double fitnessValue, Velocity velocity, Location location) {
        super();
        this.fitnessValue = fitnessValue;
        this.velocity = velocity;
        this.location = location;
    }

    public Velocity getVelocity() {
        return velocity;
    }

    public void setVelocity(Velocity velocity) {
        this.velocity = velocity;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public double getFitnessValue() {
        double[] weights = location.getLoc();
        double fitness = 0;
        int n = 10;
        // Averaging over N games to mitigate the impact of the random piece choice
        for (int i=0; i<n; i++)
            fitness += PlayerSkeleton.playAGame(weights, false, false);
        // Get average
        fitness /= n;

        fitnessValue = fitness;
        return fitnessValue;
    }
}