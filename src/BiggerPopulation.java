import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class BiggerPopulation {

    //creating a pool for the number of available cores (or threads).
    static int cores = Runtime.getRuntime().availableProcessors();

    private BiggerIndividual[] individuals;
    public BiggerPopulation(int populationSize, boolean initialise, int noFeatures) {
        individuals = new BiggerIndividual[populationSize];

        if (initialise)
            for (int i=0; i<size(); i++) {
                BiggerIndividual newIndividual = new BiggerIndividual(noFeatures);
                setIndividual(i, newIndividual);
            }
    }

    // A set of genes can be fit when testing, but not when it's actually used because the blocks are random as well
    public BiggerIndividual getFittest() {
        int fittestIdx = 0;
        int bestFitness = 0;

        ExecutorService executor = Executors.newFixedThreadPool(cores);
        //create a list to hold the Future object associated with Callable
        List<Future<BiggerPairResult>> list = new ArrayList<Future<BiggerPairResult>>();
        //Create MyCallable instance
        for (int i=0; i<size(); i++) {
            Callable<BiggerPairResult> callable = new BiggerMyCallable(getIndividual(i), i);
            //submit Callable tasks to be executed by thread pool
            Future<BiggerPairResult> future = executor.submit(callable);
            //add Future to the list, we can get return value using Future
            list.add(future);
        }
        for(Future<BiggerPairResult> fut : list){
            try {
                //print the return value of Future
                // because Future.get() waits for task to get completed
                if (fut.get().getScore() > bestFitness){
                    bestFitness = fut.get().getScore();
                    fittestIdx = fut.get().getOrder();
                }

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        //shut down the executor service now
        shutdownAndAwaitTermination(executor);
        return getIndividual(fittestIdx);
    }

    public int size() {
        return individuals.length;
    }

    public int getCores() {
        return cores;
    }

    public BiggerIndividual getIndividual(int index) {
        return individuals[index];
    }

    public void setIndividual(int index, BiggerIndividual indiv) {
        individuals[index] = indiv;
    }

    public void printStats(int generation) {




            BiggerIndividual bestInd = getFittest();

            System.out.println("BIG LOOP FINISHED AN ITERATION!");

            int fitness=bestInd.getFitness();
            System.out.print("Generation: " + (generation+1) + " Fittest: " + fitness);
            bestInd.printGenes();



    }

    void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                pool.shutdownNow();
                System.out.println("Closed a pool before termination");// Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(5, TimeUnit.SECONDS))
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

class BiggerMyCallable implements Callable<BiggerPairResult> {
    private final BiggerIndividual individual ;
    private final int order;

    BiggerMyCallable(BiggerIndividual individual, int order) {
        this.individual = individual;
        this.order = order;
    }
    @Override
    public BiggerPairResult call() throws Exception {
        //return the thread name executing this callable task
        int score = individual.getFitness();
        return new BiggerPairResult(order,score);
    }
}

class BiggerPairResult{
    private final int order;
    private final int score;

    BiggerPairResult(int order, int score){
        this.order = order;
        this.score = score;
    }

    public int getScore(){
        return this.score;
    }

    public int getOrder() {
        return this.order;
    }
}




