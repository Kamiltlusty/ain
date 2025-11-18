package pl.kamil.domain.algorithm.ga;

import pl.kamil.domain.algorithm.sa.eval.func.EvalFunc;
import pl.kamil.domain.model.Point;
import pl.kamil.domain.model.UInt16;
import pl.kamil.domain.service.RandomNumbers;
import pl.kamil.domain.service.RepresentationConversionService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GeneticAlgorithm {
    private final RandomNumbers rn;
    private final RepresentationConversionService rcs;
    private final int EVAL_FUNC_INVOKES = 10_000;
    private final int POPULATION_SIZE = 100;

    public GeneticAlgorithm(RandomNumbers rn, RepresentationConversionService rcs) {
        this.rn = rn;
        this.rcs = rcs;
    }

    public void runTask(Integer dim, int execNum, double xMin, double xMax, EvalFunc eFunc, Map<Integer, List<Double>> fxResults, boolean isBinary) {
        double initialEval = Double.MAX_VALUE;
        for (int i = 0; i < execNum; i++) {
            executeAlgorithm(dim, xMin, xMax, eFunc, fxResults, initialEval, i, isBinary);
        }
    }

    private void executeAlgorithm(Integer dim, double xMin, double xMax, EvalFunc eFunc, Map<Integer, List<Double>> fxResults, double initialEval, int i, boolean isBinary) {
        List<Double> result = new ArrayList<>();
        List<Point> population = new ArrayList<>();
        // obliczenie poczatkowej sigmy ale do poczatkowej populacji nie trzeba jej zapisywac (nie uczestniczy w mutacji) dopiero do dzieci
        double initialSigma = (xMax - xMin) * 0.05;

        // generowanie populacji (punktów w dziedzinie) i wypelnienie wymiarow losowymi wartosciami
        generatePopulation(dim, xMin, xMax, population);

        // zastosowanie funkcji ewaluacji na populacji i utworznie mapy chromosom:ewaluacja
        Map<Point, Double> populationEvaluation = evaluate(eFunc, population);

        double eval = initialEval;
        eval = addEvalToResult(populationEvaluation, eval, result);

        for (int j = 0; j < (EVAL_FUNC_INVOKES - POPULATION_SIZE) / POPULATION_SIZE; j++) {
            //  turniej, w którym będzie t tur losowania l wartosci, z ktorych najlepsza zostanie wyselekcjonowana jako rodzic tworzac grupe rodzicow
            // gdzie t to tyle co populacja, a l to liczba losowo wybranych wartosci
            int t = POPULATION_SIZE;
            int l = 2;
            List<Point> parents = select(t, l, populationEvaluation, population);
            // przed rekombinacja dla reprezentacji binarnej zmieniamy punkty na binarne
            if (isBinary) {
                parents.forEach(Point::toUInt16);
            }

            // rekombinacja
            int k = 3;
            List<Point> children = recombine(parents, k, dim, isBinary);

            // zapisanie sigmy do dzieci
            children.forEach(p -> p.setSigma(initialSigma));
            // mutacja
            mutation(children, dim, isBinary);
            // po mutacji  dla reprezentacji binarnej zmieniamy punkty z binarnych na rzeczywistoliczbowe
            if (isBinary) {
                children.forEach(Point::fromUInt16toDomain);
            }
            // ewaluacja dzieci
            Map<Point, Double> childrenEvaluation = evaluate(eFunc, children);
            eval = addEvalToResult(childrenEvaluation, eval, result);
            // zamiana - wybieram lepszego z populacji rodziców (populationEvaluation) i dzieci i zapisuje do populationEvaluation
            replace(population, populationEvaluation, children, childrenEvaluation);
        }
        fxResults.put(i, result);
    }

    private static Map<Point, Double> evaluate(EvalFunc eFunc, List<Point> data) {
        return data.stream().collect(Collectors.toMap(
                key -> key,
                eFunc::evalFunc));
    }

    private void generatePopulation(Integer dim, double xMin, double xMax, List<Point> population) {
        List<Point> pointsTmp = Stream.generate(() -> new Point(rcs))
                .limit(POPULATION_SIZE)
                .toList();

        pointsTmp.forEach(p -> p.fillCoordsWithRandValsFromDomain(dim, xMin, xMax));
        population.addAll(pointsTmp);
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

    private List<Point> recombine(List<Point> parents, int k, int dim, boolean isBinary) {
        List<Point> children = new ArrayList<>();
        if (!isBinary) {
            // z 100 rodziców losujemy k = 3 rodziców i wybieramy na zmiane ich wymiary tworzac dziecko i tak 100 razy i wychodzi nowa populacja
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
        } else {
            // uzywamy two point crossover // z 100 rodziców losujemy k = 2 rodziców i laczymy poczatek od pierwszego srodek od drugiego i koncowke od pierwszego
            for (int i = 0; i < parents.size(); i++) {
                List<Point> chosen = chooseK(parents, 2);
                Point child = new Point(rcs);
                List<UInt16> dims = new ArrayList<>(dim);

                for (int j = 0; j < dim; j++) {
                    // zamiana na bit string
                    String p1BitString = chosen.get(0).getCoords16().get(j).getBitString();
                    String p2BitString = chosen.get(1).getCoords16().get(j).getBitString();
                    List<Integer> crossovers = generateCrossovers();
                    String start = p1BitString.substring(0, crossovers.get(0));
                    String mid = p2BitString.substring(crossovers.get(0), crossovers.get(1));
                    String end = p1BitString.substring(crossovers.get(1), 16);

                    dims.add(new UInt16(start + mid + end));
                }
                child.setCoords16(dims);
                children.add(child);
            }
        }

        return children;
    }

    private List<Integer> generateCrossovers() {
        List<Integer> crossovers = new ArrayList<>();
        Integer first = rn.nextInt(15);
        Integer second = rn.nextInt(first + 1, 16);
        crossovers.add(first);
        crossovers.add(second);
        return crossovers;
    }

    private List<Point> chooseK(List<Point> parents, int k) {
        List<Point> chosen = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            chosen.add(parents.get(rn.nextInt(parents.size())));
        }
        return chosen;
    }

    private void mutation(List<Point> children, Integer dim, boolean isBinary) {
        if (!isBinary) {
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
        } else {
            for (var child : children) {
                List<UInt16> newCoords = new ArrayList<>();
                for (int i = 0; i < dim; i++) {
                    StringBuilder result = new StringBuilder();
                    String childBitString = child.getCoords16().get(i).getBitString();
                    // rozna maska dla kazdego genu/wymiaru
                    String mutationMask = createMutationMask();

                    for (int j = 0; j < childBitString.length(); j++) {
                        result.append((mutationMask.charAt(j) == '1') ?
                                (childBitString.charAt(j) == '0' ? '1' : '0') : childBitString.charAt(j));
                    }
                    newCoords.add(new UInt16(result.toString()));
                }
                child.setCoords16(newCoords);
            }
        }
    }

    private String createMutationMask() {
        StringBuilder mask = new StringBuilder();
        double probability = 0.05;
        for (int i = 0; i < 16; i++) {
            double drawnNum = rn.nextDouble(1);
            if (drawnNum < probability) {
                mask.append('1');
            } else {
                mask.append('0');
            }
        }
        return mask.toString();
    }

    private void replace(List<Point> population,
                         Map<Point, Double> populationEvaluation,
                         List<Point> children,
                         Map<Point, Double> childrenEvaluation) {
        for (int i = 0; i < POPULATION_SIZE; i++) {
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
}
