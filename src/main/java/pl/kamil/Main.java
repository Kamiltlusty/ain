package pl.kamil;

import java.util.ArrayList;

public class Main {
    public static void task1(){
        var rn = new RandomlyGeneratedNumbers();
        var uds = new DistributionService(
                new GaussianGenerator(rn),
                new UniformGenerator(rn),
                new ExcelExport(),
                new TXTExport());

        uds.createHistogramUniform(1_000_000, 100_000, 1000);
        uds.createHistogramGaussian(1_000_000, 100_000, 1000);

        double r = 100.0;
        uds.monteCarlo(r, 1000, "data_1000.txt");
        uds.monteCarlo(r, 10000, "data_10000.txt");
        uds.monteCarlo(r, 100000, "data_100000.txt");
        uds.monteCarlo(r, 500000, "data_500000.txt");
    }

    public static void task2(){
        int[] n = {2, 5, 10};
        ArrayList<String> fileNames = new ArrayList<>();
        fileNames.add("data_2.txt");
        fileNames.add("data_5.txt");
        fileNames.add("data_10.txt");

        ArrayList<LocalSearch> ls = new ArrayList<>();
        for(int i = 0; i < n.length; i++) {
            ls.add(new LocalSearch(n[i]));
            ls.getLast().repeatLocalSearch(100);
            ls.getLast().save(fileNames.get(i));
        }
    }

    public static void main(String[] args) {
        task2();
    }
}