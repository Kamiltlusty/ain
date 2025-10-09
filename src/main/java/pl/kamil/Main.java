package pl.kamil;

public class Main {
    public static void main(String[] args) {
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
}