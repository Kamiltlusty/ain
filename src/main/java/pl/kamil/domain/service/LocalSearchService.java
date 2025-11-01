package pl.kamil.domain.service;

import pl.kamil.domain.algorithm.ls.LocalSearch;
import pl.kamil.domain.algorithm.ls.eval.func.LSEvalFunc;
import pl.kamil.domain.model.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalSearchService {
    private final LSEvalFunc ef;
    private final LocalSearch ls;

    public LocalSearchService(LSEvalFunc ef,
                              LocalSearch ls
    ) {
        this.ef = ef;
        this.ls = ls;
    }

    public Map<Integer, List<Double>> executeAlgorithm(Integer dim, int execNum, double rightDomainCorner, Point rightCornerPoint) {
        Map<Integer, List<Double>> fxResults = new HashMap<>();
        for (int i = 0; i < execNum; i++) {
            List<Double> result = new ArrayList<>();

            // wypelnienie punktu wartosciami prawego rogu dziedziny
            rightCornerPoint.fillCoords(dim, rightDomainCorner);
            // przeskalowanie do zakresu [-10.0 - 10.0] dla spojnosci
            rightCornerPoint.fromAnyToDomain();

            double eval = ef.evalFunc(rightCornerPoint);
            result.add(eval);
            ls.localFirstSearch(rightCornerPoint, eval, result);
            fxResults.put(i, result);
        }
        return fxResults;
    }
}
