package pl.kamil;

import java.util.Random;

public class LocalSearch {
    static int maxIter = 10000;
    static int maxCount = 100;
    static double[] domain = {-10, 10};
    static int bits = 16;

    private  int[][] generateRandomBits(int n) {
        int[][] xBits = new int[n][bits];
        Random r = new Random();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < bits; j++) {
                xBits[i][j] = r.nextBoolean() ? 1 : 0;
            }
        }
        return xBits;
    }

    private void bitsToDoubles(int n, int[][] source, double[] target) {
        for (int i = 0; i < n; i++) {
            int intValue = 0;
            for (int k = 0; k < bits; k++) {
                intValue = (intValue << 1) | source[i][k];
            }
            target[i] = domain[0] + intValue * (domain[1] - domain[0]) / (Math.pow(2, bits) - 1);
        }
    }

    private double F(double[] x) {
        double sum = 0;
        for (double value : x) {
            sum += value * value;
        }
        return sum;
    }

    private void copyBitsInto(int[][] source, int[][] target) {
        for (int j = 0; j < source.length; j++) {
            System.arraycopy(source[j], 0, target[j], 0, bits);
        }
    }

    public void startLocalSearch(int n) {
        if (n <= 0) {throw new IllegalArgumentException("n must be greater than 0");}

        boolean maximize = true;

        int[][] xBits = generateRandomBits(n);

        double[] x = new double[n];
        bitsToDoubles(n, xBits, x);

        double bestResult = F(x);
        double currentBestResult;
        int count = 0;
        int i = 0;
        Random r = new Random();

        do {
            currentBestResult = bestResult;
            double[] xNew = new double[n];
            int[][] newBits = new int[n][bits];
            copyBitsInto(xBits, newBits);

            double newResult;
            int tries = 0;

            do {
                int dim = r.nextInt(n);
                int bit = r.nextInt(bits);
                newBits[dim][bit] = 1 - newBits[dim][bit];

                bitsToDoubles(n, newBits, xNew);

                newResult = F(xNew);
                tries++;
                i++;
            } while (((maximize && newResult <= bestResult) || (!maximize && newResult >= bestResult)) && tries < maxCount);

            if ((maximize && newResult > bestResult) || (!maximize && newResult < bestResult)) {
                x = xNew;
                bestResult = newResult;

                copyBitsInto(newBits, xBits);
            }

            if (currentBestResult == bestResult) {
                count++;
            } else {
                count = 0;
            }
        } while (i < maxIter && count < maxCount);

        System.out.println("n = " + n);
        for (int j = 0; j < n; ++j) {
            System.out.println(x[j]);
        }
        System.out.println("F(x) = " + bestResult);
    }
}
