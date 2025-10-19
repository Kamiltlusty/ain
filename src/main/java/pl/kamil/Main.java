package pl.kamil;

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
        var ls = new LocalSearch();
        int[] n = {2, 5, 10};
        for(int nCurrent : n) {
            ls.startLocalSearch(nCurrent);
        }
    }

    public static void main(String[] args) {
        task2();
    }
}