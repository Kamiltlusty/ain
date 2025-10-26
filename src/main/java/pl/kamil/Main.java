package pl.kamil;

import pl.kamil.application.DistributionService;
import pl.kamil.application.LocalSearchService;
import pl.kamil.domain.algorithm.ls.LocalSearch;
import pl.kamil.domain.algorithm.ls.NbhdFunc;
import pl.kamil.domain.algorithm.ls.eval.func.Spherical;
import pl.kamil.domain.service.RepresentationConversionService;
import pl.kamil.domain.service.GaussianGenerator;
import pl.kamil.domain.service.RandomlyGeneratedNumbers;
import pl.kamil.domain.service.UniformGenerator;
import pl.kamil.infrastructure.io.ExcelExport;
import pl.kamil.infrastructure.io.TXTExport;
import pl.kamil.infrastructure.services.DataProcessor;

import java.util.List;

public class Main {
    public static void lab1(RandomlyGeneratedNumbers rn) {
        var uds = new DistributionService(
                new GaussianGenerator(rn),
                new UniformGenerator(rn),
                new ExcelExport(),
                new TXTExport());
        /*uds.createHistogramUniform(1_000_000, 100_000, 1000);
        uds.createHistogramGaussian(1_000_000, 100_000, 1000);*/
//        double R = 100.0;
//        uds.monteCarlo(R, 1000, "dane_1000.txt");
    }

    public static void lab2(RandomlyGeneratedNumbers rn) {
        var dim = List.of(2, 5, 10);
        var execNum = 100;
        var nbhd = new NbhdFunc(rn);
        var ef = new Spherical();
        var lss = new LocalSearchService(dim,
                execNum,
                new DataProcessor(),
                new TXTExport(),
                new RepresentationConversionService(),
                ef,
                new LocalSearch(nbhd, ef)
        );
        lss.executeAlgorithm();

//        List<LocalSearchService> lssList = new ArrayList<>();
//        List<DataProcessor> dataProcessorList = new ArrayList<>();
//        List<TXTExport> txtList = new ArrayList<>();
//        for (int i = 1; i <= 4; i++) {
//            dataProcessorList.add(new DataProcessor());
//            txtList.add(new TXTExport());
//            lssList.add(new LocalSearchService(dim, execNum, dataProcessorList.getLast(), txtList.getLast(), rcs));
//            lssList.getLast().start(i);
//        }
    }


    public static void main(String[] args) {
        var rn = new RandomlyGeneratedNumbers();
        lab2(rn);
    }
}
