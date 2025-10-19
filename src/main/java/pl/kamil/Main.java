package pl.kamil;

import java.util.Random;

public class Main {
    public static void monteCarloAlgorithm(){
        var rn = new RandomlyGeneratedNumbers();
        var uds = new DistributionService(
                new GaussianGenerator(rn),
                new UniformGenerator(rn),
                new ExcelExport(),
                new TXTExport());

        /*uds.createHistogramUniform(1_000_000, 100_000, 1000);
        uds.createHistogramGaussian(1_000_000, 100_000, 1000);*/

        double R = 100.0;
        uds.monteCarlo(R, 1000, "dane_1000.txt");

        uds.monteCarlo(R, 10000, "dane_10000.txt");

        uds.monteCarlo(R, 100000, "dane_100000.txt");

        uds.monteCarlo(R, 500000, "dane_500000.txt");
    }

    public static double F(double[] x) {
        double sum = 0;
        for (int i = 0; i < x.length; i++) {
            sum += x[i] * x[i];
        }
        return sum;
    }

    public static void  LocalSearch(int n){
        int max_iter = 100;
        int max_the_same = 15;
        double[] domain = {-10, 10};
        int bits = 16;
        boolean maximize = true;

        Random rand = new Random();

        int[][] xBits = new int[n][bits];
        for (int i = 0; i < n; i++) {
            for (int b = 0; b < bits; b++) {
                xBits[i][b] = rand.nextBoolean() ? 1 : 0;
            }
        }

        double[] x = new double[n];
        for (int i = 0; i < n; i++) {
            int intValue = 0;
            for (int k = 0; k < bits; k++) {
                intValue = (intValue << 1) | xBits[i][k];
            }
            x[i] = domain[0] + intValue * (domain[1] - domain[0]) / (Math.pow(2, bits) - 1);
        }

        double best_result = F(x);
        double current_best_result;
        int the_same = 0;
        int i = 0;
        Random r = new Random();

        do {
            current_best_result = best_result;
            double[] new_x = new double[n];
            int[][] newBits = new int[n][bits];
            for (int j = 0; j < n; j++) {
                System.arraycopy(xBits[j], 0, newBits[j], 0, bits);
            }

            double new_result;
            int tries = 0;

            do {
                int dim = r.nextInt(n);
                int bit = r.nextInt(bits);
                newBits[dim][bit] = 1 - newBits[dim][bit];

                for (int j = 0; j < n; j++) {
                    int intValue = 0;
                    for (int k = 0; k < bits; k++) {
                        intValue = (intValue << 1) | newBits[j][k];
                    }
                    new_x[j] = domain[0] + intValue * (domain[1] - domain[0]) / (Math.pow(2, bits) - 1);
                }

                new_result = F(new_x);
                tries++;
                i++;
            } while (((maximize && new_result <= best_result) || (!maximize && new_result >= best_result)) && tries < max_the_same);

            if ((maximize && new_result > best_result) || (!maximize && new_result < best_result)) {
                x = new_x;
                best_result = new_result;
                for (int j = 0; j < n; j++) {
                    System.arraycopy(newBits[j], 0, xBits[j], 0, bits);
                }
            }

            if (current_best_result == best_result) {
                the_same++;
            } else {
                the_same = 0;
            }
        } while (i < max_iter && the_same < max_the_same);

        for (int j = 0; j < n; ++j) {
            System.out.println(x[j]);
        }
        System.out.println("F(x) = " + best_result);
    }

    public static void main(String[] args) {
        int[] n = {2, 5, 10};
        for(int current_n : n) {
            System.out.println("n = " + current_n);
            LocalSearch(current_n);
        }
    }
}