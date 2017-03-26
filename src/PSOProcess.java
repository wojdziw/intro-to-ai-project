/*
Credit: https://gandhim.wordpress.com/2010/04/04/particle-swarm-optimization-pso-sample-code-using-java/
*/

import java.util.Random;
import java.util.Vector;

public class PSOProcess implements PSOConstants {
    private Vector<Particle> swarm = new Vector<Particle>();
    private double[] pBest = new double[SWARM_SIZE];
    private Vector<Location> pBestLocation = new Vector<Location>();
    private double gBest;
    private Location gBestLocation;
    private double[] fitnessValueList = new double[SWARM_SIZE];

    Random generator = new Random();

    public void execute() {
        initializeSwarm();
        updateFitnessList();

        for(int i=0; i<SWARM_SIZE; i++) {
            pBest[i] = fitnessValueList[i];
            pBestLocation.add(swarm.get(i).getLocation());
        }

        int t = 0;
        double w;

        while(t < MAX_ITERATION) {
            // step 1 - update personal best
            for(int i=0; i<SWARM_SIZE; i++) {
                if(fitnessValueList[i] < pBest[i]) {
                    pBest[i] = fitnessValueList[i];
                    pBestLocation.set(i, swarm.get(i).getLocation());
                }
            }

            // step 2 - update gBest
            int bestParticleIndex = PSOUtility.getMaxPos(fitnessValueList);
            if(t == 0 || fitnessValueList[bestParticleIndex] > gBest) {
                gBest = fitnessValueList[bestParticleIndex];
                gBestLocation = swarm.get(bestParticleIndex).getLocation();
            }

            w = W_UPPERBOUND - (((double) t) / MAX_ITERATION) * (W_UPPERBOUND - W_LOWERBOUND);

            for(int i=0; i<SWARM_SIZE; i++) {
                double r1 = generator.nextDouble();
                double r2 = generator.nextDouble();

                Particle p = swarm.get(i);

                // step 3 - update velocity
                double[] newVel = new double[NO_FEATURES];
                for (int j=0; j<NO_FEATURES; j++) {
                    newVel[j] = (w * p.getVelocity().getVel()[j]) +
                            (r1 * C1) * (pBestLocation.get(i).getLoc()[j] - p.getLocation().getLoc()[j]) +
                            (r2 * C2) * (gBestLocation.getLoc()[j] - p.getLocation().getLoc()[j]);
                }
                Velocity vel = new Velocity(newVel);
                p.setVelocity(vel);

                // step 4 - update location
                double[] newLoc = new double[NO_FEATURES+1];
                for (int j=0; j<NO_FEATURES; j++) {
                    newLoc[j] = p.getLocation().getLoc()[j] + newVel[j];
                }
                Location loc = new Location(newLoc);
                p.setLocation(loc);
            }

            double fitness = 0;
            int n = 10;
            // Averaging over N games to mitigate the impact of the random piece choice
            for (int i=0; i<n; i++)
                fitness += PlayerSkeleton.playAGame(gBestLocation.getLoc(), false, false);
            // Get average
            fitness /= n;



            System.out.println("ITERATION " + t + ": ");
            System.out.println("     Value: " + fitness);

            t++;
            updateFitnessList();
        }

        System.out.println("\nSolution found at iteration " + (t - 1) + ", the solutions is:");
        System.out.println("     Best X: " + gBestLocation.getLoc()[0]);
        System.out.println("     Best Y: " + gBestLocation.getLoc()[1]);
    }

    public void initializeSwarm() {
        Particle p;
        for(int i=0; i<SWARM_SIZE; i++) {
            p = new Particle();

            // randomize location inside a space defined in Problem Set
            double[] loc = new double[NO_FEATURES+1];

            for (int j=0; j<NO_FEATURES+1; j++) { // +1 so we initialize the last weight as well
                int plusMinus = Math.random() > 0.5 ? -1 : 1;
                loc[j] = plusMinus * RANGE * Math.random();
            }

            Location location = new Location(loc);

            // randomize velocity in the range defined in Problem Set
            double[] vel = new double[NO_FEATURES+1];
            for (int j=0; j<NO_FEATURES+1; j++) { // +1 so we initialize the last weight as well
                int plusMinus = Math.random() > 0.5 ? -1 : 1;
                vel[j] = plusMinus * VELOCITY_RANGE * Math.random();
            }
            Velocity velocity = new Velocity(vel);

            p.setLocation(location);
            p.setVelocity(velocity);
            swarm.add(p);
        }
    }

    public void updateFitnessList() {
        for(int i=0; i<SWARM_SIZE; i++) {
            fitnessValueList[i] = swarm.get(i).getFitnessValue();
        }
    }
}