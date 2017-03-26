/*
Credit: https://gandhim.wordpress.com/2010/04/04/particle-swarm-optimization-pso-sample-code-using-java/
*/

public class Velocity {
    // store the Velocity in an array to accommodate multi-dimensional problem space
    private double[] vel;

    public Velocity(double[] vel) {
        super();
        this.vel = vel;
    }

    public double[] getVel() {
        return vel;
    }

    public void setVel(double[] vel) {
        this.vel = vel;
    }

}