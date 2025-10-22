package pl.kamil;

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

    public void start() {
        for (int i = 0; i < execNum; i++) {
            // zaczynam od dim 2
            Integer dimSize = dim.get(2);
            Point p = new Point(dimSize);
            // przeskalowanie do zakresu [-10.0 - 10.0] dla spojnosci
            p.setCoords(p.getCoords().stream()
                    .map(Utils::toDomain).toList());
            double eval = evalFunc(p);
            result.add(eval);
            localFirstSearch(p, eval);
            fxResults.put(i, result);
            result = new ArrayList<>();
        }

        List<Double> average = dataProcessor.average100InvokesTo1(fxResults);
        txtExp.save(average, "LOCAL_SEARCH");
    }

    public double evalFunc(Point p) {
        return p.getCoords().stream()
                .map(v -> v*v)
                .reduce(0.0, Double::sum);
    }

    public void localFirstSearch(Point p, double eval) {
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
                newEval = evalFunc(newPoint);
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
