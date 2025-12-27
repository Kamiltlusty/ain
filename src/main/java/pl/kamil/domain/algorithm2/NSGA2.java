package pl.kamil.domain.algorithm2;

import pl.kamil.domain.algorithm.Naive;
import pl.kamil.domain.algorithm2.ZDT.ZDT1;
import pl.kamil.domain.model.Point;
import pl.kamil.domain.service.RandomlyGeneratedNumbers;

import java.util.*;

public class NSGA2 {
    private final ZDT1 zdt1;
    private final Naive naive;
    private final RandomlyGeneratedNumbers rn;

    public NSGA2(ZDT1 zdt1, Naive naive, RandomlyGeneratedNumbers rn) {
        this.zdt1 = zdt1;
        this.naive = naive;
        this.rn = rn;
    }

    public List<Point> runExperiment(List<Point> population, int m, int l, int k, double alpha) {
        population.forEach(zdt1::evalFunc);
        // obliczenie poczatkowej sigmy ale do poczatkowej populacji nie trzeba jej zapisywac (nie uczestniczy w mutacji) dopiero do dzieci
        double initialSigma = (1.0 - 0.0) * alpha;

        findRanks(population);

//        while
//         EAOffSpringGen
        EAOffspringGen(population, m, l, k, initialSigma);
        return List.of();
    }


    public void findRanks(List<Point> points) {
        List<Point> tmp = new ArrayList<>(points);
        int i = 1;
        while (!tmp.isEmpty()) {
            List<Point> nondominated = naive.runExperiment(tmp);
            tmp.removeAll(nondominated);

            for (Point point : nondominated) {
                point.setRank(i);
            }
            i++;
        }
    }

    private void EAOffspringGen(List<Point> population, int m, int l, int k, double initialSigma) {
        calculateCrowdingDistances(population);
        // selection
        List<Point> parents = select(population, l);
        // crossover
        List<Point> children = recombine(parents, k, m);

        // zapisanie sigmy do dzieci
        List<Double> initialSigmas = new ArrayList<>();
        for (int z = 0; z < m; z++) {
            initialSigmas.add(initialSigma);
        }
        children.forEach(p -> p.setSigmas(new ArrayList<>(initialSigmas)));
        // mutation
        mutation(children, m);
    }

    private void calculateCrowdingDistances(List<Point> population) {
        List<List<Point>> pointsDividedByRanks = new ArrayList<>();
        int highestRank = findHighestRank(population);
        for (int i = 0; i < highestRank; i++) {
            pointsDividedByRanks.add(new ArrayList<>());
        }
        // divide points to lists by ranks
        for (Point point : population) {
            point.setCrowdingDistance(0.0);
            pointsDividedByRanks.get(point.getRank() - 1).add(point);
        }
        // sort lists inside and count crowding distance for them
        int objectivesSize = pointsDividedByRanks.get(0).get(0).getObjectives().size();
        for (int i = 0; i < objectivesSize; i++) {
            for (int j = 0; j < pointsDividedByRanks.size(); j++) {
                List<Point> rank = pointsDividedByRanks.get(j);
                // sort
                int finalI = i;
                rank.sort(Comparator.comparingDouble(p -> p.getObjectives().get(finalI)));

                // assign infinity value for extreme points
                rank.get(0).setCrowdingDistance(Double.MAX_VALUE);
                rank.get(rank.size()-1).setCrowdingDistance(Double.MAX_VALUE);

                // calculate max and min for ith objective
                Double fMin = rank.get(0).getObjectives().get(i);
                Double fMax = rank.get(rank.size() - 1).getObjectives().get(i);
                if (fMax - fMin == 0) continue;

                // resolve crowding distance
                for (int k = 1; k < rank.size() - 1; k++) {
                    Double before = rank.get(k - 1).getObjectives().get(i);
                    Double after = rank.get(k + 1).getObjectives().get(i);
                    // assign crowding distance for middle points with normalization
                    rank.get(k).setCrowdingDistance(rank.get(k).getCrowdingDistance() +
                            (Math.abs(before - after) / (fMax - fMin)));
                }
            }
        }
    }

    int findHighestRank(List<Point> population) {
        int highestRank = 1;
        for (Point point : population) {
            if (point.getRank() > highestRank) {
                highestRank = point.getRank();
            }
        }
        return highestRank;
    }

    // Crowded Tournament Selection Operator
    private List<Point> select(List<Point> points, int l) {
        List<Point> parents = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            List<Point> drawn = new ArrayList<>();
            for (int j = 0; j < l; j++) {
                drawn.add(drawPointFromPopulation(points));
            }
            Point best = drawn.get(0);
            for (int j = 1; j < l; j++) {
                Point drawnJ = drawn.get(j);
                if (drawnJ.getRank() == best.getRank()) {
                    if (drawnJ.getCrowdingDistance() > best.getCrowdingDistance()) {
                        best = drawnJ;
                    }
                } else if (drawnJ.getRank() > best.getRank()) {
                    best = drawn.get(j);
                }
            }
            parents.add(best);
        }
        return parents;
    }

    private Point drawPointFromPopulation(List<Point> population) {
        return population.get(rn.nextInt(population.size()));
    }

    private List<Point> recombine(List<Point> parents, int k, int dim) {
        List<Point> children = new ArrayList<>();

        for (int i = 0; i < parents.size(); i++) {
            List<Point> chosen = chooseK(parents, k);
            Point child = new Point();
            List<Double> dims = new ArrayList<>();

            for (int j = 0; j < dim; j++) {
                double sum = 0;
                for (Point p : chosen) {
                    sum += p.getCoords().get(j);
                }
                dims.add(sum / k);
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

    private void mutation(List<Point> children, int dim) {
        double epsilon0 = 1e-8;
        double tauPrime = 1 / Math.sqrt(2.0 * dim);
        double tau = 1 / Math.sqrt(2.0 * Math.sqrt(dim));

        for (var child : children) {
            List<Double> sigmas = child.getSigmas();

            double globalGaussian = rn.nextGaussian();
            List<Double> newSigmas = new ArrayList<>();
            for (int i = 0; i < dim; i++) {
                double sigma_i = sigmas.get(i);
                double localGaussian = rn.nextGaussian();
                double sigmaPrim = sigma_i * Math.exp(tauPrime * globalGaussian + tau * localGaussian);

                if (sigmaPrim < epsilon0) sigmaPrim = epsilon0;

                newSigmas.add(sigmaPrim);
            }
            child.setSigmas(newSigmas);

            List<Double> newCoords = new ArrayList<>();
            for (int i = 0; i < dim; i++) {
                double x = child.getCoords().get(i) + rn.nextGaussian(0, newSigmas.get(i));
                newCoords.add(x);
            }
            child.setCoords(newCoords);
        }
    }
}
