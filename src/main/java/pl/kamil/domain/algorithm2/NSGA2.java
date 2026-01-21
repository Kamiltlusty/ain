package pl.kamil.domain.algorithm2;

import pl.kamil.domain.algorithm.Naive;
import pl.kamil.domain.model.Point;
import pl.kamil.domain.service.RandomlyGeneratedNumbers;
import pl.kamil.infrastructure.adapters.TXTExport;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class NSGA2 {
    private final ParetoEvalFunc zdt;
    private final Naive naive;
    private final RandomlyGeneratedNumbers rn;
    private final TXTExport export;
    private int counter = 0;
    private final List<Integer> saveIterations = List.of(20, 50, 100, 500);
    private final List<List<Point>> savedFronts = new ArrayList<>();

    public NSGA2(ParetoEvalFunc zdt, Naive naive, RandomlyGeneratedNumbers rn, TXTExport export) {
        this.zdt = zdt;
        this.naive = naive;
        this.rn = rn;
        this.export = export;
    }

    public static List<Double> generateNDecisionVariables(int n) {
        var rng = new RandomlyGeneratedNumbers();
        List<Double> values = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            values.add(rng.nextDouble());
        }
        return values;
    }

    public List<Point> runExperiment(int populationSize, int m, int l, int k, double alpha, String functionName, int dimensions) {
        Double initialSigma = alpha;
        List<Point> population = Stream.generate(Point::new).limit(populationSize).toList();
        // generuję im m losowych wartosci zmiennych decyzyjnych
        population.forEach(p -> p.setCoords(generateNDecisionVariables(m)));
        population.forEach(p -> p.setSigmas(new ArrayList<>(Stream.generate(() -> initialSigma).limit(m).toList())));
        // ewaluacja
        for (Point p : population) {
            zdt.evalFunc(p);
        }

        findRanks(population);
        while (counter < 500) {
            counter++;
            if (saveIterations.contains(counter)) {
                saveCurrentFront(population, functionName, dimensions, counter);
            }

//         EAOffSpringGen
            List<Point> combined = EAOffspringGen(population, m, l, k);
            findRanks(combined);
            combined.sort(Comparator.comparingDouble(Point::getRank));
            List<Point> result = new ArrayList<>(populationSize);
            // wypelnia nowa populacje az napotka stopień (rank) ktory sie nie zmiesci caly do nowej populacji zwraca go, aby przekazac do crowding sort
            List<Point> overloadRank = new ArrayList<>(fillResult(combined, populationSize, result));
            calculateCrowdingDistancesFor1Rank(overloadRank);
            // do nowej generacji dodajemy wartosci z nadmiarowego stopnia do limitu rozmiaru populacji
            result.addAll(overloadRank.stream().limit(populationSize - result.size()).toList());
            population = new ArrayList<>(result);
            repairSigmasInPopulation(population, m);
            //counter++;
        }
        saveCurrentFront(population, functionName, dimensions, 500);

        return population;
    }

    private void saveCurrentFront(List<Point> population, String functionName, int dimensions, int iteration) {
        // Znajdź punkty z rankiem 1 (front Pareto)
        List<Point> front = population.stream()
                .filter(p -> p.getRank() == 1)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        // Zapisz front do pliku
        String filename = String.format("%s_%dd_iter%d", functionName, dimensions, iteration);
        export.save(front, filename, true);

        // Dodaj do listy zapisanych frontów
        savedFronts.add(front);
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

    private List<Point> EAOffspringGen(List<Point> population, int m, int l, int k) {
        calculateCrowdingDistancesForPopulation(population);
        // selection
        List<Point> parents = select(population, l);
        // crossover
        List<Point> children = recombine(parents, m);

        // mutation
        mutation(children, m);
        // zapisanie objectives dla populacji dzieci
        for (Point p : children) {
            zdt.evalFunc(p);
        }
        // combine parent and offspring populations
        return combine(population, children);
    }

    private List<Point> fillResult(List<Point> combined, int populationSize, List<Point> result) {
        int i = 1;
        while (true) {
            int finalI = i;
            List<Point> rank = combined.stream().filter(p -> p.getRank() == finalI).toList();
            if (result.size() + rank.size() >= populationSize) {
                return rank;
            }
            result.addAll(rank);
            i++;
        }
    }

    private List<Point> combine(List<Point> population, List<Point> children) {
        List<Point> combined = new ArrayList<>();
        combined.addAll(population);
        combined.addAll(children);
        return combined;
    }

    private void calculateCrowdingDistancesForPopulation(List<Point> population) {
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
                rank.get(rank.size() - 1).setCrowdingDistance(Double.MAX_VALUE);

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

    // CrowdingSort
    private void calculateCrowdingDistancesFor1Rank(List<Point> r) {
        int objectivesSize = r.get(0).getObjectives().size();
        for (int i = 0; i < objectivesSize; i++) {
            // sort
            int finalI = i;
            r.sort(Comparator.comparingDouble(p -> p.getObjectives().get(finalI)));

            // assign infinity value for extreme points
            r.get(0).setCrowdingDistance(Double.MAX_VALUE);
            r.get(r.size() - 1).setCrowdingDistance(Double.MAX_VALUE);

            // calculate max and min for ith objective
            Double fMin = r.get(0).getObjectives().get(i);
            Double fMax = r.get(r.size() - 1).getObjectives().get(i);
            if (fMax - fMin == 0) continue;

            // resolve crowding distance
            for (int k = 1; k < r.size() - 1; k++) {
                Double before = r.get(k - 1).getObjectives().get(i);
                Double after = r.get(k + 1).getObjectives().get(i);
                // assign crowding distance for middle points with normalization
                r.get(k).setCrowdingDistance(r.get(k).getCrowdingDistance() +
                        (Math.abs(before - after) / (fMax - fMin)));
            }
        }
        r.sort(Comparator.comparingDouble(Point::getCrowdingDistance).reversed());
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
//    private List<Point> select(List<Point> points, int l) {
//        List<Point> parents = new ArrayList<>();
//        for (int i = 0; i < points.size(); i++) {
//            List<Point> drawn = new ArrayList<>();
//            for (int j = 0; j < l; j++) {
//                drawn.add(drawPointFromPopulation(points));
//            }
//
//            Point best = drawn.get(0);
//            for (int j = 1; j < l; j++) {
//                Point drawnJ = drawn.get(j);
//                if (drawnJ.getRank() < best.getRank()) {
//                    best = drawnJ;
//                } else if (drawnJ.getRank() == best.getRank()) {
//                    if (drawnJ.getCrowdingDistance() > best.getCrowdingDistance()) {
//                        best = drawnJ;
//                    }
//                }
//            }
//            parents.add(best);
//        }
//        return parents;
//    }

    private List<Point> select(List<Point> population, int l) {
        List<Point> parents = new ArrayList<>();
        int phases = 3;

        for (int i = 0; i < population.size(); i++) {
            Point bestOverall = null;

            for (int phase = 1; phase <= phases; phase++) {
                int candidatesInPhase = l * (int) Math.pow(2, phase);

                List<Point> phaseCandidates = new ArrayList<>();
                for (int j = 0; j < candidatesInPhase; j++) {
                    phaseCandidates.add(population.get(rn.nextInt(population.size())));
                }

                Point bestInPhase = phaseCandidates.get(0);
                for (int j = 1; j < phaseCandidates.size(); j++) {
                    Point candidate = phaseCandidates.get(j);

                    if (candidate.getRank() < bestInPhase.getRank()) {
                        bestInPhase = candidate;
                    } else if (candidate.getRank() == bestInPhase.getRank()) {
                        if (candidate.getCrowdingDistance() > bestInPhase.getCrowdingDistance()) {
                            bestInPhase = candidate;
                        }
                    }
                }

                if (bestOverall == null) {
                    bestOverall = bestInPhase;
                } else {
                    if (bestInPhase.getRank() < bestOverall.getRank()) {
                        bestOverall = bestInPhase;
                    } else if (bestInPhase.getRank() == bestOverall.getRank()) {
                        if (bestInPhase.getCrowdingDistance() > bestOverall.getCrowdingDistance()) {
                            bestOverall = bestInPhase;
                        }
                    }
                }
            }

            parents.add(bestOverall);
        }

        return parents;
    }

    // ze srednia
//    private List<Point> recombine(List<Point> parents, int k, int dim) {
//        List<Point> children = new ArrayList<>();
//
//        for (int i = 0; i < parents.size(); i++) {
//            List<Point> chosen = chooseK(parents, k);
//            Point child = new Point();
//            List<Double> dims = new ArrayList<>();
//            List<Double> sigmas = new ArrayList<>();
//
//            for (int j = 0; j < dim; j++) {
//                double sumCoords = 0;
//                double sumSigmas = 0;
//                for (Point p : chosen) {
//                    sumCoords += p.getCoords().get(j);
//                    sumSigmas += p.getSigmas().get(j);
//                }
//                dims.add(sumCoords / k);
//                sigmas.add(sumSigmas / k);
//            }
//
//            child.setCoords(dims);
//            child.setSigmas(sigmas);
//            children.add(child);
//        }
//
//        return children;
//    }
    // sbx
    private List<Point> recombine(List<Point> parents, int dim) {
        List<Point> children = new ArrayList<>();
        double eta = 15.0;
        double crossoverProb = 0.9;

        for (int i = 0; i < parents.size(); i += 2) {
            if (i + 1 >= parents.size()) {
                children.add(parents.get(i).copy());
                break;
            }

            Point p1 = parents.get(i);
            Point p2 = parents.get(i + 1);

            if (rn.nextDouble() > crossoverProb) {
                children.add(p1.copy());
                children.add(p2.copy());
                continue;
            }

            Point c1 = new Point();
            Point c2 = new Point();
            List<Double> coords1 = new ArrayList<>();
            List<Double> coords2 = new ArrayList<>();
            List<Double> sigmas1 = new ArrayList<>();
            List<Double> sigmas2 = new ArrayList<>();

            for (int j = 0; j < dim; j++) {
                double x1 = p1.getCoords().get(j);
                double x2 = p2.getCoords().get(j);
                double sigma1 = p1.getSigmas().get(j);
                double sigma2 = p2.getSigmas().get(j);

                if (rn.nextDouble() < 0.5) {
                    double temp = x1;
                    x1 = x2;
                    x2 = temp;
                }
                double u = rn.nextDouble();
                double beta;

                if (u <= 0.5) {
                    beta = Math.pow(2.0 * u, 1.0 / (eta + 1.0));
                } else {
                    beta = Math.pow(1.0 / (2.0 * (1.0 - u)), 1.0 / (eta + 1.0));
                }
                double y1 = 0.5 * ((1 + beta) * x1 + (1 - beta) * x2);
                double y2 = 0.5 * ((1 - beta) * x1 + (1 + beta) * x2);

                double childSigma1 = Math.sqrt(Math.abs(sigma1 * sigma2)); // średnia geometryczna
                double childSigma2 = childSigma1;

                coords1.add(y1);
                coords2.add(y2);
                sigmas1.add(childSigma1);
                sigmas2.add(childSigma2);
            }

            c1.setCoords(coords1);
            c1.setSigmas(sigmas1);
            c2.setCoords(coords2);
            c2.setSigmas(sigmas2);
            children.add(c1);
            children.add(c2);
        }

        return children;
    }

    private void repairSigmasInPopulation(List<Point> population, int dim) {
        double currentSigma = 0.05;

        for (Point p : population) {
            if (p.getSigmas() == null || p.getSigmas().size() != dim) {
                List<Double> newSigmas = new ArrayList<>();
                for (int i = 0; i < dim; i++) {
                    newSigmas.add(currentSigma);
                }
                p.setSigmas(newSigmas);
            }
        }
    }

    private void mutation(List<Point> children, int dim) {
        double epsilon0 = 1e-8;
        double scaleFactor = 5;
        double tauPrime = (1 / Math.sqrt(2.0 * dim)) * scaleFactor;
        double tau = (1 / Math.sqrt(2.0 * Math.sqrt(dim))) * scaleFactor;
        double MAX_SIGMA = 0.1;
        double MIN_SIGMA = 0.001;

        for (var child : children) {
            List<Double> sigmas = child.getSigmas();

            double globalGaussian = rn.nextGaussian();
            List<Double> newSigmas = new ArrayList<>();
            for (int i = 0; i < dim; i++) {
                double sigma_i = sigmas.get(i);
                double localGaussian = rn.nextGaussian();
                double sigmaPrim = sigma_i * Math.exp(tauPrime * globalGaussian + tau * localGaussian);

                if (sigmaPrim < epsilon0) sigmaPrim = epsilon0;
                if (sigmaPrim < MIN_SIGMA) sigmaPrim = MIN_SIGMA;
                if (sigmaPrim > MAX_SIGMA) sigmaPrim = MAX_SIGMA;

                newSigmas.add(sigmaPrim);
            }
            child.setSigmas(newSigmas);

            List<Double> newCoords = new ArrayList<>();
            for (int i = 0; i < dim; i++) {
                double x = child.getCoords().get(i) + rn.nextGaussian(0, newSigmas.get(i));

                x = Math.max(0.0, Math.min(1.0, x));
                newCoords.add(x);
            }
            child.setCoords(newCoords);
        }
    }
}
