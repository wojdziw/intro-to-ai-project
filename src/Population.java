public class Population {
    private Individual[] individuals;

    public Population(int populationSize, boolean initialise, int noFeatures, double maxWeight) {
        individuals = new Individual[populationSize];

        if (initialise)
            for (int i=0; i<size(); i++) {
                Individual newIndividual = new Individual(noFeatures, maxWeight);
                setIndividual(i, newIndividual);
            }
    }

    // A set of genes can be fit when testing, but not when it's actually used because the blocks are random as well
    public Individual getFittest() {
        int fittest = 0;
        int bestFitness = 0;
        for (int i=0; i<size(); i++) {
            int currentFitness = getIndividual(i).getFitness();
            if (currentFitness > bestFitness) {
                fittest = i;
                bestFitness = currentFitness;
            }
        }
        return getIndividual(fittest);
    }

    public int size() {
        return individuals.length;
    }

    public Individual getIndividual(int index) {
        return individuals[index];
    }

    public void setIndividual(int index, Individual indiv) {
        individuals[index] = indiv;
    }
}
