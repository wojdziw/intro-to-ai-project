import static org.jenetics.engine.EvolutionResult.toBestPhenotype;
import static org.jenetics.engine.limit.bySteadyFitness;

import org.jenetics.*;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionStatistics;
import org.jenetics.engine.codecs;
import org.jenetics.util.DoubleRange;

// javadocs: http://jenetics.io/javadoc/org.jenetics/3.7/index.html
// pdf documentation: http://jenetics.io/manual/manual-3.7.0.pdf
// git: https://github.com/jenetics/jenetics
// examples: https://github.com/jenetics/jenetics/tree/master/org.jenetics.example/src/main/java/org/jenetics/example

// It seems like a bit of effort to learn how to use this lib

public class JeneticsPlayground {
    // The fitness function.
    private static double fitness(final double[] x) {
        double fitness = 0;
        int n=30;
        for (int i=0; i<n; i++)
            fitness += PlayerSkeleton.playAGame(x, false,false);
        fitness /= n;
        //return cos(0.5 + sin(x[0]))*cos(x[0])+x[1];
        return fitness;
    }

    private static void printBest() {
        System.out.println("haha");
    }

    public static void main(final String[] args) {
        final Engine<DoubleGene, Double> engine = Engine
                // Create a new builder with the given fitness
                // function and chromosome.
                .builder(
                        JeneticsPlayground::fitness,
                        codecs.ofVector(DoubleRange.of(-5,5),33)
                        )
                .populationSize(50)
                .optimize(Optimize.MAXIMUM)
                .offspringSelector(new TournamentSelector<>(5))
                .alterers(
                        new Mutator<>(0.3),
                        // There is tonnes of crossovers to choose from, dunno why chosen this one
                        new SinglePointCrossover<>(0.5),
                        new MeanAlterer<>(0.6))
                // Build an evolution engine with the
                // defined parameters.
                .build();

        // Create evolution statistics consumer.
        final EvolutionStatistics<Double, ?>
                statistics = EvolutionStatistics.ofNumber();

        final Phenotype<DoubleGene, Double> best = engine.stream()
                // Truncate the evolution stream after 7 "steady"
                // generations.
                //.limit(bySteadyFitness(100))
                // The evolution will stop after maximal 100
                // generations.
                .limit(300)
                // Update the evaluation statistics after
                // each generation
                .peek(statistics)
                .peek(r -> System.out.println(r.getTotalGenerations()+": "+r.getBestPhenotype().getFitness()))
                // Collect (reduce) the evolution stream to
                // its best phenotype.
                .collect(toBestPhenotype());

        System.out.println(statistics);
        System.out.println(best);
    }
}
