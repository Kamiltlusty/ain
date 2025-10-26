package pl.kamil.domain.algorithm.ls.eval.func;

import pl.kamil.domain.model.Point;

public class Rosenbrock implements LSEvalFunc {
    @Override
    public double evalFunc(Point p) {
        int n = p.getCoords().size();
        double sum1 = 0.0;
        double sum2 = 0.0;
        for (double v : p.getCoords()) {
            sum1 += v * v;
            sum2 += Math.cos(2 * Math.PI * v);
        }
        return -20.0 * Math.exp(-0.2 * Math.sqrt(sum1 / n))
                - Math.exp(sum2 / n) + 20 + Math.E;
    }
}
