package pl.kamil.application;

import pl.kamil.domain.algorithm.sa.SimulatedAnnealing;
import pl.kamil.domain.algorithm.sa.eval.func.EvalFunc;
import pl.kamil.domain.model.Point;
import pl.kamil.domain.service.RepresentationConversionService;
import pl.kamil.application.ports.DataExport;
import pl.kamil.infrastructure.services.DataProcessor;

import java.util.*;

public class SimulatedAnnealingService {
    private final RepresentationConversionService rcs;
    private final EvalFunc ef;
    private final SimulatedAnnealing sa;
    private final DataExport txtExp;
    private final DataProcessor dataProcessor;

    private final Map<Integer, List<Double>> fxResults = new TreeMap<>();

    public SimulatedAnnealingService(RepresentationConversionService rcs, EvalFunc ef, SimulatedAnnealing sa, DataExport txtExp, DataProcessor dataProcessor) {
        this.rcs = rcs;
        this.ef = ef;
        this.sa = sa;
        this.txtExp = txtExp;
        this.dataProcessor = dataProcessor;
    }

    public void executeAlgorithm(int dim, int execNum, double rightDomainCorner, boolean isBinary) {
        for (int i = 0; i < execNum; i++) {
            List<Double> result = new ArrayList<>();
            // dim 10
            Point p = new Point(rcs);
            // wypelnienie punktu wartosciami prawego rogu dziedziny
            p.fillCoords(dim, rightDomainCorner);

            // przeskalowanie do zakresu [-3.0 - 3.0] dla spojnosci
            p.fromAnyToDomain();

            double eval = ef.evalFunc(p);
            result.add(eval);
            sa.simulatedAnnealing(p, eval, result);

            fxResults.put(i, result);
        }

        txtExp.save(fxResults, "SIMULATED_ANNEALING");

        List<Double> average = dataProcessor.average100InvokesTo1(fxResults);
        txtExp.save(average, "SIMULATED_ANNEALING_AVERAGE");
    }
}
