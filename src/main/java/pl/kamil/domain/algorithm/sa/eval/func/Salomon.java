package pl.kamil.domain.algorithm.sa.eval.func;

import pl.kamil.domain.model.Point;

// A.2.4 Salomon
public class Salomon implements EvalFunc {
    @Override
    public double evalFunc(Point p) {
        double fx = 0.0;
        double norm = Math.sqrt(
                p.getCoords().stream().mapToDouble(c -> c*c).sum()
        );
        norm = Math.sqrt(norm);
        fx = -Math.cos(2.0*Math.PI * norm) + 0.1 * norm + 1;
        return fx;
    }
}
