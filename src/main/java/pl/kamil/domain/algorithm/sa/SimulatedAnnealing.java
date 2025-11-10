package pl.kamil.domain.algorithm.sa;

import pl.kamil.domain.algorithm.NbhdFunc;
import pl.kamil.domain.algorithm.sa.calc.control.CalculateControl;
import pl.kamil.domain.algorithm.sa.eval.func.EvalFunc;
import pl.kamil.domain.model.Point;
import pl.kamil.domain.service.RandomlyGeneratedNumbers;

import java.util.List;

public class SimulatedAnnealing {
    //    private final NbhdFunc nbhd;
    private final EvalFunc ef;
    private final NbhdFunc nbhd;
    private final RandomlyGeneratedNumbers rn;
    private final CalculateControl cc;

    private final int EVAL_NUM = 10_000;
    private int m = 1;

    public SimulatedAnnealing(EvalFunc ef, NbhdFunc nbhd, RandomlyGeneratedNumbers rn, CalculateControl cc) {
        this.ef = ef;
        this.nbhd = nbhd;
        this.rn = rn;
        this.cc = cc;
    }

    public void simulatedAnnealing(Point p, double eval, List<Double> result) {
        double newEval;
        Point newPoint;
        int counter = 0;
        double T1 = 100;
        double TN = 0.01;
        double Tk = T1;
        int N = 50;
        int Lk = EVAL_NUM / N;
        int k = 0;
        do {
            for (int i = 0; i < Lk && counter < EVAL_NUM - 1; i++) {
                // obliczenie y
                newPoint = nbhd.nbhdFunc(p, m);
                // zamiana z bitow na zakres [-3 - 3] w nowym punkcie
                newPoint.fromUInt16toDomain();
//                // obliczenie evaluacji dla y
                newEval = ef.evalFunc(newPoint);

                // do serii zapisuje zawsze mniejsza wartosc, czyli lepsza ze starej i nowej
                result.add(Math.min(eval, newEval));
                if (newEval <= eval) {
                    // nowa ewaluacja jest lepsza dlatego przypisujemy ja do eval dla nastepnych przebiegow
                    p = newPoint;
                    eval = newEval;
                } else {
                    double meter = eval - newEval;
                    if (rn.nextDouble(1) < Math.exp(meter / Tk)) {
                        // zapisujemy nowa ewaluacje pomimo ze jest gorsza ponieważ musi iśc w parze razem z nowym punktem
                        eval = newEval;
                        p = newPoint;
                    }
                }
                counter++;
            }
            k++;
            Tk = cc.calculateControl(T1, TN, N, k);
            if (counter >= EVAL_NUM - 1) break;
            if (Tk <= TN) break;
        } while (true);
    }
}