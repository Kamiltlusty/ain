package pl.kamil;

import pl.kamil.application.DistributionService;
import pl.kamil.application.usecases.LocalSearchUseCase;
import pl.kamil.domain.model.Point;
import pl.kamil.domain.service.LocalSearchService;
import pl.kamil.application.SimulatedAnnealingService;
import pl.kamil.domain.algorithm.ls.LocalSearch;
import pl.kamil.domain.algorithm.NbhdFunc;
import pl.kamil.domain.algorithm.ls.eval.func.Spherical;
import pl.kamil.domain.algorithm.sa.SimulatedAnnealing;
import pl.kamil.domain.algorithm.sa.calc.control.*;
import pl.kamil.domain.algorithm.sa.eval.func.TestFunc1;
import pl.kamil.domain.service.RepresentationConversionService;
import pl.kamil.domain.service.GaussianGenerator;
import pl.kamil.domain.service.RandomlyGeneratedNumbers;
import pl.kamil.domain.service.UniformGenerator;
import pl.kamil.infrastructure.adapters.ExcelExport;
import pl.kamil.infrastructure.adapters.TXTExport;
import pl.kamil.infrastructure.services.DataProcessor;

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
        var dim = 2;
        var execNum = 100;
        var nbhd = new NbhdFunc(rn);
        var ef = new Spherical();
        DataProcessor dp = new DataProcessor();
        var rcs = new RepresentationConversionService(-10.0, 10.0);
        var lss = new LocalSearchService(
                ef,
                new LocalSearch(nbhd, ef)
        );
        var lsuc = new LocalSearchUseCase(dp, new TXTExport(), lss);
        lsuc.coordinateAlgorithmAndSave(dim, execNum, 10, new Point(rcs));


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

    public static void lab3(RandomlyGeneratedNumbers rn) {
        var execNum = 100;
        var dim = 10;

        var ef = new TestFunc1(); // Zmieniac przy zmianie funkcji testowej
        var nbhd = new NbhdFunc(rn);
        var sa = new SimulatedAnnealingService(
                new RepresentationConversionService(-3, 3), // Zmieniac przy zmianie funkcji testowej
                ef,
                new SimulatedAnnealing(ef,
                        nbhd,
                        rn,
                        new Exp2SchemeControl()),
                new TXTExport(),
                new DataProcessor()
        );

        sa.executeAlgorithm(dim, execNum, 3); // Zmieniac przy zmianie funkcji testowej
    }


    public static void main(String[] args) {
        var rn = new RandomlyGeneratedNumbers();
        lab2(rn);
    }
}
