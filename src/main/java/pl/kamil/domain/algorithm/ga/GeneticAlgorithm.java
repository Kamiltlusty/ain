package pl.kamil.domain.algorithm.ga;

import pl.kamil.domain.algorithm.sa.eval.func.EvalFunc;
import pl.kamil.domain.model.Point;
import pl.kamil.domain.service.RandomNumbers;
import pl.kamil.domain.service.RepresentationConversionService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GeneticAlgorithm {
    private final RandomNumbers rn;
    private final RepresentationConversionService rcs;
    private final int EVAL_FUNC_INVOKES = 10_000;

    public GeneticAlgorithm(RandomNumbers rn, RepresentationConversionService rcs) {
        this.rn = rn;
        this.rcs = rcs;
    }

    public void executeAlgorithm(Integer dim, int execNum, double xMin, double xMax, EvalFunc eFunc, Map<Integer, List<Double>> fxResults) {
        double initialEval = Double.MAX_VALUE;
        for (int i = 0; i < execNum; i++) {
            List<Double> result = new ArrayList<>();
            int populationSize = 100;
            List<Point> population = new ArrayList<>(populationSize);
            // generowanie populacji (punktów w dziedzinie) i wypelnienie wymiarow losowymi wartosciami
            // oraz obliczenie poczatkowej sigmy ale do poczatkowej populacji nie trzeba jej zapisywac (nie uczestniczy w mutacji) dopiero do dzieci
            double initialSigma = (xMax - xMin) * 0.05;
            List<Point> pointsTmp = Stream.generate(() -> new Point(rcs))
                    .limit(populationSize)
                    .toList();

            pointsTmp.forEach(p -> {
                p.fillCoordsWithRandValsFromDomain(dim, xMin, xMax);
            });
            population.addAll(pointsTmp);

            // zastosowanie funkcji ewaluacji na populacji i utworznie mapy chromosom:ewaluacja
            Map<Point, Double> populationEvaluation = population.stream().collect(Collectors.toMap(
                    key -> key,
                    eFunc::evalFunc));

            double eval = initialEval;
            eval = addEvalToResult(populationEvaluation, eval, result);

            for (int j = 0; j < (EVAL_FUNC_INVOKES - populationSize) / populationSize; j++) {
                //  turniej w którym będzie t tur losowania l wartosci z ktorych najlepsza zostanie wyselekcjonowana jako rodzic tworzac grupe rodzicow
                // gdzie t to tyle co populacja, a l to liczba losowo wybranych wartosci
                int t = populationSize;
                int l = 2;
                List<Point> parents = select(t, l, populationEvaluation, population);
                // rekobinacja z 100 rodziców losujemy k = 3 rodziców i wybieramy na zmiane ich wymiary tworzac dziecko i tak 100 razy i wychodzi nowa populacja
                int k = 3;
                List<Point> children = recombine(parents, k, dim);
                // zapisanie sigmy do dzieci
                children.forEach(p -> p.setSigma(initialSigma));
                // mutacja
                mutation(children, dim);
                // ewaluacja
                Map<Point, Double> childrenEvaluation = children.stream().collect(Collectors.toMap(
                        key -> key,
                        eFunc::evalFunc));
                eval = addEvalToResult(childrenEvaluation, eval, result);
                // zamiana - wybieram lepszego z populacji rodziców (populationEvaluation) i dzieci i zapisuje do populationEvaluation
                replace(populationSize, population, populationEvaluation, children, childrenEvaluation);
            }
            fxResults.put(i, result);
        }
    }

    private double addEvalToResult(Map<Point, Double> populationEvaluation, double eval, List<Double> result) {
        for (var p : populationEvaluation.values()) {
            double newEval = p;
            if (newEval < eval) {
                result.add(newEval);
                eval = newEval;
            } else {
                result.add(eval);
            }
        }
        return eval;
    }

    private void replace(int populationSize,
                         List<Point> population,
                         Map<Point, Double> populationEvaluation,
                         List<Point> children,
                         Map<Point, Double> childrenEvaluation) {
        for (int i = 0; i < populationSize; i++) {
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

    private List<Point> recombine(List<Point> parents, int k, int dim) {
        List<Point> children = new ArrayList<>(parents.size());
        for (int i = 0; i < parents.size(); i++) {
            List<Point> chosen = chooseK(parents, k);
            Point child = new Point(rcs);
            List<Double> dims = new ArrayList<>(dim);
            for (int j = 0; j < dim; j++) {
                dims.add(chosen.get(j % k).getCoords().get(j));
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
}
