import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Population {

    //creating a pool for the number of available cores (or threads).
    //static int cores = Runtime.getRuntime().availableProcessors();
    static int cores;
    static int cols;

    private Individual[] individuals;
    public Population(int populationSize, boolean initialise, int noFeatures, double maxWeight) {
        individuals = new Individual[populationSize];

        if (initialise)
            for (int i=0; i<size(); i++) {
                Individual newIndividual = new Individual(noFeatures, maxWeight);
                setIndividual(i, newIndividual);
            }
    }

    /*
    // A set of genes can be fit when testing, but not when it's actually used because the blocks are random as well
    public Individual getFittest() {
        int fittestIdx = 0;
        int bestFitness = 0;

        ExecutorService executor = Executors.newFixedThreadPool(cores);
        //create a list to hold the Future object associated with Callable
        List<Future<PairResult>> list = new ArrayList<Future<PairResult>>();
        //Create MyCallable instance
        for (int i=0; i<size(); i++) {
            Callable<PairResult> callable = new MyCallable(getIndividual(i), i, cols, cores);
            //submit Callable tasks to be executed by thread pool
            Future<PairResult> future = executor.submit(callable);
            //add Future to the list, we can get return value using Future
            list.add(future);
        }
        for(Future<PairResult> fut : list){
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
        //executor.shutdown();
        shutdownAndAwaitTermination(executor);
        return getIndividual(fittestIdx);
    }
    */

    public Individual getFittest() {
        int fittestIdx = 0;
        int bestFitness = 0;
        for (int i=0; i<size(); i++) {
            int currentFitness = getIndividual(i).getFitness(cols, cores);
            if (currentFitness > bestFitness) {
                fittestIdx = i;
                bestFitness = currentFitness;
            }
        }
        return getIndividual(fittestIdx);
    }

    public int size() {
        return individuals.length;
    }

    public void setCC(int globalCols, int globalCores) {
        this.cols = globalCols;
        this.cores = globalCores;

    }

    public int getCores() {
        return cores;
    }

    public int getColumns() {
        return cols;
    }

    public Individual getIndividual(int index) {
        return individuals[index];
    }

    public void setIndividual(int index, Individual indiv) {
        individuals[index] = indiv;
    }

    public void printStats(int generation) {

        Individual bestInd = getFittest();

        int fitness=bestInd.getFitness(cols, cores);
        System.out.println("Generation: " + (generation+1) + " Fittest: " + fitness);
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

    class MyCallable implements Callable<PairResult> {
        private final Individual individual ;
        private final int order;
        private final int cores;
        private final int columns;

        MyCallable(Individual individual, int order, int cols, int cores) {
            this.individual = individual;
            this.order = order;
            this.columns = cols;
            this.cores = cores;
        }
        @Override
        public PairResult call() throws Exception {
            //return the thread name executing this callable task
            int score = individual.getFitness(columns, cores);
            return new PairResult(order,score);
        }
    }

    class PairResult{
        private final int order;
        private final int score;

        PairResult(int order, int score){
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




