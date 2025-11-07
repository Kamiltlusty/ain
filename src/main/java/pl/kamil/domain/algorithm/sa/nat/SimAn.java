package pl.kamil.domain.algorithm.sa.nat;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;

public class SimulatedAnnealing {

    public static void SimulatedAnnealing() {}

    public static void startAnnealing() throws IOException {
        runExperiment(1, "results_function1.txt", "results_min_function1.txt");
        runExperiment(2, "results_function2.txt", "results_min_function2.txt");
    }

    private static void runExperiment(int functionChoice, String fileAverage, String fileMin) throws IOException {
        System.out.println("Function " + functionChoice);

        int n = 5;
        int EVAL_NUM = 1_000_000;
        int N = 50; // liczba etapów schładzania
        int Lk = EVAL_NUM / N; // liczba kroków na temperaturę
        double T1 = 100.0; // temperatura początkowa
        double TN = 0.01; // temperatura końcowa
        int blockSize = 100; // co ile zapisujemy średnią
        double Tk = T1; // aktualna temperatura

        Random rnd = new Random();

        double minX, maxX, step;

        if (functionChoice == 1) {
            minX = -3;
            maxX = 3;
            step = 0.2;
        } else {
            minX = -32.768;
            maxX = 32.768;
            step = 1.0;
        }

        DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
        dfs.setDecimalSeparator(',');
        DecimalFormat df = new DecimalFormat("0.0000000000", dfs);

        try (BufferedWriter writerAvg = new BufferedWriter(new FileWriter(fileAverage));
             BufferedWriter writerMin = new BufferedWriter(new FileWriter(fileMin))) {

            double[] x = new double[n];
            for (int i = 0; i < n; i++)
                x[i] = minX + rnd.nextDouble() * (maxX - minX);

            double Fx = evaluateFunction(functionChoice, x);
            int evals = 1;

            double blockSum = Fx;
            int blockCount = 1;
            double minAverage = Fx;

            int k = 0;

            while (evals < EVAL_NUM) {
                for (int i = 0; i < Lk && evals < EVAL_NUM; i++) {
                    double[] y = neighbor(x, step, minX, maxX, rnd);
                    double Fy = evaluateFunction(functionChoice, y);
                    evals++;

                    if (Fy <= Fx) {
                        x = y;
                        Fx = Fy;
                    } else if (rnd.nextDouble() < Math.exp((Fx - Fy) / Tk)) {
                        x = y;
                        Fx = Fy;
                    }

                    blockSum += Fx;
                    blockCount++;

                    if (evals % blockSize == 0) {
                        double avg = blockSum / blockCount;
                        writerAvg.write(df.format(avg));
                        writerAvg.newLine();

                        if (avg < minAverage) minAverage = avg;
                        writerMin.write(df.format(minAverage));
                        writerMin.newLine();

                        blockSum = 0;
                        blockCount = 0;
                    }
                }

                k++;
                Tk = calculateControl(T1, TN, N, k);
                if (Tk < TN) Tk = TN;
            }
        }

        System.out.println("File saved: " + fileAverage + " & " + fileMin);
    }

    /**
     * Funkcja celu (objective function) — wybór na podstawie numeru.
     */
    private static double evaluateFunction(int functionChoice, double[] x) {
        if (functionChoice == 1) {
            return function1(x);
        } else {
            return function2(x);
        }
    }

    /**
     * Funkcja 1: -5 / (1 + sumSq) + sin(1 / tan(exp(-5 / (1 + sumSq))))
     */
    private static double function1(double[] x) {
        double sumSq = 0.0;
        for (double v : x) sumSq += v * v;
        double inner = Math.exp(-5.0 / (1.0 + sumSq));
        return -5.0 / (1.0 + sumSq) + Math.sin(1.0 / Math.tan(inner));
    }

    /**
     * Funkcja 2: Ackley-like function.
     */
    private static double function2(double[] x) {
        double a = 20.0, b = 0.2, c = 2 * Math.PI;
        int d = x.length;
        double sumSq = 0.0, sumCos = 0.0;
        for (double v : x) {
            sumSq += v * v;
            sumCos += Math.cos(c * v);
        }
        return -a * Math.exp(-b * Math.sqrt(sumSq / d))
                - Math.exp(sumCos / d)
                + a + Math.E;
    }

    /**
     * Generowanie sąsiada (nowego rozwiązania).
     */
    private static double[] neighbor(double[] vec, double step, double minX, double maxX, Random rnd) {
        double[] y = new double[vec.length];
        for (int i = 0; i < vec.length; i++) {
            y[i] = vec[i] + rnd.nextGaussian() * step;
            if (y[i] < minX) y[i] = minX;
            if (y[i] > maxX) y[i] = maxX;
        }
        return y;
    }

    /**
     * Funkcja kontrolna temperatury (geometric cooling).
     */
    private static double calculateControl(double T1, double TN, int N, int k) {
        return T1 * Math.pow(TN / T1, (double) k / N);
    }
}