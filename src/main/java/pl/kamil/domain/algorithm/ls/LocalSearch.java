package pl.kamil.domain.algorithm.ls;

import pl.kamil.domain.algorithm.NbhdFunc;
import pl.kamil.domain.algorithm.ls.eval.func.LSEvalFunc;
import pl.kamil.domain.model.Point;

import java.util.List;

public class LocalSearch {
    private final NbhdFunc nbhd;
    private final LSEvalFunc ef;

    private final int MAX_TRIES = 100;
    private final int EVAL_NUM = 10_000;
    private int m = 1;

    public LocalSearch(NbhdFunc nbhd, LSEvalFunc ef) {
        this.nbhd = nbhd;
        this.ef = ef;
    }

    public void localFirstSearch(Point p, double eval, List<Double> result) {
        double newEval;
        Point newPoint;
        int counter = 0;
        do {
            int tries = 0;
            do {
                // obliczenie x'
                newPoint = nbhd.nbhdFunc(p, m);
                // zamiana z bitow na zakres [-10 - 10] w nowym punkcie
                newPoint.fromUInt16toDomain();
                // obliczenie evaluacji dla x'
                newEval = ef.evalFunc(newPoint);
                if (eval <= newEval) {
                    result.add(eval);
                } else {
                    result.add(newEval);
                }
                counter++;
                tries++;
                if (counter >= EVAL_NUM-1) break;
            } while (eval <= newEval && tries < MAX_TRIES);
            if (newEval < eval) {
                p = newPoint;
                eval = newEval;
            }

            if (counter >= EVAL_NUM-1) break;
        } while (true);
    }
}
