/*
Credit: https://gandhim.wordpress.com/2010/04/04/particle-swarm-optimization-pso-sample-code-using-java/
*/

import java.util.Random;
import java.util.Vector;

public class ParticleSwarmAlgorithm {

    private int SWARM_SIZE = 30;
    private int MAX_ITERATION = 300;
    private int NO_FEATURES = 33;
    private double RANGE = 10;
    private double VELOCITY_RANGE = 0.5;

    private double C1 = 2.0;
    private double C2 = 2.0;
    private double W_UPPERBOUND = 1.0;
    private double W_LOWERBOUND = 0.0;

    private Vector<Particle> swarm = new Vector<>();
    private double[] pBest = new double[SWARM_SIZE];
    private Vector<double[]> pBestLocation = new Vector<>();
    private double gBest;
    private double[] gBestLocation;
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
            int bestParticleIndex = getMaxPos(fitnessValueList);
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
                    newVel[j] = (w * p.getVelocity()[j]) +
                            (r1 * C1) * (pBestLocation.get(i)[j] - p.getLocation()[j]) +
                            (r2 * C2) * (gBestLocation[j] - p.getLocation()[j]);
                }
                p.setVelocity(newVel);

                // step 4 - update location
                double[] newLoc = new double[NO_FEATURES+1];
                for (int j=0; j<NO_FEATURES; j++) {
                    newLoc[j] = p.getLocation()[j] + newVel[j];
                }
                p.setLocation(newLoc);
            }

            double fitness = 0;
            int n = 10;
            // Averaging over N games to mitigate the impact of the random piece choice
            for (int i=0; i<n; i++)
                fitness += PlayerSkeleton.playAGame(gBestLocation, false, false);
            // Get average
            fitness /= n;

            System.out.println("ITERATION " + t + ": ");
            System.out.println("     Value: " + fitness);

            t++;
            updateFitnessList();
        }
        for (int j=0; j<NO_FEATURES+1; j++) {
            System.out.print(gBestLocation[j] + " ");
        }
        System.out.println("");
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

            // randomize velocity in the range defined in Problem Set
            double[] vel = new double[NO_FEATURES+1];
            for (int j=0; j<NO_FEATURES+1; j++) { // +1 so we initialize the last weight as well
                int plusMinus = Math.random() > 0.5 ? -1 : 1;
                vel[j] = plusMinus * VELOCITY_RANGE * Math.random();
            }

            p.setLocation(loc);
            p.setVelocity(vel);
            swarm.add(p);
        }
    }

    public void updateFitnessList() {
        for(int i=0; i<SWARM_SIZE; i++) {
            fitnessValueList[i] = swarm.get(i).getFitnessValue();
        }
    }

    public static int getMaxPos(double[] list) {
        int pos = 0;
        double maxValue = list[0];

        for(int i=0; i<list.length; i++) {
            if(list[i] > maxValue) {
                pos = i;
                maxValue = list[i];
            }
        }

        return pos;
    }

    public static void main(String[] args) {
        new ParticleSwarmAlgorithm().execute();
    }
}