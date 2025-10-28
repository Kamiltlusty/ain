package pl.kamil.application;

import pl.kamil.domain.algorithm.sa.SimulatedAnnealing;
import pl.kamil.domain.algorithm.sa.eval.func.SAEvalFunc;
import pl.kamil.domain.model.Point;
import pl.kamil.domain.service.RepresentationConversionService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulatedAnnealingService {
    private final RepresentationConversionService rcs;
    private final SAEvalFunc ef;
    private final SimulatedAnnealing sa;

    private List<Double> result = new ArrayList<>();
    private final Map<Integer, List<Double>> fxResults =  new HashMap<>();

    public SimulatedAnnealingService(RepresentationConversionService rcs, SAEvalFunc ef, SimulatedAnnealing sa) {
        this.rcs = rcs;
        this.ef = ef;
        this.sa = sa;
    }

    public void executeAlgorithm(int dim, int execNum, int rightDomainCorner) {
        for (int i = 0; i < execNum; i++) {
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
            result = new ArrayList<>();
        }

//        List<Double> average = dataProcessor.average100InvokesTo1(fxResults);
//        txtExp.save(average, "SIMULATED_ANNEALING");
    }
}
