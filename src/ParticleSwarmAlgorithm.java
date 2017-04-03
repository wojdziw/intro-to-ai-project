/*
Credit: https://gandhim.wordpress.com/2010/04/04/particle-swarm-optimization-pso-sample-code-using-java/
*/

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.*;

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

            int N = 10;
            int fitness = PlayerSkeleton.playNGames(gBestLocation, false, false, N);


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

    int cores = Runtime.getRuntime().availableProcessors();
    public void updateFitnessList() {
        ExecutorService executor = Executors.newFixedThreadPool(cores);
        List<Future<PairResultPOS>> futureFitnessValueList = new ArrayList<>();

        for(int i=0; i<SWARM_SIZE; i++) {
            Callable<PairResultPOS> callable = new MyCallablePOS(swarm.get(i), i);
            Future<PairResultPOS> future = executor.submit(callable);
            futureFitnessValueList.add(future);
        }

        for(Future<PairResultPOS> fut : futureFitnessValueList){
            try {
                fitnessValueList[fut.get().getOrder()] = fut.get().getFitness();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        shutdownAndAwaitTermination(executor);
    }

    void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(1, TimeUnit.SECONDS)) {
                pool.shutdownNow();
                System.out.println("Closed a pool before termination");// Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(1, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            System.out.println(ie.getMessage());
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}

class MyCallablePOS implements Callable<PairResultPOS> {
    private final Particle particle;
    private final int index;

    MyCallablePOS(Particle particle, int index){
        this.particle = particle;
        this.index = index;

    }
    @Override
    public PairResultPOS call() throws Exception {
        //return the thread name executing this callable task
        Double ret = particle.getFitnessValue();
        return new PairResultPOS(this.index, ret);
    }
}

class PairResultPOS{
    private final int order;
    private final Double fitness;

    PairResultPOS(int order,Double fitness){
        this.order = order;
        this.fitness = fitness;
    }

    public Double getFitness(){
        return this.fitness;
    }

    public int getOrder() {
        return this.order;
    }
}
