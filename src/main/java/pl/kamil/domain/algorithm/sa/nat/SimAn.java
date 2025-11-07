package pl.kamil.domain.algorithm.sa.nat;

import pl.kamil.domain.algorithm.sa.eval.func.TestFunc1;
import pl.kamil.domain.algorithm.sa.eval.func.TestFunc2;
import pl.kamil.domain.model.Point;
import pl.kamil.domain.service.RepresentationConversionService;
import pl.kamil.infrastructure.adapters.TXTExport;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimAn {

    public static void SimAn() {}

    public static void startAnnealing() {
        try {
            // Utworzenie RCS z odpowiednimi zakresami domeny dla obu funkcji:
            RepresentationConversionService rcsFunc1 = new RepresentationConversionService(-3.0, 3.0);
            RepresentationConversionService rcsFunc2 = new RepresentationConversionService(-32.768, 32.768);

            runExperiment(1, "results_function1", "results_min_function1", rcsFunc1);
            runExperiment(2, "results_function2", "results_min_function2", rcsFunc2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void runExperiment(int functionChoice, String fileAverage, String fileMin, RepresentationConversionService rcs) {
        System.out.println("Function " + functionChoice);

        int n = 5;
        int EVAL_NUM = 1_000_000;
        int N = 50;
        int Lk = EVAL_NUM / N;
        double T1 = 100.0;
        double TN = 0.01;
        double Tk = T1;
        double step;
        double minX, maxX;

        if (functionChoice == 1) {
            minX = -3;
            maxX = 3;
            step = 0.2;
        } else {
            minX = -32.768;
            maxX = 32.768;
            step = 1.0;
        }

        Random rnd = new Random();
        TXTExport exporter = new TXTExport();

        List<Double> avgHistory = new ArrayList<>();
        List<Double> minHistory = new ArrayList<>();

        // inicjalizacja punktu startowego
        Point x = new Point(rcs);
        List<Double> initial = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            initial.add(minX + rnd.nextDouble() * (maxX - minX));
        }
        x.setCoords(initial);

        double Fx = evaluateFunction(functionChoice, x);
        int evals = 1;

        double blockSum = Fx;
        int blockCount = 1;
        double minAverage = Fx;

        int k = 0;

        while (evals < EVAL_NUM) {
            for (int i = 0; i < Lk && evals < EVAL_NUM; i++) {
                Point y = neighbor(x, step, minX, maxX, rnd);
                double Fy = evaluateFunction(functionChoice, y);
                evals++;

                if (Fy <= Fx) {
                    x = y.copy();
                    Fx = Fy;
                } else if (rnd.nextDouble() < Math.exp((Fx - Fy) / Tk)) {
                    x = y.copy();
                    Fx = Fy;
                }

                blockSum += Fx;
                blockCount++;

                if (evals % 100 == 0) { // blockSize = 100
                    double avg = blockSum / blockCount;
                    avgHistory.add(avg);

                    if (avg < minAverage) minAverage = avg;
                    minHistory.add(minAverage);

                    blockSum = 0;
                    blockCount = 0;
                }
            }

            k++;
            Tk = calculateControl(T1, TN, N, k);
            if (Tk < TN) Tk = TN;
        }

        exporter.save(avgHistory, fileAverage);
        exporter.save(minHistory, fileMin);

        System.out.println("File saved: " + fileAverage + " & " + fileMin);
    }

    private static double evaluateFunction(int functionChoice, Point p) {
        if (functionChoice == 1) {
            return new TestFunc1().evalFunc(p);
        } else {
            return new TestFunc2().evalFunc(p);
        }
    }

    private static Point neighbor(Point current, double step, double minX, double maxX, Random rnd) {
        Point neighbor = current.copy();
        List<Double> coords = new ArrayList<>(neighbor.getCoords());

        for (int i = 0; i < coords.size(); i++) {
            double newVal = coords.get(i) + rnd.nextGaussian() * step;
            if (newVal < minX) newVal = minX;
            if (newVal > maxX) newVal = maxX;
            coords.set(i, newVal);
        }

        neighbor.setCoords(coords);
        return neighbor;
    }

    private static double calculateControl(double T1, double TN, int N, int k) {
        return T1 * Math.pow(TN / T1, (double) k / N);
    }
}
