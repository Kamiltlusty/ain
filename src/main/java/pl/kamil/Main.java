package pl.kamil;

import java.util.ArrayList;
import java.util.List;

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
//        double R = 100.0;
//        uds.monteCarlo(R, 1000, "dane_1000.txt");

        var dim = List.of(2, 5, 10);
        var execNum = 100;
        var dp = new DataProcessor();
        var de = new TXTExport();
        var lss = new LocalSearchService(dim, execNum, dp, de);
        lss.start();
    }
}
