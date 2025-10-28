package pl.kamil.domain.algorithm.sa;

import pl.kamil.domain.algorithm.NbhdFunc;
import pl.kamil.domain.algorithm.sa.calc.length.CalculateLength;
import pl.kamil.domain.algorithm.sa.calc.control.CalculateControl;
import pl.kamil.domain.algorithm.sa.eval.func.SAEvalFunc;
import pl.kamil.domain.model.Point;
import pl.kamil.domain.service.RandomlyGeneratedNumbers;

import java.util.List;

public class SimulatedAnnealing {
    //    private final NbhdFunc nbhd;
    private final SAEvalFunc ef;
    private final NbhdFunc nbhd;
    private final RandomlyGeneratedNumbers rn;
    private final CalculateLength cl;
    private final CalculateControl cc;

    private final int EVAL_NUM = 10_000;
    private int m = 1;

    public SimulatedAnnealing(SAEvalFunc ef, NbhdFunc nbhd, RandomlyGeneratedNumbers rn, CalculateLength cl, CalculateControl cc) {
        this.ef = ef;
        this.nbhd = nbhd;
        this.rn = rn;
        this.cl = cl;
        this.cc = cc;
    }

    public void simulatedAnnealing(Point p, double eval, List<Double> result) {
        double newEval;
        Point newPoint;
        int counter = 0;
        double T1 = 100;
        double TN = 0;
        double Tk = T1;
        double Lk = 100;
        int N = 50;
        do {
            int tries = 0;
            for (int i = 0; i < Lk; i++) {
                // obliczenie y
                newPoint = nbhd.nbhdFunc(p, m);
                // zamiana z bitow na zakres [-3 - 3] w nowym punkcie
                newPoint.fromUInt16toDomain();
//                // obliczenie evaluacji dla y
                newEval = ef.evalFunc(newPoint);
                if (newEval <= eval) {
                    // dodajemy nowa ewaluacje bo jest lepsza
                    result.add(newEval);
                    p = newPoint;
                    eval = newEval;
                } else {
                    double meter = eval - newEval;
                    if (rn.nextDouble(1) <= Math.exp(meter / Tk)) {
                        // dodajemy nowa ewaluacje pomimo ze jest gorsza
                    } else {
                        // dodajemy stara evaluacje
                        result.add(eval);
                    }
                }
                Lk = cc.calculateLength();
                Tk = cl.calculateControl(T1, TN, N);
                counter++;
                if (counter >= EVAL_NUM - 1) break;
            }
        }
        while (true);
    }
}