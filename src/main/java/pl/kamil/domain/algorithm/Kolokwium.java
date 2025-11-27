package pl.kamil.domain.algorithm;

import pl.kamil.domain.algorithm.sa.eval.func.EvalFunc;
import pl.kamil.domain.model.Point;
import pl.kamil.domain.service.RandomNumbers;
import pl.kamil.domain.service.RepresentationConversionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Kolokwium {
    private final RandomNumbers rn;
    private final RepresentationConversionService rcs;

    public Kolokwium(RandomNumbers rn, RepresentationConversionService rcs) {
        this.rn = rn;
        this.rcs = rcs;
    }

    public int[][] runTask(Integer dim, int execNum, int population_size, double xMin,
                           double xMax, EvalFunc eFunc, double wi,
                           Map<Integer, List<Double>> fxResults,
                           Double optimum, Map<Integer, List<Double>> ECDF,
                           int k, double alpha, int l)
    {
        double initialEval = Double.MAX_VALUE;

        // liczba wywołań
        int eval_func_invokes = 10_000 * dim;
        List<Integer> invokeThresholds = new ArrayList<>();
        List<Double> qualityThresholds = new ArrayList<>();

        // obliczenie progów
        evaluateInvokeThresholds(invokeThresholds, dim);
        evaluateQualityThresholds(qualityThresholds);

        int[][] thresholdValues = new int[invokeThresholds.size()][qualityThresholds.size()];

        for (int i = 0; i < execNum; i++) {
            List<Double> difference = executeAlgorithm(dim, population_size, eval_func_invokes,
                    xMin, xMax, eFunc, wi, fxResults, initialEval, i, optimum, ECDF, k, alpha, l);

            ecdfChartDataGenerator(difference, invokeThresholds, qualityThresholds, thresholdValues);
        }
        return thresholdValues;
    }

    private void ecdfChartDataGenerator(List<Double> difference, List<Integer> invokeThresholds, List<Double> qualityThresholds, int[][] thresholdValues) {
        for (int i = 0; i < qualityThresholds.size(); i++) {
            for (int j = 0; j < invokeThresholds.size(); j++) {
                Integer index = invokeThresholds.get(j);
                Double invokeT = difference.get(index - 1);
                if (invokeT < qualityThresholds.get(qualityThresholds.size()-1-i)) {
                    thresholdValues[j][i] += 1;
                }
            }
        }
    }

    private void evaluateInvokeThresholds(List<Integer> invokeThresholds, Integer dim) {
        List<Double> exponent = DoubleStream.iterate(0.0, x -> x + 0.1)
                .limit(41).boxed().toList();
        List<Integer> thresholds = exponent.stream()
                .map(ex -> (int) Math.round(dim * Math.pow(10, ex)))
                .toList();
        invokeThresholds.addAll(thresholds);
    }

    private void evaluateQualityThresholds(List<Double> qualityThresholds) {
        List<Double> thresholds = IntStream.range(0, 51)
                .mapToDouble(i -> -8.0 + i * 0.2)
                .map(x -> Math.pow(10, x))
                .boxed()
                .toList();

        qualityThresholds.addAll(thresholds);
    }

    private List<Double> executeAlgorithm(Integer dim, int population_size, int eval_func_invokes, double xMin, double xMax,
                                          EvalFunc eFunc, double wi, Map<Integer, List<Double>> fxResults,
                                          double initialEval, int i,
                                          Double optimum, Map<Integer, List<Double>> ECDF, int k, double alpha, int l) {
        List<Double> ecdfPerAlgorithm = new ArrayList<>();
        List<Double> result = new ArrayList<>();
        List<Point> population = new ArrayList<>();

        // obliczenie poczatkowej sigmy ale do poczatkowej populacji nie trzeba jej zapisywac (nie uczestniczy w mutacji) dopiero do dzieci
        double initialSigma = (xMax - xMin) * alpha;

        // generowanie populacji (punktów w dziedzinie) i wypelnienie wymiarow losowymi wartosciami
        generatePopulation(dim, xMin, xMax, population, population_size);

        // zastosowanie funkcji ewaluacji na populacji i utworznie mapy chromosom:ewaluacja
        Map<Point, Double> populationEvaluation = evaluate(eFunc, population, wi, xMin, xMax);

        double eval = initialEval;
        eval = addEvalToResult(populationEvaluation, eval, result, optimum, ecdfPerAlgorithm);

        for (int j = 0; j < (eval_func_invokes - population_size) / population_size; j++) {
            //  turniej, w którym będzie t tur losowania l wartosci, z ktorych najlepsza zostanie wyselekcjonowana jako rodzic tworzac grupe rodzicow
            // gdzie t to tyle co populacja, a l to liczba losowo wybranych wartosci
            int t = population_size;
            List<Point> parents = select(t, l, populationEvaluation, population);

            // rekombinacja
            List<Point> children = recombine(parents, k, dim);

            // zapisanie sigmy do dzieci
            children.forEach(p -> p.setSigma(initialSigma));
            // mutacja
            mutation(children, dim);

            // ewaluacja dzieci
            Map<Point, Double> childrenEvaluation = evaluate(eFunc, children, wi, xMin, xMax);
            eval = addEvalToResult(childrenEvaluation, eval, result, optimum, ecdfPerAlgorithm);
            // zamiana - wybieram lepszego z populacji rodziców (populationEvaluation) i dzieci i zapisuje do populationEvaluation
            replace(population, populationEvaluation, children, childrenEvaluation, population_size);
        }
        fxResults.put(i, result);
        ECDF.put(i, ecdfPerAlgorithm);
        return ecdfPerAlgorithm;
    }



    private static Map<Point, Double> evaluate(EvalFunc eFunc, List<Point> data, double wi, double xMin, double xMax) {
        return data.stream().collect(Collectors.toMap(
                key -> key,
                p -> eFunc.evalFunc(p) + constraintPenalizing(p, wi, xMin, xMax)));
    }

    private static double constraintPenalizing(Point p, double wi, double xMin, double xMax) {
        double penalty = 0.0;

        for (double xi : p.getCoords()) {
            if (xi < xMin) {
                penalty += wi * (xMin - xi);
            } else if (xi > xMax) {
                penalty += wi * (xi - xMax);
            }
        }

        return penalty;
    }

    private void generatePopulation(Integer dim, double xMin, double xMax, List<Point> population, int population_size) {
        List<Point> pointsTmp = Stream.generate(() -> new Point(rcs))
                .limit(population_size)
                .toList();

        pointsTmp.forEach(p -> p.fillCoordsWithRandValsFromDomain(dim, xMin, xMax));
        population.addAll(pointsTmp);
    }

    private double addEvalToResult(Map<Point, Double> populationEvaluation, double eval, List<Double> result, Double optimum, List<Double> ecdfPerAlgorithm) {
        for (var p : populationEvaluation.values()) {
            double newEval = p;
            eval = Math.min(eval, newEval);
            result.add(eval);
            ecdfPerAlgorithm.add(eval - optimum);
        }
        return eval;
    }

    private List<Point> select(int t, int l, Map<Point, Double> populationEvaluation, List<Point> population) {
        List<Point> parents = new ArrayList<>();
        for (int i = 0; i < t; i++) {
            Point best = drawPointFromPopulation(population);
            for (int j = 0; j < l; j++) {
                Point drawnPoint = drawPointFromPopulation(population);
                // minimalizacja
                if (populationEvaluation.get(drawnPoint) <= populationEvaluation.get(best)) {
                    best = drawnPoint;
                }
            }
            parents.add(best);
        }
        return parents;
    }

    private Point drawPointFromPopulation(List<Point> population) {
        return population.get(rn.nextInt(population.size()));
    }

//    private List<Point> recombine(List<Point> parents, int k, int dim) {
//        List<Point> children = new ArrayList<>();
//        // z 100 rodziców losujemy k = 3 rodziców i wybieramy na zmiane ich wymiary tworzac dziecko i tak 100 razy i wychodzi nowa populacja
//        for (int i = 0; i < parents.size(); i++) {
//            List<Point> chosen = chooseK(parents, k);
//            Point child = new Point(rcs);
//            List<Double> dims = new ArrayList<>(dim);
//            for (int j = 0; j < dim; j++) {
//                dims.add(chosen.get(j % k).getCoords().get(j));
//            }
//            child.setCoords(dims);
//            children.add(child);
//        }
//        return children;
//    }

    private List<Point> recombine(List<Point> parents, int k, int dim) {
        List<Point> children = new ArrayList<>();

        for (int i = 0; i < parents.size(); i++) {
            // Wybierz k rodziców
            List<Point> chosen = chooseK(parents, k);
            Point child = new Point(rcs);
            List<Double> dims = new ArrayList<>(dim);

            for (int j = 0; j < dim; j++) {
                // Arithmetic Crossover: średnia wartości rodziców w danym wymiarze
                double sum = 0;
                for (Point p : chosen) {
                    sum += p.getCoords().get(j);
                }
                dims.add(sum / k); // średnia z k rodziców
            }

            child.setCoords(dims);
            children.add(child);
        }

        return children;
    }


    private List<Point> chooseK(List<Point> parents, int k) {
        List<Point> chosen = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            chosen.add(parents.get(rn.nextInt(parents.size())));
        }
        return chosen;
    }

    private void mutation(List<Point> children, Integer dim) {
        double epsilon0 = 1e-8;
        double tau = 1 / Math.sqrt(dim);
        // krok samo adaptacji
        for (var child : children) {
            double sigmaPrim = child.getSigma() * Math.exp(tau * rn.nextGaussian());
            if (sigmaPrim < epsilon0) {
                sigmaPrim = epsilon0;
            }
            child.setSigma(sigmaPrim);
        }
        // mutacja
        for (var child : children) {
            List<Double> newCoords = new ArrayList<>();
            for (int i = 0; i < dim; i++) {
                newCoords.add(child.getCoords().get(i) + rn.nextGaussian(0, child.getSigma()));
            }
            child.setCoords(newCoords);
        }
    }

    private void replace(List<Point> population,
                         Map<Point, Double> populationEvaluation,
                         List<Point> children,
                         Map<Point, Double> childrenEvaluation,
                         int population_size
    ) {
        for (int i = 0; i < population_size; i++) {
            Point parent = population.get(i);
            Double parentFitness = populationEvaluation.get(parent);

            Point child = children.get(i);
            Double childFitness = childrenEvaluation.get(child);

            // listy zapewniaja spojnosc rodzic dziecko
            if (childFitness < parentFitness) {
                population.set(i, child);
                populationEvaluation.remove(parent);
                populationEvaluation.put(child, childFitness);
            }
        }
    }

    /**
     * Generuje dane funkcji dwuwymiarowej (siatkę punktów) i zapisuje je do pliku.
     * @param eFunc    funkcja ewaluacji przyjmująca punkt 2D
     * @param xMin     minimalna wartość w domenie
     * @param xMax     maksymalna wartość w domenie
     * @param gridSize liczba punktów w każdym wymiarze siatki (gridSize x gridSize)
     * @param fileName nazwa pliku wyjściowego
     */
    public void generateGridData2D(EvalFunc eFunc, double xMin, double xMax, int gridSize, String fileName) {
        // Obliczenie kroku siatki – równomierny rozkład między xMin a xMax
        double step = (xMax - xMin) / (gridSize - 1);
        try (java.io.PrintWriter writer = new java.io.PrintWriter(fileName)) {
            // Iteracja po wszystkich punktach siatki
            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    // Wyliczenie współrzędnych aktualnego punktu
                    double x = xMin + i * step;
                    double y = xMin + j * step;

                    // Utworzenie punktu reprezentowanego przez obiekt Point
                    Point p = new Point(rcs);
                    p.setCoords(List.of(x, y));

                    // Wywołanie funkcji ewaluacji dla punktu (x, y)
                    double value = eFunc.evalFunc(p);

                    // Zapis wyniku do pliku w formacie: x y f(x, y)
                    writer.println(x + " " + y + " " + value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
