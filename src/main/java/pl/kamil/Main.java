package pl.kamil;

import pl.kamil.application.DistributionService;
import pl.kamil.application.usecases.LocalSearchUseCase;
//import pl.kamil.domain.algorithm.ga.GeneticAlgorithm;
import pl.kamil.domain.algorithm.Naive;
import pl.kamil.domain.algorithm.Kung;
import pl.kamil.domain.algorithm.Topic8Ex2;
import pl.kamil.domain.algorithm2.NSGA2;
import pl.kamil.domain.algorithm2.ZDT.*;
import pl.kamil.domain.model.Point;
import pl.kamil.domain.service.*;
import pl.kamil.domain.algorithm.ls.LocalSearch;
import pl.kamil.domain.algorithm.NbhdFunc;
import pl.kamil.domain.algorithm.ls.eval.func.Spherical;
import pl.kamil.infrastructure.adapters.SaveFrontAndPointsImpl;
import pl.kamil.infrastructure.adapters.WritePointsFromTopic8Ex2Impl;
import pl.kamil.infrastructure.adapters.ExcelExport;
import pl.kamil.infrastructure.adapters.TXTExport;
import pl.kamil.infrastructure.services.DataProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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

//    public static void lab4(RandomNumbers rn) {
//        var dim = 10;
//        var execNum = 100;
//        var xMin = -32.768;
//        var xMax = 32.768;
//        Double optimum = 0.0;
//        final Map<Integer, List<Double>> fxResults = new TreeMap<>();
//        final Map<Integer, List<Double>> ECDF = new TreeMap<>();
//        DataExport txtExp = new TXTExport();
//        var ga = new GeneticAlgorithm(rn, new RepresentationConversionService(xMin, xMax));
//        boolean isBinary = true;
//
//        ga.runTask(dim, execNum, xMin, xMax, new TestFunc2(), fxResults, isBinary, optimum, ECDF);
//        txtExp.save(fxResults, "GENETIC_ALGORITHM_F2_REAL");
//        txtExp.save(ECDF, "ECDF_FUNCTION2_REAL");
//    }

//    public static void kolokwium(RandomNumbers rn) {
//        var dim = 30;
//        var execNum = 100;
//        var xMin = -10.24;
//        var xMax = 10.24;
//        Double optimum = 0.0;
//        int population_size = 1000;
//        var alpha = 0.001;
//        var k = 2; // liczba rodzicow do rekombinacji
//        var wi = 200.0; // koszt naruszenia ograniczeń
//        var l = 2; // liczba uczestnikow turnieju
//
//        final Map<Integer, List<Double>> fxResults = new TreeMap<>();
//        final Map<Integer, List<Double>> ECDF = new TreeMap<>();
//        DataExport txtExp = new TXTExport();
//        var kolokwium = new Kolokwium(rn, new RepresentationConversionService(xMin, xMax));
//
//        int[][] ecdfValues = kolokwium.runTask(dim, execNum, population_size, xMin, xMax,
//                new Whitley(), wi, fxResults, optimum, ECDF, k, alpha, l);
//        txtExp.save(fxResults, "ROSENBROCK_REAL");
//        txtExp.save(ECDF, "ECDF_ROSENBROCK_REAL");
//        txtExp.save(ecdfValues, "Wartosci_do_wykresu_ecdf");
//
//        kolokwium.generateGridData2D(new GeneralizedRosenbrock(), -30, 30, 100, "rosenbrock.txt");
//        kolokwium.generateGridData2D(new Salomon(), -100, 100, 100, "salomon.txt");
//        kolokwium.generateGridData2D(new Whitley(), -10.24, 10.24, 100, "whitley.txt");
//    }

    public static void lab8() {
//        var rng = new RandomlyGeneratedNumbers();
//        // tworzę punkty
//        List<Point> points2dim = Stream.generate(Point::new).limit(100).toList();
//        List<Point> points5dim = Stream.generate(Point::new).limit(1000).toList();
//
//        // generuję im wartości losowe w przestrzeni dwukryterialnej czyli takie co mają dwa wymiary
//        points2dim.forEach(p -> p.setCoords(List.of(rng.nextDouble(), rng.nextDouble())));
//        // przestrzeń 5 kryterialna
//        points5dim.forEach(p -> p.setCoords(List.of(
//                rng.nextDouble(), rng.nextDouble(), rng.nextDouble(), rng.nextDouble(), rng.nextDouble()))
//        );
//
//        // Test algorytmu naiwnego
        Naive naive = new Naive();
//        List<Point> nondominated2dimNaive = naive.runExperiment(points2dim);
//        List<Point> nondominated5dimNaive = naive.runExperiment(points5dim);
//
//        System.out.println("Algorytm naiwny");
//        System.out.println("2D: " + nondominated2dimNaive.size() + " punktow niezdominowanych z " + points2dim.size());
//        System.out.println("5D: " + nondominated5dimNaive.size() + " punktow niezdominowanych z " + points5dim.size());
//
//        // usuniecie z Listy wszystkich punktów punktów niezdominowanych w celu uzyskania punktów zdominowanych
//        List<Point> dominated2dimNaive = new ArrayList<>(points2dim);
//        List<Point> dominated5dimNaive = new ArrayList<>(points5dim);
//        dominated2dimNaive.removeAll(nondominated2dimNaive);
//        dominated5dimNaive.removeAll(nondominated5dimNaive);
//
//        // Test algorytmu Kung'a
//        Kung kung = new Kung();
//        List<Point> nondominated2dimKung = kung.runExperiment(points2dim);
//        List<Point> nondominated5dimKung = kung.runExperiment(points5dim);
//
//        System.out.println("\nAlgorytm Kung'a");
//        System.out.println("2D: " + nondominated2dimKung.size() + " punktow niezdominowanych z " + points2dim.size());
//        System.out.println("5D: " + nondominated5dimKung.size() + " punktow niezdominowanych z " + points5dim.size());
//
//        // usuniecie z Listy wszystkich punktów punktów niezdominowanych w celu uzyskania punktów zdominowanych
//        List<Point> dominated2dimKung = new ArrayList<>(points2dim);
//        List<Point> dominated5dimKung = new ArrayList<>(points5dim);
//        dominated2dimKung.removeAll(nondominated2dimKung);
//        dominated5dimKung.removeAll(nondominated5dimKung);
//
//        var saveNdomAndDom = new SaveNonDominatedAndDominatedPointsImpl();
//        saveNdomAndDom.save(nondominated2dimNaive, dominated2dimNaive, "punkty2Naive", false);
//        saveNdomAndDom.save(nondominated5dimNaive, dominated5dimNaive, "punkty5Naive", false);
//        saveNdomAndDom.save(nondominated2dimKung, dominated2dimKung, "punkty2Kung", false);
//        saveNdomAndDom.save(nondominated5dimKung, dominated5dimKung, "punkty5Kung", false);
//
//        System.out.println("\nPorownanie wynikow");

        var di = new WritePointsFromTopic8Ex2Impl();
        List<Point> pointsToAlgorithm = di.write("MO-D3R");
        Topic8Ex2 topic8Ex2 = new Topic8Ex2(naive);
        List<Topic8Ex2.FrontAndPoint> fap = topic8Ex2.findFronts(pointsToAlgorithm);
        SaveFrontAndPointsImpl sfp = new SaveFrontAndPointsImpl();
        sfp.save(fap, "frontyIPunkty");
//        testOfTime(naive, points2dim, kung, points5dim);
    }

    private static void testOfTime(Naive naive, List<Point> points2dim, Kung kung, List<Point> points5dim) {
        System.out.println("\nPomiar czasu wykonania");
        // Dla 2D
        long startTime = System.nanoTime();
        naive.runExperiment(points2dim);
        long naiveTime2D = System.nanoTime() - startTime;

        startTime = System.nanoTime();
        kung.runExperiment(points2dim);
        long kungTime2D = System.nanoTime() - startTime;

        // Dla 5D
        startTime = System.nanoTime();
        naive.runExperiment(points5dim);
        long naiveTime5D = System.nanoTime() - startTime;

        startTime = System.nanoTime();
        kung.runExperiment(points5dim);
        long kungTime5D = System.nanoTime() - startTime;

        System.out.println("Czas wykonania dla 2D:");
        System.out.println("  Naiwny: " + naiveTime2D / 1000000.0 + " ms");
        System.out.println("  Kung:   " + kungTime2D / 1000000.0 + " ms");
        System.out.println("  Przyspieszenie: " + (double) naiveTime2D / kungTime2D + "x");

        System.out.println("\nCzas wykonania dla 5D:");
        System.out.println("  Naiwny: " + naiveTime5D / 1000000.0 + " ms");
        System.out.println("  Kung:   " + kungTime5D / 1000000.0 + " ms");
        System.out.println("  Przyspieszenie: " + (double) naiveTime5D / kungTime5D + "x");
    }

    public static void lab9() {
        int populationSize = 100;
        int m = 50;
        var eFun = new ZDT6();
        int l = 2;
        int k = 2;
        var alpha = 0.04;

        var nsga2 = new NSGA2(eFun, new Naive(), new RandomlyGeneratedNumbers());
        List<Point> points = nsga2.runExperiment(populationSize, m, l, k, alpha);
        TXTExport export = new TXTExport();
        export.save(points, "front", true);
    }

    public static void main(String[] args) {
        lab9();
    }
}
