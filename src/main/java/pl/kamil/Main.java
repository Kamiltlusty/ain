package pl.kamil;

import pl.kamil.application.DistributionService;
import pl.kamil.application.LocalSearchService;
import pl.kamil.infrastructure.adapters.GaussianGenerator;
import pl.kamil.infrastructure.adapters.RandomlyGeneratedNumbers;
import pl.kamil.infrastructure.adapters.UniformGenerator;
import pl.kamil.infrastructure.io.ExcelExport;
import pl.kamil.infrastructure.io.TXTExport;
import pl.kamil.infrastructure.services.DataProcessor;

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
        //var dp = new DataProcessor();
        //var de = new TXTExport();
        //var lss = new LocalSearchService(dim, execNum, dp, de);
        //lss.start();

        List<LocalSearchService> lssList = new ArrayList<>();
        List<DataProcessor> dataProcessorList = new ArrayList<>();
        List<TXTExport> txtList = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            dataProcessorList.add(new DataProcessor());
            txtList.add(new TXTExport());
            lssList.add(new LocalSearchService(dim, execNum, dataProcessorList.getLast(), txtList.getLast()));
            lssList.getLast().start(i);
        }
    }
}
