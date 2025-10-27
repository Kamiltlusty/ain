package pl.kamil.application;

import pl.kamil.domain.model.Point;
import pl.kamil.domain.service.RepresentationConversionService;

public class SimulatedAnnealingService {
    private final RepresentationConversionService rcs;

    public SimulatedAnnealingService(RepresentationConversionService rcs) {
        this.rcs = rcs;
    }

    public void executeAlgorithm(int dim, int execNum, int rightDomainCorner) {
        for (int i = 0; i < execNum; i++) {
            // dim 10
            Point p = new Point(rcs);
            // wypelnienie punktu wartosciami prawego rogu dziedziny
            p.fillCoords(dim, rightDomainCorner);
            // przeskalowanie do zakresu [-3.0 - 3.0] dla spojnosci
            p.fromAnyToDomain();
//
//            double eval = ef.evalFunc(p);
//            result.add(eval);
//            ls.localFirstSearch(p, eval, result);
//            fxResults.put(i, result);
//            result = new ArrayList<>();
        }

//        List<Double> average = dataProcessor.average100InvokesTo1(fxResults);
//        txtExp.save(average, "LOCAL_SEARCH");
    }
}
