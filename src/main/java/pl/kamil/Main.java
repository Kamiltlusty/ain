package pl.kamil;

import pl.kamil.application.DistributionService;
import pl.kamil.application.usecases.LocalSearchUseCase;
//import pl.kamil.domain.algorithm.ga.GeneticAlgorithm;
import pl.kamil.domain.algorithm.Naive;
import pl.kamil.domain.algorithm.Kung;
import pl.kamil.domain.model.Point;
import pl.kamil.domain.service.*;
import pl.kamil.domain.algorithm.ls.LocalSearch;
import pl.kamil.domain.algorithm.NbhdFunc;
import pl.kamil.domain.algorithm.ls.eval.func.Spherical;
import pl.kamil.infrastructure.adapters.ExcelExport;
import pl.kamil.infrastructure.adapters.SaveNonDominatedAndDominatedPointsImpl;
import pl.kamil.infrastructure.adapters.TXTExport;
import pl.kamil.infrastructure.services.DataProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.FileReader;

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
        var rng = new RandomlyGeneratedNumbers();

        // Tworzę punkty
        List<Point> points2dim = Stream.generate(Point::new).limit(100).toList();
        List<Point> points5dim = Stream.generate(Point::new).limit(1000).toList();

        // Generuję im wartości losowe
        points2dim.forEach(p -> p.setCoords(List.of(rng.nextDouble(), rng.nextDouble())));
        points5dim.forEach(p -> p.setCoords(List.of(
                rng.nextDouble(), rng.nextDouble(), rng.nextDouble(), rng.nextDouble(), rng.nextDouble()))
        );

        // Test algorytmu naiwnego
        Naive naive = new Naive();
        List<Point> nondominated2dimNaive = naive.runExperiment(points2dim);
        List<Point> nondominated5dimNaive = naive.runExperiment(points5dim);

        System.out.println("Algorytm naiwny");
        System.out.println("2D: " + nondominated2dimNaive.size() + " punktow niezdominowanych z " + points2dim.size());
        System.out.println("5D: " + nondominated5dimNaive.size() + " punktow niezdominowanych z " + points5dim.size());

        // Usuniecie z Listy wszystkich punktów punktów niezdominowanych w celu uzyskania punktów zdominowanych
        List<Point> dominated2dimNaive = new ArrayList<>(points2dim);
        List<Point> dominated5dimNaive = new ArrayList<>(points5dim);
        dominated2dimNaive.removeAll(nondominated2dimNaive);
        dominated5dimNaive.removeAll(nondominated5dimNaive);

        // Test algorytmu Kung'a
        Kung kung = new Kung();
        List<Point> nondominated2dimKung = kung.runExperiment(points2dim);
        List<Point> nondominated5dimKung = kung.runExperiment(points5dim);

        System.out.println("\nAlgorytm Kung'a");
        System.out.println("2D: " + nondominated2dimKung.size() + " punktow niezdominowanych z " + points2dim.size());
        System.out.println("5D: " + nondominated5dimKung.size() + " punktow niezdominowanych z " + points5dim.size());

        // Usuniecie z Listy wszystkich punktów punktów niezdominowanych w celu uzyskania punktów zdominowanych
        List<Point> dominated2dimKung = new ArrayList<>(points2dim);
        List<Point> dominated5dimKung = new ArrayList<>(points5dim);
        dominated2dimKung.removeAll(nondominated2dimKung);
        dominated5dimKung.removeAll(nondominated5dimKung);

        var saveNdomAndDom = new SaveNonDominatedAndDominatedPointsImpl();
        saveNdomAndDom.save(nondominated2dimNaive,
                dominated2dimNaive,
                "punkty2",
                false
        );
        saveNdomAndDom.save(nondominated5dimNaive,
                dominated5dimNaive,
                "punkty5",
                false);

        // Fronty niedominowane
        System.out.println("\nZnajdowanie kolejnych frontow");

        // Plik z moodle ma tabulatory jako separatory
        List<Point> moodlePoints = readPointsFromFile("MO-D3R.txt");
        System.out.println("Wczytano " + moodlePoints.size() + " punktow z pliku");

        if (moodlePoints.isEmpty()) {
            System.out.println("BŁĄD: Nie wczytano żadnych punktow. Sprawdź format pliku.");
            return;
        }

        // Algorytm do znajdowania kolejnych frontów
        List<List<Point>> allFronts = findConsecutiveFronts(moodlePoints, new Kung()); // lub new Naive()

        System.out.println("Znaleziono " + allFronts.size() + " frontow niedominowanych");

        // Eksport danych
        exportFronts(allFronts);

        System.out.println("\nDane zostaly zapisane do plikow:");
        System.out.println("1. fronty_MO-D3R.txt - dane wszystkich frontow");

        System.out.println("\nPorownanie wynikow");
        testOfTime(naive, points2dim, kung, points5dim);
    }

    // Wczytuje punkty dwukryterialne z pliku tekstowego, obsługując tabulatory lub spacje jako separatory
    private static List<Point> readPointsFromFile(String filename) {
        List<Point> points = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                // Pomijaj puste linie i linie zaczynające się od komentarza
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Sprawdza czy linia zawiera tylko liczby
                if (isFirstLine) {
                    if (line.matches(".*[a-zA-Z].*")) {
                        isFirstLine = false;
                        continue;
                    }
                    isFirstLine = false;
                }

                // Używamy tabulatora lub spacji jako separatora
                String[] parts = line.split("[\\t\\s]+"); // obsługuje zarowno tabulatory jak i spacje

                if (parts.length >= 2) {
                    try {
                        double x = Double.parseDouble(parts[0].trim());
                        double y = Double.parseDouble(parts[1].trim());
                        Point point = new Point();
                        point.setCoords(List.of(x, y));
                        points.add(point);
                    } catch (NumberFormatException e) {
                        // Pomija linie które nie są liczbami
                        System.err.println("Ostrzeżenie: Pominięto linię (nie liczby): " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Błąd podczas czytania pliku " + filename + ": " + e.getMessage());
            // Jeśli plik nie istnieje, generuj przykładowe dane
            System.out.println("Generuję przykładowe dane...");
            RandomlyGeneratedNumbers localRng = new RandomlyGeneratedNumbers();
            for (int i = 0; i < 100; i++) {
                Point point = new Point();
                point.setCoords(List.of(localRng.nextDouble(), localRng.nextDouble()));
                points.add(point);
            }
        }
        return points;
    }

    // Znajduje kolejne fronty Pareto poprzez iteracyjne usuwanie aktualnie niezdominowanych punktów
    private static List<List<Point>> findConsecutiveFronts(List<Point> allPoints, pl.kamil.domain.algorithm.ParetoAlgorithm algorithm) {
        List<List<Point>> allFronts = new ArrayList<>();
        List<Point> remainingPoints = new ArrayList<>(allPoints);
        int frontNumber = 1;

        while (!remainingPoints.isEmpty()) {
            // Znajdź front niezdominowany dla obecnych punktów
            List<Point> currentFront = algorithm.runExperiment(remainingPoints);

            if (currentFront.isEmpty()) {
                break;
            }

            System.out.println("Front " + frontNumber + ": " + currentFront.size() + " punktow");
            allFronts.add(new ArrayList<>(currentFront));

            // Usuń znaleziony front z pozostałych punktów
            remainingPoints.removeAll(currentFront);
            frontNumber++;
        }

        return allFronts;
    }

    // Zapisuje wszystkie fronty Pareto do pliku tekstowego
    private static void exportFronts(List<List<Point>> allFronts) {
        TXTExport txtExport = new TXTExport();
        List<Double> frontNumbers = new ArrayList<>();
        List<Double> xCoords = new ArrayList<>();
        List<Double> yCoords = new ArrayList<>();

        for (int frontIndex = 0; frontIndex < allFronts.size(); frontIndex++) {
            List<Point> front = allFronts.get(frontIndex);
            int frontNumber = frontIndex + 1;

            for (Point point : front) {
                List<Double> coords = point.getCoords();
                if (coords.size() >= 2) {
                    frontNumbers.add((double) frontNumber);
                    xCoords.add(coords.get(0));
                    yCoords.add(coords.get(1));
                }
            }
        }

        txtExport.save(frontNumbers, xCoords, yCoords, "fronty_MO-D3R");
    }

    // Porównuje wydajność czasową algorytmów Naive i Kung dla zbiorów 2D i 5D
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

    public static void main(String[] args) {
        var rn = new RandomlyGeneratedNumbers();
        lab8();
    }
}
