package pl.kamil.domain.algorithm.ga;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import pl.kamil.domain.algorithm.sa.eval.func.TestFunc1;
import pl.kamil.domain.algorithm.sa.eval.func.TestFunc2;
import pl.kamil.domain.model.Point;
import pl.kamil.domain.service.RepresentationConversionService;
import pl.kamil.infrastructure.adapters.TXTExport;

public class GeneticAlgorithmBinary {

    // Parametry algorytmu
    static final int POP_SIZE = 320; // rozmiar populacji
    static final int N = 10; // liczba wymiarów
    static final int BITS_PER_DIM = 16; // bity na wymiar
    static final int GENOME_LENGTH = N * BITS_PER_DIM;
    static final int MAX_EVALUATIONS = 10_000;
    static final int RUNS = 100;
    static final double CROSSOVER_RATE = 0.07;
    static final double MUTATION_RATE = 0.99; // prawdopodobienstwo mutacji

    static final Random randGen = new Random();
    static final RepresentationConversionService rcs = new RepresentationConversionService(-5.0, 5.0);
    static final TXTExport exporter = new TXTExport();

    public static void main(String[] args) throws IOException {
        runExperiment("binary", false);
        runExperiment("gray", true);
    }

    // Uruchomienie eksperymentu
    private static void runExperiment(String name, boolean grayCoding) throws IOException {
        System.out.println("Running " + name + " coding...");

        double[] avgBest1 = new double[MAX_EVALUATIONS];
        double[] avgBest2 = new double[MAX_EVALUATIONS];

        for (int run = 0; run < RUNS; run++) {
            double[] series1 = runSingleOptimization(1, grayCoding);
            double[] series2 = runSingleOptimization(2, grayCoding);

            for (int i = 0; i < MAX_EVALUATIONS; i++) {
                avgBest1[i] += series1[i];
                avgBest2[i] += series2[i];
            }
            System.out.println("Run " + (run + 1) + "/" + RUNS + " done.");
        }

        for (int i = 0; i < MAX_EVALUATIONS; i++) {
            avgBest1[i] /= RUNS;
            avgBest2[i] /= RUNS;
        }

        exporter.save(Arrays.stream(avgBest1).boxed().toList(), "results_func1_" + name);
        exporter.save(Arrays.stream(avgBest2).boxed().toList(), "results_func2_" + name);

        System.out.println("Saved results for " + name + ".");
    }

    // Pojedyncze uruchomienie algorytmu
    private static double[] runSingleOptimization(int funcId, boolean grayCoding) {
        List<boolean[]> population = initPopulation();
        double[] fitness = evaluatePopulation(population, funcId, grayCoding);
        double bestSoFar = Double.MAX_VALUE;

        double[] bestSeries = new double[MAX_EVALUATIONS];
        int evals = POP_SIZE;
        int index = 0;

        // inicjalne najlepsze
        bestSoFar = Arrays.stream(fitness).min().getAsDouble();
        for (int i = 0; i < POP_SIZE && index < MAX_EVALUATIONS; i++) {
            bestSeries[index++] = bestSoFar;
        }

        // glowna petla ewolucji
        while (evals < MAX_EVALUATIONS) {
            List<boolean[]> newPop = new ArrayList<>();

            // 1. Znajdź indeks najlepszego osobnika
            int bestIndex = 0;
            for (int i = 1; i < fitness.length; i++) {
                if (fitness[i] < fitness[bestIndex]) {
                    bestIndex = i;
                }
            }

            // 2. Skopiuj najlepszego osobnika (elitaryzm)
            boolean[] bestIndividual = Arrays.copyOf(population.get(bestIndex), GENOME_LENGTH);
            newPop.add(bestIndividual);

            // 3. Tworzenie reszty populacji
            while (newPop.size() < POP_SIZE) {
                boolean[] p1 = tournamentSelection(population, fitness);
                boolean[] p2 = tournamentSelection(population, fitness);
                boolean[] c1 = Arrays.copyOf(p1, p1.length);
                boolean[] c2 = Arrays.copyOf(p2, p2.length);

                if (randGen.nextDouble() < CROSSOVER_RATE) {
                    int point = randGen.nextInt(GENOME_LENGTH);
                    for (int i = point; i < GENOME_LENGTH; i++) {
                        boolean temp = c1[i];
                        c1[i] = c2[i];
                        c2[i] = temp;
                    }
                }

                mutate(c1);
                mutate(c2);

                newPop.add(c1);
                if (newPop.size() < POP_SIZE) newPop.add(c2);
            }

            // 4. Zastąp starą populację nową
            population = newPop;

            // 5. Ewaluacja nowej populacji
            fitness = evaluatePopulation(population, funcId, grayCoding);
            evals += POP_SIZE;

            // 6. Aktualizacja najlepszego wyniku
            double currentBest = Arrays.stream(fitness).min().getAsDouble();
            if (currentBest < bestSoFar) bestSoFar = currentBest;

            for (int i = 0; i < POP_SIZE && index < MAX_EVALUATIONS; i++) {
                bestSeries[index++] = bestSoFar;
            }
        }

        // jeśli niepelna tablica (np. ostatnia generacja)
        for (; index < MAX_EVALUATIONS; index++) bestSeries[index] = bestSoFar;

        return bestSeries;
    }

    // Inicjalizacja
    private static List<boolean[]> initPopulation() {
        List<boolean[]> pop = new ArrayList<>();
        for (int i = 0; i < POP_SIZE; i++) {
            boolean[] genome = new boolean[GENOME_LENGTH];
            for (int j = 0; j < GENOME_LENGTH; j++) {
                genome[j] = randGen.nextDouble() < 0.5;
            }
            pop.add(genome);
        }
        return pop;
    }

    // Mutacja
    private static void mutate(boolean[] genome) {
        for (int i = 0; i < genome.length; i++) {
            if (randGen.nextDouble() < MUTATION_RATE) {
                genome[i] = !genome[i];
            }
        }
    }

    // Selekcja turniejowa
    private static boolean[] tournamentSelection(List<boolean[]> pop, double[] fitness) {
        int i1 = randGen.nextInt(pop.size());
        int i2 = randGen.nextInt(pop.size());
        return fitness[i1] < fitness[i2] ? pop.get(i1) : pop.get(i2);
    }

    // Ewaluacja populacji
    private static double[] evaluatePopulation(List<boolean[]> pop, int funcId, boolean grayCoding) {
        double[] fit = new double[pop.size()];

        var func = (funcId == 1) ? new TestFunc2() : new TestFunc1();

        for (int i = 0; i < pop.size(); i++) {
            double[] decoded = decode(pop.get(i), grayCoding);
            Point p = new Point(Arrays.stream(decoded).boxed().collect(Collectors.toList()), rcs);
            fit[i] = func.evalFunc(p);
        }

        return fit;
    }

    // Dekodowanie binarne/gray
    private static double[] decode(boolean[] genome, boolean grayCoding) {
        double[] result = new double[N];
        for (int d = 0; d < N; d++) {
            int start = d * BITS_PER_DIM;
            int val = 0;
            for (int i = 0; i < BITS_PER_DIM; i++) {
                val = (val << 1) | (genome[start + i] ? 1 : 0);
            }
            if (grayCoding) val = grayToBinary(val);
            // zakres [-5, 5]
            double x = -5 + 10.0 * val / (Math.pow(2, BITS_PER_DIM) - 1);
            result[d] = x;
        }
        return result;
    }

    // Kodowanie z gray na binarny
    private static int grayToBinary(int n) {
        int mask;
        for (mask = n >> 1; mask != 0; mask = mask >> 1) {
            n = n ^ mask;
        }
        return n;
    }
}

