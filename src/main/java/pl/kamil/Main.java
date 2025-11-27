package pl.kamil;

import pl.kamil.application.DistributionService;
import pl.kamil.application.ports.DataExport;
import pl.kamil.application.usecases.LocalSearchUseCase;
import pl.kamil.domain.algorithm.Kolokwium;
import pl.kamil.domain.algorithm.ga.GeneticAlgorithm;
import pl.kamil.domain.algorithm.sa.eval.func.TestFunc2;
import pl.kamil.domain.eval.func.GeneralizedRosenbrock;
import pl.kamil.domain.eval.func.Salomon;
import pl.kamil.domain.eval.func.Whitley;
import pl.kamil.domain.model.Point;
import pl.kamil.domain.service.*;
import pl.kamil.domain.algorithm.ls.LocalSearch;
import pl.kamil.domain.algorithm.NbhdFunc;
import pl.kamil.domain.algorithm.ls.eval.func.Spherical;
import pl.kamil.infrastructure.adapters.ExcelExport;
import pl.kamil.infrastructure.adapters.TXTExport;
import pl.kamil.infrastructure.services.DataProcessor;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

//    public static void lab3(RandomlyGeneratedNumbers rn) {
//        var execNum = 100;
//        var dim = 10;
//
//        var ef = new TestFunc1(); // Zmieniac przy zmianie funkcji testowej
//        var nbhd = new NbhdFunc(rn);
//        var sa = new SimulatedAnnealingService(
//                new RepresentationConversionService(-3, 3), // Zmieniac przy zmianie funkcji testowej
//                ef,
//                new SimulatedAnnealing(ef,
//                        nbhd,
//                        rn,
//                        new Exp2SchemeControl()),
//                new TXTExport(),
//                new DataProcessor()
//        );
//
//        sa.executeAlgorithm(dim, execNum, 3); // Zmieniac przy zmianie funkcji testowej
//    }

    public static void lab3() {
        /*var simAnn = new SimulatedAnnealing();
        try {
            simAnn.startAnnealing();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public static void lab4(RandomNumbers rn) {
        var dim = 10;
        var execNum = 100;
        var xMin = -32.768;
        var xMax = 32.768;
        Double optimum = 0.0;
        final Map<Integer, List<Double>> fxResults = new TreeMap<>();
        final Map<Integer, List<Double>> ECDF = new TreeMap<>();
        DataExport txtExp = new TXTExport();
        var ga = new GeneticAlgorithm(rn, new RepresentationConversionService(xMin, xMax));
        boolean isBinary = true;

        ga.runTask(dim, execNum, xMin, xMax, new TestFunc2(), fxResults, isBinary, optimum, ECDF);
        txtExp.save(fxResults, "GENETIC_ALGORITHM_F2_REAL");
        txtExp.save(ECDF, "ECDF_FUNCTION2_REAL");
    }

    public static void kolokwium(RandomNumbers rn) {
        var dim = 15;
        var execNum = 100;
        var xMin = -30;
        var xMax = 30;
        Double optimum = 0.0;
        int population_size = 100;
        var alpha = 0.001;
        var k = 3; // liczba rodzicow do rekombinacji
        var wi = 100.0; // koszt naruszenia ograniczeń
        var l = 2; // liczba uczestnikow turnieju

        final Map<Integer, List<Double>> fxResults = new TreeMap<>();
        final Map<Integer, List<Double>> ECDF = new TreeMap<>();
        DataExport txtExp = new TXTExport();
        var kolokwium = new Kolokwium(rn, new RepresentationConversionService(xMin, xMax));

        int[][] ecdfValues = kolokwium.runTask(dim, execNum, population_size, xMin, xMax,
                new GeneralizedRosenbrock(), wi, fxResults, optimum, ECDF, k, alpha, l);
        txtExp.save(fxResults, "ROSENBROCK_REAL");
        txtExp.save(ECDF, "ECDF_ROSENBROCK_REAL");
        txtExp.save(ecdfValues, "Wartosci_do_wykresu_ecdf");

        kolokwium.generateGridData2D(new GeneralizedRosenbrock(), -30, 30, 100, "rosenbrock.txt");
        kolokwium.generateGridData2D(new Salomon(), -100, 100, 100, "salomon.txt");
        kolokwium.generateGridData2D(new Whitley(), -10.24, 10.24, 100, "whitley.txt");
    }

    public static void main(String[] args) {
        var rn = new RandomlyGeneratedNumbers();
        kolokwium(rn);
    }
}
