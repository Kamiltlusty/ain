package pl.kamil.application;

import pl.kamil.domain.algorithm.ls.LocalSearch;
import pl.kamil.domain.algorithm.ls.NbhdFunc;
import pl.kamil.domain.algorithm.ls.eval.func.LSEvalFunc;
import pl.kamil.domain.model.Point;
import pl.kamil.domain.service.RepresentationConversionService;
import pl.kamil.infrastructure.io.DataExport;
import pl.kamil.infrastructure.services.DataProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalSearchService {
    private final DataExport txtExp;
    private final RepresentationConversionService rcs;
    private final LSEvalFunc ef;
    private final LocalSearch ls;
    private final NbhdFunc nbhd;
    private final DataProcessor dataProcessor;

    private final List<Integer> dim;
    private final int execNum;
    private final List<Point> points = new ArrayList<>();
    private List<Double> result = new ArrayList<>();
    private final Map<Integer, List<Double>> fxResults =  new HashMap<>();

    public LocalSearchService(List<Integer> dim,
                              int execNum,
                              DataProcessor dataProcessor,
                              DataExport txtExp,
                              RepresentationConversionService rcs,
                              NbhdFunc nbhd,
                              LSEvalFunc ef,
                              LocalSearch ls
    ) {
        this.dim = dim;
        this.execNum = execNum;
        this.dataProcessor = dataProcessor;
        this.txtExp = txtExp;
        this.rcs = rcs;
        this.nbhd = nbhd;
        this.ef = ef;
        this.ls = ls;
    }

    public void start() {
        for (int i = 0; i < execNum; i++) {
            // zaczynam od dim 2
            Integer dimSize = dim.get(2);
            Point p = new Point(dimSize, rcs);
            // przeskalowanie do zakresu [-10.0 - 10.0] dla spojnosci
            p.fromAnyToDomain();

            double eval = ef.evalFunc(p);
            result.add(eval);
            ls.localFirstSearch(p, eval, result);
            fxResults.put(i, result);
            result = new ArrayList<>();
        }

        List<Double> average = dataProcessor.average100InvokesTo1(fxResults);
        txtExp.save(average, "LOCAL_SEARCH");
    }
}
