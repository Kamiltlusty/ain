package pl.kamil.application;

import pl.kamil.domain.algorithm.ls.LocalSearch;
import pl.kamil.domain.algorithm.ls.eval.func.LSEvalFunc;
import pl.kamil.domain.model.Point;
import pl.kamil.infrastructure.io.DataExport;
import pl.kamil.infrastructure.services.DataProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalSearchService {
    private final DataExport txtExp;
    private final LSEvalFunc ef;
    private final LocalSearch ls;
    private final DataProcessor dp;

    private final Map<Integer, List<Double>> fxResults = new HashMap<>();

    public LocalSearchService(DataProcessor dp,
                              DataExport txtExp,
                              LSEvalFunc ef,
                              LocalSearch ls
    ) {
        this.dp = dp;
        this.txtExp = txtExp;
        this.ef = ef;
        this.ls = ls;
    }

    public void executeAlgorithm(Integer dim, int execNum, double rightDomainCorner, Point rightCornerPoint) {
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

        txtExp.save(fxResults, "LOCAL_SEARCH");

        List<Double> average = dp.average100InvokesTo1(fxResults);
        txtExp.save(average, "LOCAL_SEARCH_AVERAGE");
    }
}
