package pl.kamil.application;

import pl.kamil.infrastructure.services.DataProcessor;
import pl.kamil.domain.model.UInt16;
import pl.kamil.domain.service.Utils;
import pl.kamil.domain.model.Point;
import pl.kamil.infrastructure.io.DataExport;

import java.util.*;

public class LocalSearchService {
    private final List<Integer> dim;
    private final int execNum;
    private final List<Point> points = new ArrayList<>();
    private final int EVAL_NUM = 10_000;
    private int m = 1;
    private static final Random rand = new Random();
    private List<Double> result = new ArrayList<>();
    private final Map<Integer, List<Double>> fxResults =  new HashMap<>();
    private final DataProcessor dataProcessor;
    private final int MAX_TRIES = 100;
    private final DataExport txtExp;

    public LocalSearchService(List<Integer> dim, int execNum, DataProcessor dataProcessor, DataExport txtExp) {
        this.dim = dim;
        this.execNum = execNum;
        this.dataProcessor = dataProcessor;
        this.txtExp = txtExp;
    }

    public void start(int algorithm) {
        for (int i = 0; i < execNum; i++) {
            // zaczynam od dim 2
            Integer dimSize = dim.get(2);
            Point p = new Point(dimSize);
            // przeskalowanie do zakresu [-10.0 - 10.0] dla spojnosci
            p.setCoords(p.getCoords().stream()
                    .map(Utils::toDomain).toList());
            double eval = evalFunc(p);
            result.add(eval);
            localFirstSearch(p, eval, algorithm);
            fxResults.put(i, result);
            result = new ArrayList<>();
        }

        List<Double> average = dataProcessor.average100InvokesTo1(fxResults);
        txtExp.save(average, "LOCAL_SEARCH_"+algorithm);
    }

    public double evalFunc(Point p) {
        return p.getCoords().stream()
                .map(v -> v*v)
                .reduce(0.0, Double::sum);
    }

    //Rastrigin
    public double evalFunc2(Point p) {
        int n = p.getCoords().size();
        return 10 * n + p.getCoords().stream()
                .map(v -> v*v - 10 * Math.cos(2 * Math.PI * v))
                .reduce(0.0, Double::sum);
    }

    //Rosenbrock
    public double evalFunc3(Point p) {
        int n = p.getCoords().size();
        double sum1 = 0.0;
        double sum2 = 0.0;
        for (double v : p.getCoords()) {
            sum1 += v * v;
            sum2 += Math.cos(2 * Math.PI * v);
        }
        return -20.0 * Math.exp(-0.2 * Math.sqrt(sum1 / n))
                - Math.exp(sum2 / n) + 20 + Math.E;
    }

    //Ackley
    public double evalFunc4(Point p) {
        double sum = 0.0;
        double prod = 1.0;
        List<Double> coords = p.getCoords();
        for (int i = 0; i < coords.size(); i++) {
            double xi = coords.get(i);
            sum += xi * xi / 4000.0;
            prod *= Math.cos(xi / Math.sqrt(i + 1));
        }
        return sum - prod + 1;
    }

    public void localFirstSearch(Point p, double eval, int algorithm) {
        double newEval;
        Point newPoint;
        int counter = 0;
        do {
            int tries = 0;
            do {
                // obliczenie x'
                newPoint = nbhdFunc(p);
                // zamiana z bitow na zakres [-10 - 10] w nowym punkcie
                newPoint.setCoords(newPoint.getCoords16().stream()
                        .map(Utils::toDomain)
                        .toList());
                // obliczenie evaluacji dla x'
                switch (algorithm) {
                    case 1:
                        newEval = evalFunc(newPoint);
                        break;
                    case 2:
                        newEval = evalFunc2(newPoint);
                        break;
                    case 3:
                        newEval = evalFunc3(newPoint);
                        break;
                    case 4:
                        newEval = evalFunc4(newPoint);
                        break;
                    default:
                        newEval = evalFunc(newPoint);
                }
                if (eval <= newEval) {
                    result.add(eval);
                } else {
                    result.add(newEval);
                }
                counter++;
                tries++;
                if (counter >= EVAL_NUM-1) break;
            } while (eval <= newEval && tries < MAX_TRIES);
            if (newEval < eval) {
                p = newPoint;
                eval = newEval;
            }

            if (counter >= EVAL_NUM-1) break;
        } while (true);
    }

    // nbhd -> neighborhood
    public Point nbhdFunc(Point p) {
        Point neighbor = p.copy();

        // zamiana z zakresu [-10 - 10] na uint16
        neighbor.setCoords16(new ArrayList<>(neighbor.getCoords().stream()
                        .map(Utils::toUInt16)
                        .toList()));

        // obliczenie losowego wymiaru i pobranie wymiaru z punktu
        int dimCount = neighbor.getCoords16().size();
        int randomDim = rand.nextInt(dimCount);
        UInt16 originalUInt16 = neighbor.getCoords16().get(randomDim);

        // perturbacja
        UInt16 perturbed = perturb(originalUInt16, m);

        // wstawienie do sasiada
        neighbor.getCoords16().set(randomDim, perturbed);
        return neighbor;
    }

    public UInt16 perturb(UInt16 uInt16, int m) {
        int value = uInt16.getVal();
        for (int j = 0; j < 16; j++) {
            if (Math.random() < ((double) m / 16)) {
                value ^= (1 << j); // XOR, odwraca bit j
            }
        }
        return new UInt16(value);
    }
}
