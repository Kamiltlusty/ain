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

    public GeneticAlgorithm(RandomNumbers rn) {
        this.rn = rn;
    }

    public void executeAlgorithm(Integer dim, int execNum, double xMin, double xMax, EvalFunc eFunc) {
        for (int i = 0; i < execNum; i++) {
            int populationSize = 100;
            List<Point> population = new ArrayList<>(populationSize);
            // generowanie populacji (punktów w dziedzinie) i wypelnienie wymiarow losowymi wartosciami
            population.addAll(Stream.generate(() -> new Point(new RepresentationConversionService(xMin, xMax)))
                    .limit(populationSize)
                    .peek(p -> p.fillCoordsWithRandValsFromDomain(dim, xMin, xMax))
                    .toList());
            // zastosowanie funkcji ewaluacji na populacji i utworznie mapy chromosom:ewaluacja
            Map<Point, Double> populationEvaluation = population.stream().collect(Collectors.toMap(
                            key -> key,
                            eFunc::evalFunc));
            //  turniej w którym będzie t tur losowania l wartosci z ktorych najlepsza zostanie wyselekcjonowana jako rodzic tworzac grupe rodzicow
            // gdzie t to 10% z populacji, a l to liczba losowo wybranych wartosci
            int t = populationSize/10;
            int l = 10;
            select(t, l, populationEvaluation, population);
        }
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
